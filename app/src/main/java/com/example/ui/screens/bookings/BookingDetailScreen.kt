package com.example.ui.screens.bookings

import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingStatus
import com.example.ui.components.BookingTimeline
import com.example.ui.components.DigitalTicketCard
import com.example.ui.components.GuideInfoCard
import com.example.ui.components.LiveExperienceTrackingSection
import com.example.ui.components.safeOpenLocationMap
import com.example.ui.components.safeSharePass
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDark
import com.example.ui.theme.TagusBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    bookingId: String,
    viewModel: BookingsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(bookingId) {
        viewModel.loadBookingById(bookingId)
    }

    val booking by viewModel.selectedBooking.collectAsState()
    val currency by viewModel.currentCurrency.collectAsState()

    var showCancelDialog by remember { mutableStateOf(false) }
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var showNotesDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (booking == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Booking Pass", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("booking_detail_back")) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    CircularProgressIndicator(color = TagusBlue)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading Pass #${bookingId}...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(onClick = onNavigateBack) {
                        Text("Return to Bookings")
                    }
                }
            }
        }
        return
    }

    val bookingData = booking!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pass #${bookingData.bookingId}", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("booking_detail_back")) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            safeSharePass(context, bookingData)
                        }
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = "Share Pass")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("booking_detail_scroll"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Digital Boarding Ticket with QR Code
            item {
                DigitalTicketCard(
                    booking = bookingData,
                    currency = currency,
                    onModifyBooking = { showRescheduleDialog = true },
                    onCancelBooking = { showCancelDialog = true },
                    onValidateBoarding = {
                        viewModel.setBookingStatus(bookingData.bookingId, BookingStatus.STARTED)
                    }
                )
            }

            // Quick Actions Strip
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Reschedule Button
                    if (bookingData.status in listOf(BookingStatus.CONFIRMED, BookingStatus.PENDING)) {
                        Button(
                            onClick = { showRescheduleDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TagusBlue, contentColor = Color.White),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(Icons.Outlined.EditCalendar, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reschedule", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Add to Calendar Button
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_INSERT)
                                    .setData(CalendarContract.Events.CONTENT_URI)
                                    .putExtra(CalendarContract.Events.TITLE, "🇵🇹 TukTuk24 Tour: ${bookingData.tourTitle}")
                                    .putExtra(CalendarContract.Events.EVENT_LOCATION, bookingData.pickupLocationName)
                                    .putExtra(
                                        CalendarContract.Events.DESCRIPTION,
                                        "Booking Ref: ${bookingData.bookingId}\nGuests: ${bookingData.guestCount}\nLanguage: ${bookingData.selectedLanguage}\nDriver: ${bookingData.guideInfo?.name ?: "Assigned Chauffeur"}"
                                    )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Calendar not available", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Outlined.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Calendar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 2. Real-time Tour Timeline Status & Live Dispatch Section
            item {
                LiveExperienceTrackingSection(
                    booking = bookingData,
                    onStatusChange = { newStatus ->
                        viewModel.setBookingStatus(bookingData.bookingId, newStatus)
                    },
                    onAdvanceStatus = {
                        viewModel.advanceBookingStatus(bookingData.bookingId)
                    }
                )
            }

            // 3. Assigned Driver / Guide Info
            if (bookingData.guideInfo != null) {
                item {
                    Text("Your Tuk-Tuk Chauffeur & Storyteller", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    GuideInfoCard(guide = bookingData.guideInfo!!)
                }
            }

            // 4. Meeting Point & Pickup Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Meeting & Pickup Point", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TagusBlue)
                            IconButton(
                                onClick = {
                                    safeOpenLocationMap(context, bookingData.pickupLocationName)
                                }
                            ) {
                                Icon(Icons.Outlined.Directions, contentDescription = "Navigate to Pickup", tint = TagusBlue)
                            }
                        }

                        Text(bookingData.pickupLocationName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Please arrive 10 minutes prior to departure (${bookingData.timeSlot}). Your electric tuk-tuk chauffeur will display a digital TukTuk24 sign with your name.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                safeOpenLocationMap(context, bookingData.pickupLocationName)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Outlined.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open in Google Maps Navigation", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 5. Special Requests & Accessibility Notes
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Special Requests & Tour Notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { showNotesDialog = true }) {
                                Icon(Icons.Outlined.Edit, contentDescription = "Edit Notes", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Text(
                            text = if (!bookingData.notes.isNullOrBlank()) bookingData.notes!!
                                   else "No special requests added. Tap edit to specify child seats, dietary restrictions, or mobility needs.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (!bookingData.notes.isNullOrBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 6. Primary Traveler Contact Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Lead Traveler Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(bookingData.customerName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Email, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(bookingData.customerEmail, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Phone, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(bookingData.customerPhone, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // 7. Lisbon 24/7 Concierge Support Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.SupportAgent, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Lisbon Dispatch Concierge", fontWeight = FontWeight.Bold, color = GoldPrimary, style = MaterialTheme.typography.titleSmall)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Need real-time assistance or last-minute changes? Our local operations team in Lisbon is available 24/7.",
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+351210000000"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = NavyDark)
                            ) {
                                Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call Hotline", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=351912345678"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.White)
                            ) {
                                Icon(Icons.Filled.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 8. Management Actions (Cancel Booking / Delete)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (bookingData.status in listOf(BookingStatus.CONFIRMED, BookingStatus.PENDING, BookingStatus.GUIDE_ASSIGNED)) {
                        OutlinedButton(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("cancel_booking_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cancel Booking (100% Refundable)", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (bookingData.status in listOf(BookingStatus.CANCELLED, BookingStatus.COMPLETED, BookingStatus.REFUNDED)) {
                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("delete_booking_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Remove from Booking History", fontWeight = FontWeight.Medium)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Cancel Booking Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Experience?", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Free cancellation applies up to 24 hours before departure.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Full refund of ${currency.symbol}${String.format(java.util.Locale.US, "%.2f", bookingData.priceBreakdown.finalTotalEur)} will be credited back to your original payment method.",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelBooking(bookingData.bookingId)
                        showCancelDialog = false
                        Toast.makeText(context, "Booking #${bookingData.bookingId} cancelled. Refund issued.", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = Color.White)
                ) {
                    Text("Confirm Cancellation", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Reservation")
                }
            }
        )
    }

    // Reschedule Dialog
    if (showRescheduleDialog) {
        var newDate by remember { mutableStateOf(bookingData.bookingDate) }
        var newTimeSlot by remember { mutableStateOf(bookingData.timeSlot) }

        val availableDates = listOf("2026-08-16", "2026-08-17", "2026-08-18", "2026-08-19", "2026-08-20")
        val availableSlots = listOf("09:00 AM", "10:30 AM", "02:00 PM", "04:30 PM", "06:00 PM")

        AlertDialog(
            onDismissRequest = { showRescheduleDialog = false },
            title = { Text("Reschedule Experience", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Choose a new date:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableDates) { d ->
                            FilterChip(
                                selected = d == newDate,
                                onClick = { newDate = d },
                                label = { Text(d) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TagusBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Choose a new departure time:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableSlots) { slot ->
                            FilterChip(
                                selected = slot == newTimeSlot,
                                onClick = { newTimeSlot = slot },
                                label = { Text(slot) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• Free unlimited rescheduling with TukTuk24 Guarantee",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rescheduleBooking(bookingData.bookingId, newDate, newTimeSlot)
                        showRescheduleDialog = false
                        Toast.makeText(context, "Rescheduled to $newDate at $newTimeSlot", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark)
                ) {
                    Text("Confirm Reschedule", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRescheduleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Notes Dialog
    if (showNotesDialog) {
        var currentNotesInput by remember { mutableStateOf(bookingData.notes ?: "") }

        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            title = { Text("Special Requests & Notes", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Inform your chauffeur about child safety seats, mobility assistance, or anniversaries/birthdays.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = currentNotesInput,
                        onValueChange = { currentNotesInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Traveling with infant, need rear child safety seat") },
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateBookingNotes(bookingData.bookingId, currentNotesInput)
                        showNotesDialog = false
                        Toast.makeText(context, "Special requests updated", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TagusBlue, contentColor = Color.White)
                ) {
                    Text("Save Notes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotesDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Booking Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Past Booking?", fontWeight = FontWeight.Bold) },
            text = { Text("This will remove booking #${bookingData.bookingId} from your local device records.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBooking(bookingData.bookingId)
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = Color.White)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Keep")
                }
            }
        )
    }
}
