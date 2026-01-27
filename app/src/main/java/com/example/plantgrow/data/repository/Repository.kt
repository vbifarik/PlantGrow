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
    suspend fun populateInitialPlantsData() {

        // Создаем список растений для заполнения
        val popularVegetables = listOf(
            Plant(
                name = "Картофель 'Адретта'",
                description = "Немецкий сорт картофеля с желтой мякотью и отличным вкусом.",
                imageUrl = "https://avatars.mds.yandex.net/i?id=66d2b8f13184b4453f2098661231abad_l-4555484-images-thumbs&n=13",
                mainGenus = "Картофель",
                yield = "высокая",
                ripeningPeriod = "среднеранний",
                soilType = "суглинки/глина",
                cultivationMethod = "открытый грунт",
                fruitPurpose = "столовый",
                diseaseResistance = "средняя",
                soilPh = "слабокислая (ph 5.5 - 6.5)",
                lifeForm = "однолетник",
                fruitShape = "овальная",
                fruitSize = "крупные",
                cultivationRegions = "Центральный, Северо-Западный",
                fruitColor = "Желтый",
                fleshColor = "Желтый",
                taste = "отличный",
                storageAbility = "высокая",
                frostResistance = "средняя"
            ),

            Plant(
                name = "Картофель 'Ред Скарлет'",
                description = "Ранний сорт с красной кожурой и желтой мякотью.",
                imageUrl = "https://main-cdn.sbermegamarket.ru/big2/hlr-system/165/672/168/041/522/17/100066868936b1.jpg",
                mainGenus = "Картофель",
                yield = "очень высокая",
                ripeningPeriod = "ранний",
                soilType = "супесчаная",
                cultivationMethod = "открытый грунт",
                fruitPurpose = "столовый",
                diseaseResistance = "высокая",
                soilPh = "слабокислая (ph 5.5 - 6.5)",
                lifeForm = "однолетник",
                fruitShape = "удлиненно-овальная",
                fruitSize = "крупные",
                cultivationRegions = "Центральный, Центрально-Черноземный",
                fruitColor = "Красный",
                fleshColor = "Желтый",
                taste = "хороший",
                storageAbility = "высокая"
            ),

            Plant(
                name = "Картофель 'Гала'",
                description = "Немецкий сорт с высокой урожайностью и устойчивостью к болезням.",
                imageUrl = "https://example.com/images/potato_gala.jpg",
                mainGenus = "Картофель",
                yield = "очень высокая",
                ripeningPeriod = "ранний",
                soilType = "суглинки",
                cultivationMethod = "открытый грунт",
                fruitPurpose = "столовый",
                diseaseResistance = "очень высокая",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "округлая",
                fruitSize = "средние",
                cultivationRegions = "все регионы",
                fruitColor = "Желтый",
                fleshColor = "Желтый",
                taste = "хороший",
                storageAbility = "высокая"
            ),

            Plant(
                name = "Картофель 'Удача'",
                description = "Ранний российский сорт с белой мякотью и хорошей лежкостью.",
                imageUrl = "https://example.com/images/potato_udacha.jpg",
                mainGenus = "Картофель",
                yield = "высокая",
                ripeningPeriod = "ранний",
                soilType = "любая",
                cultivationMethod = "открытый грунт",
                fruitPurpose = "столовый",
                diseaseResistance = "средняя",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "овальная",
                fruitSize = "крупные",
                cultivationRegions = "Центральный, Средневолжский, Дальневосточный",
                fruitColor = "Светло-бежевый",
                fleshColor = "Белый",
                taste = "хороший",
                storageAbility = "высокая"
            ),

            // ТОМАТЫ (еще 4 сорта)
            Plant(
                name = "Томат 'Бычье сердце'",
                description = "Крупноплодный сорт с сердцевидными плодами и отличным вкусом.",
                imageUrl = "https://example.com/images/tomato_bull_heart.jpg",
                mainGenus = "Томат",
                yield = "средняя",
                ripeningPeriod = "средний",
                soilType = "суглинки",
                cultivationMethod = "открытый грунт, теплица",
                fruitPurpose = "салатный",
                diseaseResistance = "средняя",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "сердцевидная",
                fruitSize = "очень крупные",
                cultivationRegions = "Центральный, Центрально-Черноземный",
                vitaminContent = "высокая",
                fruitColor = "Розовый",
                leafColor = "Зеленый",
                fleshColor = "Розовый",
                taste = "отличный, сладкий",
                growthHabit = "индетерминатные",
                storageAbility = "низкая",
            ),

            Plant(
                name = "Томат 'Черри Блосэм'",
                description = "Кистевой сорт черри томатов с мелкими сладкими плодами.",
                imageUrl = "https://example.com/images/tomato_cherry_blossom.jpg",
                mainGenus = "Томат",
                yield = "очень высокая",
                ripeningPeriod = "ранний",
                soilType = "супесчаная",
                cultivationMethod = "открытый грунт, теплица",
                fruitPurpose = "для салатов, консервации",
                diseaseResistance = "высокая",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "округлая",
                fruitSize = "мелкие",
                cultivationRegions = "все регионы (в теплицах)",
                vitaminContent = "высокая",
                fruitColor = "Красный",
                taste = "очень сладкий",
                growthHabit = "индетерминатные",
                branchingType = "индетерминатные",
            ),

            Plant(
                name = "Томат 'Де Барао'",
                description = "Высокорослый сорт с обильным плодоношением до осени.",
                imageUrl = "https://example.com/images/tomato_de_barao.jpg",
                mainGenus = "Томат",
                yield = "очень высокая",
                ripeningPeriod = "поздний",
                soilType = "суглинки",
                cultivationMethod = "теплица",
                fruitPurpose = "универсальное",
                diseaseResistance = "высокая",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "сливовидная",
                fruitSize = "средние",
                cultivationRegions = "все регионы (в теплицах)",
                vitaminContent = "высокая",
                fruitColor = "Красный",
                taste = "кисло-сладкий",
                growthHabit = "индетерминатные",
                storageAbility = "средняя",
            ),

            Plant(
                name = "Томат 'Розовый гигант'",
                description = "Крупноплодный розовый томат с сахарной мякотью.",
                imageUrl = "https://example.com/images/tomato_pink_giant.jpg",
                mainGenus = "Томат",
                yield = "средняя",
                ripeningPeriod = "средний",
                soilType = "суглинки",
                cultivationMethod = "теплица",
                fruitPurpose = "салатный",
                diseaseResistance = "средняя",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "плоско-округлая",
                fruitSize = "очень крупные",
                cultivationRegions = "Центральный, Центрально-Черноземный",
                vitaminContent = "очень высокая",
                fruitColor = "Розовый",
                fleshColor = "Розовый",
                taste = "очень сладкий",
                growthHabit = "индетерминатные",
            ),

            // ПЕРЦЫ (еще 4 сорта)
            Plant(
                name = "Перец 'Богатырь'",
                description = "Крупноплодный сорт сладкого перца с конусовидными плодами.",
                imageUrl = "https://example.com/images/pepper_bogatyr.jpg",
                mainGenus = "Перец сладкий",
                yield = "очень высокая",
                ripeningPeriod = "средний",
                soilType = "суглинки",
                cultivationMethod = "открытый грунт, теплица",
                fruitPurpose = "универсальное",
                diseaseResistance = "высокая",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "конусовидная",
                fruitSize = "крупные",
                cultivationRegions = "Центральный, Центрально-Черноземный",
                vitaminContent = "очень высокая",
                fruitColor = "Красный",
                taste = "сладкий",
                storageAbility = "средняя",
            ),

            Plant(
                name = "Перец 'Огонек'",
                description = "Острый перец с мелкими плодами и высокой жгучестью.",
                imageUrl = "https://example.com/images/pepper_ogonek.jpg",
                mainGenus = "Перец острый",
                yield = "высокая",
                ripeningPeriod = "средний",
                soilType = "супесчаная",
                cultivationMethod = "открытый грунт, горшечная культура",
                fruitPurpose = "пряность",
                diseaseResistance = "очень высокая",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "конусовидная",
                fruitSize = "мелкие",
                cultivationRegions = "все регионы",
                fruitColor = "Красный",
                taste = "очень острый",
                storageAbility = "высокая",
            ),

            Plant(
                name = "Перец 'Здоровье'",
                description = "Ранний сорт сладкого перца для открытого грунта.",
                imageUrl = "https://example.com/images/pepper_zdorovie.jpg",
                mainGenus = "Перец сладкий",
                yield = "высокая",
                ripeningPeriod = "ранний",
                soilType = "супесчаная",
                cultivationMethod = "открытый грунт",
                fruitPurpose = "универсальное",
                diseaseResistance = "высокая",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "призмовидная",
                fruitSize = "средние",
                cultivationRegions = "Центральный, Северо-Западный",
                fruitColor = "Красный",
                taste = "сладкий",
                frostResistance = "низкая",
            ),

            Plant(
                name = "Перец 'Черный кардинал'",
                description = "Декоративный сорт с черно-фиолетовыми плодами.",
                imageUrl = "https://example.com/images/pepper_black_cardinal.jpg",
                mainGenus = "Перец сладкий",
                yield = "средняя",
                ripeningPeriod = "средний",
                soilType = "супесчаная",
                cultivationMethod = "открытый грунт, декоративный",
                fruitPurpose = "декоративный, салатный",
                diseaseResistance = "средняя",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "конусовидная",
                fruitSize = "средние",
                cultivationRegions = "Центральный, Южные регионы",
                fruitColor = "Черно-фиолетовый",
                taste = "сладкий",
            ),

            // ОГУРЦЫ (еще 4 сорта)
            Plant(
                name = "Огурец 'Конкурент'",
                description = "Пчелоопыляемый сорт для открытого грунта с отличными засолочными качествами.",
                imageUrl = "https://example.com/images/cucumber_konkurent.jpg",
                mainGenus = "Огурец",
                yield = "высокая",
                ripeningPeriod = "ранний",
                soilType = "суглинки",
                cultivationMethod = "открытый грунт",
                fruitPurpose = "для засолки",
                diseaseResistance = "высокая",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "цилиндрическая",
                fruitSize = "средние",
                cultivationRegions = "Центральный, Северо-Кавказский",
                fruitColor = "Зеленый",
                taste = "хороший",
                storageAbility = "низкая",
            ),

            Plant(
                name = "Огурец 'Герман F1'",
                description = "Партенокарпический гибрид с пучковым плодоношением.",
                imageUrl = "https://example.com/images/cucumber_german.jpg",
                mainGenus = "Огурец",
                yield = "очень высокая",
                ripeningPeriod = "ранний",
                soilType = "супесчаная",
                cultivationMethod = "теплица, открытый грунт",
                fruitPurpose = "универсальное",
                diseaseResistance = "очень высокая",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "цилиндрическая",
                fruitSize = "мелкие",
                cultivationRegions = "все регионы",
                fruitColor = "Темно-зеленый",
                taste = "отличный, без горечи",
            ),

            Plant(
                name = "Огурец 'Нежинский'",
                description = "Классический засолочный сорт с отличным вкусом.",
                imageUrl = "https://example.com/images/cucumber_nezhensky.jpg",
                mainGenus = "Огурец",
                yield = "высокая",
                ripeningPeriod = "средний",
                soilType = "суглинки",
                cultivationMethod = "открытый грунт",
                fruitPurpose = "для засолки",
                diseaseResistance = "средняя",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "удлиненно-яйцевидная",
                fruitSize = "средние",
                cultivationRegions = "Центрально-Черноземный, Северо-Кавказский",
                fruitColor = "Зеленый",
                taste = "отличный",
                storageAbility = "низкая",
            ),

            Plant(
                name = "Огурец 'Муромский 36'",
                description = "Скороспелый сорт для открытого грунта, один из самых ранних.",
                imageUrl = "https://example.com/images/cucumber_muromsky.jpg",
                mainGenus = "Огурец",
                yield = "средняя",
                ripeningPeriod = "очень ранний",
                soilType = "супесчаная",
                cultivationMethod = "открытый грунт",
                fruitPurpose = "салатный, для засолки",
                diseaseResistance = "средняя",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "яйцевидная",
                fruitSize = "мелкие",
                cultivationRegions = "Центральный, Северо-Западный",
                fruitColor = "Светло-зеленый",
                taste = "хороший",
                frostResistance = "средняя",
            ),

            // КАПУСТА (еще 4 сорта)
            Plant(
                name = "Капуста 'Июньская'",
                description = "Раннеспелая белокочанная капуста для летнего потребления.",
                imageUrl = "https://example.com/images/cabbage_june.jpg",
                mainGenus = "Капуста белокочанная",
                yield = "средняя",
                ripeningPeriod = "ранний",
                soilType = "суглинки",
                cultivationMethod = "открытый грунт",
                fruitPurpose = "салатный",
                diseaseResistance = "средняя",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "округлая",
                fruitSize = "средние",
                cultivationRegions = "все регионы",
                vitaminContent = "высокая",
                taste = "нежный",
                storageAbility = "низкая",
            ),

            Plant(
                name = "Капуста 'Московская поздняя'",
                description = "Поздний сорт с очень крупными кочанами и высокой лежкостью.",
                imageUrl = "https://example.com/images/cabbage_moscow_late.jpg",
                mainGenus = "Капуста белокочанная",
                yield = "очень высокая",
                ripeningPeriod = "поздний",
                soilType = "суглинки/глина",
                cultivationMethod = "открытый грунт",
                fruitPurpose = "для квашения, хранения",
                diseaseResistance = "высокая",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "округлая",
                fruitSize = "очень крупные",
                cultivationRegions = "Центральный, Северо-Западный",
                vitaminContent = "высокая",
                taste = "отличный",
                storageAbility = "очень высокая",
            ),

            Plant(
                name = "Капуста цветная 'Снежный шар'",
                description = "Ранний сорт цветной капусты с плотными белыми головками.",
                imageUrl = "https://example.com/images/cauliflower_snowball.jpg",
                mainGenus = "Капуста цветная",
                yield = "средняя",
                ripeningPeriod = "ранний",
                soilType = "суглинки",
                cultivationMethod = "открытый грунт",
                fruitPurpose = "кулинарный",
                diseaseResistance = "средняя",
                soilPh = "нейтральная (ph 6.5-7)",
                lifeForm = "однолетник",
                fruitShape = "округлая",
                fruitSize = "средние",
                cultivationRegions = "Центральный, Центрально-Черноземный",
                vitaminContent = "очень высокая",
                fruitColor = "Белый",
                taste = "нежный",
                storageAbility = "низкая",
            )
        )
        popularVegetables.forEach { plant ->
            plantDao.insert(plant)
        }
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