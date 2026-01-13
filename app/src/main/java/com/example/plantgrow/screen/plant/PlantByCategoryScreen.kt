package com.example.plantgrow.screen.plant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.plantgrow.data.plant.Plant
import com.example.plantgrow.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantByCategoryScreen(
    viewModel: PlantByCategoryViewModel = hiltViewModel(),
    navController: NavController
) {
    val plants by viewModel.plants.collectAsStateWithLifecycle(initialValue = emptyList())
    var isLoading by remember { mutableStateOf(true) }
    val categoryName = viewModel.categoryName

    LaunchedEffect(Unit) {
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        categoryName,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5E7A3C),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5E7A3C))
                }
            } else if (plants.isEmpty()) {
                EmptyPlantsByCategoryScreen(categoryName = categoryName)
            } else {
                // Передаем функцию onPlantClick для перехода к детальному экрану
                PlantsByCategoryList(
                    plants = plants,
                    onPlantClick = { plant ->
                        navController.navigate(Screens.PlantDetail.createRoute(plant.id))
                    }
                )
            }
        }
    }
}

@Composable
fun PlantsByCategoryList(
    plants: List<Plant>,
    onPlantClick: (Plant) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(plants, key = { it.id }) { plant ->
            // Передаем функцию onClick в PlantCard
            PlantCard(
                plant = plant,
                onClick = { onPlantClick(plant) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantCard(
    plant: Plant,
    onClick: () -> Unit, // Добавляем параметр для клика
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick), // Делаем карточку кликабельной
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок с названием
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Эмодзи растения
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getPlantEmoji(plant.mainGenus),
                        fontSize = 30.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = plant.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (plant.mainGenus.isNotEmpty()) {
                        Text(
                            text = plant.mainGenus,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Описание
            if (plant.description.isNotEmpty()) {
                Text(
                    text = plant.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Функция для получения эмодзи по роду растения
private fun getPlantEmoji(genus: String): String {
    return when {
        genus.contains("Картофель", ignoreCase = true) -> "🥔"
        genus.contains("Томат", ignoreCase = true) -> "🍅"
        genus.contains("Перец", ignoreCase = true) -> "🌶️"
        genus.contains("Огурец", ignoreCase = true) -> "🥒"
        genus.contains("Капуста", ignoreCase = true) -> "🥬"
        genus.contains("Морковь", ignoreCase = true) -> "🥕"
        genus.contains("Лук", ignoreCase = true) -> "🧅"
        genus.contains("Чеснок", ignoreCase = true) -> "🧄"
        genus.contains("Свекла", ignoreCase = true) -> "🔴"
        genus.contains("Редис", ignoreCase = true) -> "🌶️"
        genus.contains("Кабачок", ignoreCase = true) -> "🥒"
        genus.contains("Тыква", ignoreCase = true) -> "🎃"
        genus.contains("Баклажан", ignoreCase = true) -> "🍆"
        genus.contains("Горох", ignoreCase = true) -> "🫘"
        genus.contains("Фасоль", ignoreCase = true) -> "🫘"
        genus.contains("Кукуруза", ignoreCase = true) -> "🌽"
        genus.contains("Салат", ignoreCase = true) -> "🥬"
        genus.contains("Шпинат", ignoreCase = true) -> "🍃"
        genus.contains("Базилик", ignoreCase = true) -> "🌿"
        genus.contains("Укроп", ignoreCase = true) -> "🌿"
        genus.contains("Петрушка", ignoreCase = true) -> "🌿"
        else -> "🌱"
    }
}

@Composable
fun EmptyPlantsByCategoryScreen(categoryName: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🌱",
            fontSize = 80.sp,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Нет растений в категории",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "'$categoryName'",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5E7A3C)
        )
    }
}