package com.example.plantgrow.screen.plantDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    viewModel: PlantDetailViewModel = hiltViewModel(),
    navController: NavController
) {
    val plant by viewModel.plant.collectAsStateWithLifecycle()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        plant?.name ?: "Растение",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5E7A3C),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF5E7A3C))
            }
        } else if (plant == null) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Растение не найдено",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            PlantDetailContent(
                plant = plant!!,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun PlantDetailContent(
    plant: Plant,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Заголовок с эмодзи
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getPlantEmoji(plant.mainGenus),
                    fontSize = 50.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (plant.mainGenus.isNotEmpty()) {
                    Text(
                        text = plant.mainGenus,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Полное описание
        if (plant.description.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF9F9F9)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📖 Описание",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = plant.description,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // Основные характеристики
        val mainCharacteristics = buildList {
            if (plant.yield.isNotEmpty()) add("🌱 Урожайность" to plant.yield)
            if (plant.ripeningPeriod.isNotEmpty()) add("⏱️ Срок созревания" to plant.ripeningPeriod)
            if (plant.soilType.isNotEmpty()) add("🌍 Тип почвы" to plant.soilType)
            if (plant.cultivationMethod.isNotEmpty()) add("🏡 Способ выращивания" to plant.cultivationMethod)
            if (plant.fruitPurpose.isNotEmpty()) add("🎯 Назначение плодов" to plant.fruitPurpose)
            if (plant.taste.isNotEmpty()) add("👅 Вкус" to plant.taste)
            if (plant.vitaminContent.isNotEmpty()) add("💊 Витаминность" to plant.vitaminContent)
        }

        if (mainCharacteristics.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📊 Основные характеристики",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    mainCharacteristics.forEach { (title, value) ->
                        PlantDetailCharacteristicItem(
                            title = title,
                            value = value
                        )
                    }
                }
            }
        }

        // Внешний вид
        val appearanceCharacteristics = buildList {
            if (plant.fruitShape.isNotEmpty()) add("🔶 Форма плодов" to plant.fruitShape)
            if (plant.fruitSize.isNotEmpty()) add("📏 Размер плодов" to plant.fruitSize)
            if (plant.fruitColor.isNotEmpty()) add("🎨 Цвет плодов" to plant.fruitColor)
            if (plant.leafColor.isNotEmpty()) add("🍃 Цвет листьев" to plant.leafColor)
            if (plant.fleshColor.isNotEmpty()) add("🔪 Цвет мякоти" to plant.fleshColor)
            if (plant.growthHabit.isNotEmpty()) add("🌿 Габитус" to plant.growthHabit)
        }

        if (appearanceCharacteristics.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0F8FF)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "👀 Внешний вид",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    appearanceCharacteristics.forEach { (title, value) ->
                        PlantDetailCharacteristicItem(
                            title = title,
                            value = value
                        )
                    }
                }
            }
        }

        // Устойчивость и требования
        val resistanceCharacteristics = buildList {
            if (plant.diseaseResistance.isNotEmpty() && plant.diseaseResistance != "Не указано")
                add("🛡️ Устойчивость к болезням" to plant.diseaseResistance)
            if (plant.frostResistance.isNotEmpty() && plant.frostResistance != "Не указано")
                add("❄️ Устойчивость к заморозкам" to plant.frostResistance)
            if (plant.droughtResistance.isNotEmpty() && plant.droughtResistance != "Не указано")
                add("☀️ Засухоустойчивость" to plant.droughtResistance)
            if (plant.pestResistance.isNotEmpty() && plant.pestResistance != "Не указано")
                add("🐛 Устойчивость к вредителям" to plant.pestResistance)
            if (plant.soilPh.isNotEmpty()) add("🧪 pH почвы" to plant.soilPh)
            if (plant.storageAbility.isNotEmpty()) add("📦 Лежкость" to plant.storageAbility)
        }

        if (resistanceCharacteristics.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "⚙️ Требования и устойчивость",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    resistanceCharacteristics.forEach { (title, value) ->
                        PlantDetailCharacteristicItem(
                            title = title,
                            value = value
                        )
                    }
                }
            }
        }

        // Регионы возделывания
        if (plant.cultivationRegions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🗺️ Регионы возделывания",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = plant.cultivationRegions,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // Дополнительные характеристики
        val additionalCharacteristics = buildList {
            if (plant.lifeForm.isNotEmpty()) add("🌳 Жизненная форма" to plant.lifeForm)
            if (plant.branchingType.isNotEmpty()) add("🌿 Характер ветвления" to plant.branchingType)
        }

        if (additionalCharacteristics.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0F8FF)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📋 Дополнительные характеристики",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    additionalCharacteristics.forEach { (title, value) ->
                        PlantDetailCharacteristicItem(
                            title = title,
                            value = value
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlantDetailCharacteristicItem(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка или эмодзи в заголовке уже включена
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Разделительная линия
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE0E0E0))
        )
    }
}
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