package com.example.play_6sem.data

import com.example.play_6sem.model.DetectiveCase
import com.example.play_6sem.model.MathLevelConfig
import com.example.play_6sem.model.MathTask
import com.example.play_6sem.model.WordLevelConfig
import com.example.play_6sem.model.WordStarRule
import kotlin.random.Random

object GameContent {
    val wordLevels = listOf(
        WordLevelConfig(
            id = 1,
            mainWord = "РАЗВЕДЧИК",
            words = listOf(
                "ДЕВИЗ", "ДЕВА", "ЗАВ", "РАД", "ЧЕРДАК", "ЗЕВА", "ЗЕВ", "РАЗ",
                "КАВЕР", "ИКРА", "КЕД", "РАК", "АКР", "ЧЕРВА", "РЕКА", "КИР",
                "РЕВ", "ДЕВКА", "ВАДИ", "ВИД", "КИЧ", "ЧЕК", "ВИЗА", "ДАР",
                "КРИ", "ЧИР"
            ),
            starRules = listOf(
                WordStarRule("Найти 10 слов", foundCount = 10),
                WordStarRule("Найти 22 слова", foundCount = 22),
                WordStarRule("Найти 3 слова на букву В", startsWith = 'В', startsWithCount = 3)
            ),
            ruleText = "Составляйте новые слова из букв исходного слова. Каждую плитку можно использовать только один раз."
        ),
        WordLevelConfig(
            id = 2,
            mainWord = "МНОЖИТЕЛЬ",
            words = listOf(
                "ЕЛЬ", "ЖИМ", "ИОН", "ЛЕН", "ЛОМ", "ЛОТ", "ЛЬЕ", "МЕЛ", "МОЛ",
                "МОТ", "НОЖ", "ТОЛ", "ТОН", "ЕНОТ", "ЖИТО", "ЖМОТ", "ЛЕНЬ",
                "ЛЕТО", "ЛИНЬ", "ЛОЖЕ", "ЛОЖЬ", "МЕЛЬ", "МОЛЬ", "НИТЬ",
                "ТЕЛО", "ТЕНЬ", "ТЛЕН", "ТМИН", "ТОЛЬ", "ЖЕЛЬТЬ", "ЖЕТОН",
                "ЖИЛЕТ", "ЖИЛЬЕ", "ЖИТЬЕ", "ЛИМОН", "МЕТИЛ", "ОЛЕНЬ", "ОМЛЕТ",
                "ОТЕЛЬ", "ОТЖИМ", "ЖИТЕЛЬ", "МЕНТОЛ", "МОТЕЛЬ", "ОТМЕЛЬ",
                "МНОЖИТЕЛЬ"
            ),
            starRules = listOf(
                WordStarRule("Найти 12 слов", foundCount = 12),
                WordStarRule("Найти 25 слов", foundCount = 25),
                WordStarRule("Найти 4 слова на букву Т", startsWith = 'Т', startsWithCount = 4)
            ),
            ruleText = "Слова засчитываются автоматически, когда собранное слово есть в списке уровня."
        ),
        WordLevelConfig(
            id = 3,
            mainWord = "РАСПУТНИК",
            words = listOf(
                "АКР", "АКТ", "ИСК", "КАП", "КАР", "КИТ", "КУН", "ПАК", "ПАН",
                "ПАР", "ПАС", "ПИК", "ПИР", "ПУК", "РАК", "РИС", "САП", "СПА",
                "СУК", "СУП", "ТИК", "ТИП", "ТИР", "ТУР", "АИСТ", "ИКРА",
                "КАНТ", "КАПР", "КАРП", "КАРТ", "КИПА", "КНУТ", "КРАН", "КРАП",
                "КУРС", "КУСТ", "НАСТ", "ПАКТ", "ПАНК", "ПАРИ", "ПАРК", "ПАРС",
                "ПАУК", "ПИКА", "ПИРС", "ПИСК", "ПИТА", "ПРУС", "ПРУТ", "ПУСК",
                "РАПС", "РАУС", "РИСК", "РУКА", "РУНА", "РУСТ", "САНИ", "СКАТ",
                "СКИП", "СТАН", "СТУК", "СУКА", "СУРА", "ТАНК", "ТИНА", "ТРАК",
                "ТРАП", "ТРУП", "ТРУС", "УРАН", "УРНА", "УСТА", "УТКА", "АНТИК",
                "ИСКРА", "КАРСТ", "КИСТА", "КРУПА", "НИТКА", "ПАРИК", "ПИКАН",
                "ПИНТА", "ПИРАТ", "ПУАНТ", "ПУНКТ", "РИСКА", "РУИНА", "РУТИН",
                "САНКИ", "САПУН", "САТИН", "САТИР", "СКАУТ", "СКРИП", "СПИНА",
                "СПИРТ", "СПРУТ", "СТРУП", "СТУПА", "ТАКСИ", "ТАПКИ", "ТИПУН",
                "ТИРАН", "ТРАНС", "УРИНА", "НАТИСК", "ПАРНИК", "ПИАСТР", "ПУТИНА",
                "РАТНИК", "РУТИНА", "СПРИНТ", "СТРУНА", "РАСПУТНИК"
            ),
            starRules = listOf(
                WordStarRule("Найти 17 слов", foundCount = 17),
                WordStarRule("Найти 61 слово", foundCount = 61),
                WordStarRule("Найти 8 слов на букву К", startsWith = 'К', startsWithCount = 8)
            ),
            ruleText = "Сохраняйте найденные слова: при повторном входе они останутся на поле."
        ),
        WordLevelConfig(
            id = 4,
            mainWord = "ЯЗЫЧЕСТВО",
            words = listOf(
                "ВЕС", "ВОЗ", "ВЯЗ", "ЗОВ", "СЕВ", "СЕТ", "СОЯ", "СЫЧ", "ЧЕС",
                "ВЕТО", "ОВЕС", "СВЕТ", "СВОЗ", "ВЫЧЕТ", "ОТВЕС", "ОТЗЫВ",
                "ОТСЕВ", "СОВЕТ", "СОЗЫВ", "СВЕТОЧ", "ЯЗЫЧЕСТВО"
            ),
            starRules = listOf(
                WordStarRule("Найти 5 слов", foundCount = 5),
                WordStarRule("Найти 13 слов", foundCount = 13),
                WordStarRule("Найти 4 слова на букву С", startsWith = 'С', startsWithCount = 4)
            ),
            ruleText = "Следующий уровень откроется, когда получите хотя бы одну звезду."
        )
    )

