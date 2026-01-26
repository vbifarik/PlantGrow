package com.example.plantgrow.data.plant

data class PlantGenusResult(
    val genus: String,
    val plantCount: Int
)

data class PlantCategory(
    val genus: String,
    val plantCount: Int,
    val iconEmoji: String
) {
    companion object {
        fun getEmojiForGenus(genus: String): String {
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
    }
}