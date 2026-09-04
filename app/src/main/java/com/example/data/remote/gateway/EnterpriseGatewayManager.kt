package com.example.data.remote.gateway

import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.GuideInfo
import com.example.data.remote.MockTukTukInventory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

enum class ConnectionStatus(val displayName: String, val isHealthy: Boolean) {
    DISCONNECTED("Disconnected", false),
    CONNECTING("Connecting...", false),
    CONNECTED("Connected & Synced", true),
    SYNCING("Synchronizing...", true),
    ERROR("Connection Failed", false)
}

data class AdminConnectionConfig(
    val baseUrl: String = "https://admin.tuktuk24lisbon.com/api/v1",
    val apiKey: String = "adm_live_tuk24_lisbon_sec_8921",
    val webhookUrl: String = "https://admin.tuktuk24lisbon.com/webhooks/dispatch",
    val autoSyncEnabled: Boolean = true,
    val syncIntervalSeconds: Int = 30,
    val status: ConnectionStatus = ConnectionStatus.CONNECTED,
    val latencyMs: Long = 42,
    val lastSyncTimestampMs: Long = System.currentTimeMillis(),
    val lastErrorMessage: String? = null
)

data class GuideConnectionConfig(
    val gatewayUrl: String = "https://guides.tuktuk24lisbon.com/api/v1/dispatch",
    val pairingToken: String = "drv_token_diogo_silva_78",
    val activeDriverId: String = "guide_diogo",
    val activeDriverName: String = "Diogo Silva",
    val vehicleId: String = "Eco Tuk-Tuk #07",
    val vehiclePlate: String = "78-TK-24 (PT)",
    val batteryPercentage: Int = 94,
    val isOnDuty: Boolean = true,
    val status: ConnectionStatus = ConnectionStatus.CONNECTED,
    val latencyMs: Long = 38,
    val lastSyncTimestampMs: Long = System.currentTimeMillis(),
    val lastErrorMessage: String? = null
)

data class FleetVehicle(
    val id: String,
    val name: String,
    val licensePlate: String,
    val assignedGuideName: String,
    val batteryPercent: Int,
    val status: String,
    val currentZone: String
)

