import re

content = open("app/src/main/java/com/example/ui/screens/AnalyzerScreen.kt").read()

if "import android.Manifest" not in content:
    content = content.replace("import com.example.ui.viewmodel.LisanViewModel", 
"""import com.example.ui.viewmodel.LisanViewModel
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat""")

# Now replace the function start
start_fun = """@Composable
fun AnalyzerScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {"""

new_start_fun = """@Composable
fun AnalyzerScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.startAnalyzerPronunciationPractice()
        } else {
            viewModel.setAnalyzerPracticeFeedback("Please allow microphone access to use pronunciation practice.\\nآپ کو تلفظ کی مشق کے لیے مائیکروفون کی اجازت دینی ہوگی۔\\n\\nYou can still listen and continue by typing.\\nآپ پھر بھی سن سکتے ہیں اور لکھ کر سیکھ سکتے ہیں۔")
        }
    }"""

content = content.replace(start_fun, new_start_fun)

# Now replace the clickable block
clickable_old = """                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (isPracticing) {
                                        viewModel.stopAnalyzerPronunciationPractice()
                                    } else {
                                        viewModel.startAnalyzerPronunciationPractice()
                                    }
                                },"""

clickable_new = """                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (isPracticing) {
                                        viewModel.stopAnalyzerPronunciationPractice()
                                    } else {
                                        val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                            viewModel.startAnalyzerPronunciationPractice()
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    }
                                },"""

content = content.replace(clickable_old, clickable_new)

with open("app/src/main/java/com/example/ui/screens/AnalyzerScreen.kt", "w") as f:
    f.write(content)

