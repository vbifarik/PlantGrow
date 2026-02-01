package com.example.plantgrow.screen.plantCategory

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.plantgrow.ImageResourceHelper
import com.example.plantgrow.R
import com.example.plantgrow.data.plant.PlantCategory
import com.example.plantgrow.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantCategoryScreen(
    viewModel: PlantCategoryViewModel = hiltViewModel(),
    navController: NavController,
    bedId: Int?
) {
    val plantCategories by viewModel.filteredPlantCategories.collectAsStateWithLifecycle(initialValue = emptyList())
    var isLoading by remember { mutableStateOf(true) }
    val search = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Категории растений",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5E7A3C),
                    titleContentColor = Color.White
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    modifier = Modifier.fillMaxWidth()
                        .weight(10f),
                    value = search.value,
                    onValueChange = {newText -> search.value = newText})
                IconButton(onClick = {viewModel.updateSearchQuery(search.value)},
                    modifier = Modifier.weight(2f),) {
                    Icon(Icons.Filled.Search, contentDescription = "Поиск категории растения")
                }
            }
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5E7A3C))
                }
            } else if (plantCategories.isEmpty()) {
                EmptyCategoryScreen()
            } else {
                PlantCategoriesList(
                    categories = plantCategories,
                    onCategoryClick = { category ->
                        if (bedId != null) {
                            navController.navigate(Screens.PlantByCategoryWithBed.createRoute(bedId, category.genus))
                        } else {
                            navController.navigate(Screens.PlantByCategory.createRoute(category.genus))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PlantCategoriesList(
    categories: List<PlantCategory>,
    onCategoryClick: (PlantCategory) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(categories, key = { it.genus }) { category ->
            PlantCategoryCard(
                category = category,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantCategoryCard(
    category: PlantCategory,
    onClick: () -> Unit
) {
    val imageResId = ImageResourceHelper.getImageResIdByGenus(category.genus)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                Image(
                    painter = rememberAsyncImagePainter(
                        model = imageResId,
                    ),
                    contentDescription = category.genus,
                    modifier = Modifier.fillMaxSize(0.65f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.genus,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${category.plantCount} ${getPlantCountText(category.plantCount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "→",
                    fontSize = 20.sp,
                    color = Color(0xFF5E7A3C)
                )
            }
        }
    }
}

private fun getPlantCountText(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "растение"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "растения"
        else -> "растений"
    }
}

@Composable
fun EmptyCategoryScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📂",
            fontSize = 80.sp,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Категории не найдены",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Заполните базу данных растений",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}