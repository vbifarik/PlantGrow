package com.example.plantgrow.screen.bedDetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.plantgrow.data.bed.Bed
import com.example.plantgrow.data.bedplant.BedPlantWithPlant
import com.example.plantgrow.data.plant.Plant
import com.example.plantgrow.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BedDetailScreen(
    viewModel: BedDetailViewModel = hiltViewModel(),
    navController: NavController
) {
    val bed by viewModel.bed.collectAsState()
    val bedPlants by viewModel.bedPlants.collectAsState(initial = emptyList())
    val unplantedPlants by viewModel.unplantedPlants.collectAsState(initial = emptyList())
    val selectedPlant by viewModel.selectedPlant.collectAsState()
    val selectedTiles by viewModel.selectedTiles.collectAsState()
    val plantedPlants by viewModel.getPlantedPlantsOnGrid().collectAsState(initial = emptyMap())

    var isLoading by remember { mutableStateOf(true) }
    var showGridView by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        bed?.name ?: "Грядка",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5E7A3C),
                    titleContentColor = Color.White
                ),
                actions = {
                    Button(
                        onClick = { showGridView = !showGridView },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8BC34A)
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (showGridView) "📋 Список" else "🧱 Сетка",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    bed?.let {
                        navController.navigate(Screens.PlantCategoryWithBed.createRoute(it.id))
                    }
                },
                containerColor = Color(0xFF5E7A3C),
                contentColor = Color.White
            ) {
                Text(
                    text = "Добавить",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
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
            } else if (bed == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Грядка не найдена")
                }
            } else if (showGridView) {
                // Отображаем грядку в виде сетки с боковой панелью
                BedGridViewWithSidebar(
                    bed = bed!!,
                    bedPlants = bedPlants,
                    unplantedPlants = unplantedPlants,
                    selectedPlant = selectedPlant,
                    selectedTiles = selectedTiles,
                    plantedPlants = plantedPlants,
                    onPlantSelect = { plant ->
                        viewModel.selectPlant(if (selectedPlant?.id == plant.id) null else plant)
                    },
                    onTileClick = { x, y ->
                        viewModel.selectTile(x, y)
                    },
                    onPlantClick = {
                        viewModel.plantSelectedPlant()
                    },
                    onClearTiles = {
                        viewModel.clearSelectedTiles()
                    }
                )
            } else if (bedPlants.isEmpty()) {
                EmptyBedDetailScreen(
                    bedName = bed!!.name,
                    onAddClick = {
                        navController.navigate(Screens.PlantCategory.route)
                    }
                )
            } else {
                BedPlantsList(
                    bedPlants = bedPlants,
                    onPlantClick = { plant ->
                        navController.navigate(Screens.PlantDetail.createRoute(plant.id))
                    }
                )
            }
        }
    }
}