    fun generateMathLevel(level: Int): MathLevelConfig {
        return when (level) {
            1 -> MathLevelConfig(
                id = 1,
                title = "Верный выбор",
                description = "Сложение и вычитание однозначных чисел. 4 варианта ответа, 10 секунд на пример. Пройти: 4 из 5.",
                tasks = (1..5).map {
                    val a = Random.nextInt(1, 10)
                    val b = Random.nextInt(1, 10)
                    if (Random.nextBoolean()) MathTask("$a + $b", a + b) else MathTask("$a - $b", a - b)
                },
                perTaskTimeSec = 10,
                lives = 2,
                manualInput = false,
                requiredCorrect = 4,
                scoreOnPass = 40
            )

            2 -> MathLevelConfig(
                id = 2,
                title = "Быстрый спринт",
                description = "4 варианта ответа, 7 секунд на пример. 2 жизни: вторая ошибка завершает уровень.",
                tasks = (1..5).map {
                    val a = Random.nextInt(2, 20)
                    val b = Random.nextInt(1, 16)
                    if (Random.nextBoolean()) MathTask("$a + $b", a + b) else MathTask("$a - $b", a - b)
                },
                perTaskTimeSec = 7,
                lives = 2,
                manualInput = false,
                requiredCorrect = 4,
                scoreOnPass = 50
            )

            3 -> MathLevelConfig(
                id = 3,
                title = "Ручной ввод",
                description = "Введите ответ с клавиатуры. 6 примеров, 7 секунд на пример. Пройти: 5 из 6.",
                tasks = (1..6).map {
                    val a = Random.nextInt(10, 40)
                    val b = Random.nextInt(5, 25)
                    if (Random.nextBoolean()) MathTask("$a + $b", a + b) else MathTask("$a - $b", a - b)
                },
                perTaskTimeSec = 7,
                lives = 2,
                manualInput = true,
                requiredCorrect = 5,
                scoreOnPass = 150
            )

            4 -> MathLevelConfig(
                id = 4,
                title = "Два действия",
                description = "Решите выражение с приоритетом умножения. Ручной ввод, 10 секунд на пример.",
                tasks = (1..5).map {
                    val a = Random.nextInt(4, 15)
                    val b = Random.nextInt(2, 8)
                    val c = Random.nextInt(2, 6)
                    MathTask("$a + $b × $c", a + b * c)
                },
                perTaskTimeSec = 10,
                lives = 2,
                manualInput = true,
                requiredCorrect = 4,
                scoreOnPass = 170
            )

            5 -> MathLevelConfig(
                id = 5,
                title = "Большие числа",
                description = "Одно действие с двузначными и трехзначными числами. Ручной ввод, 15 секунд на пример.",
                tasks = (1..5).map {
                    val a = Random.nextInt(20, 250)
                    val b = Random.nextInt(10, 160)
                    if (Random.nextBoolean()) MathTask("$a + $b", a + b) else MathTask("$a - $b", a - b)
                },
                perTaskTimeSec = 15,
                lives = 2,
                manualInput = true,
                requiredCorrect = 4,
                scoreOnPass = 220
            )

            6 -> MathLevelConfig(
                id = 6,
                title = "Найди пропущенное",
                description = "Введите число вместо пропуска. 7 примеров, 12 секунд на пример.",
                tasks = (1..7).map {
                    val missing = Random.nextInt(-20, 80)
                    val total = Random.nextInt(20, 120)
                    MathTask("${total - missing} + __ = $total", missing)
                },
                perTaskTimeSec = 12,
                lives = 2,
                manualInput = true,
                requiredCorrect = 5,
                scoreOnPass = 230
            )

            else -> MathLevelConfig(
                id = 7,
                title = "Верно или нет",
                description = "Оцените готовое утверждение. 10 утверждений, 4 секунды на каждое. Пройти: 9 из 10.",
                tasks = (1..10).map {
                    val b = Random.nextInt(2, 10)
                    val answer = Random.nextInt(3, 13)
                    val a = b * answer
                    val shown = if (Random.nextBoolean()) answer else answer + Random.nextInt(1, 4)
                    MathTask("$a ÷ $b = $shown", if (shown == answer) 1 else 0, shownAnswer = shown)
                },
                perTaskTimeSec = 4,
                lives = 3,
                manualInput = false,
                requiredCorrect = 9,
                scoreOnPass = 100,
                maxSpeedScore = 5,
                correctBonus = 8,
                errorPenalty = 10
            )
        }
        return when (level) {
            1 -> MathLevelConfig(
                id = 1,
                title = "Верный выбор",
                tasks = (1..5).map {
                    val a = Random.nextInt(1, 10)
                    val b = Random.nextInt(1, 10)
                    if (Random.nextBoolean()) MathTask("$a + $b", a + b) else MathTask("$a - $b", a - b)
                },
                perTaskTimeSec = 10,
                lives = 2,
                manualInput = false,
                requiredCorrect = 4,
                scoreOnPass = 40
            )

            2 -> MathLevelConfig(
                id = 2,
                title = "Быстрый спринт",
                tasks = (1..5).map {
                    val a = Random.nextInt(2, 15)
                    val b = Random.nextInt(2, 15)
                    MathTask("$a + $b", a + b)
                },
                perTaskTimeSec = 7,
                lives = 2,
                manualInput = false,
                requiredCorrect = 4,
                scoreOnPass = 50
            )

            3 -> MathLevelConfig(
                id = 3,
                title = "Ручной ввод",
                tasks = (1..6).map {
                    val a = Random.nextInt(10, 40)
                    val b = Random.nextInt(5, 20)
                    MathTask("$a + $b", a + b)
                },
                perTaskTimeSec = 7,
                lives = 2,
                manualInput = true,
                requiredCorrect = 5,
                scoreOnPass = 150
            )

            4 -> MathLevelConfig(
                id = 4,
                title = "Два действия",
                tasks = (1..5).map {
                    val a = Random.nextInt(4, 15)
                    val b = Random.nextInt(2, 8)
                    val c = Random.nextInt(2, 6)
                    MathTask("$a + $b × $c", a + b * c)
                },
                perTaskTimeSec = 10,
                lives = 2,
                manualInput = true,
                requiredCorrect = 4,
                scoreOnPass = 170
            )

            else -> MathLevelConfig(
                id = 5,
                title = "Смешанный марафон",
                tasks = (1..7).map {
                    val a = Random.nextInt(20, 150)
                    val b = Random.nextInt(10, 80)
                    if (Random.nextBoolean()) MathTask("$a + $b", a + b) else MathTask("$a - $b", a - b)
                },
                perTaskTimeSec = 12,
                lives = 2,
                manualInput = true,
                requiredCorrect = 6,
                scoreOnPass = 220
            )
        }
    }

