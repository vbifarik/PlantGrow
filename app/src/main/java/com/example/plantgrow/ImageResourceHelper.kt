package com.example.plantgrow

object ImageResourceHelper {
    fun getImageResIdByGenus(genus: String?): Int {
        return when (genus) {
            "Картофель" -> R.drawable.ic_potato
            "Томат" -> R.drawable.ic_tomato
            "Перец сладкий", "Перец" -> R.drawable.ic_pepper
            "Перец острый", "Перец жгучий" -> R.drawable.ic_chili
            "Огурец" -> R.drawable.ic_cucumber
            "Капуста белокочанная", "Капуста" -> R.drawable.ic_cabbage
            "Капуста цветная" -> R.drawable.ic_cauliflower
            else -> R.drawable.ic_chili
        }
    }
}
