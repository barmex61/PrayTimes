package com.fatih.prayertime.util.utils

import com.fatih.prayertime.domain.model.JuzInfo

object QuranUtils {

    // Lazy loading ile juzList'i sadece kullanıldığında oluştur
    val juzList by lazy {
        listOf(
            JuzInfo(1, "Al-Fatiha - Al-Baqarah", "1-141"),
            JuzInfo(2, "Al-Baqarah", "142-252"),
            JuzInfo(3, "Al-Baqarah - Al-i İmran","sd"),
            JuzInfo(4, "Al-i İmran - An-Nisa", "1-23"),
            JuzInfo(5, "An-Nisa", "24-50"),
            JuzInfo(6, "An-Nisa - Al-Ma'idah", "51-87"),
            JuzInfo(7, "Al-Ma'idah - En'am", "88-120"),
            JuzInfo(8, "En'am - Al-A'raf", "1-41"),
            JuzInfo(9, "Al-A'raf -En-fal", "42-70"),
            JuzInfo(10, "En-fal - Tevbe", "1-58"),
            JuzInfo(11, "Tevbe - Hud", "59-104"),
            JuzInfo(12, "Hud - Yusuf", "105-153"),
            JuzInfo(13, "Yusuf - İbrahim", "1-38"),
            JuzInfo(14, "El-Hicr - Nahl", "39-59"),
            JuzInfo(15, "El-isra - Kehf", "60-120"),
            JuzInfo(16, "Kehf - Ta-ha", "1-53"),
            JuzInfo(17, "El-enbiya - Hac", "54-119"),
            JuzInfo(18, "El-mü'minun - Furkan", "1-29"),
            JuzInfo(19, "Furkan - Neml", "30-71"),
            JuzInfo(20, "Neml - Ankebut", "1-58"),
            JuzInfo(21, "Ankebut - Ahzab", "59-119"),
            JuzInfo(22, "Ahzab - Yasin", "1-59"),
            JuzInfo(23, "Yasin - Zümer", "60-114"),
            JuzInfo(24, "Zumer - Fussilet", "1-59"),
            JuzInfo(25, "Fussilet -  Casiye", "60-114"),
            JuzInfo(26, "Casiye - Zariyat", "1-60"),
            JuzInfo(27, "Zariyat - Hadid", "61-130"),
            JuzInfo(28, "El-mücadele - Tahrim", "1-30"),
            JuzInfo(29, "Mülk - Murselat", "31-73"),
            JuzInfo(30, "Nebe' - Nas", "1-30")
        )
    }

