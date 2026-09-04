package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.remote.PaymentService
import com.example.data.remote.SimulatedPaymentService
import com.example.domain.payment.DefaultDomainPaymentService
import com.example.domain.auth.FirebaseAuthService
import com.example.data.repository.*
import com.example.ui.navigation.Screen
import com.example.ui.navigation.TukTukBottomBar
import com.example.ui.screens.ai.AiAssistantScreen
import com.example.ui.screens.ai.AiAssistantViewModel
import com.example.ui.screens.booking.BookingEngineScreen
import com.example.ui.screens.booking.BookingViewModel
import com.example.ui.screens.bookings.BookingDetailScreen
import com.example.ui.screens.bookings.BookingsListScreen
import com.example.ui.screens.bookings.BookingsViewModel
import com.example.ui.screens.detail.TourDetailScreen
import com.example.ui.screens.detail.TourDetailViewModel
import com.example.ui.screens.explore.ExploreScreen
import com.example.ui.screens.explore.ExploreViewModel
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.home.HomeViewModel
import com.example.ui.screens.auth.AuthenticationScreen
import com.example.ui.screens.auth.AuthViewModel
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.ProfileViewModel
import com.example.ui.screens.wishlist.WishlistScreen
import com.example.ui.screens.wishlist.WishlistViewModel
import com.example.ui.theme.TukTuk24Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TukTuk24Theme {
                TukTuk24App()
            }
        }
    }
}

@Composable
fun TukTuk24App() {
    val context = LocalContext.current

    // Initialize Database & Repositories
    val db = remember { AppDatabase.getDatabase(context) }
    val paymentService = remember { DefaultDomainPaymentService() }

    val tourRepo = remember { TourRepository() }
    val bookingRepo = remember { BookingRepository(db.bookingDao(), paymentService) }
    val wishlistRepo = remember { WishlistRepository(db.wishlistDao()) }
    val userRepo = remember { UserRepository() }
    val aiRepo = remember { AiAssistantRepository(tourRepo) }

    // Initialize ViewModels
    val authVm = remember { AuthViewModel(userRepo) }
    val homeVm = remember { HomeViewModel(tourRepo, wishlistRepo, userRepo) }
    val exploreVm = remember { ExploreViewModel(tourRepo, wishlistRepo, userRepo) }
    val detailVm = remember { TourDetailViewModel(tourRepo, wishlistRepo, userRepo) }
    val bookingVm = remember { BookingViewModel(tourRepo, bookingRepo, userRepo) }
    val bookingsVm = remember { BookingsViewModel(bookingRepo, userRepo) }
    val wishlistVm = remember { WishlistViewModel(wishlistRepo, tourRepo, userRepo) }
    val profileVm = remember { ProfileViewModel(userRepo) }
    val aiVm = remember { AiAssistantViewModel(aiRepo, userRepo) }

    val userProfile by userRepo.userProfile.collectAsState()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = remember {
        setOf(
            Screen.Home.route,
            Screen.Explore.route,
            Screen.Wishlist.route,
            Screen.Bookings.route,
            Screen.Profile.route
        )
    }

    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                TukTukBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Auth.route,
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeVm,
                    onNavigateToTourDetail = { tourId ->
                        navController.navigate(Screen.TourDetail.createRoute(tourId))
                    },
                    onNavigateToExplore = { dest ->
                        exploreVm.setInitialDestination(dest)
                        navController.navigate(Screen.Explore.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToAiAssistant = {
                        navController.navigate(Screen.AiAssistant.route)
                    }
                )
            }

            composable(Screen.Explore.route) {
                ExploreScreen(
                    viewModel = exploreVm,
                    onNavigateToTourDetail = { tourId ->
                        navController.navigate(Screen.TourDetail.createRoute(tourId))
                    }
                )
            }

            composable(
                route = Screen.TourDetail.route,
                arguments = listOf(navArgument("tourId") { type = NavType.StringType })
            ) { backStackEntry ->
                val tourId = backStackEntry.arguments?.getString("tourId") ?: ""
                TourDetailScreen(
                    tourId = tourId,
                    viewModel = detailVm,
                    isGuest = userProfile.isGuest,
                    onNavigateToAuth = { navController.navigate(Screen.Auth.route) },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToBooking = { id, date, time, guests ->
                        bookingVm.setDate(date)
                        bookingVm.setTimeSlot(time)
                        bookingVm.setGuestCount(guests)
                        navController.navigate(Screen.BookingEngine.createRoute(id))
                    }
                )
            }

            composable(
                route = Screen.BookingEngine.route,
                arguments = listOf(navArgument("tourId") { type = NavType.StringType })
            ) { backStackEntry ->
                val tourId = backStackEntry.arguments?.getString("tourId") ?: ""
                BookingEngineScreen(
                    tourId = tourId,
                    viewModel = bookingVm,
                    isGuest = userProfile.isGuest,
                    onNavigateToAuth = { navController.navigate(Screen.Auth.route) },
                    onNavigateBack = { navController.popBackStack() },
                    onBookingSuccess = { bookingId ->
                        navController.navigate(Screen.BookingDetail.createRoute(bookingId)) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            composable(Screen.Bookings.route) {
                BookingsListScreen(
                    viewModel = bookingsVm,
                    onNavigateToBookingDetail = { bookingId ->
                        navController.navigate(Screen.BookingDetail.createRoute(bookingId))
                    },
                    onNavigateToExplore = {
                        navController.navigate(Screen.Explore.route)
                    }
                )
            }

            composable(
                route = Screen.BookingDetail.route,
                arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
                BookingDetailScreen(
                    bookingId = bookingId,
                    viewModel = bookingsVm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Wishlist.route) {
                WishlistScreen(
                    viewModel = wishlistVm,
                    onNavigateToTourDetail = { tourId ->
                        navController.navigate(Screen.TourDetail.createRoute(tourId))
                    },
                    onNavigateToExplore = {
                        navController.navigate(Screen.Explore.route)
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = profileVm,
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }

            composable(Screen.Auth.route) {
                AuthenticationScreen(
                    userRepository = userRepo,
                    viewModel = authVm,
                    onAuthSuccess = {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Auth.route) { inclusive = true }
                            }
                        }
                    },
                    onNavigateBack = {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Auth.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.AiAssistant.route) {
                AiAssistantScreen(
                    viewModel = aiVm,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTourDetail = { tourId ->
                        navController.navigate(Screen.TourDetail.createRoute(tourId))
                    }
                )
            }
        }
    }
}
}