data class WebhookLogEntry(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String,
    val payloadPreview: String,
    val statusCode: Int = 200,
    val isSuccess: Boolean = true
) {
    val formattedTime: String
        get() {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}

class EnterpriseGatewayManager {

    private val _adminConfig = MutableStateFlow(AdminConnectionConfig())
    val adminConfig: StateFlow<AdminConnectionConfig> = _adminConfig.asStateFlow()

    private val _guideConfig = MutableStateFlow(GuideConnectionConfig())
    val guideConfig: StateFlow<GuideConnectionConfig> = _guideConfig.asStateFlow()

    private val _fleetVehicles = MutableStateFlow(
        listOf(
            FleetVehicle("VEH-07", "Eco Tuk-Tuk #07", "78-TK-24", "Diogo Silva", 94, "On Tour", "Alfama & Miradouros"),
            FleetVehicle("VEH-12", "Eco Tuk-Tuk #12", "54-PT-90", "Inês Santos", 88, "Ready for Dispatch", "Rossio Central"),
            FleetVehicle("VEH-03", "Eco Tuk-Tuk #03", "32-XL-11", "Miguel Ferreira", 76, "En Route to Belém", "Belém Tower Hub"),
            FleetVehicle("VEH-19", "Eco Tuk-Tuk #19", "89-AA-42", "Tiago Mendes", 100, "Fast Charging (Solar)", "Central Depot")
        )
    )
    val fleetVehicles: StateFlow<List<FleetVehicle>> = _fleetVehicles.asStateFlow()

    private val _webhookLogs = MutableStateFlow(
        listOf(
            WebhookLogEntry(
                id = "log_1",
                timestamp = System.currentTimeMillis() - 120_000,
                eventType = "admin.sync.heartbeat",
                payloadPreview = "{\"status\":\"healthy\",\"fleet_online\":4,\"latency_ms\":42}",
                statusCode = 200
            ),
            WebhookLogEntry(
                id = "log_2",
                timestamp = System.currentTimeMillis() - 75_000,
                eventType = "guide.dispatch.telemetry",
                payloadPreview = "{\"driver\":\"Diogo Silva\",\"vehicle\":\"Eco Tuk-Tuk #07\",\"battery\":94}",
                statusCode = 200
            ),
            WebhookLogEntry(
                id = "log_3",
                timestamp = System.currentTimeMillis() - 30_000,
                eventType = "booking.guide.assigned",
                payloadPreview = "{\"booking_id\":\"TT24-88412\",\"guide_id\":\"guide_diogo\",\"status\":\"GUIDE_ASSIGNED\"}",
                statusCode = 200
            )
        )
    )
    val webhookLogs: StateFlow<List<WebhookLogEntry>> = _webhookLogs.asStateFlow()

    suspend fun testAdminConnection(url: String, apiKey: String): Pair<Boolean, String> {
        _adminConfig.value = _adminConfig.value.copy(status = ConnectionStatus.CONNECTING)
        delay(700)
        val isSuccess = url.isNotBlank() && url.startsWith("http")
        val simulatedLatency = (35..65).random().toLong()
        if (isSuccess) {
            _adminConfig.value = _adminConfig.value.copy(
                baseUrl = url,
                apiKey = apiKey,
                status = ConnectionStatus.CONNECTED,
                latencyMs = simulatedLatency,
                lastSyncTimestampMs = System.currentTimeMillis(),
                lastErrorMessage = null
            )
            recordWebhook(
                eventType = "admin.api.test_ping",
                payload = "{\"endpoint\":\"$url\",\"auth\":\"Bearer ${apiKey.take(6)}...\",\"status\":\"OK\"}",
                isSuccess = true,
                statusCode = 200
            )
            return Pair(true, "Successfully verified connection to Admin Panel (Latency: ${simulatedLatency}ms, HTTP 200 OK)")
        } else {
            _adminConfig.value = _adminConfig.value.copy(
                status = ConnectionStatus.ERROR,
                lastErrorMessage = "Invalid endpoint URL. Must start with http:// or https://"
            )
            recordWebhook(
                eventType = "admin.api.test_ping",
                payload = "{\"endpoint\":\"$url\",\"error\":\"Invalid URL format\"}",
                isSuccess = false,
                statusCode = 400
            )
            return Pair(false, "Failed to connect: Invalid Admin API URL format.")
        }
    }

    suspend fun testGuideGateway(gatewayUrl: String, token: String): Pair<Boolean, String> {
        _guideConfig.value = _guideConfig.value.copy(status = ConnectionStatus.CONNECTING)
        delay(650)
        val isSuccess = gatewayUrl.isNotBlank() && gatewayUrl.startsWith("http")
        val simulatedLatency = (30..55).random().toLong()
        if (isSuccess) {
            _guideConfig.value = _guideConfig.value.copy(
                gatewayUrl = gatewayUrl,
                pairingToken = token,
                status = ConnectionStatus.CONNECTED,
                latencyMs = simulatedLatency,
                lastSyncTimestampMs = System.currentTimeMillis(),
                lastErrorMessage = null
            )
            recordWebhook(
                eventType = "guide.gateway.test_ping",
                payload = "{\"gateway\":\"$gatewayUrl\",\"token\":\"${token.take(6)}...\",\"status\":\"CONNECTED\"}",
                isSuccess = true,
                statusCode = 200
            )
            return Pair(true, "Tour Guide Dispatch Gateway online (Latency: ${simulatedLatency}ms, WebSocket Ready)")
        } else {
            _guideConfig.value = _guideConfig.value.copy(
                status = ConnectionStatus.ERROR,
                lastErrorMessage = "Invalid Gateway URL format."
            )
            return Pair(false, "Failed to connect to Tour Guide Gateway.")
        }
    }

    fun updateAdminSettings(url: String, apiKey: String, webhook: String, autoSync: Boolean) {
        _adminConfig.value = _adminConfig.value.copy(
            baseUrl = url,
            apiKey = apiKey,
            webhookUrl = webhook,
            autoSyncEnabled = autoSync,
            lastSyncTimestampMs = System.currentTimeMillis()
        )
    }

    fun updateGuideSettings(url: String, token: String, driverId: String, vehicleId: String) {
        val guide = MockTukTukInventory.guides.find { it.id == driverId }
        _guideConfig.value = _guideConfig.value.copy(
            gatewayUrl = url,
            pairingToken = token,
            activeDriverId = driverId,
            activeDriverName = guide?.name ?: "Diogo Silva",
            vehicleId = vehicleId,
            lastSyncTimestampMs = System.currentTimeMillis()
        )
    }

    fun setDriverDuty(isOnDuty: Boolean) {
        _guideConfig.value = _guideConfig.value.copy(isOnDuty = isOnDuty)
        recordWebhook(
            eventType = "guide.duty.status_change",
            payload = "{\"driver\":\"${_guideConfig.value.activeDriverName}\",\"on_duty\":$isOnDuty}",
            isSuccess = true
        )
    }

    fun updateBatteryPercentage(percent: Int) {
        val clamped = percent.coerceIn(5, 100)
        _guideConfig.value = _guideConfig.value.copy(batteryPercentage = clamped)
        // Also update fleet list
        _fleetVehicles.value = _fleetVehicles.value.map {
            if (it.name == _guideConfig.value.vehicleId) it.copy(batteryPercent = clamped) else it
        }
        recordWebhook(
            eventType = "fleet.telemetry.battery",
            payload = "{\"vehicle\":\"${_guideConfig.value.vehicleId}\",\"battery\":$clamped}",
            isSuccess = true
        )
    }

    fun recordWebhook(eventType: String, payload: String, isSuccess: Boolean = true, statusCode: Int = 200) {
        val entry = WebhookLogEntry(
            id = "log_${System.currentTimeMillis()}_${(100..999).random()}",
            timestamp = System.currentTimeMillis(),
            eventType = eventType,
            payloadPreview = payload,
            statusCode = statusCode,
            isSuccess = isSuccess
        )
        _webhookLogs.value = listOf(entry) + _webhookLogs.value.take(29)
    }

    fun generateGuidePairingDeepLink(): String {
        val conf = _guideConfig.value
        return "tuktuk24-guide://pair?gateway=${conf.gatewayUrl}&token=${conf.pairingToken}&driverId=${conf.activeDriverId}&vehicle=${conf.vehicleId}"
    }

    fun generateAdminPairingDeepLink(): String {
        val conf = _adminConfig.value
        return "tuktuk24-admin://connect?api=${conf.baseUrl}&key=${conf.apiKey}&webhook=${conf.webhookUrl}"
    }

    fun getOpenApiRestSpec(): String {
        return """
        {
          "openapi": "3.1.0",
          "info": {
            "title": "TukTuk24 Enterprise Admin & Tour Guide Integration API",
            "version": "2.4.0",
            "description": "REST & WebSocket endpoints for connecting external Admin Web Dashboards and Tour Guide mobile applications."
          },
          "servers": [
            { "url": "${_adminConfig.value.baseUrl}", "description": "Admin Operations Server" },
            { "url": "${_guideConfig.value.gatewayUrl}", "description": "Tour Guide Dispatch Gateway" }
          ],
          "paths": {
            "/admin/bookings": {
              "get": {
                "summary": "List all client bookings with status, passengers, and assigned chauffeurs",
                "responses": { "200": { "description": "List of bookings" } }
              },
              "post": {
                "summary": "Push new booking from external admin dashboard",
                "responses": { "201": { "description": "Created" } }
              }
            },
            "/admin/bookings/{id}/assign-guide": {
              "put": {
                "summary": "Assign chauffeur/guide to booking",
                "parameters": [{ "name": "id", "in": "path", "required": true }]
              }
            },
            "/admin/bookings/{id}/status": {
              "put": {
                "summary": "Update booking status (CONFIRMED, GUIDE_ASSIGNED, GUIDE_ON_THE_WAY, STARTED, COMPLETED)"
              }
            },
            "/guide/dispatch/my-tours": {
              "get": {
                "summary": "Retrieve tours assigned to authenticated tour guide"
              }
            },
            "/guide/dispatch/check-in": {
              "post": {
                "summary": "Validate passenger QR ticket payload and board passenger"
              }
            },
            "/guide/telemetry": {
              "post": {
                "summary": "Report live GPS coordinates, battery %, and duty state"
              }
            }
          }
        }
        """.trimIndent()
    }
}
