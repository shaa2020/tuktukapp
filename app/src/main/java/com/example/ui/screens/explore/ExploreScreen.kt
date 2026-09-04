package com.example.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TourCategory
import com.example.data.model.ExperienceType
import com.example.data.repository.FilterParams
import com.example.data.repository.TourSortOption
import com.example.ui.components.TourCardVertical
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDark
import com.example.ui.theme.TagusBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onNavigateToTourDetail: (String) -> Unit
) {
    val tours by viewModel.filteredTours.collectAsState()
    val filterParams by viewModel.filterParams.collectAsState()
    val currency by viewModel.currentCurrency.collectAsState()
    val wishlistedIds by viewModel.wishlistedIds.collectAsState()

    var showFilterBottomSheet by remember { mutableStateOf(false) }

    val destinations = remember { listOf("All", "Lisbon", "Sintra", "Cascais", "Cabo da Roca", "Fátima", "Nazaré") }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Explore Private Tours",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar + Filter Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = filterParams.query,
                        onValueChange = { viewModel.updateQuery(it) },
                        placeholder = { Text("Search Sintra, Belém, Sunset...") },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                        trailingIcon = {
                            if (filterParams.query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateQuery("") }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("explore_search_input"),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showFilterBottomSheet = true },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .testTag("filter_options_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Filters",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Destination Selector Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(destinations) { dest ->
                        val isSelected = (filterParams.destination == dest) || (dest == "All" && filterParams.destination.isNullOrEmpty())
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val newDest = if (dest == "All") null else dest
                                viewModel.applyFilters(filterParams.copy(destination = newDest))
                            },
                            label = { Text(dest) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TagusBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Curated Travel Vibe Chips
                val vibeCollections = remember {
                    listOf(
                        "👥 Group Fleet Convoy" to "Convoy",
                        "🌅 Sunset Specials" to "Sunset",
                        "🏰 Palaces & Castles" to "Palace",
                        "🍷 Wine & Tapas" to "Wine",
                        "🌊 Ocean Coastline" to "Coast",
                        "⭐ Top Rated 4.9+" to "Best"
                    )
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(vibeCollections) { (label, keyword) ->
                        val isQueryActive = filterParams.query.contains(keyword, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isQueryActive) GoldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.clickable {
                                if (isQueryActive) {
                                    viewModel.updateQuery("")
                                } else {
                                    viewModel.updateQuery(keyword)
                                }
                            }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isQueryActive) NavyDark else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("explore_screen_container")
        ) {
            if (tours.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.SearchOff,
                            contentDescription = null,
                            tint = TagusBlue,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No experiences match your filter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try clearing search keywords or resetting price and category filters.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.applyFilters(FilterParams()) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                        ) {
                            Text("Reset All Filters", color = TagusBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${tours.size} Tuk-Tuk Experiences",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Sorted by ${filterParams.sortOption.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TagusBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    items(tours) { tour ->
                        TourCardVertical(
                            tour = tour,
                            currency = currency,
                            isWishlisted = wishlistedIds.contains(tour.id),
                            onWishlistToggle = { viewModel.toggleWishlist(tour.id) },
                            onClick = { onNavigateToTourDetail(tour.id) }
                        )
                    }
                }
            }
        }
    }

    if (showFilterBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterBottomSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            FilterBottomSheetContent(
                currentParams = filterParams,
                onApply = { updatedParams ->
                    viewModel.applyFilters(updatedParams)
                    showFilterBottomSheet = false
                },
                onReset = {
                    viewModel.applyFilters(FilterParams())
                    showFilterBottomSheet = false
                }
            )
        }
    }
}

@Composable
private fun FilterBottomSheetContent(
    currentParams: FilterParams,
    onApply: (FilterParams) -> Unit,
    onReset: () -> Unit
) {
    var maxPrice by remember { mutableStateOf(currentParams.maxPriceEur.toFloat()) }
    var selectedCategory by remember { mutableStateOf(currentParams.categoryId) }
    var experienceType by remember { mutableStateOf(currentParams.experienceType) }
    var minRating by remember { mutableStateOf(currentParams.minRating) }
    var pickupOnly by remember { mutableStateOf(currentParams.pickupOnly) }
    var selectedSort by remember { mutableStateOf(currentParams.sortOption) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .testTag("filter_bottom_sheet_content")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filter Experiences", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            TextButton(onClick = onReset) {
                Text("Reset All", color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Price Slider
        Text("Max Base Price: €${maxPrice.toInt()}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Slider(
            value = maxPrice,
            onValueChange = { maxPrice = it },
            valueRange = 50f..500f,
            steps = 8,
            colors = SliderDefaults.colors(thumbColor = TagusBlue, activeTrackColor = TagusBlue)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Selection
        Text("Category", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All Categories") }
                )
            }
            items(TourCategory.values()) { cat ->
                FilterChip(
                    selected = selectedCategory == cat.id,
                    onClick = { selectedCategory = cat.id },
                    label = { Text(cat.displayName) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Experience Type
        Text("Experience Type", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            FilterChip(
                selected = experienceType == null,
                onClick = { experienceType = null },
                label = { Text("All Types") }
            )
            FilterChip(
                selected = experienceType == ExperienceType.PRIVATE,
                onClick = { experienceType = ExperienceType.PRIVATE },
                label = { Text("Private Only") }
            )
            FilterChip(
                selected = experienceType == ExperienceType.SHARED,
                onClick = { experienceType = ExperienceType.SHARED },
                label = { Text("Shared") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hotel Pickup Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Includes Hotel Pickup", style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = pickupOnly,
                onCheckedChange = { pickupOnly = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onApply(
                    currentParams.copy(
                        maxPriceEur = maxPrice.toDouble(),
                        categoryId = selectedCategory,
                        experienceType = experienceType,
                        minRating = minRating,
                        pickupOnly = pickupOnly,
                        sortOption = selectedSort
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Apply Filters", fontWeight = FontWeight.Bold)
        }
    }
}