    val detectiveCases = listOf(
        DetectiveCase(
            id = 1,
            title = "Яблоко на перемене",
            story = "В классе пропало красное яблоко со стола учителя. Лена сказала: «Я выходила за мелом и видела Диму у двери». Дима ответил: «Я заглянул только за телефоном и сразу ушел». Коля уверял: «Я вообще не был в классе после звонка». Через минуту он добавил: «Яблоко было кислым, его никто бы не захотел».",
            options = listOf("Лена", "Дима", "Коля"),
            correctIndex = 2,
            explanation = "Коля выдал себя: он утверждал, что не был в классе, но знал вкус яблока.",
            scoreOnPass = 40
        ),
        DetectiveCase(
            id = 2,
            title = "Та же чашка кофе",
            story = "Посетитель кафе нашел в кофе муху и попросил заменить напиток. Официант быстро принес новую чашку. Посетитель попробовал кофе и сразу сказал: «Вы принесли тот же самый». Чашка была чистой, мухи уже не было, температура почти не изменилась.",
            options = listOf("По температуре", "Кофе был уже сладким", "По форме чашки"),
            correctIndex = 1,
            explanation = "До жалобы посетитель положил сахар в кофе. «Новая» чашка тоже оказалась сладкой.",
            scoreOnPass = 50
        ),
        DetectiveCase(
            id = 3,
            title = "Восемь колокольчиков",
            story = "На вывеске магазина написано «Семь колокольчиков», но над дверью висят восемь настоящих колокольчиков. Покупатели постоянно заходят и говорят владельцу об ошибке. Он благодарит их, улыбается и ничего не меняет уже третий месяц.",
            options = listOf("Он не умеет считать", "Это привлекает покупателей", "Так требуют правила улицы"),
            correctIndex = 1,
            explanation = "Ошибка специально заметная: люди заходят сообщить о ней и заодно смотрят товары.",
            scoreOnPass = 60
        ),
        DetectiveCase(
            id = 4,
            title = "Сон сторожа",
            story = "Ночной сторож прибежал к начальнику склада и сказал, что видел во сне пожар в дальнем ангаре. Начальник проверил ангар и нашел замыкание в щитке. Пожар предотвратили, но утром сторожа уволили.",
            options = listOf("Несправедливо", "Справедливо: он спал на смене", "Справедливо: он опоздал"),
            correctIndex = 1,
            explanation = "Сторож спас склад, но признался, что спал во время ночной смены.",
            scoreOnPass = 70
        )
    )
}
