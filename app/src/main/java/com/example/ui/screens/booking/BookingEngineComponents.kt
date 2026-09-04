package com.example.ui.screens.booking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDark
import com.example.ui.theme.TagusBlue
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookingStepIndicator(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val steps = listOf("Schedule", "Options", "Checkout")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, stepName ->
            val stepNumber = index + 1
            val isCompleted = currentStep > stepNumber
            val isCurrent = currentStep == stepNumber

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> Color(0xFF10B981)
                                isCurrent -> TagusBlue
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    } else {
                        Text(
                            text = stepNumber.toString(),
                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stepName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    color = if (isCurrent) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (index < steps.size - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .width(28.dp)
                        .padding(horizontal = 4.dp),
                    color = if (currentStep > stepNumber) Color(0xFF10B981) else MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
fun TourSummaryBanner(
    tour: Tour,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = tour.mainImageUrl,
                contentDescription = tour.title,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tour.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${tour.rating} (${tour.reviewCount}) • ${tour.durationHours}h",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = tour.destination,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatFriendlyDate(dateStr: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = parser.parse(dateStr) ?: return dateStr
        val formatter = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.US)
        val todayStr = parser.format(Date())
        val calTomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val tomorrowStr = parser.format(calTomorrow.time)

        when (dateStr) {
            todayStr -> "Today (${SimpleDateFormat("MMM d", Locale.US).format(date)})"
            tomorrowStr -> "Tomorrow (${SimpleDateFormat("MMM d", Locale.US).format(date)})"
            else -> formatter.format(date)
        }
    } catch (_: Exception) {
        dateStr
    }
}

@Composable
fun ScheduleSection(
    selectedDate: String,
    selectedTimeSlot: String,
    onDateSelected: (String) -> Unit,
    onTimeSlotSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Calendar for DatePicker
    val initialDateCal = remember(selectedDate) {
        val cal = Calendar.getInstance()
        try {
            val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(selectedDate)
            if (d != null) cal.time = d
        } catch (_: Exception) {}
        cal
    }

    // Interactive DatePicker - supports any future date!
    val openDatePicker = {
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val picked = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                onDateSelected(picked)
            },
            initialDateCal.get(Calendar.YEAR),
            initialDateCal.get(Calendar.MONTH),
            initialDateCal.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        datePicker.show()
    }

    // Interactive TimePicker - supports any time of day!
    val openTimePicker = {
        var initHour = 10
        var initMinute = 30
        try {
            val parsedTime = SimpleDateFormat("hh:mm a", Locale.US).parse(selectedTimeSlot)
            if (parsedTime != null) {
                val cal = Calendar.getInstance().apply { time = parsedTime }
                initHour = cal.get(Calendar.HOUR_OF_DAY)
                initMinute = cal.get(Calendar.MINUTE)
            }
        } catch (_: Exception) {}

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                val formatted = SimpleDateFormat("hh:mm a", Locale.US).format(cal.time)
                onTimeSlotSelected(formatted)
            },
            initHour,
            initMinute,
            false
        ).show()
    }

    // Generate quick shortcut dates (today + next 6 days)
    val upcomingDates = remember {
        val list = mutableListOf<Pair<String, String>>()
        val cal = Calendar.getInstance()
        val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dayFormat = SimpleDateFormat("EEE d", Locale.US)
        for (i in 0..6) {
            val dateKey = keyFormat.format(cal.time)
            val label = when (i) {
                0 -> "Today"
                1 -> "Tmrw"
                else -> dayFormat.format(cal.time)
            }
            list.add(dateKey to label)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    val standardTimeSlots = listOf("09:00 AM", "10:30 AM", "01:30 PM", "03:30 PM", "05:00 PM", "06:30 PM (Sunset)")
    val isCustomTime = standardTimeSlots.none { it.startsWith(selectedTimeSlot) || selectedTimeSlot.startsWith(it) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Date & Departure Time", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = openDatePicker,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Outlined.EditCalendar, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pick Any Date", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prominent Selected Date Card (Tap to pick any date on full calendar)
            Surface(
                onClick = openDatePicker,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, TagusBlue.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TagusBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Event, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = formatFriendlyDate(selectedDate),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Exact Date: $selectedDate • Tap to change",
                                style = MaterialTheme.typography.bodySmall,
                                color = TagusBlue
                            )
                        }
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick date shortcut chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(upcomingDates) { (dateKey, label) ->
                    val isSelected = dateKey == selectedDate
                    FilterChip(
                        selected = isSelected,
                        onClick = { onDateSelected(dateKey) },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TagusBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                item {
                    AssistChip(
                        onClick = openDatePicker,
                        label = { Text("+ More Dates") },
                        leadingIcon = {
                            Icon(Icons.Filled.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))

            // Time slots header with Custom Time Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Departure Time", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = openTimePicker,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Filled.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Set Any Time", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "Selected Time: $selectedTimeSlot",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Time chips: custom slot (if selected) + standard slots + Set Any Time chip
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isCustomTime) {
                    item {
                        FilterChip(
                            selected = true,
                            onClick = openTimePicker,
                            leadingIcon = {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onPrimary)
                            },
                            label = {
                                Text("$selectedTimeSlot (Custom)", fontWeight = FontWeight.Bold)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                items(standardTimeSlots) { slot ->
                    val isSelected = slot == selectedTimeSlot
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTimeSlotSelected(slot) },
                        label = {
                            Text(
                                text = slot,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                item {
                    AssistChip(
                        onClick = openTimePicker,
                        label = { Text("Set Any Time...") },
                        leadingIcon = {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GuestsAndLanguageSection(
    guestCount: Int,
    onGuestCountChange: (Int) -> Unit,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    availableLanguages: List<String>,
    perGuestPriceEur: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    var showCustomPartyDialog by remember { mutableStateOf(false) }
    var inputGuestsText by remember { mutableStateOf("$guestCount") }

    val languages = if (availableLanguages.isNotEmpty()) availableLanguages else listOf("English", "Portuguese", "Spanish", "French", "German")

    // Multi Tuk-Tuk fleet calculation: Each Tuk-Tuk comfortably seats up to 6 guests
    val vehiclesNeeded = (guestCount + 5) / 6

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Guest Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Group, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Party Size", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = if (guestCount <= 6) {
                            "Private Eco Tuk-Tuk ($guestCount ${if (guestCount == 1) "guest" else "guests"})"
                        } else {
                            "Group Convoy: $vehiclesNeeded Tuk-Tuks travelling together"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (guestCount <= 6) MaterialTheme.colorScheme.onSurfaceVariant else TagusBlue,
                        fontWeight = if (guestCount <= 6) FontWeight.Normal else FontWeight.SemiBold
                    )
                }

                // Counter (+ / -) & direct number entry
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledIconButton(
                        onClick = { if (guestCount > 1) onGuestCountChange(guestCount - 1) },
                        enabled = guestCount > 1,
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = "Decrease Guests", modifier = Modifier.size(16.dp))
                    }

                    // Tapping number opens custom party size dialog
                    Surface(
                        onClick = {
                            inputGuestsText = "$guestCount"
                            showCustomPartyDialog = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .widthIn(min = 28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$guestCount",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Plus button is UNLIMITED: can book any number of guests
                    FilledIconButton(
                        onClick = { onGuestCountChange(guestCount + 1) },
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = TagusBlue, contentColor = Color.White)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Increase Guests", modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Quick Party Size presets
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(1, 2, 4, 6, 8, 10, 12, 16).forEach { size ->
                    item {
                        FilterChip(
                            selected = guestCount == size,
                            onClick = { onGuestCountChange(size) },
                            label = { Text("$size ${if (size == 1) "Guest" else "Guests"}", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TagusBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                item {
                    AssistChip(
                        onClick = {
                            inputGuestsText = "$guestCount"
                            showCustomPartyDialog = true
                        },
                        label = { Text("Any Size...", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                        }
                    )
                }
            }

            // Fleet Convoy Announcement for large groups
            if (guestCount > 6) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TagusBlue.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, TagusBlue.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsCar,
                            contentDescription = null,
                            tint = TagusBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Large Group Convoy ($vehiclesNeeded Tuk-Tuks)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TagusBlue
                            )
                            Text(
                                text = "We gladly welcome any party size! Your $guestCount guests will travel together in a private convoy of $vehiclesNeeded Tuk-Tuks with dedicated private guides.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))

            // Guided Language
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Translate, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Narrated Tour Language", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(languages) { lang ->
                    val isSelected = lang == selectedLanguage
                    FilterChip(
                        selected = isSelected,
                        onClick = { onLanguageSelected(lang) },
                        label = { Text(lang) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TagusBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }

    if (showCustomPartyDialog) {
        AlertDialog(
            onDismissRequest = { showCustomPartyDialog = false },
            title = { Text("Enter Party Size", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Enter the total number of guests in your party. We accommodate any size with private Tuk-Tuk fleet convoys!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputGuestsText,
                        onValueChange = { inputGuestsText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Number of Guests") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val num = inputGuestsText.toIntOrNull() ?: guestCount
                        onGuestCountChange(num.coerceAtLeast(1))
                        showCustomPartyDialog = false
                    }
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomPartyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PickupSelectionSection(
    pickupPoints: List<PickupPoint>,
    selectedPickup: PickupPoint?,
    onPickupSelected: (PickupPoint) -> Unit,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Place, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Meeting & Pickup Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "Complimentary central meeting points or convenient hotel door pickup.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                pickupPoints.forEach { point ->
                    val isSelected = point.id == selectedPickup?.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onPickupSelected(point) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) TagusBlue else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        color = if (isSelected) TagusBlue.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onPickupSelected(point) },
                                colors = RadioButtonDefaults.colors(selectedColor = TagusBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = point.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = point.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (point.extraFeeEur > 0) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "+$currencySymbol${String.format(Locale.US, "%.0f", point.extraFeeEur)}",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFD1FAE5)
                                ) {
                                    Text(
                                        text = "FREE",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF047857),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExtrasSelectionSection(
    availableExtras: List<TourExtra>,
    selectedExtras: Set<TourExtra>,
    onToggleExtra: (TourExtra) -> Unit,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    if (availableExtras.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Stars, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enhance Your Experience", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "Optional authentic Portuguese culinary and cultural tastings.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                availableExtras.forEach { extra ->
                    val isChecked = selectedExtras.any { it.id == extra.id }
                    val badgeLabel = when (extra.id) {
                        "ext_pastel" -> "Pastel & Wine"
                        "ext_champagne" -> "Panoramic Toast"
                        "ext_photo" -> "Most Popular"
                        "ext_vip_pickup" -> "VIP Doorstep"
                        "ext_wedding_pack" -> "Special Occasion"
                        else -> null
                    }
                    val icon = when (extra.iconName) {
                        "BakeryDining" -> Icons.Filled.BakeryDining
                        "LocalBar" -> Icons.Filled.LocalBar
                        "CameraAlt" -> Icons.Filled.CameraAlt
                        "Hotel" -> Icons.Filled.Hotel
                        "Celebration" -> Icons.Filled.Celebration
                        "Headphones" -> Icons.Filled.Headphones
                        else -> Icons.Filled.Stars
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onToggleExtra(extra) }
                            .border(
                                width = if (isChecked) 1.5.dp else 1.dp,
                                color = if (isChecked) GoldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        color = if (isChecked) GoldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { onToggleExtra(extra) },
                                colors = CheckboxDefaults.colors(checkedColor = GoldPrimary, checkmarkColor = NavyDark)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = if (isChecked) GoldPrimary.copy(alpha = 0.2f) else TagusBlue.copy(alpha = 0.1f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isChecked) GoldPrimary else TagusBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = extra.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    if (badgeLabel != null) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = GoldPrimary.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = badgeLabel,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFFD97706),
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = extra.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "+$currencySymbol${String.format(Locale.US, "%.2f", extra.priceEur)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TagusBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TravelerDetailsCard(
    customerName: String,
    onCustomerNameChange: (String) -> Unit,
    customerEmail: String,
    onCustomerEmailChange: (String) -> Unit,
    customerPhone: String,
    onCustomerPhoneChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    savedTravelers: List<SavedTraveler>,
    onSelectSavedTraveler: (SavedTraveler) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Badge, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lead Traveler Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            // Quick select from saved travelers if any
            if (savedTravelers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Autofill from saved profile travelers:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(savedTravelers) { traveler ->
                        AssistChip(
                            onClick = { onSelectSavedTraveler(traveler) },
                            label = { Text(traveler.fullName) },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = customerName,
                onValueChange = onCustomerNameChange,
                label = { Text("Full Name *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_input_name"),
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = TagusBlue) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = customerEmail,
                onValueChange = onCustomerEmailChange,
                label = { Text("Email Address (for Digital Pass) *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_input_email"),
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = TagusBlue) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = customerPhone,
                onValueChange = onCustomerPhoneChange,
                label = { Text("Mobile Phone (WhatsApp Updates) *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_input_phone"),
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = TagusBlue) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = { Text("Special Requests / Notes (Optional)") },
                placeholder = { Text("e.g. Need child booster seat, celebrating birthday") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_input_notes"),
                maxLines = 3,
                leadingIcon = { Icon(Icons.Outlined.NoteAlt, contentDescription = null, tint = TagusBlue) },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun PaymentMethodSelector(
    selectedProvider: PaymentProvider,
    onProviderSelected: (PaymentProvider) -> Unit,
    modifier: Modifier = Modifier
) {
    var mbWayPhone by remember { mutableStateOf("912 345 678") }

    val providers: List<Pair<PaymentProvider, String>> = listOf(
        PaymentProvider.CARD to "Credit / Debit Card",
        PaymentProvider.MB_WAY to "MB WAY (Portugal's #1)",
        PaymentProvider.GOOGLE_PAY to "Google Pay",
        PaymentProvider.APPLE_PAY to "Apple Pay",
        PaymentProvider.PAYPAL to "PayPal Express",
        PaymentProvider.CASH_ON_ARRIVAL to "Pay on Arrival (Cash / POS)"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Payment, contentDescription = null, tint = TagusBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Payment Method", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("256-bit Encrypted", style = MaterialTheme.typography.labelSmall, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                providers.forEach { (provider, label) ->
                    val isSelected = provider == selectedProvider
                    val isMbWay = provider == PaymentProvider.MB_WAY
                    val accentColor = if (isMbWay) Color(0xFFE30613) else TagusBlue

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onProviderSelected(provider) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) accentColor else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        color = if (isSelected) accentColor.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onProviderSelected(provider) },
                                    colors = RadioButtonDefaults.colors(selectedColor = accentColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                val iconVector = when (provider) {
                                    PaymentProvider.CARD -> Icons.Filled.CreditCard
                                    PaymentProvider.MB_WAY -> Icons.Filled.PhoneAndroid
                                    PaymentProvider.GOOGLE_PAY -> Icons.Filled.AccountBalanceWallet
                                    PaymentProvider.APPLE_PAY -> Icons.Filled.PhoneIphone
                                    PaymentProvider.PAYPAL -> Icons.Filled.AccountBalance
                                    PaymentProvider.SUMUP -> Icons.Filled.PointOfSale
                                    PaymentProvider.CASH_ON_ARRIVAL -> Icons.Filled.Payments
                                }

                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = null,
                                    tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )

                                if (isMbWay) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFE30613).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "MB WAY",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFE30613),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                } else if (provider == PaymentProvider.GOOGLE_PAY || provider == PaymentProvider.APPLE_PAY) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF10B981).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "1-TAP",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF10B981),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            if (isSelected && isMbWay) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFE30613).copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "Portuguese Mobile Number for Instant Notification",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE30613)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = mbWayPhone,
                                        onValueChange = { mbWayPhone = it },
                                        prefix = { Text("+351 ", fontWeight = FontWeight.Bold) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "⚡ You will receive an instant push in your MB WAY app. Approve within 4 minutes.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PromoCodeSection(
    promoCode: String,
    onPromoCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Discount, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Promotional Voucher", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                AssistChip(
                    onClick = { onPromoCodeChange("TUK2026") },
                    label = { Text("Use TUK2026 (-€15)") },
                    leadingIcon = { Icon(Icons.Filled.Bolt, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = promoCode,
                onValueChange = onPromoCodeChange,
                placeholder = { Text("Enter promo code (e.g. TUK2026)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_promo_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    if (promoCode.trim().uppercase() == "TUK2026") {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "Valid Code", tint = Color(0xFF10B981))
                    }
                }
            )
        }
    }
}

@Composable
fun PriceBreakdownCard(
    breakdown: PriceBreakdown,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Detailed Price Breakdown", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            BreakdownRow("Base Private Tour Fare", "$currencySymbol${String.format(Locale.US, "%.2f", breakdown.basePriceEur)}")
            if (breakdown.guestCount > 1) {
                val perGuest = if (breakdown.guestCount > 1) breakdown.guestPriceEur / (breakdown.guestCount - 1) else 15.0
                BreakdownRow("Additional Guests (${breakdown.guestCount - 1} × $currencySymbol${String.format(Locale.US, "%.2f", perGuest)})", "+$currencySymbol${String.format(Locale.US, "%.2f", breakdown.guestPriceEur)}")
            }
            if (breakdown.extrasPriceEur > 0) {
                BreakdownRow("Selected Experience Tastings", "+$currencySymbol${String.format(Locale.US, "%.2f", breakdown.extrasPriceEur)}")
            }
            if (breakdown.pickupFeeEur > 0) {
                BreakdownRow("Door-to-Door Hotel Pickup", "+$currencySymbol${String.format(Locale.US, "%.2f", breakdown.pickupFeeEur)}")
            }
            if (breakdown.discountAmountEur > 0) {
                BreakdownRow("Promo Discount (TUK2026)", "-$currencySymbol${String.format(Locale.US, "%.2f", breakdown.discountAmountEur)}", isDiscount = true)
            }
            BreakdownRow("Portuguese Tourism Taxes & VAT (6%)", "$currencySymbol${String.format(Locale.US, "%.2f", breakdown.taxesAndFeesEur)}")

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Amount Due", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "$currencySymbol${String.format(Locale.US, "%.2f", breakdown.finalTotalEur)}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: String, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isDiscount) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isDiscount) Color(0xFF059669) else MaterialTheme.colorScheme.onSurface
        )
    }
}
