package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GoldPrimary

/**
 * Official TukTuk24 Company & Application Logo Composable.
 * Features the signature Tuk-Tuk vehicle silhouette, stacked bold "TUK TUK",
 * and prominent golden "24".
 */
@Composable
fun TukTuk24Logo(
    modifier: Modifier = Modifier,
    isDarkBackground: Boolean = true,
    logoHeight: Dp = 36.dp,
    showSubtitle: Boolean = false,
    subtitleText: String = "Private Travel Experiences in Portugal"
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Official Vector Logo (Vehicle + TUK TUK + 24)
            Image(
                painter = painterResource(
                    id = if (isDarkBackground) R.drawable.ic_tuktuk24_logo_dark else R.drawable.ic_tuktuk24_logo
                ),
                contentDescription = "TukTuk24 Official Logo",
                modifier = Modifier
                    .height(logoHeight)
                    .width(logoHeight * 2.6f)
            )
        }

        if (showSubtitle) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitleText,
                style = MaterialTheme.typography.labelSmall,
                color = if (isDarkBackground) Color(0xFF94A3B8) else Color(0xFF64748B),
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Compact Icon / Badge for TukTuk24
 */
@Composable
fun TukTuk24Badge(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.25f))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_tuktuk24_logo_dark),
            contentDescription = "TukTuk24 Emblem",
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        )
    }
}
