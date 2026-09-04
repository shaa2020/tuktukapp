package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppCurrency
import com.example.data.model.AppLanguage
import com.example.data.model.TourCategory
import com.example.ui.components.SearchBarComponent
import com.example.ui.components.SlidingPromotionBanner
import com.example.ui.components.TourCardVertical
import com.example.ui.components.TukTuk24Logo
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDark
import com.example.ui.theme.TagusBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToTourDetail: (String) -> Unit,
    onNavigateToExplore: (String?) -> Unit,
    onNavigateToAiAssistant: () -> Unit
) {
    val currency by viewModel.currentCurrency.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedQuickSearch by viewModel.selectedQuickSearch.collectAsState()
    val wishlistedIds by viewModel.wishlistedIds.collectAsState()

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
            .testTag("home_screen_container")
    ) {
        // Top Bar Branding Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyDark)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TukTuk24Logo(
                    isDarkBackground = true,
                    logoHeight = 38.dp,
                    showSubtitle = true,
                    subtitleText = "Private Travel Experiences in Portugal",
                    modifier = Modifier.testTag("tuktuk24_home_brand_logo")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Currency Chip
                    AssistChip(
                        onClick = { showCurrencyDialog = true },
                        label = { Text(currency.code, fontWeight = FontWeight.Bold) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.testTag("currency_selector_chip")
                    )

                    // Language Chip
                    AssistChip(
                        onClick = { showLanguageDialog = true },
                        label = { Text("${language.flagEmoji} ${language.code.uppercase()}", fontWeight = FontWeight.Bold) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.testTag("language_selector_chip")
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Search Bar Component
            SearchBarComponent(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                selectedQuickSearch = selectedQuickSearch,
                onQuickSearchClick = { dest ->
                    viewModel.onQuickSearchClick(dest)
                    onNavigateToExplore(dest)
                },
                onOpenFilter = { onNavigateToExplore(selectedQuickSearch) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Sliding Promotion Carousel Banner
            SlidingPromotionBanner(
                onNavigateToDestination = { dest -> onNavigateToExplore(dest) },
                onNavigateToAiAssistant = onNavigateToAiAssistant
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Real-Time Weather & Sunset Countdown Widget
            com.example.ui.components.LisbonWeatherSunsetWidget(
                onExploreSunsetTours = { dest -> onNavigateToExplore(dest) }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // AI Travel Assistant Hero Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAiAssistant() }
                    .testTag("ai_assistant_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = TagusBlue)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = GoldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TukTuk24 AI Assistant",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Plan your 3-day trip in Lisbon, Sintra or Cascais instantly",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Popular Experiences
            SectionHeader(title = "Popular Experiences", onSeeAll = { onNavigateToExplore(null) })
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalTourList(
                tours = viewModel.getPopularTours(),
                currency = currency,
                wishlistedIds = wishlistedIds,
                onWishlistToggle = viewModel::toggleWishlist,
                onTourClick = onNavigateToTourDetail
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Recommended Experiences
            SectionHeader(title = "Recommended For You", onSeeAll = { onNavigateToExplore(null) })
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalTourList(
                tours = viewModel.getRecommendedTours(),
                currency = currency,
                wishlistedIds = wishlistedIds,
                onWishlistToggle = viewModel::toggleWishlist,
                onTourClick = onNavigateToTourDetail
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Popular Destinations
            SectionHeader(title = "Popular Destinations")
            Spacer(modifier = Modifier.height(12.dp))
            DestinationsRow(onDestinationClick = { onNavigateToExplore(it) })

            Spacer(modifier = Modifier.height(24.dp))

            // 7. Categories Grid
            SectionHeader(title = "Experience Categories")
            Spacer(modifier = Modifier.height(12.dp))
            CategoriesGrid(onCategoryClick = { onNavigateToExplore(null) })

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Last-Minute Availability
            SectionHeader(title = "Last-Minute Availability", onSeeAll = { onNavigateToExplore(null) })
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalTourList(
                tours = viewModel.getLastMinuteTours(),
                currency = currency,
                wishlistedIds = wishlistedIds,
                onWishlistToggle = viewModel::toggleWishlist,
                onTourClick = onNavigateToTourDetail
            )

            Spacer(modifier = Modifier.height(24.dp))

            // VIP Fleet Convoy & Group Experiences Showcase
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToTourDetail("tour_fleet_convoy") }
                    .testTag("fleet_convoy_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldPrimary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "FLEET & CONVOY BOOKING",
                                color = GoldPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                letterSpacing = 1.sp
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = GoldPrimary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Groups, contentDescription = null, tint = NavyDark, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Private Tuk-Tuk Convoy (Up to 24 Guests)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Planning a wedding, bachelorette party, or corporate summit in Lisbon? Book 2 to 5 synchronized electric tuk-tuks traveling together with linked radio storytelling & panoramic miradouro stops.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Fleet Pricing", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                            Text("From €240 / group", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = GoldPrimary)
                        }

                        Button(
                            onClick = { onNavigateToTourDetail("tour_fleet_convoy") },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark)
                        ) {
                            Text("Book Convoy", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 12. Customer Reviews
            SectionHeader(title = "Verified Customer Reviews")
            Spacer(modifier = Modifier.height(12.dp))
            ReviewsPreviewCard()

            Spacer(modifier = Modifier.height(24.dp))

            // 13. Travel Inspiration Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Why TukTuk24?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• 100% Eco-Friendly Electric Tuk-Tuks\n• Local Certified Storyteller Guides\n• Door-to-Door Hotel Pickup\n• Instant Flexible Confirmation & 24h Cancellation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Currency selector dialog
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Select Currency") },
            text = {
                Column {
                    AppCurrency.entries.forEach { curr ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateCurrency(curr)
                                    showCurrencyDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(curr.symbol, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
                            Text("${curr.name} (${curr.code})", style = MaterialTheme.typography.bodyLarge)
                        }
                        HorizontalDivider()
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) { Text("Close") }
            }
        )
    }

    // Language selector dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language") },
            text = {
                Column {
                    AppLanguage.entries.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(lang.flagEmoji, modifier = Modifier.width(36.dp), fontSize = 20.sp)
                            Text(lang.displayName, style = MaterialTheme.typography.bodyLarge)
                        }
                        HorizontalDivider()
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Close") }
            }
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    onSeeAll: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (onSeeAll != null) {
            TextButton(onClick = onSeeAll) {
                Text("See All", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HorizontalTourList(
    tours: List<com.example.data.model.Tour>,
    currency: AppCurrency,
    wishlistedIds: Set<String>,
    onWishlistToggle: (String) -> Unit,
    onTourClick: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(tours) { tour ->
            TourCardVertical(
                tour = tour,
                currency = currency,
                isWishlisted = wishlistedIds.contains(tour.id),
                onWishlistToggle = { onWishlistToggle(tour.id) },
                onClick = { onTourClick(tour.id) },
                modifier = Modifier.width(280.dp)
            )
        }
    }
}

@Composable
fun DestinationsRow(onDestinationClick: (String) -> Unit) {
    val dests = listOf(
        Pair("Lisbon", "https://images.unsplash.com/photo-1588614959060-4d144f28b207?auto=format&fit=crop&w=400&q=80"),
        Pair("Sintra", "https://images.unsplash.com/photo-1590077428593-a55bb07c4665?auto=format&fit=crop&w=400&q=80"),
        Pair("Cascais", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=400&q=80"),
        Pair("Cabo da Roca", "https://images.unsplash.com/photo-1512100356356-de1b84283e18?auto=format&fit=crop&w=400&q=80"),
        Pair("Nazaré", "https://images.unsplash.com/photo-1518684079-3c830dcef090?auto=format&fit=crop&w=400&q=80")
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(dests) { (name, img) ->
            Card(
                modifier = Modifier
                    .size(width = 130.dp, height = 100.dp)
                    .clickable { onDestinationClick(name) },
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = img,
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f))
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoriesGrid(onCategoryClick: (TourCategory) -> Unit) {
    val categories = TourCategory.entries
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(categories) { cat ->
            val icon = when (cat) {
                TourCategory.HISTORIC -> Icons.Filled.AccountBalance
                TourCategory.SIGHTSEEING -> Icons.Filled.Visibility
                TourCategory.COASTAL -> Icons.Filled.Water
                TourCategory.SUNSET -> Icons.Filled.WbSunny
                TourCategory.FOOD_WINE -> Icons.Filled.Restaurant
                TourCategory.DAY_TRIP -> Icons.Filled.DirectionsCar
                TourCategory.FAMILY -> Icons.Filled.FamilyRestroom
                TourCategory.COUPLES -> Icons.Filled.Favorite
                TourCategory.FLEET_CONVOY -> Icons.Filled.Groups
            }
            val isConvoy = cat == TourCategory.FLEET_CONVOY

            Card(
                modifier = Modifier
                    .clickable { onCategoryClick(cat) }
                    .testTag("cat_chip_${cat.name}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isConvoy) NavyDark else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isConvoy) GoldPrimary else TagusBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = cat.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isConvoy) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    if (isConvoy) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldPrimary.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "VIP",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldPrimary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewsPreviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = GoldPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("4.97 Average Rating", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(" (1,240+ verified reviews)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "“The best way to experience Lisbon & Sintra! Peaceful electric tuk-tuk, amazing guides, and breathtaking sunset views.”",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "— Sarah J. (United States)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
