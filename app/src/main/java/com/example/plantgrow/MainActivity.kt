package com.example.plantgrow

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.plantgrow.navigation.NavBarItems
import com.example.plantgrow.navigation.NavGraph
import com.example.plantgrow.navigation.Screens
import com.example.plantgrow.notification.WateringNotificationScheduler
import com.example.plantgrow.ui.theme.PlantGrowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var notificationScheduler: WateringNotificationScheduler

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationScheduler = WateringNotificationScheduler(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestNotificationPermission()
        } else {
            notificationScheduler.scheduleDailyNotifications()
        }

        setContent {
            PlantGrowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestNotificationPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            notificationScheduler.scheduleDailyNotifications()
        }
    }

}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route

    val hideBottomBarRoutes = listOf(
        Screens.BedDetail.route.replace("/{bedId}", ""), // "bed_detail"
        Screens.PestDetail.route.replace("/{pestId}", ""), // "pest_detail"
        Screens.PlantCategoryWithBed.route, // "plant_category/{bedId}" - полный маршрут
        Screens.PlantByCategory.route.replace("/{genus}", ""), // "plant_by_category"
        Screens.PlantByCategoryWithBed.route.replace("/{bedId}/{genus}", ""), // "plant_by_category"
        Screens.PlantDetail.route.replace("/{plantId}", "") // "plant_detail"
    )

    // Дополнительная логика для plant_category
    val showBottomBar = if (currentRoute?.startsWith("plant_category") == true) {
        // Только если это ТОЧНО "plant_category" (без bedId), показываем BottomBar
        currentRoute == Screens.PlantCategory.route
    } else {
        // Для остальных маршрутов обычная проверка
        hideBottomBarRoutes.none { route ->
            currentRoute?.startsWith(route) == true
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier.fillMaxHeight(0.1f),
        containerColor = Color(0xFF5E7A3C)
    ) {
        NavBarItems.BottomNavItems.forEach { navItem ->
            val isSelected = currentRoute == navItem.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // Навигация с очисткой стека при нажатии на активный элемент
                    if (currentRoute != navItem.route) {
                        navController.navigate(navItem.route) {
                            // Очищаем стек до корня
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Запускаем как single top
                            launchSingleTop = true
                            // Восстанавливаем состояние
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.label,
                        modifier = Modifier.height(24.dp)
                    )
                },
                label = {
                    Text(
                        text = navItem.label,
                        fontSize = 12.sp // Немного меньше текст
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = Color(0xFF195334) // Темно-зеленый индикатор
                )
            )
        }
    }
}