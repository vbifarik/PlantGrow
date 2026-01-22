package com.example.plantgrow.screen.bedDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
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
    val unplantedPlantsWithQuantity by viewModel.unplantedPlantsWithQuantity.collectAsState(initial = emptyList())
    val selectedPlantWithQuantity by viewModel.selectedPlantWithQuantity.collectAsState()
    val selectedTiles by viewModel.selectedTiles.collectAsState()
    val plantedPlants by viewModel.getPlantedPlantsOnGrid().collectAsState(initial = emptyMap())

    // Извлекаем растение и количество
    val selectedPlant = selectedPlantWithQuantity?.first
    val availableQuantity = selectedPlantWithQuantity?.second ?: 0

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
                    modifier = Modifier.padding(bottom = 15.dp),
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
                // Отображаем грядку в виде сетки с выдвижной панелью
                BedGridViewWithSlidingPanel(
                    bed = bed!!,
                    bedPlants = bedPlants,
                    unplantedPlantsWithQuantity = unplantedPlantsWithQuantity,
                    selectedPlant = selectedPlant,
                    availableQuantity = availableQuantity,
                    selectedTiles = selectedTiles,
                    plantedPlants = plantedPlants,
                    onPlantSelect = { plantWithQuantity ->
                        val current = viewModel.selectedPlantWithQuantity.value
                        if (current?.first?.id == plantWithQuantity.first.id) {
                            viewModel.selectPlant(null)
                        } else {
                            viewModel.selectPlant(plantWithQuantity)
                        }
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
                    },
                    onDeleteClick = { bedPlantId ->
                        viewModel.removePlantFromBed(bedPlantId)
                    }
                )
            }
        }
    }
}

