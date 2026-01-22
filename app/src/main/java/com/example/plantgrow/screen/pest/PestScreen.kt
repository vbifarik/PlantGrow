package com.example.plantgrow.screen.pest

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.plantgrow.data.pest.Pest
import com.example.plantgrow.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PestScreen(viewModel: PestViewModel = hiltViewModel(), navController: NavController) {
    val pests by viewModel.pests.collectAsStateWithLifecycle(initialValue = emptyList())
    val message = remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Вредители",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5E7A3C),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Вернуться назад")
                    }
                    Button(
                        onClick = {
                            viewModel.populatePestDatabase()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8BC34A)
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Добавить вредителей", fontSize = 14.sp)
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)){
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    modifier = Modifier.fillMaxWidth()
                        .weight(10f),
                    value = message.value,
                    onValueChange = {newText -> message.value = newText})
                IconButton(onClick = {viewModel.updateSearchQuery(message.value)},
                    modifier = Modifier.weight(2f),) {
                    Icon(Icons.Filled.Search, contentDescription = "Поиск вредителей")
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()
            ) {
                items(pests, key = { it.id }) { pest ->
                    PestCard(pest = pest,
                        onClick = {
                            navController.navigate(Screens.PestDetail.createRoute(pest.id))
                        }
                    )
                }
            }
        }
    }

}
@Composable
fun PestCard(
    pest: Pest,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                text = "🐛",
                fontSize = 32.sp,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pest.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = pest.description.take(50) + if (pest.description.length > 50) "..." else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}