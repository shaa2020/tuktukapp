package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.AppCurrency
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.ui.theme.*

fun safeOpenLocationMap(context: Context, locationName: String) {
    val encoded = Uri.encode(locationName)
    try {
        val geoIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$encoded"))
        context.startActivity(geoIntent)
    } catch (e: Exception) {
        try {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$encoded"))
            context.startActivity(webIntent)
        } catch (e2: Exception) {
            Toast.makeText(context, "Location: $locationName", Toast.LENGTH_LONG).show()
        }
    }
}

fun safeSharePass(context: Context, booking: Booking) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "TukTuk24 Boarding Pass - ${booking.tourTitle}")
            putExtra(
                Intent.EXTRA_TEXT,
                "🇵🇹 TukTuk24 Official Boarding Pass\n\n" +
                "Tour: ${booking.tourTitle}\n" +
                "Booking Reference: ${booking.bookingId}\n" +
                "Lead Passenger: ${booking.customerName}\n" +
                "Date: ${booking.bookingDate} at ${booking.timeSlot}\n" +
                "Meeting Location: ${booking.pickupLocationName}\n" +
                "Party Size: ${booking.guestCount} Guests (${booking.selectedLanguage})\n" +
                "Chauffeur: ${booking.guideInfo?.name ?: "Assigned Chauffeur"}\n\n" +
                "Official QR Payload: ${booking.qrCodePayload}"
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Boarding Pass"))
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open share sheet", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun DigitalTicketCard(
    booking: Booking,
    currency: AppCurrency,
    onModifyBooking: () -> Unit,
    onCancelBooking: () -> Unit,
    onValidateBoarding: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPassModal by remember { mutableStateOf(false) }
    var initialTabForModal by remember { mutableIntStateOf(0) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("digital_ticket_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(NavyDark, Color(0xFF1E293B), Color(0xFF0F172A))
                        )
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "TUKTUK24",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldPrimary,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GoldPrimary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "PASS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "OFFICIAL DIGITAL BOARDING PASS",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(booking.status.badgeColorHex)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = booking.status.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Ticket Body
            Column(modifier = Modifier.padding(20.dp)) {
                // Tour Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            initialTabForModal = 0
                            showPassModal = true
                        }
                ) {
                    AsyncImage(
                        model = booking.tourImageUrl,
                        contentDescription = booking.tourTitle,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = booking.tourTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Booking Ref: ${booking.bookingId}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = TagusBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Key Info Grid Details
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("DATE & TIME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${booking.bookingDate} @ ${booking.timeSlot}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("PASSENGERS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${booking.guestCount} Guests (${booking.selectedLanguage})", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    Text("PICKUP / MEETING LOCATION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(booking.pickupLocationName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Interactive QR Code Box (Tap to Enlarge & Validate)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            initialTabForModal = 0
                            showPassModal = true
                        }
                        .testTag("pass_qr_box"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                RenderQrCodeCanvas(
                                    payload = booking.qrCodePayload,
                                    modifier = Modifier.size(110.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TAP TO EXPAND FULL PASS & BOARDING QR",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = TagusBlue,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = booking.qrCodePayload,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Live Chauffeur Dispatch Radar Trigger
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            initialTabForModal = 2
                            showPassModal = true
                        }
                        .testTag("ticket_radar_trigger"),
                    color = NavyDark,
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "LIVE CHAUFFEUR DISPATCH RADAR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldPrimary,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${booking.guideInfo?.name ?: "Diogo Silva"} • Eco Tuk-Tuk #07 • 4 min away",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Surface(
                            shape = CircleShape,
                            color = GoldPrimary,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.NearMe, contentDescription = "Track Live", tint = NavyDark, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row: Wallet, Pass & QR, Map, Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Google Wallet Button
                    OutlinedButton(
                        onClick = {
                            initialTabForModal = 1
                            showPassModal = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ticket_wallet_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp), tint = TagusBlue)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Wallet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Expand Pass Button
                    Button(
                        onClick = {
                            initialTabForModal = 0
                            showPassModal = true
                        },
                        modifier = Modifier
                            .weight(1.1f)
                            .testTag("ticket_view_pass_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TagusBlue, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Outlined.QrCode2, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View Pass", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Map Button (Safe Launcher)
                    OutlinedButton(
                        onClick = {
                            safeOpenLocationMap(context, booking.pickupLocationName)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ticket_map_button"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Outlined.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Map", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Share Button (Safe Chooser)
                    OutlinedButton(
                        onClick = {
                            safeSharePass(context, booking)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ticket_share_button"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Interactive Digital Pass & Google Wallet Modal
    if (showPassModal) {
        DigitalBoardingPassDialog(
            booking = booking,
            initialTabIndex = initialTabForModal,
            onDismiss = { showPassModal = false },
            onValidateBoarding = {
                onValidateBoarding?.invoke()
                Toast.makeText(context, "Pass Verified! Welcome aboard your TukTuk24 experience.", Toast.LENGTH_LONG).show()
            }
        )
    }
}

@Composable
fun DigitalBoardingPassDialog(
    booking: Booking,
    initialTabIndex: Int = 0,
    onDismiss: () -> Unit,
    onValidateBoarding: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(initialTabIndex) }
    var isSavedOffline by remember { mutableStateOf(false) }
    var isAddedToWallet by remember { mutableStateOf(true) }
    var isBoardingValidated by remember { mutableStateOf(booking.status == BookingStatus.STARTED || booking.status == BookingStatus.COMPLETED) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyDark)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.ConfirmationNumber, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TukTuk24 Digital Pass",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close Pass")
                    }
                }

                // Tab Selector: Full Pass vs. Google Wallet vs. Live Radar
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = TagusBlue
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pass & QR", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Wallet", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.NearMe, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Live Radar", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    )
                }

                // Tab Content
                if (selectedTab == 0) {
                    // TAB 0: Boarding Pass & High-Res QR Code View
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Pass Status Banner
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isBoardingValidated) Color(0xFFD1FAE5) else Color(0xFFE0F2FE),
                            border = BorderStroke(1.dp, if (isBoardingValidated) Color(0xFF10B981) else TagusBlue.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isBoardingValidated) Icons.Filled.CheckCircle else Icons.Filled.Verified,
                                    contentDescription = null,
                                    tint = if (isBoardingValidated) Color(0xFF047857) else TagusBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isBoardingValidated) "PASS VALIDATED • BOARDED" else "VALIDATED & READY FOR BOARDING",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = if (isBoardingValidated) Color(0xFF047857) else TagusBlue
                                    )
                                    Text(
                                        text = if (isBoardingValidated) "Experience marked as underway with Chauffeur" else "Present this digital QR to your TukTuk24 chauffeur",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Large High-Contrast QR Code Card
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            border = BorderStroke(2.dp, Color(0xFF0F172A))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                RenderQrCodeCanvas(
                                    payload = booking.qrCodePayload,
                                    modifier = Modifier.size(190.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "REF: ${booking.bookingId}",
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.5.sp,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Tour Details Summary
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(booking.tourTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Passenger: ${booking.customerName}", style = MaterialTheme.typography.bodySmall)
                                    Text("${booking.guestCount} Guests (${booking.selectedLanguage})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Departure: ${booking.bookingDate} @ ${booking.timeSlot}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TagusBlue)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Pickup: ${booking.pickupLocationName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Interactive Boarding & Scan Verification Buttons
                        Button(
                            onClick = {
                                isBoardingValidated = true
                                onValidateBoarding()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("validate_boarding_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBoardingValidated) Color(0xFF10B981) else TagusBlue,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = if (isBoardingValidated) Icons.Filled.Check else Icons.Outlined.QrCodeScanner,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBoardingValidated) "Pass Verified (Boarding Confirmed)" else "Simulate Chauffeur QR Scan & Board",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    isSavedOffline = true
                                    Toast.makeText(context, "Pass #${booking.bookingId} exported as PDF Voucher to Downloads", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSavedOffline) Icons.Filled.CloudDone else Icons.Outlined.Download,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSavedOffline) Color(0xFF10B981) else TagusBlue
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isSavedOffline) "PDF Saved" else "Download PDF",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    safeSharePass(context, booking)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share Pass", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else if (selectedTab == 1) {
                    // TAB 1: Official Google Wallet Pass Simulation
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Google Wallet Official Card Mockup
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_wallet_pass_mockup"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyDark),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                // Google Wallet Brand Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("G", fontWeight = FontWeight.ExtraBold, color = Color(0xFF4285F4), fontSize = 14.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Google Wallet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    Icon(Icons.Outlined.Nfc, contentDescription = "NFC Tap & Go", tint = GoldPrimary, modifier = Modifier.size(22.dp))
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Transit Pass Title
                                Text("TUKTUK24 OFFICIAL TRANSIT PASS", color = GoldPrimary, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(booking.tourTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2)

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                                Spacer(modifier = Modifier.height(14.dp))

                                // Details Grid
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("PASSENGER", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text(booking.customerName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("DATE & TIME", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text("${booking.bookingDate} ${booking.timeSlot}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("MEETING POINT", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text(booking.pickupLocationName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                                    }
                                    Column(modifier = Modifier.weight(0.6f)) {
                                        Text("TUK-TUK FLEET", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text("Eco 100% Electric", color = Color(0xFF34D399), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Barcode Strip
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        RenderBarcodeStrip(modifier = Modifier.fillMaxWidth().height(36.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(booking.bookingId, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black, letterSpacing = 2.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Add to Google Wallet Action
                        Button(
                            onClick = {
                                isAddedToWallet = true
                                Toast.makeText(context, "Pass #${booking.bookingId} active in Google Wallet", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(18.dp), tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAddedToWallet) "✓ Saved in Google Wallet" else "Add to Google Wallet",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Contactless NFC Boarding Simulation
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "NFC Handshake: TukTuk24 Chauffeur device confirmed boarding!", Toast.LENGTH_LONG).show()
                                isBoardingValidated = true
                                onValidateBoarding()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(Icons.Outlined.Nfc, contentDescription = null, modifier = Modifier.size(18.dp), tint = TagusBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simulate NFC Tap-to-Board", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // TAB 2: Live Chauffeur Radar & Driver Dispatch Telemetry
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Live GPS Radar Scope
                        ChauffeurGpsRadarVisualizer(
                            pickupLocationName = booking.pickupLocationName
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Assigned Driver Profile Card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        modifier = Modifier.size(52.dp),
                                        color = GoldPrimary.copy(alpha = 0.2f)
                                    ) {
                                        if (booking.guideInfo?.avatarUrl != null) {
                                            AsyncImage(
                                                model = booking.guideInfo.avatarUrl,
                                                contentDescription = booking.guideInfo.name,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                        } else {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Filled.Person, contentDescription = null, tint = NavyDark, modifier = Modifier.size(28.dp))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = booking.guideInfo?.name ?: "Diogo Silva",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Star, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "${booking.guideInfo?.rating ?: 4.98} (${booking.guideInfo?.reviewCount ?: 312} reviews)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = "Speaks: ${booking.guideInfo?.languages?.joinToString(", ") ?: "English, Portuguese, Spanish"}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(14.dp))

                                // Vehicle Telemetry Grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Vehicle", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Eco Tuk-Tuk #07", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text("License Plate", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("78-TK-24 (PT)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text("Battery", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Bolt, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                            Text("94%", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF10B981))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Direct Chauffeur Communication Actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val phone = booking.guideInfo?.phone ?: "+351912345678"
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = TagusBlue, contentColor = Color.White)
                                    ) {
                                        Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            val whatsappNum = booking.guideInfo?.whatsapp ?: "351912345678"
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$whatsappNum"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.White)
                                    ) {
                                        Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun RenderQrCodeCanvas(payload: String, modifier: Modifier = Modifier) {
    val hash = payload.hashCode()
    Canvas(modifier = modifier) {
        val count = 15
        val cellSize = size.width / count

        // Draw white background
        drawRect(color = Color.White, size = size)

        // Draw finder corners
        fun drawFinder(x: Float, y: Float) {
            // Outer 7x7 square
            drawRect(
                color = Color.Black,
                topLeft = Offset(x, y),
                size = Size(cellSize * 7, cellSize * 7)
            )
            // Inner 5x5 white
            drawRect(
                color = Color.White,
                topLeft = Offset(x + cellSize, y + cellSize),
                size = Size(cellSize * 5, cellSize * 5)
            )
            // Center 3x3 black
            drawRect(
                color = Color.Black,
                topLeft = Offset(x + cellSize * 2, y + cellSize * 2),
                size = Size(cellSize * 3, cellSize * 3)
            )
        }

        drawFinder(0f, 0f)
        drawFinder(cellSize * (count - 7), 0f)
        drawFinder(0f, cellSize * (count - 7))

        // Deterministic QR data cells
        for (row in 0 until count) {
            for (col in 0 until count) {
                val inTopLeft = row < 7 && col < 7
                val inTopRight = row < 7 && col >= (count - 7)
                val inBottomLeft = row >= (count - 7) && col < 7
                if (inTopLeft || inTopRight || inBottomLeft) continue

                val isFilled = ((row * 31 + col * 17 + hash) % 3) == 0 || (row == 6 && col % 2 == 0) || (col == 6 && row % 2 == 0)
                if (isFilled) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(col * cellSize, row * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}

@Composable
fun RenderBarcodeStrip(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val barCount = 40
        val totalWidth = size.width
        val barWidth = totalWidth / (barCount * 1.5f)

        var currentX = 0f
        for (i in 0 until barCount) {
            val thicknessMultiplier = if (i % 3 == 0) 1.8f else if (i % 5 == 0) 2.2f else 1.0f
            val w = barWidth * thicknessMultiplier
            drawRect(
                color = Color.Black,
                topLeft = Offset(currentX, 0f),
                size = Size(w, size.height)
            )
            currentX += w + (barWidth * (if (i % 4 == 0) 1.5f else 0.8f))
            if (currentX >= totalWidth) break
        }
    }
}
