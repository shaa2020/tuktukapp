package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Brightness5
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDark
import com.example.ui.theme.TagusBlue

data class CityWeatherInfo(
    val city: String,
    val tempCelsius: Int,
    val condition: String,
    val weatherIcon: String,
    val sunsetTime: String,
    val sunsetCountdown: String,
    val bestTourType: String,
    val uvIndex: String,
    val gradientColors: List<Color>
)

val LisbonWeatherPresets = listOf(
    CityWeatherInfo(
        city = "Lisbon",
        tempCelsius = 24,
        condition = "Golden Hour Clear",
        weatherIcon = "☀️",
        sunsetTime = "19:54",
        sunsetCountdown = "In ~2h 15m",
        bestTourType = "Miradouro Ridge & Alfama Sunset",
        uvIndex = "Moderate (4)",
        gradientColors = listOf(Color(0xFF0C4A6E), Color(0xFF0369A1), Color(0xFFE08E00))
    ),
    CityWeatherInfo(
        city = "Sintra",
        tempCelsius = 20,
        condition = "Misty Forest Breeze",
        weatherIcon = "🌲",
        sunsetTime = "19:58",
        sunsetCountdown = "In ~2h 19m",
        bestTourType = "Pena Palace & Moorish Castle Tour",
        uvIndex = "Low (2)",
        gradientColors = listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF0D9488))
    ),
    CityWeatherInfo(
        city = "Cascais",
        tempCelsius = 22,
        condition = "Atlantic Ocean Breeze",
        weatherIcon = "🌊",
        sunsetTime = "19:56",
        sunsetCountdown = "In ~2h 17m",
        bestTourType = "Boca do Inferno & Coastline Tuk-Tuk",
        uvIndex = "Moderate (5)",
        gradientColors = listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF0284C7))
    )
)

@Composable
fun LisbonWeatherSunsetWidget(
    onExploreSunsetTours: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val current = LisbonWeatherPresets[selectedIndex]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weather_sunset_widget"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(current.gradientColors))
                .padding(18.dp)
        ) {
            Column {
                // Header: Location Switcher & Live indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LisbonWeatherPresets.forEachIndexed { index, info ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (selectedIndex == index) Color.White else Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.clickable { selectedIndex = index }
                            ) {
                                Text(
                                    text = "${info.weatherIcon} ${info.city}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedIndex == index) NavyDark else Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.25f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF34D399))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "LIVE FORECAST",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF6EE7B7),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Center Row: Temp & Sunset Countdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Temperature & Condition
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${current.tempCelsius}°C",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = current.condition,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "UV: ${current.uvIndex}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Sunset Badge
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = GoldPrimary.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.WbTwilight,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "SUNSET ${current.sunsetTime}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldPrimary
                                )
                                Text(
                                    text = current.sunsetCountdown,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Call to Action: Recommended Tour
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.28f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExploreSunsetTours(current.city) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Outlined.Explore,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "PRIME VIEWING EXPERIENCE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                                Text(
                                    text = current.bestTourType,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                        Icon(
                            Icons.Filled.ArrowForward,
                            contentDescription = "View Tours",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
