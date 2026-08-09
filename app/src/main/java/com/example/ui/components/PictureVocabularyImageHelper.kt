package com.example.ui.components

import androidx.compose.ui.unit.sp

import androidx.compose.foundation.Canvas

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.*

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip

import androidx.compose.ui.geometry.CornerRadius

import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.geometry.Size

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.Path

import androidx.compose.ui.graphics.StrokeCap

import androidx.compose.ui.graphics.drawscope.Stroke

import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.unit.dp

import androidx.compose.ui.layout.ContentScale

import coil.compose.SubcomposeAsyncImage

import com.example.data.repository.PictureVocabularyImageMap

object PictureVocabularyAssetHelper {

    fun getCleanWord(key: String): String {
        var s = key.lowercase()
            .replace("img_", "")
            .replace("cat_", "")
            .replace("_", " ")
            .trim()

        s = when (s) {
            "food and drinks" -> "food"
            "fruits vegetables" -> "fruit"
            "birds insects" -> "bird"
            "home furniture" -> "furniture"
            "kitchen items" -> "kitchen"
            "clothes fashion" -> "clothes"
            "professions jobs" -> "job"
            "body parts" -> "portrait"
            "school education" -> "school"
            "nature geography" -> "nature"
            "verbs actions" -> "running"
            "adjectives opposites" -> "colors"
            "shapes colors" -> "color"
            "numbers math" -> "math"
            "technology devices" -> "laptop"
            "sports hobbies" -> "sports"
            "health hygiene" -> "fitness"
            "roti" -> "bread"
            "sabzi" -> "vegetables"
            "gosht" -> "meat"
            "machli" -> "fish"
            "billi" -> "cat"
            "kutta" -> "dog"
            "aam" -> "mango"
            "kela" -> "banana"
            "sib" -> "apple"
            else -> s
        }
        s = s.replace(Regex("\\d+"), "").trim()
        if (s.isEmpty()) s = "vocabulary"
        return s.replace(" ", "")
    }

    fun getIconForAssetKey(key: String): ImageVector {
        val k = key.lowercase()
        return when {
            k.contains("pizza") -> Icons.Default.LocalPizza
            k.contains("coffee") || k.contains("tea") -> Icons.Default.Coffee
            k.contains("milk") || k.contains("water") || k.contains("juice") || k.contains("drink") -> Icons.Default.LocalDrink
            k.contains("apple") || k.contains("fruit") || k.contains("orange") || k.contains("banana") -> Icons.Default.Eco
            k.contains("bread") || k.contains("cake") || k.contains("cookie") -> Icons.Default.BakeryDining
            k.contains("rice") || k.contains("soup") || k.contains("food") || k.contains("pasta") || k.contains("burger") -> Icons.Default.Restaurant
            k.contains("egg") -> Icons.Default.Egg
            k.contains("cheese") || k.contains("butter") -> Icons.Default.Kitchen
            k.contains("cat") || k.contains("dog") || k.contains("animal") -> Icons.Default.Pets
            k.contains("bird") || k.contains("pigeon") || k.contains("sparrow") -> Icons.Default.Flight
            k.contains("house") || k.contains("door") || k.contains("window") || k.contains("bed") || k.contains("furniture") -> Icons.Default.Home
            k.contains("school") || k.contains("book") || k.contains("pen") || k.contains("teacher") -> Icons.Default.School
            k.contains("car") || k.contains("bus") || k.contains("train") || k.contains("airplane") || k.contains("transport") -> Icons.Default.DirectionsCar
            k.contains("sun") || k.contains("moon") || k.contains("weather") || k.contains("rain") -> Icons.Default.WbSunny
            k.contains("tree") || k.contains("flower") || k.contains("nature") -> Icons.Default.Park
            k.contains("doctor") || k.contains("nurse") || k.contains("job") || k.contains("profession") -> Icons.Default.Work
            k.contains("phone") || k.contains("laptop") || k.contains("camera") || k.contains("tech") -> Icons.Default.Devices
            k.contains("sport") || k.contains("football") -> Icons.Default.SportsSoccer
            k.contains("shirt") || k.contains("clothes") || k.contains("shoe") -> Icons.Default.Checkroom
            k.contains("body") || k.contains("face") -> Icons.Default.Face
            else -> Icons.Default.Category
        }
    }