@Composable
fun BedGridViewWithSlidingPanel(
    bed: Bed,
    bedPlants: List<BedPlantWithPlant>,
    unplantedPlantsWithQuantity: List<Pair<Plant, Int>>,
    selectedPlant: Plant?,
    availableQuantity: Int,
    selectedTiles: Set<Pair<Int, Int>>,
    plantedPlants: Map<Pair<Int, Int>, Plant>,
    onPlantSelect: (Pair<Plant, Int>) -> Unit,
    onTileClick: (Int, Int) -> Unit,
    onPlantClick: () -> Unit,
    onClearTiles: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPanelVisible by remember { mutableStateOf(true) }
    val panelWidth by animateDpAsState(
        targetValue = if (isPanelVisible) 300.dp else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Основная сетка грядки - занимает всё пространство
        BedGridView(
            bed = bed,
            bedPlants = bedPlants,
            selectedTiles = selectedTiles,
            plantedPlants = plantedPlants,
            availableQuantity = availableQuantity,
            onTileClick = onTileClick,
            selectedPlant = selectedPlant,
            onPlantClick = onPlantClick,
            onClearTiles = onClearTiles,
            modifier = Modifier
                .fillMaxSize()
                .padding(end = if (isPanelVisible) 300.dp else 0.dp)
        )

        // Панель растений - поверх сетки
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(panelWidth)
                .align(Alignment.TopEnd)
                .zIndex(1f)
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        bottomStart = 12.dp
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        bottomStart = 12.dp
                    )
                )
                .animateContentSize()
        ) {
            if (isPanelVisible) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    // Заголовок панели
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF5E7A3C))
                            .padding(vertical = 12.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val totalPlants = unplantedPlantsWithQuantity.sumOf { it.second }
                        Text(
                            text = "🌱 Не посажены ($totalPlants шт.)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    if (unplantedPlantsWithQuantity.isEmpty()) {
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
                                    textAlign = TextAlign.Center,
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
                            items(unplantedPlantsWithQuantity, key = { it.first.id }) { (plant, quantity) ->
                                UnplantedPlantItem(
                                    plant = plant,
                                    quantity = quantity,
                                    isSelected = selectedPlant?.id == plant.id,
                                    onClick = { onPlantSelect(plant to quantity) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Кнопка переключения панели - всегда видна
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = if (isPanelVisible) (-300).dp else 0.dp)
                .zIndex(2f)
        ) {
            FloatingActionButton(
                onClick = { isPanelVisible = !isPanelVisible },
                containerColor = Color(0xFF5E7A3C),
                contentColor = Color.White,
                modifier = Modifier
                    .size(width = 48.dp, height = 64.dp) // Увеличиваем высоту для текста
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(4.dp)
                ) {
                    // Показываем стрелку в виде текста
                    Text(
                        text = if (isPanelVisible) "◀" else "▶",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Текстовое описание
                    Text(
                        text = if (isPanelVisible) "Скрыть" else "Растения",
                        fontSize = 10.sp,
                        maxLines = 2,
                        textAlign = TextAlign.Center,
                        lineHeight = 10.sp
                    )
                }
            }
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
    availableQuantity: Int,
    onPlantClick: () -> Unit,
    onClearTiles: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridSizeX = bed.tileX
    val gridSizeY = bed.tileY
    val tileSize = 50.dp

    val scrollStateH = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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

                    // Показываем информацию о количестве
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📊 Доступно: $availableQuantity растений",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "📌 Выбрано: ${selectedTiles.size} клеток",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2196F3)
                        )
                    }

                    // Предупреждение если выбрано слишком много клеток
                    if (selectedTiles.size > availableQuantity) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚠️ Нельзя выбрать больше клеток чем растений!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF44336),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (availableQuantity > 0) {
                        Text(
                            text = "👇 Выберите клетки для посадки (максимум $availableQuantity)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "⚠️ Нет доступных растений для посадки",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF44336),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Контейнер с горизонтальной прокруткой для сетки
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((gridSizeY * tileSize.value + 40).dp)
                .horizontalScroll(scrollStateH)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(8.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                // Координаты X (горизонтальные)
                Row(
                    modifier = Modifier.padding(start = 20.dp)
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
                                fontSize = 10.sp,
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
                                .size(20.dp)
                                .padding(end = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$y",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Клетки для этой строки
                        for (x in 1..gridSizeX) {
                            val coordinates = Pair(x, y)
                            val isSelected = selectedTiles.contains(coordinates)
                            val plantedPlant = plantedPlants[coordinates]

                            // Проверяем, можно ли выбрать эту клетку
                            val canSelect = selectedPlant != null &&
                                    availableQuantity > 0 &&
                                    selectedTiles.size < availableQuantity &&
                                    !plantedPlants.containsKey(coordinates)

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
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = when {
                                            plantedPlant != null -> Color(0xFF4CAF50)
                                            isSelected -> Color(0xFF689F38)
                                            !canSelect && selectedPlant != null -> Color(0xFF9E9E9E)
                                            else -> Color(0xFFE0E0E0)
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable(enabled = canSelect || isSelected) {
                                        if (canSelect || isSelected) {
                                            onTileClick(x, y)
                                        }
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
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = plant.name.take(6) + if (plant.name.length > 6) "..." else "",
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B5E20),
                                            maxLines = 2,
                                            lineHeight = 8.sp
                                        )
                                    }
                                } ?: run {
                                    if (isSelected) {
                                        Text(
                                            text = "✓",
                                            fontSize = 18.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else if (!canSelect && selectedPlant != null) {
                                        Text(
                                            text = "✗",
                                            fontSize = 14.sp,
                                            color = Color(0xFF9E9E9E),
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

        Spacer(modifier = Modifier.height(12.dp))

        // Статистика и управление
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                        text = "✅ Выбрано: ${selectedTiles.size}/${availableQuantity} клеток",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (selectedTiles.isNotEmpty()) {
                    Button(
                        onClick = onClearTiles,
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "🗑️", fontSize = 14.sp)
                            Text(
                                text = "Очистить",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Кнопка посадки - показываем только если можно посадить
        if (selectedPlant != null && selectedTiles.isNotEmpty() && selectedTiles.size <= availableQuantity && availableQuantity > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onPlantClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5E7A3C),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                enabled = selectedTiles.isNotEmpty() && selectedTiles.size <= availableQuantity
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "🌱",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ПОСАДИТЬ '${selectedPlant.name.take(15)}${if (selectedPlant.name.length > 15) "..." else ""}'",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "на ${selectedTiles.size} из $availableQuantity клеток",
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        } else if (selectedPlant != null && selectedTiles.size > availableQuantity) {
            // Показываем предупреждение если выбрано слишком много клеток
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                ),
                border = BorderStroke(1.dp, Color(0xFFF44336))
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚠️ Выбрано слишком много клеток! У вас только $availableQuantity растений",
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Легенда
        if (gridSizeX <= 10) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF9F9F9)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                        LegendItem(
                            color = Color(0xFFF5F5F5),
                            borderColor = Color(0xFF9E9E9E),
                            text = "Заблок."
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
fun UnplantedPlantItem(
    plant: Plant,
    quantity: Int,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
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
                }

                // Показываем количество
                Text(
                    text = "×$quantity",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else Color(0xFF4CAF50)
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✔ Доступно для посадки: $quantity растений",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

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
    onPlantClick: (Plant) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(bedPlants, key = { it.bedPlant.id }) { bedPlantWithPlant ->
            BedPlantCard(
                bedPlantWithPlant = bedPlantWithPlant,
                onPlantClick = { onPlantClick(bedPlantWithPlant.plant) },
                onDeleteClick = { onDeleteClick(bedPlantWithPlant.bedPlant.id) }
            )
        }
    }
}

@Composable
fun BedPlantCard(
    bedPlantWithPlant: BedPlantWithPlant,
    onPlantClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Диалог подтверждения удаления
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Удаление растения",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Вы точно хотите удалить растение?")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "'${bedPlantWithPlant.plant.name}'",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (bedPlantWithPlant.bedPlant.posX != null && bedPlantWithPlant.bedPlant.posY != null) {
                        Text(
                            text = "⚠️ Растение посажено на клетке ${bedPlantWithPlant.bedPlant.posX},${bedPlantWithPlant.bedPlant.posY}",
                            color = Color(0xFFF44336),
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFF44336)
                    )
                ) {
                    Text("УДАЛИТЬ", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("ОТМЕНА")
                }
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Основная информация о растении
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onPlantClick)
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
                    Text(
                        text = "🌱",
                        fontSize = 24.sp
                    )
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

            // Панель с кнопкой удаления
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🗑️", fontSize = 14.sp)
                        Text(
                            text = "Удалить",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}