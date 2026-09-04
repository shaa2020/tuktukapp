package com.example.ui.screens.profile

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TagusBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToAuth: () -> Unit = {}
) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsState()
    val currency by viewModel.currentCurrency.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddTravelerDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showMeetingHubsDialog by remember { mutableStateOf(false) }
    var showEcoCertificationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Account & Settings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "TukTuk24 Lisbon Concierge",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("profile_screen_scroll"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HERO PROFILE BANNER
            item {
                if (profile.isGuest) {
                    ProfileGuestCard(onNavigateToAuth = onNavigateToAuth)
                } else {
                    ProfileHeroCard(
                        profile = profile,
                        onEditProfileClick = { showEditProfileDialog = true }
                    )
                }
            }

            // TRAVEL WALLET & VOUCHERS QUICK TILES
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                Toast.makeText(
                                    context,
                                    "Your booked vouchers & QR passes are ready in Bookings",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TagusBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = TagusBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "My Passes",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    "1 Active Ticket",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showEcoCertificationDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Eco,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Eco Fleet",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    "Certified Fleet",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // APP PREFERENCES & TRAVEL SETTINGS
            item {
                Text(
                    text = "Preferences & Travel Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Currency
                ProfileInteractiveItem(
                    icon = Icons.Filled.AttachMoney,
                    iconTint = TagusBlue,
                    title = "Currency",
                    subtitle = "${currency.name} (${currency.symbol})",
                    onClick = { showCurrencyDialog = true }
                )

                // Language
                ProfileInteractiveItem(
                    icon = Icons.Filled.Language,
                    iconTint = TagusBlue,
                    title = "Language",
                    subtitle = "${language.flagEmoji} ${language.displayName}",
                    onClick = { showLanguageDialog = true }
                )

                // Push Notifications Toggle
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = null,
                                tint = TagusBlue,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Tour Arrival & Pickup Alerts",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Driver dispatch notices in Lisbon",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = profile.notificationsEnabled,
                            onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                            modifier = Modifier.testTag("toggle_notifications")
                        )
                    }
                }

                // Offline Audio & Maps Toggle
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Filled.CloudDownload,
                                contentDescription = null,
                                tint = TagusBlue,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Offline Audio Guides & Maps",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Keep historic routes cached without data",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = profile.offlineGuidesEnabled,
                            onCheckedChange = { viewModel.setOfflineGuidesEnabled(it) },
                            modifier = Modifier.testTag("toggle_offline_guides")
                        )
                    }
                }
            }

            // SAVED TRAVELERS & COMPANIONS (INTERACTIVE CRUD)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Saved Travelers & Companions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "1-tap fast booking for groups & family",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = { showAddTravelerDialog = true },
                        modifier = Modifier.testTag("add_traveler_btn")
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            tint = TagusBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Add",
                            fontWeight = FontWeight.Bold,
                            color = TagusBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (profile.savedTravelers.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.PersonOutline,
                                contentDescription = null,
                                tint = TagusBlue.copy(alpha = 0.7f),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "No companions saved yet. Add your family or friends for instant group bookings.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    profile.savedTravelers.forEach { traveler ->
                        SavedTravelerCard(
                            traveler = traveler,
                            onDeleteClick = {
                                viewModel.removeTraveler(traveler.id)
                                Toast.makeText(
                                    context,
                                    "${traveler.fullName} removed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }

            // SUPPORT & LISBON CONCIERGE
            item {
                Text(
                    text = "Support & Lisbon Hubs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                // WhatsApp Concierge
                ProfileInteractiveItem(
                    icon = Icons.Filled.SupportAgent,
                    iconTint = Color(0xFF25D366),
                    title = "24/7 WhatsApp Concierge",
                    subtitle = "Chat with Lisbon operations team directly",
                    onClick = {
                        val whatsappUrl = "https://wa.me/351910000000?text=Ol%C3%A1%20TukTuk24!%20I%20have%20a%20question%20about%20my%20Lisbon%20tour."
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "WhatsApp contact: +351 910 000 000",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )

                // Phone Line
                ProfileInteractiveItem(
                    icon = Icons.Filled.Phone,
                    iconTint = TagusBlue,
                    title = "Direct Dispatch Call Line",
                    subtitle = "+351 910 000 000 (Lisbon Central Office)",
                    onClick = {
                        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+351910000000"))
                        try {
                            context.startActivity(callIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Phone: +351 910 000 000", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Meeting Hubs
                ProfileInteractiveItem(
                    icon = Icons.Filled.Place,
                    iconTint = Color(0xFFE11D48),
                    title = "Official Lisbon Meeting Hubs",
                    subtitle = "Praça do Comércio, Rossio & Belém Tower",
                    onClick = { showMeetingHubsDialog = true }
                )

                // Fleet certification
                ProfileInteractiveItem(
                    icon = Icons.Filled.Security,
                    iconTint = GoldPrimary,
                    title = "Fleet Insurance & Certifications",
                    subtitle = "RNAAT nº 1234/2026 • 100% Eco Safety Guaranteed",
                    onClick = { showEcoCertificationDialog = true }
                )
            }

            // ACCOUNT LOGOUT
            item {
                if (!profile.isGuest) {
                    OutlinedButton(
                        onClick = { showLogoutConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("sign_out_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            Icons.Filled.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Sign Out of Account",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer branding
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TukTuk24 Lisbon Tours • Version 2.4.0",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "100% Clean Electric Mobility in Portugal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dialogs
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = profile.fullName,
            currentEmail = profile.email,
            currentPhone = profile.phone,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, email, phone ->
                viewModel.updateProfile(name, email, phone)
                showEditProfileDialog = false
            }
        )
    }

    if (showAddTravelerDialog) {
        AddTravelerDialog(
            onDismiss = { showAddTravelerDialog = false },
            onAdd = { name, email, phone, passport ->
                viewModel.addTraveler(name, email, phone, passport)
                showAddTravelerDialog = false
            }
        )
    }

    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            onDismiss = { showCurrencyDialog = false },
            onSelectCurrency = { viewModel.updateCurrency(it) }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            onDismiss = { showLanguageDialog = false },
            onSelectLanguage = { viewModel.updateLanguage(it) }
        )
    }

    if (showMeetingHubsDialog) {
        MeetingHubsDialog(
            onDismiss = { showMeetingHubsDialog = false }
        )
    }

    if (showEcoCertificationDialog) {
        EcoCertificationDialog(
            onDismiss = { showEcoCertificationDialog = false }
        )
    }

    if (showLogoutConfirmDialog) {
        LogoutConfirmDialog(
            onDismiss = { showLogoutConfirmDialog = false },
            onConfirmLogout = {
                viewModel.logout()
                showLogoutConfirmDialog = false
                Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