@Composable
fun BedGridViewWithSidebar(
    bed: Bed,
    bedPlants: List<BedPlantWithPlant>,
    unplantedPlants: List<Plant>,
    selectedPlant: Plant?,
    selectedTiles: Set<Pair<Int, Int>>,
    plantedPlants: Map<Pair<Int, Int>, Plant>,
    onPlantSelect: (Plant) -> Unit,
    onTileClick: (Int, Int) -> Unit,
    onPlantClick: () -> Unit,
    onClearTiles: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Основная сетка грядки с горизонтальной прокруткой (75%)
        Box(
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight()
                .padding(end = 8.dp)
        ) {
            BedGridView(
                bed = bed,
                bedPlants = bedPlants,
                selectedTiles = selectedTiles,
                plantedPlants = plantedPlants,
                onTileClick = onTileClick,
                selectedPlant = selectedPlant,
                onPlantClick = onPlantClick,
                onClearTiles = onClearTiles
            )
        }

        // Боковая панель с растениями (25%)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            UnplantedPlantsPanel(
                unplantedPlants = unplantedPlants,
                selectedPlant = selectedPlant,
                onPlantSelect = onPlantSelect
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BedGridView(
    bed: Bed,
    bedPlants: List<BedPlantWithPlant>,
    selectedTiles: Set<Pair<Int, Int>>,
    plantedPlants: Map<Pair<Int, Int>, Plant>,
    onTileClick: (Int, Int) -> Unit,
    selectedPlant: Plant?,
    onPlantClick: () -> Unit,
    onClearTiles: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridSizeX = bed.tileX
    val gridSizeY = bed.tileY
    val tileSize = 60.dp

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Информация о выбранном растении
        selectedPlant?.let { plant ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "✅ Выбрано: ${plant.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "👇 Выберите клетки для посадки",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Контейнер с горизонтальной прокруткой для сетки
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((gridSizeY * tileSize.value + 50).dp)
                .horizontalScroll(scrollState)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(8.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                // Координаты X (горизонтальные)
                Row(
                    modifier = Modifier.padding(start = 25.dp)
                ) {
                    for (x in 1..gridSizeX) {
                        Box(
                            modifier = Modifier
                                .size(tileSize)
                                .padding(1.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$x",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Сама сетка с координатами Y
                for (y in 1..gridSizeY) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Координата Y (вертикальная)
                        Box(
                            modifier = Modifier
                                .size(25.dp)
                                .padding(end = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$y",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Клетки для этой строки
                        for (x in 1..gridSizeX) {
                            val coordinates = Pair(x, y)
                            val isSelected = selectedTiles.contains(coordinates)
                            val plantedPlant = plantedPlants[coordinates]

                            Box(
                                modifier = Modifier
                                    .size(tileSize)
                                    .padding(1.dp)
                                    .background(
                                        color = when {
                                            plantedPlant != null -> Color(0xFFC8E6C9)
                                            isSelected -> Color(0xFF8BC34A)
                                            else -> Color(0xFFF5F5F5)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = when {
                                            plantedPlant != null -> Color(0xFF4CAF50)
                                            isSelected -> Color(0xFF689F38)
                                            else -> Color(0xFFE0E0E0)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        onTileClick(x, y)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Отображаем посаженное растение
                                plantedPlant?.let { plant ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "🌱",
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = plant.name.take(8) + if (plant.name.length > 8) "..." else "",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B5E20),
                                            maxLines = 2,
                                            lineHeight = 9.sp
                                        )
                                    }
                                } ?: run {
                                    if (isSelected && selectedPlant != null) {
                                        Text(
                                            text = "✓",
                                            fontSize = 20.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Статистика и управление
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "🧱 Грядка ${gridSizeX}x${gridSizeY}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1B5E20),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "🌱 Посажено: ${plantedPlants.size} клеток",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50)
                )
                Text(
                    text = "✅ Выбрано клеток: ${selectedTiles.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (selectedTiles.isNotEmpty()) {
                    Text(
                        text = "🗑️ Очистить выбор",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5E7A3C),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onClearTiles() }
                            .padding(4.dp)
                    )
                }

                // Инструкция по прокрутке
                Text(
                    text = "↔️ Прокручивайте в стороны",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }

        // Кнопка посадки
        if (selectedPlant != null && selectedTiles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF5E7A3C))
                    .clickable(onClick = onPlantClick),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🌱",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Посадить '${selectedPlant.name}' на ${selectedTiles.size} клеток",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Легенда (опционально, можно убрать если места мало)
        if (gridSizeX <= 10) { // Показываем легенду только если сетка небольшая
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF9F9F9)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "📋 Легенда:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LegendItem(
                            color = Color(0xFFF5F5F5),
                            borderColor = Color(0xFFE0E0E0),
                            text = "Пустая"
                        )
                        LegendItem(
                            color = Color(0xFF8BC34A),
                            borderColor = Color(0xFF689F38),
                            text = "Выбрана"
                        )
                        LegendItem(
                            color = Color(0xFFC8E6C9),
                            borderColor = Color(0xFF4CAF50),
                            text = "Посажена"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(
    color: Color,
    borderColor: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(4.dp))
                .border(1.dp, borderColor, RoundedCornerShape(4.dp))
        )
        Text(
            text = text,
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun UnplantedPlantsPanel(
    unplantedPlants: List<Plant>,
    selectedPlant: Plant?,
    onPlantSelect: (Plant) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Заголовок панели
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF5E7A3C))
                .padding(vertical = 12.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🌱 Не посажены (${unplantedPlants.size})",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        if (unplantedPlants.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎉",
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Все растения посажены!",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(unplantedPlants, key = { it.id }) { plant ->
                    UnplantedPlantItem(
                        plant = plant,
                        isSelected = selectedPlant?.id == plant.id,
                        onClick = { onPlantSelect(plant) }
                    )
                }
            }
        }
    }
}

@Composable
fun UnplantedPlantItem(
    plant: Plant,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF8BC34A) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = plant.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF1B5E20),
                maxLines = 2
            )

            plant.mainGenus?.let { genus ->
                Text(
                    text = genus,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) Color.White.copy(alpha = 0.9f)
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✔ Выбрано для посадки",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

// Остальные функции остаются без изменений...
@Composable
fun EmptyBedDetailScreen(
    bedName: String,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🪴",
            fontSize = 80.sp,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "На грядке '$bedName' пока нет растений",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Нажмите + чтобы добавить первое растение",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BedPlantsList(
    bedPlants: List<BedPlantWithPlant>,
    onPlantClick: (Plant) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(bedPlants, key = { it.bedPlant.id }) { bedPlantWithPlant ->
            BedPlantCard(
                bedPlantWithPlant = bedPlantWithPlant,
                onPlantClick = { onPlantClick(bedPlantWithPlant.plant) }
            )
        }
    }
}

@Composable
fun BedPlantCard(
    bedPlantWithPlant: BedPlantWithPlant,
    onPlantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onPlantClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bedPlantWithPlant.plant.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Количество: ${bedPlantWithPlant.bedPlant.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (bedPlantWithPlant.bedPlant.plantingDate.isNotEmpty()) {
                    Text(
                        text = "Посажено: ${bedPlantWithPlant.bedPlant.plantingDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Показываем координаты, если растение посажено на сетке
                if (bedPlantWithPlant.bedPlant.posX != null && bedPlantWithPlant.bedPlant.posY != null) {
                    Text(
                        text = "📍 Позиция: ${bedPlantWithPlant.bedPlant.posX},${bedPlantWithPlant.bedPlant.posY}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5E7A3C),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}