    fun getColorForAssetKey(key: String): Color {
        val k = key.lowercase()
        return when {
            k.contains("tea") || k.contains("coffee") || k.contains("bread") || k.contains("cookie") -> Color(0xFFFFF8E1)
            k.contains("milk") || k.contains("water") || k.contains("juice") -> Color(0xFFE1F5FE)
            k.contains("cheese") || k.contains("banana") || k.contains("sun") || k.contains("butter") || k.contains("honey") -> Color(0xFFFFFDE7)
            k.contains("apple") || k.contains("pizza") || k.contains("cherry") || k.contains("strawberry") -> Color(0xFFFFEBEE)
            k.contains("cat") || k.contains("dog") || k.contains("animal") -> Color(0xFFF3E5F5)
            k.contains("tree") || k.contains("nature") || k.contains("grass") -> Color(0xE8F5E9)
            k.contains("car") || k.contains("bus") || k.contains("house") -> Color(0xFFE0F7FA)
            else -> Color(0xFFF0F4F8)
        }
    }

    fun getTintForAssetKey(key: String): Color {
        val k = key.lowercase()
        return when {
            k.contains("pizza") || k.contains("apple") -> Color(0xFFE53935)
            k.contains("coffee") || k.contains("bread") || k.contains("cookie") -> Color(0xFF6D4C41)
            k.contains("milk") || k.contains("water") -> Color(0xFF0288D1)
            k.contains("juice") || k.contains("orange") -> Color(0xFFFB8C00)
            k.contains("cheese") || k.contains("banana") || k.contains("honey") -> Color(0xFFFBC02D)
            k.contains("tree") || k.contains("nature") -> Color(0xFF388E3C)
            k.contains("cat") || k.contains("dog") -> Color(0xFF8E24AA)
            else -> Color(0xFF1E88E5)
        }
    }
}

@Composable
fun PictureCardThumbnail(
    assetKey: String,
    imageUrl: String? = null,
    word: String? = null,
    alt: String? = null,
    modifier: Modifier = Modifier,
    sizeMultiplier: Float = 1f
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cleanWord = word?.takeIf { it.isNotBlank() } ?: PictureVocabularyAssetHelper.getCleanWord(assetKey)
    
    val resId = androidx.compose.runtime.remember(assetKey, cleanWord) {
        val id1 = context.resources.getIdentifier(assetKey.lowercase(), "drawable", context.packageName)
        if (id1 != 0) id1
        else {
            val cleanKey = "img_" + cleanWord.lowercase().replace(" ", "_")
            val id2 = context.resources.getIdentifier(cleanKey, "drawable", context.packageName)
            if (id2 != 0) id2 else 0
        }
    }

    val modelToLoad: Any? = if (resId != 0) resId else null
    
    val contentDesc = alt ?: "Picture of $cleanWord"
    val bg = PictureVocabularyAssetHelper.getColorForAssetKey(assetKey)
    val icon = PictureVocabularyAssetHelper.getIconForAssetKey(assetKey)
    val tint = PictureVocabularyAssetHelper.getTintForAssetKey(assetKey)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (modelToLoad != null) {
            SubcomposeAsyncImage(
                model = modelToLoad,
                contentDescription = contentDesc,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bg),
                        contentAlignment = Alignment.Center
                    ) {
                        VectorGraphic(
                            key = cleanWord,
                            fallbackIcon = icon,
                            tint = tint,
                            modifier = Modifier.fillMaxSize(0.85f * sizeMultiplier)
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bg),
                        contentAlignment = Alignment.Center
                    ) {
                        VectorGraphic(
                            key = cleanWord,
                            fallbackIcon = icon,
                            tint = tint,
                            modifier = Modifier.fillMaxSize(0.85f * sizeMultiplier)
                        )
                    }
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                VectorGraphic(
                    key = cleanWord,
                    fallbackIcon = icon,
                    tint = tint,
                    modifier = Modifier.fillMaxSize(0.85f * sizeMultiplier)
                )
            }
        }
    }
}

@Composable
fun VectorGraphic(
    key: String,
    fallbackIcon: ImageVector = Icons.Default.Category,
    tint: Color = Color(0xFF1E88E5),
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val emoji = com.example.utils.EmojiMapper.getEmojiForWord(key)
        androidx.compose.material3.Text(
            text = emoji,
            fontSize = 72.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
