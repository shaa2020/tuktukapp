package com.example.ui.screens.booking

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDark
import com.example.ui.theme.TagusBlue
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingEngineScreen(
    tourId: String,
    viewModel: BookingViewModel,
    onNavigateBack: () -> Unit,
    onBookingSuccess: (String) -> Unit,
    onNavigateToAuth: () -> Unit,
    isGuest: Boolean = false
) {
    val context = LocalContext.current

    LaunchedEffect(tourId) {
        viewModel.loadTour(tourId)
    }

    val tour by viewModel.tour.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()
    val currency by viewModel.currentCurrency.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedTimeSlot by viewModel.selectedTimeSlot.collectAsState()
    val guestCount by viewModel.guestCount.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedPickup by viewModel.selectedPickup.collectAsState()
    val selectedExtras by viewModel.selectedExtras.collectAsState()

    val customerName by viewModel.customerName.collectAsState()
    val customerEmail by viewModel.customerEmail.collectAsState()
    val customerPhone by viewModel.customerPhone.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val promoCode by viewModel.promoCode.collectAsState()
    val selectedPaymentProvider by viewModel.selectedPaymentProvider.collectAsState()

    var currentStep by remember { mutableIntStateOf(1) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val priceBreakdown = viewModel.calculatePriceBreakdown()

    LaunchedEffect(bookingState) {
        if (bookingState is BookingUiState.Success) {
            val booking = (bookingState as BookingUiState.Success).booking
            viewModel.resetBookingState()
            onBookingSuccess(booking.bookingId)
        }
    }

    if (tour == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val tourData = tour!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Book Experience",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tourData.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentStep > 1) {
                                currentStep -= 1
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.testTag("booking_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            // Sticky Floating Summary & Navigation Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total (${guestCount} ${if (guestCount == 1) "Guest" else "Guests"})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${currency.symbol}${String.format(Locale.US, "%.2f", priceBreakdown?.finalTotalEur ?: tourData.basePriceEur)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (currentStep > 1) {
                                OutlinedButton(
                                    onClick = { currentStep -= 1 },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Text("Back")
                                }
                            }

                            Button(
                                onClick = {
                                    when (currentStep) {
                                        1 -> currentStep = 2
                                        2 -> currentStep = 3
                                        3 -> {
                                            if (customerName.isBlank() || customerEmail.isBlank() || customerPhone.isBlank()) {
                                                Toast.makeText(context, "Please fill in lead traveler name, email, and phone.", Toast.LENGTH_SHORT).show()
                                            } else {
                                                showConfirmationDialog = true
                                            }
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentStep == 3) GoldPrimary else TagusBlue,
                                    contentColor = if (currentStep == 3) NavyDark else Color.White
                                ),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("booking_next_button")
                            ) {
                                if (currentStep == 3) {
                                    Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Review & Pay", fontWeight = FontWeight.Bold)
                                } else {
                                    Text("Continue", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Filled.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Step Indicator Header
            BookingStepIndicator(currentStep = currentStep)

            // Step Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
            ) {
                item {
                    TourSummaryBanner(
                        tour = tourData,
                        currencySymbol = currency.symbol
                    )
                }

                when (currentStep) {
                    1 -> {
                        // STEP 1: Schedule & Party
                        item {
                            ScheduleSection(
                                selectedDate = selectedDate,
                                selectedTimeSlot = selectedTimeSlot,
                                onDateSelected = { viewModel.setDate(it) },
                                onTimeSlotSelected = { viewModel.setTimeSlot(it) }
                            )
                        }

                        item {
                            GuestsAndLanguageSection(
                                guestCount = guestCount,
                                onGuestCountChange = { viewModel.setGuestCount(it) },
                                selectedLanguage = selectedLanguage,
                                onLanguageSelected = { viewModel.setLanguage(it) },
                                availableLanguages = tourData.languages,
                                perGuestPriceEur = tourData.perGuestPriceEur,
                                currencySymbol = currency.symbol
                            )
                        }
                    }

                    2 -> {
                        // STEP 2: Meeting Point & Extras
                        item {
                            PickupSelectionSection(
                                pickupPoints = tourData.pickupPoints,
                                selectedPickup = selectedPickup,
                                onPickupSelected = { viewModel.setPickup(it) },
                                currencySymbol = currency.symbol
                            )
                        }

                        if (tourData.availableExtras.isNotEmpty()) {
                            item {
                                ExtrasSelectionSection(
                                    availableExtras = tourData.availableExtras,
                                    selectedExtras = selectedExtras,
                                    onToggleExtra = { viewModel.toggleExtra(it) },
                                    currencySymbol = currency.symbol
                                )
                            }
                        }
                    }

                    3 -> {
                        // STEP 3: Contact Details, Promo, Payment & Breakdown
                        item {
                            TravelerDetailsCard(
                                customerName = customerName,
                                onCustomerNameChange = { viewModel.setCustomerInfo(it, customerEmail, customerPhone) },
                                customerEmail = customerEmail,
                                onCustomerEmailChange = { viewModel.setCustomerInfo(customerName, it, customerPhone) },
                                customerPhone = customerPhone,
                                onCustomerPhoneChange = { viewModel.setCustomerInfo(customerName, customerEmail, it) },
                                notes = notes,
                                onNotesChange = { viewModel.setNotes(it) },
                                savedTravelers = userProfile.savedTravelers,
                                onSelectSavedTraveler = { viewModel.selectSavedTraveler(it) }
                            )
                        }

                        item {
                            PromoCodeSection(
                                promoCode = promoCode,
                                onPromoCodeChange = { viewModel.setPromoCode(it) }
                            )
                        }

                        item {
                            PaymentMethodSelector(
                                selectedProvider = selectedPaymentProvider,
                                onProviderSelected = { viewModel.setPaymentProvider(it) }
                            )
                        }

                        if (priceBreakdown != null) {
                            item {
                                PriceBreakdownCard(
                                    breakdown = priceBreakdown,
                                    currencySymbol = currency.symbol
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation & Payment Dialog
    if (showConfirmationDialog && priceBreakdown != null) {
        val isProcessing = bookingState is BookingUiState.Processing

        AlertDialog(
            onDismissRequest = { if (!isProcessing) showConfirmationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = TagusBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Your Reservation", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = tourData.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Date: $selectedDate at $selectedTimeSlot")
                    Text("• Party: $guestCount Guests ($selectedLanguage)")
                    Text("• Meeting: ${selectedPickup?.name ?: "Central Meeting Point"}")
                    Text("• Lead Traveler: $customerName")
                    Text("• Payment: ${selectedPaymentProvider.name}")

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFD1FAE5),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Free cancellation up to 24h before tour",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF047857),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total to Pay:", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${currency.symbol}${String.format(Locale.US, "%.2f", priceBreakdown.finalTotalEur)}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (isProcessing) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Securing your Tuk-Tuk pass...", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    if (bookingState is BookingUiState.Error) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = (bookingState as BookingUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitBooking() },
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (isProcessing) "Processing..." else "Pay & Confirm", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                if (!isProcessing) {
                    TextButton(onClick = { showConfirmationDialog = false }) {
                        Text("Change Details")
                    }
                }
            }
        )
    }
}