    // Türkçe sure isimleri için lazy loading
    val turkishNames by lazy {
        mapOf(
            "Al-Faatiha" to "Fâtiha Sûresi",
            "Al-Baqara" to "Bakara Sûresi",
            "Aal-i-Imraan" to "Âl-i İmrân Sûresi",
            "An-Nisaa" to "Nisâ Sûresi",
            "Al-Maaida" to "Mâide Sûresi " ,
            "Al-An'aam" to "En'âm Sûresi",
            "Al-A'raaf" to "A'râf Sûresi",
            "Al-Anfaal" to "Enfâl Sûresi",
            "At-Tawba" to "Tevbe Sûresi",
            "Yunus" to "Yunus Sûresi",
            "Hud" to "Hûd Sûresi",
            "Yusuf" to "Yusuf Sûresi",
            "Ar-Ra'd" to "Ra'd Sûresi",
            "Ibrahim" to "İbrâhim Sûresi",
            "Al-Hijr" to "Hicr Sûresi",
            "An-Nahl" to "Nahl Sûresi",
            "Al-Israa" to "İsrâ Sûresi",
            "Al-Kahf" to "Kehf Sûresi",
            "Maryam" to "Meryem Sûresi",
            "Taa-Haa" to "Tâhâ Sûresi",
            "Al-Anbiyaa" to "Enbiyâ Sûresi",
            "Al-Hajj" to "Hac Sûresi",
            "Al-Muminoon" to "Mü'minûn Sûresi",
            "An-Noor" to "Nûr Sûresi",
            "Al-Furqaan" to "Furkan Sûresi",
            "Ash-Shu'araa" to "Şuarâ Sûresi",
            "An-Naml" to "Neml Sûresi",
            "Al-Qasas" to "Kasas Sûresi",
            "Al-Ankaboot" to "Ankebût Sûresi",
            "Ar-Room" to "Rûm Sûresi",
            "Luqman" to "Lokman Sûresi",
            "As-Sajda" to "Secde Sûresi",
            "Al-Ahzaab" to "Ahzâb Sûresi",
            "Saba" to "Sebe' Sûresi",
            "Faatir" to "Fâtır Sûresi",
            "Yaseen" to "Yâsîn Sûresi",
            "As-Saaffaat" to "Sâffât Sûresi",
            "Saad" to "Sâd Sûresi",
            "Az-Zumar" to "Zümer Sûresi",
            "Ghafir" to "Mü'min Sûresi",
            "Fussilat" to "Fussilet Sûresi",
            "Ash-Shura" to "Şûrâ Sûresi",
            "Az-Zukhruf" to "Zühruf Sûresi",
            "Ad-Dukhaan" to "Duhân Sûresi",
            "Al-Jaathiya" to "Câsiye Sûresi",
            "Al-Ahqaf" to "Ahkâf Sûresi",
            "Muhammad" to "Muhammed Sûresi",
            "Al-Fath" to "Fetih Sûresi",
            "Al-Hujuraat" to "Hucûrât Sûresi",
            "Qaaf" to "Kâf Sûresi",
            "Adh-Dhaariyat" to "Zâriyat Sûresi",
            "At-Tur" to "Tûr Sûresi",
            "An-Najm" to "Necm Sûresi",
            "Al-Qamar" to "Kamer Sûresi",
            "Ar-Rahmaan" to "Rahmân Sûresi",
            "Al-Waaqia" to "Vâkıa Sûresi",
            "Al-Hadid" to "Hadid Sûresi",
            "Al-Mujaadila" to "Mücadele Sûresi",
            "Al-Hashr" to "Haşr Sûresi",
            "Al-Mumtahana" to "Mümtehine Sûresi",
            "As-Saff" to "Saf Sûresi",
            "Al-Jumu'a" to "Cum'a Sûresi",
            "Al-Munaafiqoon" to "Münafıkun Sûresi",
            "At-Taghaabun" to "Tegâbun Sûresi",
            "At-Talaaq" to "Talak Sûresi",
            "At-Tahrim" to "Tahrim Sûresi",
            "Al-Mulk" to "Mülk Sûresi",
            "Al-Qalam" to "Kalem Sûresi",
            "Al-Haaqqa" to "Hâkka Sûresi",
            "Al-Ma'aarij" to "Me'aric Sûresi",
            "Nooh" to "Nuh Sûresi",
            "Al-Jinn" to "Cin Sûresi",
            "Al-Muzzammil" to "Müzzemmil Sûresi",
            "Al-Muddaththir" to "Müddessir Sûresi",
            "Al-Qiyaama" to "Kıyamet Sûresi",
            "Al-Insaan" to "İnsan Sûresi",
            "Al-Mursalaat" to "Mursalat Sûresi",
            "An-Naba" to "Nebe' Sûresi",
            "An-Naazi'aat" to "Naziyat Sûresi",
            "Abasa" to "Abese Sûresi",
            "At-Takwir" to "Tekvir Sûresi",
            "Al-Infitaar" to "İnfitar Sûresi",
            "Al-Mutaffifin" to "Mutaffifin Sûresi",
            "Al-Inshiqaaq" to "İnşikak Sûresi",
            "Al-Burooj" to "Bürûc Sûresi",
            "At-Taariq" to "Târık Sûresi",
            "Al-A'laa" to "A'lâ Sûresi",
            "Al-Ghaashiya" to "Gâşiye Sûresi",
            "Al-Fajr" to "Fecr Sûresi",
            "Al-Balad" to "Beled Sûresi",
            "Ash-Shams" to "Şems Sûresi",
            "Al-Lail" to "Leyl Sûresi",
            "Ad-Dhuhaa" to "Duhâ Sûresi",
            "Ash-Sharh" to "İnşirâh Sûresi",
            "At-Tin" to "Tin Sûresi",
            "Al-Alaq" to "Alak Sûresi",
            "Al-Qadr" to "Kadir Sûresi",
            "Al-Bayyina" to "Beyyine Sûresi",
            "Az-Zalzala" to "Zilzâl Sûresi",
            "Al-Aadiyaat" to "Âdiyât Sûresi",
            "Al-Qaari'a" to "Kâria Sûresi",
            "At-Takaathur" to "Tekâsür Sûresi",
            "Al-Asr" to "Asr Sûresi",
            "Al-Humaza" to "Hümeze Sûresi",
            "Al-Fil" to "Fil Sûresi",
            "Quraish" to "Kureyş Sûresi",
            "Al-Maa'un" to "Mâûn Sûresi",
            "Al-Kawthar" to "Kevser Sûresi",
            "Al-Kaafiroon" to "Kâfirûn Sûresi",
            "An-Nasr" to "Nasr Sûresi",
            "Al-Masad" to "Tebbet Sûresi",
            "Al-Ikhlaas" to "İhlâs Sûresi",
            "Al-Falaq" to "Felâk Sûresi",
            "An-Naas" to "Nâs Sûresi"
        )
    }
    
