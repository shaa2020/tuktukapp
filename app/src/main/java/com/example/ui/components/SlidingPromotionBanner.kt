package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDark
import com.example.ui.theme.TagusBlue
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

data class PromotionSlide(
    val id: String,
    val badge: String,
    val title: String,
    val description: String,
    val promoCode: String? = null,
    val actionText: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val accentGlowColor: Color,
    val destinationFilter: String? = null,
    val isAiAction: Boolean = false
)

val defaultPromotionSlides = listOf(
    PromotionSlide(
        id = "lisbon_summer",
        badge = "LIMITED TIME OFFER 🔥",
        title = "20% OFF Lisbon Electric TukTuk",
        description = "Glide through historic Alfama & Belém. Exclusive promo discount.",
        promoCode = "TUKSUMMER20",
        actionText = "Explore Lisbon",
        icon = Icons.Default.LocalOffer,
        gradientColors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0284C7)),
        accentGlowColor = Color(0xFF38BDF8),
        destinationFilter = "Lisbon"
    ),
    PromotionSlide(
        id = "sintra_sunset",
        badge = "ROYAL EXPERIENCE 🍷",
        title = "Sintra Romantic Sunset Tour",
        description = "Includes complimentary Port Wine tasting & luxury palace stops.",
        promoCode = "SINTRAWINE",
        actionText = "Explore Sintra",
        icon = Icons.Default.WineBar,
        gradientColors = listOf(Color(0xFF2E073F), Color(0xFF5B21B6), Color(0xFF9333EA)),
        accentGlowColor = Color(0xFFC084FC),
        destinationFilter = "Sintra"
    ),
    PromotionSlide(
        id = "cascais_coastal",
        badge = "COASTAL ESCAPE 🌊",
        title = "Cascais & Estoril Ocean Drive",
        description = "Door-to-door hotel pickup, scenic cliff views & free photo stops.",
        promoCode = "COASTAL2026",
        actionText = "Explore Cascais",
        icon = Icons.Default.WavingHand,
        gradientColors = listOf(Color(0xFF064E3B), Color(0xFF0D9488), Color(0xFF14B8A6)),
        accentGlowColor = Color(0xFF2DD4BF),
        destinationFilter = "Cascais"
    ),
    PromotionSlide(
        id = "ai_assistant",
        badge = "AI SMART TRIP BUILDER ✨",
        title = "Tailor Your Custom Portugal Route",
        description = "Get personalized itineraries & instant TukTuk recommendations.",
        promoCode = null,
        actionText = "Launch AI Assistant",
        icon = Icons.Default.AutoAwesome,
        gradientColors = listOf(NavyDark, Color(0xFF1E293B), TagusBlue),
        accentGlowColor = GoldPrimary,
        isAiAction = true
    )
)

const val SLIDE_DURATION_MS = 5000

@Composable
fun SlidingPromotionBanner(
    slides: List<PromotionSlide> = defaultPromotionSlides,
    onNavigateToDestination: (String?) -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val coroutineScope = rememberCoroutineScope()
    val progressAnim = remember { Animatable(0f) }

    // Auto-advance timer that runs when not scrolling
    LaunchedEffect(pagerState.isScrollInProgress, slides.size) {
        if (!pagerState.isScrollInProgress && slides.size > 1) {
            progressAnim.snapTo(0f)
            progressAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = SLIDE_DURATION_MS, easing = LinearEasing)
            )
            val nextPage = (pagerState.currentPage + 1) % slides.size
            coroutineScope.launch {
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                )
            }
        } else {
            progressAnim.snapTo(0f)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sliding_promotion_banner")
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val slide = slides[page]

            // Zero-recomposition GPU graphicsLayer transformation
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                        val pageScale = lerp(0.93f, 1.0f, 1f - pageOffset.coerceIn(0f, 1f))
                        val pageAlpha = lerp(0.65f, 1.0f, 1f - pageOffset.coerceIn(0f, 1f))

                        scaleX = pageScale
                        scaleY = pageScale
                        alpha = pageAlpha
                    }
            ) {
                PromotionSlideCard(
                    slide = slide,
                    onCardClick = {
                        if (slide.isAiAction) {
                            onNavigateToAiAssistant()
                        } else {
                            onNavigateToDestination(slide.destinationFilter)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Timer Progress Line & Dot Indicators
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // High performance progress bar using GPU graphicsLayer scaling
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = progressAnim.value
                            transformOrigin = TransformOrigin(0f, 0f)
                        }
                        .clip(CircleShape)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(GoldPrimary, slides[pagerState.currentPage].accentGlowColor)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interactive Pager Indicator Dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(slides.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateFloatAsState(
                        targetValue = if (isSelected) 26f else 8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "indicator_width"
                    )
                    val color by animateColorAsState(
                        targetValue = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        animationSpec = tween(durationMillis = 250),
                        label = "indicator_color"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun PromotionSlideCard(
    slide: PromotionSlide,
    onCardClick: () -> Unit
) {
    // Hardware accelerated pulse animation using graphicsLayer
    val infiniteTransition = rememberInfiniteTransition(label = "banner_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = slide.accentGlowColor.copy(alpha = 0.25f),
                spotColor = slide.accentGlowColor.copy(alpha = 0.35f)
            )
            .clickable { onCardClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = slide.gradientColors
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            // Decorative background light aura with deferred GPU scaling
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.TopEnd)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                slide.accentGlowColor.copy(alpha = 0.22f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Badge Label
                    Surface(
                        color = Color.White.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, slide.accentGlowColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = slide.badge,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Promo Code Chip if available
                    if (!slide.promoCode.isNullOrBlank()) {
                        Surface(
                            color = GoldPrimary,
                            shape = RoundedCornerShape(10.dp),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = NavyDark,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = slide.promoCode,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = NavyDark,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.25f),
                                        Color.White.copy(alpha = 0.08f)
                                    )
                                )
                            )
                            .padding(1.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = slide.icon,
                                contentDescription = slide.title,
                                tint = GoldPrimary,
                                modifier = Modifier
                                    .size(24.dp)
                                    .graphicsLayer {
                                        scaleX = pulseScale
                                        scaleY = pulseScale
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = slide.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 18.sp,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = slide.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.92f),
                            fontSize = 12.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onCardClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = NavyDark
                        ),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = slide.actionText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = NavyDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
