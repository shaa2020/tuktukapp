package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ExperienceType
import com.example.data.repository.FilterParams
import com.example.data.repository.TourSortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilter: FilterParams,
    onApplyFilter: (FilterParams) -> Unit,
    onDismiss: () -> Unit
) {
    var maxPrice by remember { mutableFloatStateOf(currentFilter.maxPriceEur.toFloat()) }
    var minRating by remember { mutableFloatStateOf(currentFilter.minRating.toFloat()) }
    var selectedType by remember { mutableStateOf(currentFilter.experienceType) }
    var pickupOnly by remember { mutableStateOf(currentFilter.pickupOnly) }
    var instantOnly by remember { mutableStateOf(currentFilter.instantConfirmationOnly) }
    var selectedSort by remember { mutableStateOf(currentFilter.sortOption) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("filter_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Experiences",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = {
                    maxPrice = 300f
                    minRating = 0f
                    selectedType = null
                    pickupOnly = false
                    instantOnly = false
                    selectedSort = TourSortOption.RECOMMENDED
                }) {
                    Text("Reset")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sort Options
            Text("Sort By", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TourSortOption.entries.forEach { sortOpt ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedSort == sortOpt,
                            onClick = { selectedSort = sortOpt }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(sortOpt.displayName, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Max Price Slider
            Text(
                text = "Max Price: €${maxPrice.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = maxPrice,
                onValueChange = { maxPrice = it },
                valueRange = 50f..350f,
                steps = 6
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Experience Type
            Text("Experience Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                FilterChip(
                    selected = selectedType == null,
                    onClick = { selectedType = null },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedType == ExperienceType.PRIVATE,
                    onClick = { selectedType = ExperienceType.PRIVATE },
                    label = { Text("Private Only") }
                )
                FilterChip(
                    selected = selectedType == ExperienceType.SHARED,
                    onClick = { selectedType = ExperienceType.SHARED },
                    label = { Text("Shared Only") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle switches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Hotel Pickup Available", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = pickupOnly, onCheckedChange = { pickupOnly = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Instant Confirmation", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = instantOnly, onCheckedChange = { instantOnly = it })
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onApplyFilter(
                        currentFilter.copy(
                            maxPriceEur = maxPrice.toDouble(),
                            minRating = minRating.toDouble(),
                            experienceType = selectedType,
                            pickupOnly = pickupOnly,
                            instantConfirmationOnly = instantOnly,
                            sortOption = selectedSort
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("apply_filter_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Apply Filters", fontWeight = FontWeight.Bold)
            }
        }
    }
}
