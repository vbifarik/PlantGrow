package com.example.plantgrow.data.plant

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.plantgrow.data.bed.Bed

@Entity(
    tableName = "plants",
    indices = [Index("mainGenus"), Index("name")]
)
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val imageUrl: String? = null,

    val mainGenus: String = "", // Основной род
    val yield: String = "", // Урожайность
    val ripeningPeriod: String = "", // Срок созревания
    val soilType: String = "", // Тип почвы
    val cultivationMethod: String = "", // Способ выращивания
    val fruitPurpose: String = "", // Назначение плодов
    val diseaseResistance: String = "", // Устойчивость к болезням
    val soilPh: String = "", // Требования к pH почвы
    val lifeForm: String = "", // Жизненная форма
    val fruitShape: String = "", // Форма плодов
    val fruitSize: String = "", // Размер плодов
    val cultivationRegions: String = "", // Регионы возделывания
    val vitaminContent: String = "", // Витаминность
    val fruitColor: String = "", // Цвет плодов
    val leafColor: String = "", // Цвет листьев
    val fleshColor: String = "", // Цвет мякоти
    val taste: String = "", // Вкус плодов
    val storageAbility: String = "", // Лежкость
    val frostResistance: String = "Не указано",
    val droughtResistance: String = "Не указано",
    val pestResistance: String = "Не указано",
    val growthHabit: String = "", // Габитус
    val branchingType: String = "", // Характер ветвления
    val fleshDensity: String = "" // Плотность мякоти
)
