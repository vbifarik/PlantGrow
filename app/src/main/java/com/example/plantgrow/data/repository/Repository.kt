package com.example.plantgrow.data.repository

import com.example.plantgrow.data.bed.Bed
import com.example.plantgrow.data.bed.BedDao
import com.example.plantgrow.data.bedplant.BedPlant
import com.example.plantgrow.data.bedplant.BedPlantDao
import com.example.plantgrow.data.bedplant.BedPlantWithPlant
import com.example.plantgrow.data.pest.Pest
import com.example.plantgrow.data.pest.PestDao
import com.example.plantgrow.data.plant.GenusWithCount
import com.example.plantgrow.data.plant.Plant
import com.example.plantgrow.data.plant.PlantCategory
import com.example.plantgrow.data.plant.PlantDao
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class GardenRepository @Inject constructor(
    private val bedDao: BedDao,
    private val plantDao: PlantDao,
    private val pestDao: PestDao,
    private val bedPlantDao: BedPlantDao
) {
    fun getAllBeds(): Flow<List<Bed>> = bedDao.getAllBeds()
    suspend fun getBedById(bedId: Int): Bed? = bedDao.getBedById(bedId)
    suspend fun insertBed(bed: Bed): Long = bedDao.insert(bed)
    suspend fun updateBed(bed: Bed) = bedDao.update(bed)
    suspend fun deleteBed(bed: Bed) = bedDao.delete(bed.id)

    // === Методы для растений на грядках ===
    suspend fun addPlantToBed(bedPlant: BedPlant): Long = bedPlantDao.insert(bedPlant)
    suspend fun removePlantFromBed(bedPlantId: Int) = bedPlantDao.deleteById(bedPlantId)
    suspend fun updateBedPlant(bedPlant: BedPlant) = bedPlantDao.update(bedPlant)
    suspend fun getBedPlantById(bedPlantId: Int): BedPlant? = bedPlantDao.getBedPlantById(bedPlantId)
    suspend fun getBedPlant(bedId: Int, plantId: Int): BedPlant? = bedPlantDao.getBedPlant(bedId, plantId)
    fun getBedPlantsWithPlants(bedId: Int): Flow<List<BedPlantWithPlant>> = bedPlantDao.getBedPlantsWithPlants(bedId)

    suspend fun isPlantPlantedOnBed(bedId: Int, plantId: Int): Boolean {
        return bedPlantDao.getBedPlant(bedId, plantId) != null
    }

    suspend fun getPlantQuantityOnBed(bedId: Int, plantId: Int): Int {
        return bedPlantDao.getBedPlant(bedId, plantId)?.quantity ?: 0
    }

    // === Методы для растений ===
    fun getAllPlants(): Flow<List<Plant>> = plantDao.getAllPlants()
    suspend fun getPlantById(plantId: Int): Plant? = plantDao.getPlantById(plantId)
    suspend fun insertPlant(plant: Plant): Long = plantDao.insert(plant)
    suspend fun updatePlant(plant: Plant) = plantDao.update(plant)
    suspend fun deletePlant(plant: Plant) = plantDao.delete(plant)
    suspend fun searchPlants(searchQuery: String): List<Plant> = plantDao.searchPlants(searchQuery)
    suspend fun getPlantsByGenus(genus: String): List<Plant> = plantDao.getPlantsByGenus(genus)
    fun getPlantGenus(): Flow<List<PlantCategory>> = plantDao.getPlantGenus()

    // === Методы для вредителей ===
    fun getAllPests(): Flow<List<Pest>> = pestDao.getAllPests()
    suspend fun getPestById(pestId: Int): Pest? = pestDao.getPestById(pestId)

    suspend fun getAllGenera(): List<String> = plantDao.getAllGenera()
    suspend fun getGeneraWithCount(): List<GenusWithCount> = plantDao.getGeneraWithCount()

    fun getUnplantedPlants(bedId: Int): Flow<List<Plant>> {
        return plantDao.getUnplantedPlants(bedId)
    }
    suspend fun getPlantedCountOnGrid(bedId: Int, plantId: Int): Int {
        return bedPlantDao.getPlantedCountOnGrid(bedId, plantId)
    }
    suspend fun getAllBedPlantsForPlant(bedId: Int, plantId: Int): List<BedPlant> {
        return bedPlantDao.getAllBedPlantsForPlant(bedId, plantId)
    }
    // Удалить запись растения без позиции
    suspend fun removeUnplantedBedPlant(bedId: Int, plantId: Int) {
        bedPlantDao.deleteUnplantedBedPlant(bedId, plantId)
    }
    suspend fun populatePests(){
        val pests = listOf(
            Pest(
                name = "Колорадский жук",
                description = "Листоед, питающийся листьями паслёновых культур: картофеля, томата, баклажана.",
                prevention = "Ручной сбор жуков и личинок, использование инсектицидов, посадка растений-репеллентов.",
                image = "https://example.com/images/colorado_beetle.jpg"
            ),
            Pest(
                name = "Тля",
                description = "Мелкие насекомые, высасывающие соки из растений, вызывая деформацию листьев и распространение вирусов.",
                prevention = "Привлечение божьих коровок, опрыскивание мыльным раствором, удаление пораженных частей.",
                image = "https://example.com/images/aphid.jpg"
            ),
            Pest(
                name = "Паутинный клещ",
                description = "Микроскопический клещ, питающийся соками растений, образует паутину на нижней стороне листьев.",
                prevention = "Повышение влажности, опрыскивание водой, применение акарицидов.",
                image = "https://example.com/images/spider_mite.jpg"
            ),
            Pest(
                name = "Белокрылка",
                description = "Мелкие белые насекомые, обитающие на нижней стороне листьев, высасывающие соки растений.",
                prevention = "Желтые клеевые ловушки, опрыскивание инсектицидами, удаление зараженных растений.",
                image = "https://example.com/images/whitefly.jpg"
            ),
            Pest(
                name = "Слизни",
                description = "Брюхоногие моллюски без раковины, питающиеся листьями, стеблями и плодами растений.",
                prevention = "Установка ловушек с пивом, посыпание дорожек золой или яичной скорлупой, ручной сбор.",
                image = "https://example.com/images/slug.jpg"
            ),
            Pest(
                name = "Медведка",
                description = "Крупное насекомое, роющее ходы в почве и повреждающее корни растений.",
                prevention = "Ловушки с навозом, глубокое перекапывание почвы, применение специальных препаратов.",
                image = "https://example.com/images/mole_cricket.jpg"
            ),
            Pest(
                name = "Капустная белянка",
                description = "Бабочка, гусеницы которой питаются листьями капусты и других крестоцветных.",
                prevention = "Сбор гусениц вручную, использование сеток, опрыскивание биопрепаратами.",
                image = "https://example.com/images/cabbage_butterfly.jpg"
            ),
            Pest(
                name = "Проволочник",
                description = "Личинки жука-щелкуна, повреждающие клубни, корнеплоды и подземные части растений.",
                prevention = "Внесение извести в кислые почвы, посадка бобовых культур, применение приманок.",
                image = "https://example.com/images/wireworm.jpg"
            ),
            Pest(
                name = "Морковная муха",
                description = "Муха, личинки которой прогрызают ходы в корнеплодах моркови и других зонтичных.",
                prevention = "Смешанные посадки с луком, ранний посев, укрытие нетканым материалом.",
                image = "https://example.com/images/carrot_fly.jpg"
            ),
            Pest(
                name = "Крестоцветная блошка",
                description = "Мелкие прыгающие жуки, выедающие дырки в листьях капусты, редиса, редьки.",
                prevention = "Регулярный полив, опудривание золой или табачной пылью, укрытие агроволокном.",
                image = "https://example.com/images/flea_beetle.jpg"
            ),
            Pest(
                name = "Картофельная моль",
                description = "Бабочка, гусеницы которой повреждают клубни картофеля как в поле, так и при хранении.",
                prevention = "Глубокое окучивание, своевременная уборка урожая, обработка клубней перед хранением.",
                image = "https://example.com/images/potato_moth.jpg"
            ),
            Pest(
                name = "Трипсы",
                description = "Мелкие насекомые, высасывающие соки растений, оставляющие серебристые пятна на листьях.",
                prevention = "Синие клеевые ловушки, опрыскивание инсектицидами, поддержание высокой влажности.",
                image = "https://example.com/images/thrips.jpg"
            ),
            Pest(
                name = "Капустная совка",
                description = "Ночная бабочка, гусеницы которой выедают отверстия в листьях капусты и других овощей.",
                prevention = "Глубокая перекопка почвы, сбор гусениц вручную, применение биопрепаратов.",
                image = "https://example.com/images/cabbage_moth.jpg"
            ),
            Pest(
                name = "Яблонная плодожорка",
                description = "Бабочка, гусеницы которой повреждают плоды яблони, груши, сливы.",
                prevention = "Накладка ловчих поясов, сбор падалицы, обработка инсектицидами в период лёта.",
                image = "https://example.com/images/codling_moth.jpg"
            ),
            Pest(
                name = "Листовертка",
                description = "Бабочка, гусеницы которой скручивают листья растений, питаясь ими изнутри.",
                prevention = "Обрезка поврежденных листьев, применение феромонных ловушек, обработка инсектицидами.",
                image = "https://example.com/images/leaf_roller.jpg"
            ),
            Pest(
                name = "Щитовка",
                description = "Мелкие насекомые, покрытые щитком, высасывающие соки из стеблей и листьев растений.",
                prevention = "Обработка мыльно-спиртовым раствором, механическое удаление, применение инсектицидов.",
                image = "https://example.com/images/scale_insect.jpg"
            ),
            Pest(
                name = "Мучнистый червец",
                description = "Насекомые, покрытые белым восковым налетом, высасывающие соки растений.",
                prevention = "Обработка спиртовым раствором, повышение влажности, применение системных инсектицидов.",
                image = "https://example.com/images/mealybug.jpg"
            ),
            Pest(
                name = "Нематода",
                description = "Микроскопические черви, поражающие корни растений, вызывая образование галлов.",
                prevention = "Севооборот, посадка бархатцев, использование устойчивых сортов, обработка почвы.",
                image = "https://example.com/images/nematode.jpg"
            ),
            Pest(
                name = "Саранча",
                description = "Крупные насекомые, стаи которых могут полностью уничтожить растительность на больших площадях.",
                prevention = "Глубокая вспашка, использование инсектицидов, установка защитных барьеров.",
                image = "https://example.com/images/locust.jpg"
            ),
            Pest(
                name = "Хрущ",
                description = "Личинки майского жука, повреждающие корни деревьев, кустарников и овощных культур.",
                prevention = "Перекопка почвы с удалением личинок, посадка люпина, применение инсектицидов.",
                image = "https://example.com/images/chafer.jpg"
            )
        )
        for (pest in pests){
            pestDao.insert(pest)
        }
    }
}