package com.example.plantgrow.screen.bed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.plantgrow.data.bed.Bed
import com.example.plantgrow.navigation.Screens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BedScreen(
    navController: NavController,
    viewModel: BedViewModel = hiltViewModel()
) {
    val beds by viewModel.beds.collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var newBedName by remember { mutableStateOf("") }
    var bedTileX by remember { mutableStateOf("10") }
    var bedTileY by remember { mutableStateOf("10") }

    // Состояние для отображения статуса заполнения БД
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Мои грядки",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5E7A3C),
                    titleContentColor = Color.White
                ),
                actions = {
                    // Кнопка для заполнения БД
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    viewModel.populatePlantsDatabase()
                                    showSuccessMessage = true
                                } catch (e: Exception) {
                                    showErrorMessage = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8BC34A)
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("🌱", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Добавить растения", fontSize = 14.sp)
                    }
                }
            )
        },
        floatingActionButton = {
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5E7A3C)
                )
            ) {
                Text("+", fontSize = 24.sp, color = Color.White)
            }
        },
        bottomBar = {
            BottomNavigationBarWithEmoji(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (beds.isEmpty()) {
                EmptyBedScreen(
                    modifier = Modifier.weight(1f)
                )
            } else {
                BedList(
                    beds = beds,
                    modifier = Modifier.weight(1f),
                    navController = navController
                )
            }
        }

        // Диалог добавления грядки
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Новая грядка") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newBedName,
                            onValueChange = { newBedName = it },
                            label = { Text("Название грядки") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = bedTileX,
                            onValueChange = { bedTileX = it },
                            label = { Text("Размер по x") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = bedTileY,
                            onValueChange = { bedTileY = it },
                            label = { Text("Размер по н") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newBedName.isNotBlank()) {
                                scope.launch {
                                    viewModel.addBed(newBedName, bedTileX.toInt(), bedTileY.toInt())
                                    newBedName = ""
                                    showAddDialog = false
                                }
                            }
                        }
                    ) {
                        Text("Добавить")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showAddDialog = false }
                    ) {
                        Text("Отмена")
                    }
                }
            )
        }

        // Диалог успешного заполнения БД
        if (showSuccessMessage) {
            AlertDialog(
                onDismissRequest = { showSuccessMessage = false },
                title = { Text("✅ Успешно") },
                text = { Text("База данных растений успешно заполнена!") },
                confirmButton = {
                    TextButton(
                        onClick = { showSuccessMessage = false }
                    ) {
                        Text("ОК")
                    }
                }
            )
        }

        // Диалог ошибки
        if (showErrorMessage) {
            AlertDialog(
                onDismissRequest = { showErrorMessage = false },
                title = { Text("❌ Ошибка") },
                text = { Text("Не удалось заполнить базу данных растений") },
                confirmButton = {
                    TextButton(
                        onClick = { showErrorMessage = false }
                    ) {
                        Text("ОК")
                    }
                }
            )
        }
    }
}

@Composable
fun EmptyBedScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🌿",
            fontSize = 80.sp,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "У вас пока нет грядок",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Нажмите + чтобы добавить первую",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BedList(
    beds: List<Bed>,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(beds, key = { it.id }) { bed ->
            BedCard(bed = bed,
                onClick = {
                navController.navigate(Screens.BedDetail.createRoute(bed.id))
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BedCard(bed: Bed, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🌿",
                fontSize = 32.sp,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bed.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = "ID: ${bed.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBarWithEmoji(navController: NavController) {
    NavigationBar(
        containerColor = Color(0xFF5E7A3C)
    ) {
        // Грядки
        NavigationBarItem(
            icon = {
                Text("🌿", fontSize = 24.sp, color = Color.White)
            },
            label = {
                Text(
                    "Грядки",
                    color = Color.White,
                    fontSize = 12.sp
                )
            },
            selected = true,
            onClick = { /* Уже на этом экране */ }
        )

        // Вредители
        NavigationBarItem(
            icon = {
                Text("🐛", fontSize = 24.sp, color = Color.White)
            },
            label = {
                Text(
                    "Вредители",
                    color = Color.White,
                    fontSize = 12.sp
                )
            },
            selected = false,
            onClick = { navController.navigate(Screens.Pest.route) }
        )

        // Растения
        NavigationBarItem(
            icon = {
                Text("🌱", fontSize = 24.sp, color = Color.White)
            },
            label = {
                Text(
                    "Растения",
                    color = Color.White,
                    fontSize = 12.sp
                )
            },
            selected = false,
            onClick = { navController.navigate(Screens.PlantCategory.route) } // Без bedId
        )
    }
}