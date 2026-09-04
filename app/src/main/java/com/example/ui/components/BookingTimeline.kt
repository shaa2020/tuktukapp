package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.ui.theme.*

private val stepsList = listOf(
    BookingStatus.PENDING to "Reservation Logged",
    BookingStatus.CONFIRMED to "Slot Confirmed",
    BookingStatus.GUIDE_ASSIGNED to "Chauffeur Assigned",
    BookingStatus.GUIDE_ON_THE_WAY to "Tuk-Tuk En Route",
    BookingStatus.STARTED to "Tour In Progress",
    BookingStatus.COMPLETED to "Tour Completed"
)

@Composable
fun LiveExperienceTrackingSection(
    booking: Booking,
    onStatusChange: (BookingStatus) -> Unit,
    onAdvanceStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var rating by remember { mutableIntStateOf(5) }
    var reviewSubmitted by remember { mutableStateOf(false) }

    val currentStepIndex = stepsList.indexOfFirst { it.first == booking.status }.let { if (it == -1) 1 else it }

    // Pulsing animation for active tracker
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_experience_status_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)),
        border = BorderStroke(
            width = 1.dp,
            color = if (booking.status == BookingStatus.COMPLETED) StatusGreen.copy(alpha = 0.5f)
                    else TagusBlue.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Live Dispatch Badge & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (booking.status == BookingStatus.COMPLETED) StatusGreen
                                else TagusBlue.copy(alpha = pulseAlpha)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Live Experience Status",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Real-time dispatch from Lisbon Center",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(booking.status.badgeColorHex)
                ) {
                    Text(
                        text = booking.status.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stage Quick Navigator Chips (Allow user to immediately complete or jump)
            Text(
                text = "Simulate & Switch Experience Stage:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(stepsList) { (status, label) ->
                    val isCurrent = booking.status == status
                    FilterChip(
                        selected = isCurrent,
                        onClick = { onStatusChange(status) },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (isCurrent) {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (status == BookingStatus.COMPLETED) StatusGreen else TagusBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Direct Action Buttons: "Next Stage" and "Complete & Finish Tour"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onAdvanceStatus,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.FastForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (booking.status == BookingStatus.COMPLETED) "Reset to Start" else "Next Stage",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                if (booking.status != BookingStatus.COMPLETED) {
                    Button(
                        onClick = { onStatusChange(BookingStatus.COMPLETED) },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen, contentColor = Color.White)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Finish & Complete", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Context Card depending on status
            when (booking.status) {
                BookingStatus.PENDING -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldLight.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.HourglassTop, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Reservation Logged", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Your reservation request is being confirmed by our Lisbon operations center.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                BookingStatus.CONFIRMED -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TagusBlueLight.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Verified, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Tour Confirmed & Slot Reserved", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Departure on ${booking.bookingDate} at ${booking.timeSlot}. Chauffeur will be assigned soon.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                BookingStatus.GUIDE_ASSIGNED, BookingStatus.GUIDE_ON_THE_WAY -> {
                    // Live Chauffeur & Vehicle HUD
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, TagusBlue.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (booking.status == BookingStatus.GUIDE_ON_THE_WAY) Icons.Filled.DirectionsCarFilled else Icons.Filled.PersonPin,
                                        contentDescription = null,
                                        tint = TagusBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (booking.status == BookingStatus.GUIDE_ON_THE_WAY) "Tuk-Tuk En Route to Pickup" else "Chauffeur Assigned",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TagusBlue
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFD1FAE5)
                                ) {
                                    Text(
                                        text = if (booking.status == BookingStatus.GUIDE_ON_THE_WAY) "ETA ~12 MIN" else "READY",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color(0xFF047857),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = booking.guideInfo?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80",
                                    contentDescription = "Chauffeur",
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = booking.guideInfo?.name ?: "Diogo Silva",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Eco Tuk-Tuk #LIS-24 (100% Electric)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    FilledIconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${booking.guideInfo?.phone ?: "+351912345678"}"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.size(34.dp),
                                        shape = CircleShape,
                                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Icon(Icons.Filled.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                                    }

                                    FilledIconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=351912345678"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.size(34.dp),
                                        shape = CircleShape,
                                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF25D366), contentColor = Color.White)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Live Chauffeur GPS Radar Proximity
                            ChauffeurGpsRadarVisualizer(
                                pickupLocationName = booking.pickupLocationName
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Current GPS: Navigating near Terreiro do Paço to meet you at ${booking.pickupLocationName}.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                BookingStatus.STARTED -> {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Explore, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Private Tour In Progress 🇵🇹", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 13.sp)
                                }
                                Surface(shape = RoundedCornerShape(6.dp), color = GoldLight) {
                                    Text("LIVE ON ROUTE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NavyDark, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Currently visiting historical viewpoints: Miradouro de Santa Luzia & Castelo de São Jorge. Audio narration active in ${booking.selectedLanguage}.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                BookingStatus.COMPLETED -> {
                    // Completed Celebration & Review Box
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, StatusGreen.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.Celebration, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Tour Completed & Finished! 🎉", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "We hope you had a memorable journey across Lisbon's 7 Hills with Diogo!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (!reviewSubmitted) {
                                Text("Rate Your Experience", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    for (i in 1..5) {
                                        IconButton(
                                            onClick = { rating = i },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                                                contentDescription = "$i Stars",
                                                tint = if (i <= rating) GoldPrimary else MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        reviewSubmitted = true
                                        Toast.makeText(context, "Thank you for rating $rating/5 stars!", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark)
                                ) {
                                    Text("Submit Review ($rating/5)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFD1FAE5)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Review recorded: $rating/5 Stars. Obrigado!", fontWeight = FontWeight.Bold, color = Color(0xFF047857), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {}
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(14.dp))

            // Step-by-Step Vertical Timeline
            Text(
                text = "Dispatch & Tour Progression",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            stepsList.forEachIndexed { index, (status, label) ->
                val isCompleted = index < currentStepIndex
                val isCurrent = index == currentStepIndex
                val isUpcoming = index > currentStepIndex

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Left Timeline Column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(28.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> StatusGreen
                                        isCurrent -> if (status == BookingStatus.COMPLETED) StatusGreen else TagusBlue
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted || (isCurrent && status == BookingStatus.COMPLETED)) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            } else if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            } else {
                                Text(
                                    text = (index + 1).toString(),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (index < stepsList.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(26.dp)
                                    .background(
                                        if (index < currentStepIndex) StatusGreen
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Right Step Content
                    Column(modifier = Modifier.padding(bottom = if (index < stepsList.size - 1) 8.dp else 0.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = when {
                                    isCurrent -> if (status == BookingStatus.COMPLETED) StatusGreen else TagusBlue
                                    isCompleted -> MaterialTheme.colorScheme.onSurface
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )

                            Text(
                                text = when (status) {
                                    BookingStatus.PENDING -> "Step 1"
                                    BookingStatus.CONFIRMED -> "Step 2"
                                    BookingStatus.GUIDE_ASSIGNED -> "Step 3"
                                    BookingStatus.GUIDE_ON_THE_WAY -> "Step 4"
                                    BookingStatus.STARTED -> "Step 5"
                                    BookingStatus.COMPLETED -> "Done"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = when (status) {
                                BookingStatus.PENDING -> "Server verified reservation request"
                                BookingStatus.CONFIRMED -> "Payment approved & private tuk-tuk slot secured"
                                BookingStatus.GUIDE_ASSIGNED -> "Diogo Silva designated as your Lisbon storyteller"
                                BookingStatus.GUIDE_ON_THE_WAY -> "100% Electric vehicle en route to your meeting point"
                                BookingStatus.STARTED -> "Scenic tuk-tuk exploration of Alfama & Castelo underway"
                                BookingStatus.COMPLETED -> "Experience completed. Digital memories saved."
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingTimeline(
    currentStatus: BookingStatus,
    modifier: Modifier = Modifier
) {
    // Retained for backward compatibility
    val currentStepIndex = stepsList.indexOfFirst { it.first == currentStatus }.let { if (it == -1) 1 else it }

    Column(modifier = modifier.fillMaxWidth()) {
        stepsList.forEachIndexed { index, (status, label) ->
            val isPassed = index <= currentStepIndex
            val isCurrent = index == currentStepIndex

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(28.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(
                                if (isPassed) StatusGreen
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPassed) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    if (index < stepsList.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(26.dp)
                                .background(
                                    if (index < currentStepIndex) StatusGreen
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isPassed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