    // Türkçe çeviriler için lazy loading
    val turkishTranslations by lazy {
        mapOf(
            "The Opening" to "Açılış",
            "The Cow" to "İnek",
            "The Family of Imraan" to "İmrân Ailesi",
            "The Women" to "Kadınlar",
            "The Table" to "Sofra",
            "The Cattle" to "Sığırlar",
            "The Heights" to "Yüksek Yerler",
            "The Spoils of War" to "Savaş Ganimetleri",
            "The Repentance" to "Tevbe",
            "Jonas" to "Yûnus",
            "Hud" to "Hûd",
            "Joseph" to "Yûsuf",
            "The Thunder" to "Gök Gürültüsü",
            "Abraham" to "İbrâhim",
            "The Rock" to "Taşlar",
            "The Bee" to "Arı",
            "The Night Journey" to "Gece Yolculuğu",
            "The Cave" to "Mağara",
            "Mary" to "Meryem",
            "Taa-Haa" to "Tâhâ",
            "The Prophets" to "Peygamberler",
            "The Pilgrimage" to "Hac",
            "The Believers" to "Mü'minler",
            "The Light" to "Nûr",
            "The Criterion" to "Furkan",
            "The Poets" to "Şuarâ",
            "The Ant" to "Naml",
            "The Stories" to "Kasas",
            "The Spider" to "Ankebût",
            "The Romans" to "Rûm",
            "Luqman" to "Lokman",
            "The Prostration" to "Secde",
            "The Clans" to "Ahzâb",
            "Sheba" to "Sebe",
            "The Originator" to "Fâtır",
            "Yaseen" to "Yâsîn",
            "Those drawn up in Ranks" to "Safât",
            "The letter Saad" to "Sâd",
            "The Groups" to "Zümer",
            "The Forgiver" to "Ğâfir",
            "Explained in detail" to "Fussilet",
            "Consultation" to "Şûrâ",
            "Ornaments of gold" to "Zühruf",
            "The Smoke" to "Duhân",
            "Crouching" to "Câsiyâ",
            "The Dunes" to "Ahkâf",
            "Muhammad" to "Muhammed",
            "The Victory" to "Fetih",
            "The Inner Apartments" to "Hucûrât",
            "The letter Qaaf" to "Kâf",
            "The Winnowing Winds" to "Zâriyat",
            "The Mount" to "Tûr",
            "The Star" to "Necm",
            "The Moon" to "Kamâr",
            "The Beneficent" to "Merhametli",
            "The Inevitable" to "Kaçınılmaz",
            "The Iron" to "Demir",
            "The Pleading Woman" to "Hikaye Eden Kadın",
            "The Exile" to "Sürgün",
            "She that is to be examined" to "Sınav Edilen",
            "The Ranks" to "Sıralar",
            "Friday" to "Cuma",
            "The Hypocrites" to "Münafıklar",
            "Mutual Disillusion" to "Karşılıklı Aldanış",
            "Divorce" to "Boşanma",
            "The Prohibition" to "Yasak",
            "The Sovereignty" to "Egemenlik",
            "The Pen" to "Kalem",
            "The Reality" to "Gerçeklik",
            "The Ascending Stairways" to "Yükselen Merdivenler",
            "Noah" to "Nuh",
            "The Jinn" to "Cinler",
            "The Enshrouded One" to "Sarılan Kişi",
            "The Cloaked One" to "Örtülen Kişi",
            "The Resurrection" to "Kıyamet",
            "Man" to "İnsan",
            "The Emissaries" to "Elçiler",
            "The Announcement" to "Duyuru",
            "Those who drag forth" to "Sürükleyiciler",
            "He frowned" to "Kaşlarını Çattı",
            "The Overthrowing" to "Devrilme",
            "The Cleaving" to "Yarılma",
            "Defrauding" to "Dolandırma",
            "The Splitting Open" to "Açılma",
            "The Constellations" to "Takımyıldızlar",
            "The Morning Star" to "Sabah Yıldızı",
            "The Most High" to "Yüce",
            "The Overwhelming" to "Baskın",
            "The Dawn" to "Şafak",
            "The City" to "Şehir",
            "The Sun" to "Güneş",
            "The Night" to "Gece",
            "The Morning Hours" to "Sabah Saatleri",
            "The Consolation" to "Teselli",
            "The Fig" to "İncir",
            "The Clot" to "Alak",
            "The Power, Fate" to "Güç, Kader",
            "The Evidence" to "Delil",
            "The Earthquake" to "Deprem",
            "The Chargers" to "Koşucular",
            "The Calamity" to "Felaket",
            "Competition" to "Rekabet",
            "The Declining Day, Epoch" to "Gün Batımı, Dönem",
            "The Traducer" to "Karalayıcı",
            "The Elephant" to "Fil",
            "Quraysh" to "Kureyş",
            "Almsgiving" to "Sadaka",
            "Abundance" to "Bolluk",
            "The Disbelievers" to "Kafirlər",
            "Divine Support" to "İlahi Yardım",
            "The Palm Fibre" to "Palmiye Lifi",
            "Sincerity" to "Samimiyet",
            "The Dawn" to "Şafak",
            "Mankind" to "İnsanlar"
        )
    }

