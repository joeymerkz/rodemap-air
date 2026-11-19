package io.github.jqssun.airplay.link

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import io.github.jqssun.airplay.Prefs
import io.github.jqssun.airplay.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.URL

/**
 * Links this receiver to the owner's Rodemap account ("Link to Rodemap").
 *
 * The owner generates a 6-digit code in Rodemap (Work > Rodemap Air > Add a
 * receiver) and types it here; enroll exchanges it for a bearer token that is
 * stored in the app's private settings prefs. While a token is stored, the
 * AirPlay service sends a heartbeat every 60 s so the receiver shows as
 * online in Rodemap. A 401 from the server means the link was revoked there:
 * the token is cleared and the UI falls back to the not-linked state.
 */
object RodemapLink {
    private const val TAG = "RodemapLink"
    private const val BASE_URL = "https://rodemap.app"
    private const val ENROLL_PATH = "/api/air/receiver/enroll"
    private const val HEARTBEAT_PATH = "/api/air/receiver/heartbeat"

    sealed interface EnrollResult {
        data object Success : EnrollResult

        /** [message] is the server's own error text, or null for a generic failure. */
        data class Failure(val message: String?) : EnrollResult
    }

    fun isLinked(prefs: SharedPreferences): Boolean = prefs[Prefs.RODEMAP_TOKEN].isNotEmpty()

    fun unlink(prefs: SharedPreferences) {
        Prefs.RODEMAP_TOKEN.write(prefs, "")
        Prefs.RODEMAP_RECEIVER_ID.write(prefs, "")
    }

    suspend fun enroll(context: Context, prefs: SharedPreferences, code: String): EnrollResult {
        val body = JSONObject()
            .put("code", code)
            .put("model", Build.MODEL ?: "")
            .put("appVersion", appVersion(context))
            .put("ipLan", lanIp())
            .put("receiverName", receiverName(prefs))
        return try {
            val (status, json) = post(ENROLL_PATH, body, bearer = null)
            val token = json?.optString("token").orEmpty()
            if (status == 200 && token.isNotEmpty()) {
                Prefs.RODEMAP_TOKEN.write(prefs, token)
                Prefs.RODEMAP_RECEIVER_ID.write(prefs, json?.optString("receiverId").orEmpty())
                Log.i(TAG, "Linked to Rodemap")
                EnrollResult.Success
            } else {
                EnrollResult.Failure(json?.optString("error")?.takeIf { it.isNotBlank() })
            }
        } catch (e: Exception) {
            Log.w(TAG, "Enroll failed", e)
            EnrollResult.Failure(null)
        }
    }

    /**
     * One heartbeat round. Returns false only when the server rejected the
     * token (401: unknown or revoked) — in that case the stored token has
     * already been cleared. Network failures keep the link.
     */
    suspend fun heartbeat(context: Context, prefs: SharedPreferences): Boolean {
        val token = prefs[Prefs.RODEMAP_TOKEN]
        if (token.isEmpty()) return true
        val body = JSONObject()
            .put("ipLan", lanIp())
            .put("appVersion", appVersion(context))
            .put("receiverName", receiverName(prefs))
        return try {
            val (status, _) = post(HEARTBEAT_PATH, body, bearer = token)
            if (status == 401) {
                Log.i(TAG, "Heartbeat rejected (401); clearing Rodemap link")
                unlink(prefs)
                false
            } else {
                true
            }
        } catch (e: Exception) {
            Log.w(TAG, "Heartbeat failed", e)
            true
        }
    }

    private fun receiverName(prefs: SharedPreferences): String =
        prefs[Prefs.SERVER_NAME].ifBlank { Prefs.SERVER_NAME.def }

    // BuildConfig is not generated in this module; the manifest versionName
    // read through PackageManager is the same value
    private fun appVersion(context: Context): String =
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
            .getOrNull().orEmpty()

    /** Current LAN IPv4, preferring wlan/eth interfaces; "" when none found. */
    fun lanIp(): String = runCatching {
        val ifaces = NetworkInterface.getNetworkInterfaces()?.toList().orEmpty()
            .filter { it.isUp && !it.isLoopback }
            .sortedBy { if (it.name.startsWith("wlan") || it.name.startsWith("eth")) 0 else 1 }
        for (iface in ifaces) {
            for (addr in iface.interfaceAddresses) {
                val ip = addr.address
                if (ip is Inet4Address && !ip.isLoopbackAddress) return ip.hostAddress.orEmpty()
            }
        }
        ""
    }.getOrDefault("")

    private suspend fun post(
        path: String,
        body: JSONObject,
        bearer: String?,
    ): Pair<Int, JSONObject?> = withContext(Dispatchers.IO) {
        val conn = URL(BASE_URL + path).openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "POST"
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            if (bearer != null) conn.setRequestProperty("Authorization", "Bearer $bearer")
            conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            val status = conn.responseCode
            val text = runCatching {
                (if (status in 200..299) conn.inputStream else conn.errorStream)
                    ?.bufferedReader()?.readText()
            }.getOrNull()
            status to text?.let { runCatching { JSONObject(it) }.getOrNull() }
        } finally {
            conn.disconnect()
        }
    }
}
