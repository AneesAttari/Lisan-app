import re

with open('app/src/main/java/com/example/ui/components/PictureVocabularyImageHelper.kt', 'r') as f:
    content = f.read()

start_idx = content.find('@Composable\nfun VectorGraphic')
if start_idx == -1:
    print("Not found")
    exit(1)

new_func = """@Composable
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
"""

content = content[:start_idx] + new_func

with open('app/src/main/java/com/example/ui/components/PictureVocabularyImageHelper.kt', 'w') as f:
    f.write(content)

print("Done")