    // Fonksiyon olarak tanımlayarak sadece ihtiyaç olduğunda çağrılmasını sağla
    fun getTransliterations() = mapOf(
        "Turkish" to "tr.transliteration",
        "English" to "en.transliteration",
    )

    // En büyük veri yapısını lazy loading ile yükle
    private val _surahVerseRanges by lazy {
        mapOf(
            1 to 1..7,
            2 to 8..293,
            3 to 294..493,
            4 to 494..669,
            5 to 670..789,
            6 to 790..954,
            7 to 955..1160,
            8 to 1161..1235,
            9 to 1236..1364,
            10 to 1365..1473,
            11 to 1474..1596,
            12 to 1597..1707,
            13 to 1708..1750,
            14 to 1751..1802,
            15 to 1803..1901,
            16 to 1902..2029,
            17 to 2030..2140,
            18 to 2141..2250,
            19 to 2251..2348,
            20 to 2349..2483,
            21 to 2484..2595,
            22 to 2596..2673,
            23 to 2674..2791,
            24 to 2792..2855,
            25 to 2856..2932,
            26 to 2933..3159,
            27 to 3160..3252,
            28 to 3253..3340,
            29 to 3341..3409,
            30 to 3410..3469,
            31 to 3470..3503,
            32 to 3504..3533,
            33 to 3534..3606,
            34 to 3607..3660,
            35 to 3661..3705,
            36 to 3706..3788,
            37 to 3789..3970,
            38 to 3971..4058,
            39 to 4059..4133,
            40 to 4134..4218,
            41 to 4219..4272,
            42 to 4273..4325,
            43 to 4326..4414,
            44 to 4415..4473,
            45 to 4474..4510,
            46 to 4511..4545,
            47 to 4546..4583,
            48 to 4584..4612,
            49 to 4613..4630,
            50 to 4631..4675,
            51 to 4676..4735,
            52 to 4736..4784,
            53 to 4785..4846,
            54 to 4847..4901,
            55 to 4902..4979,
            56 to 4980..5075,
            57 to 5076..5104,
            58 to 5105..5126,
            59 to 5127..5150,
            60 to 5151..5163,
            61 to 5164..5177,
            62 to 5178..5188,
            63 to 5189..5199,
            64 to 5200..5217,
            65 to 5218..5229,
            66 to 5230..5241,
            67 to 5242..5271,
            68 to 5272..5323,
            69 to 5324..5375,
            70 to 5376..5419,
            71 to 5420..5447,
            72 to 5448..5475,
            73 to 5476..5495,
            74 to 5496..5551,
            75 to 5552..5591,
            76 to 5592..5622,
            77 to 5623..5672,
            78 to 5673..5712,
            79 to 5713..5758,
            80 to 5759..5800,
            81 to 5801..5829,
            82 to 5830..5848,
            83 to 5849..5884,
            84 to 5885..5909,
            85 to 5910..5931,
            86 to 5932..5948,
            87 to 5949..5967,
            88 to 5968..5993,
            89 to 5994..6023,
            90 to 6024..6043,
            91 to 6044..6058,
            92 to 6059..6079,
            93 to 6080..6090,
            94 to 6091..6098,
            95 to 6099..6106,
            96 to 6107..6125,
            97 to 6126..6130,
            98 to 6131..6138,
            99 to 6139..6146,
            100 to 6147..6157,
            101 to 6158..6168,
            102 to 6169..6176,
            103 to 6177..6179,
            104 to 6180..6188,
            105 to 6189..6193,
            106 to 6194..6197,
            107 to 6198..6204,
            108 to 6205..6207,
            109 to 6208..6213,
            110 to 6214..6216,
            111 to 6217..6221,
            112 to 6222..6225,
            113 to 6226..6230,
            114 to 6231..6236
        )
    }
    
    val SURAH_VERSE_RANGES: Map<Int, IntRange>
        get() = _surahVerseRanges


}