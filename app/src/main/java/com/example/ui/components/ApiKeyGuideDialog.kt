package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

@Composable
fun ApiKeyGuideDialog(
    onDismiss: () -> Unit,
    onSaveKey: (String) -> Unit,
    isCustomKeyExceeded: Boolean = false
) {
    val context = LocalContext.current
    val colors = EditorialTheme.colors
    var inputKey by remember { mutableStateOf("") }
    var keySavedSuccess by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = "API Key",
                                    tint = Color(0xFF10B981)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "100% FREE API KEY GUIDE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentPurple,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = if (isCustomKeyExceeded) "نئی اے پی آئی کی (API Key) شامل کرنے کا طریقہ" else "مفت اے پی آئی کی (API Key) حاصل کرنے کی رہنمائی",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.container)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = colors.border)
                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Tutorial Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Introduction Banner depending on limit state
                    Surface(
                        color = colors.container,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, colors.borderAccent)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "السلام علیکم ورحمة الله وبركاته!",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )
                            Text(
                                text = "لسان گرامر (Lisan Grammar) میں خوش آمدید!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentPurple
                            )
                            if (isCustomKeyExceeded) {
                                Text(
                                    text = "Notice: Your current custom API Key limit was exceeded. Please insert a new, different API Key below to continue unlimited access.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary,
                                    lineHeight = 17.sp
                                )
                                Text(
                                    text = "آپ کی درج کردہ اے پی آئی کی (API Key) کی حد ختم ہو چکی ہے۔ برائے مہربانی نیچے ایک نئی مفت اے پی آئی کی (API Key) شامل کریں۔",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.primary,
                                    lineHeight = 22.sp
                                )
                            } else {
                                Text(
                                    text = "اگر آپ اس ایپ کی تمام تمام سہولیات جیسے AI ٹیوٹر (Tutor Chat)، AI آواز سے بات چیت (Voice Conversations)، اور تمام کوئزز کو ہمیشہ بغیر کسی حد کے (Unlimited) مفت استعمال کرنا چاہتے ہیں، تو اپنی اے پی آئی کی (API Key) یہاں درج کریں۔",
                                    fontSize = 13.sp,
                                    color = colors.textPrimary,
                                    lineHeight = 22.sp
                                )
                                Text(
                                    text = "• یہ بالکل 100% مفت ہے اور اس کے لیے کسی پیسے یا کریڈٹ کارڈ کی ضرورت نہیں ہے۔\n• یہ سیٹ اپ صرف ایک بار (Once) ضروری ہے جس کے بعد آپ ایپ بغیر کسی رکاوٹ کے استعمال کر سکتے ہیں۔",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    // Direct Link Button
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = Color.White)
                            Text("ڈائریکٹ لنک: گوگل اے آئی سٹوڈیو (Google AI Studio) کیز پیج کھولیں", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }
                    }

                    Text(
                        text = "تصویری مراحل • STEP-BY-STEP VISUAL GUIDE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textMuted,
                        letterSpacing = 1.sp
                    )

                    // Step 1: Search Google AI Studio
                    VisualStepCard(
                        stepBadge = "Step 1 of 5",
                        titleEn = "Step 1: Search 'Google AI Studio' on Google\nOpen Google and search for 'Google AI Studio'. Click the official website result 'aistudio.google.com' (do not click sponsored ads).",
                        titleUrdu = "قدم 1 (تصویر 1): گوگل پر \'گوگل اے آئی سٹوڈیو (Google AI Studio)\' تلاش کریں\nگوگل کھول کر \'گوگل اے آئی سٹوڈیو (Google AI Studio)\' سرچ کریں۔ نتائج میں برائے کرم اصل ویب سائٹ aistudio.google.com پر کلک کریں۔ (کسی اشتہار / Sponsored Result پر کلک نہ کریں۔)",
                        mockContent = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF1F5F9), CircleShape)
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Google ai studio", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text("Sponsored Result", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Text("Try Google AI Studio - Gemini API for Developers", fontSize = 12.sp, color = Color(0xFF1D4ED8))
                                Text("https://aistudio.google.com > gemini > api", fontSize = 10.sp, color = Color.Gray)
                                Text("Start creating in Google AI Studio...", fontSize = 10.sp, color = Color.DarkGray)

                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFEF08A), RoundedCornerShape(8.dp))
                                        .border(2.dp, Color.Red, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                            Text("Google AI Studio", fontSize = 11.sp, color = Color.DarkGray)
                                        }
                                        Text("https://aistudio.google.com", fontSize = 10.sp, color = Color(0xFF4B5563))
                                        Text("Google AI Studio", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                                        Text("Start exploring and building with Google latest models...", fontSize = 10.sp, color = Color.Black)
                                    }
                                }
                            }
                        }
                    )

                    // Step 2: Open Menu
                    VisualStepCard(
                        stepBadge = "Step 2 of 5",
                        titleEn = "Step 2: Sign in & Open Navigation Menu\nSign in with your Gmail account if prompted, then click the navigation menu icon (☰) in the top-left corner.",
                        titleUrdu = "قدم 2 (تصویر 2): سائن اِن کریں اور مینو (☰) کھولیں\nاپنے جی میل اکاؤنٹ سے سائن اِن کریں، پھر اوپر بائیں جانب موجود مینو کے نشان (☰) پر کلک کریں۔",
                        mockContent = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFFEF08A), RoundedCornerShape(6.dp))
                                            .border(2.dp, Color.Red, RoundedCornerShape(6.dp))
                                            .padding(6.dp)
                                    ) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black, modifier = Modifier.size(20.dp))
                                    }

                                    Text("aistudio.google.com", fontSize = 11.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Build your ideas with Gemini", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }
                    )

                    // Step 3: Click Dashboard
                    VisualStepCard(
                        stepBadge = "Step 3 of 5",
                        titleEn = "Step 3: Click 'Dashboard'\nIn the navigation menu drawer, locate the MANAGE section and click on Dashboard.",
                        titleUrdu = "قدم 3 (تصویر 3): \'ڈیش بورڈ (Dashboard)\' پر کلک کریں\nنیویگیشن مینو (Navigation Menu) میں، مینج (MANAGE) سیکشن تلاش کریں اور ڈیش بورڈ (Dashboard) پر کلک کریں۔",
                        mockContent = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Text("Google AI Studio", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("EXPLORE", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text("  • Playground", fontSize = 11.sp, color = Color.DarkGray)
                                Text("  • History", fontSize = 11.sp, color = Color.DarkGray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("BUILD", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text("  • New app", fontSize = 11.sp, color = Color.DarkGray)
                                Text("  • My apps", fontSize = 11.sp, color = Color.DarkGray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("MANAGE", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFEF08A), RoundedCornerShape(8.dp))
                                        .border(2.dp, Color.Red, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.Speed, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                            Text("Dashboard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text("  • Documentation", fontSize = 11.sp, color = Color.DarkGray)
                            }
                        }
                    )

                    // Step 4: Click API Keys
                    VisualStepCard(
                        stepBadge = "Step 4 of 5",
                        titleEn = "Step 4: Click 'API Keys'\nIn the dashboard sidebar under PROJECT, click on API Keys.",
                        titleUrdu = "قدم 4 (تصویر 4): \'اے پی آئی کیز (API Keys)\' پر کلک کریں\nڈیش بورڈ (Dashboard) کی سائیڈ بار میں پروجیکٹ (PROJECT) کے تحت اے پی آئی کیز (API Keys) پر کلک کریں۔",
                        mockContent = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Text("Dashboard", fontSize = 12.sp, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("PROJECT", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFEF08A), RoundedCornerShape(8.dp))
                                        .border(2.dp, Color.Red, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Key, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        Text("API Keys", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text("  • Projects", fontSize = 11.sp, color = Color.DarkGray)
                                Text("USAGE & BILLING", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text("  • Usage", fontSize = 11.sp, color = Color.DarkGray)
                                Text("  • Rate Limit", fontSize = 11.sp, color = Color.DarkGray)
                            }
                        }
                    )

                    // Step 5: Click Create API key & Copy (Matching Google AI Studio screenshot UI)
                    VisualStepCard(
                        stepBadge = "Step 5 of 5",
                        titleEn = "Step 5: Copy Your API Key\nAfter clicking 'Create API key', locate your key card (...4RJg). Click the highlighted Copy icon (📄) next to the key code to copy it.",
                        titleUrdu = "قدم 5 (تصویر 5): اپنی \'اے پی آئی کی (API Key)\' کاپی کریں\n\'کریٹ اے پی آئی کی (Create API key)\' پر کلک کرنے کے بعد، اپنی کی (Key) کا کارڈ تلاش کریں (مثلاً ...4RJg)۔ اسے کاپی کرنے کے لیے کی کوڈ (Key code) کے ساتھ موجود نمایاں کاپی آئیکن (Copy icon) 📄 پر کلک کریں۔",
                        mockContent = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                // Header Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("API Keys", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                                    Surface(
                                        color = Color(0xFF2563EB),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Create API key", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // API Key Card (exact replica of user's screenshot)
                                Surface(
                                    color = Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                                        // Row 1: Key preview ...4RJg + 4 inline options (Copy highlighted in yellow/red, Pricing, Analytics, Menu)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "...4RJg",
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF1E40AF)
                                            )

                                            // Exact 4 options beside key name matching screenshot
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // 1. COPY ICON (HIGHLIGHTED WITH YELLOW BG & RED BORDER FOR CLEAR GUIDANCE)
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFFEF08A), RoundedCornerShape(6.dp))
                                                        .border(1.5.dp, Color.Red, RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                    ) {
                                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Key", modifier = Modifier.size(13.dp), tint = Color.Black)
                                                        Text("Copy", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                                    }
                                                }

                                                // 2. PRICING ICON ($)
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 5.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.AttachMoney, contentDescription = "Pricing", modifier = Modifier.size(13.dp), tint = Color(0xFF475569))
                                                }

                                                // 3. ANALYTICS ICON (Bar Chart)
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 5.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.BarChart, contentDescription = "Analytics", modifier = Modifier.size(13.dp), tint = Color(0xFF475569))
                                                }

                                                // 4. MORE OPTIONS ICON (Three dots)
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 5.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(13.dp), tint = Color(0xFF475569))
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Gemini API Key 3", fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)

                                        Spacer(modifier = Modifier.height(8.dp))
                                        HorizontalDivider(color = Color(0xFFE2E8F0))
                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Details from screenshot
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Project", fontSize = 11.sp, color = Color.Gray)
                                            Text("Assistant", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Medium)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Project ID", fontSize = 11.sp, color = Color.Gray)
                                            Text("assistant-46285", fontSize = 11.sp, color = Color.DarkGray, fontFamily = FontFamily.Monospace)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Created", fontSize = 11.sp, color = Color.Gray)
                                            Text("Aug 5, 2026", fontSize = 11.sp, color = Color.DarkGray)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Billing tier", fontSize = 11.sp, color = Color.Gray)
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Set up billing", fontSize = 11.sp, color = Color(0xFF2563EB))
                                                Text("Free tier", fontSize = 10.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )

                    // Final Step
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669))
                                Text("Final Action: Paste Key in Lisan Grammar App", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF065F46))
                            }
                            Text(
                                text = "Return to this app, paste your key into the 'Insert Your API Key' input box below, and tap Save.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF047857),
                                lineHeight = 17.sp
                            )
                            Text(
                                text = "واپس اس ایپ میں آئیں اور نیچے 'اپنی اے پی آئی کی (API Key) درج کریں' والے خانے میں کی (Key) پیسٹ کر کے محفوظ کر لیں۔ آپ کی مفت خدمات بغیر کسی رکاوٹ کے جاری ہو جائیں گی!\n\nگوگل (Google) اپنی اے پی آئی کی (API Key) اپڈیٹ کر سکتا ہے۔ گوگل (Google) سال میں ایک بار یا کچھ مہینوں میں اپنی اے پی آئی کی (API Key) اپڈیٹ کر سکتا ہے۔ تو جب ایسا ہو، تو آپ اے آئی سٹوڈیو (Google AI Studio) میں جائیں اور اپنی نئی اپڈیٹ شدہ اے پی آئی کی (API Key) کاپی کریں۔ پریشان نہ ہوں، یہ مہینوں میں صرف ایک بار ہوتا ہے۔",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF065F46),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = colors.border)
                Spacer(modifier = Modifier.height(10.dp))

                // Paste Input Box & Save Button
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اپنی اے پی آئی کی (API Key) درج کریں (PASTE HERE):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )

                        val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                        TextButton(
                            onClick = {
                                val clipText = clipboardManager.getText()?.text
                                if (!clipText.isNullOrBlank()) {
                                    inputKey = clipText.trim()
                                }
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.ContentPaste, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                                Text("پیسٹ کریں (Paste)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                            }
                        }
                    }

                    OutlinedTextField(
                        value = inputKey,
                        onValueChange = { inputKey = it },
                        placeholder = { Text("AIzaSy... یا کی کاپی کر کے یہاں پیسٹ کریں", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            if (inputKey.isNotEmpty()) {
                                IconButton(onClick = { inputKey = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    )

                    Button(
                        onClick = {
                            if (inputKey.isNotBlank()) {
                                onSaveKey(inputKey.trim())
                                keySavedSuccess = true
                                onDismiss()
                            }
                        },
                        enabled = inputKey.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(
                            text = if (keySavedSuccess) "Key Saved! ✓" else "محفوظ کریں (Save Key)",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VisualStepCard(
    stepBadge: String,
    titleEn: String = "",
    titleUrdu: String,
    mockContent: @Composable () -> Unit
) {
    val colors = EditorialTheme.colors
    val cardShape = RoundedCornerShape(20.dp)
    Surface(
        color = colors.container,
        shape = cardShape,
        border = BorderStroke(1.dp, colors.border),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .padding(bottom = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color(0xFFDC2626),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stepBadge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }

                if (titleEn.isNotBlank()) {
                    Text(
                        text = titleEn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                        lineHeight = 17.sp
                    )
                }

                Text(
                    text = titleUrdu,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.primary,
                    lineHeight = 22.sp
                )

                mockContent()
            }

            // Bottom Accent Strip
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF3B82F6).copy(alpha = 0.3f),
                                Color(0xFF2563EB),
                                Color(0xFF1D4ED8).copy(alpha = 0.3f)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun ApiKeyGuideCardInline(
    onOpenGuide: () -> Unit,
    onSaveKey: (String) -> Unit
) {
    val context = LocalContext.current
    val colors = EditorialTheme.colors
    var keyText by remember { mutableStateOf("") }

    val cardShape = RoundedCornerShape(18.dp)
    Surface(
        color = colors.surface,
        shape = cardShape,
        border = BorderStroke(1.5.dp, Color(0xFFF59E0B)),
        shadowElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp).padding(bottom = 6.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFFF59E0B))
                    Text(
                        text = "مفت جیمنی اے پی آئی کی (Gemini API Key) حاصل کریں",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colors.primary
                    )
                }

                Text(
                    text = "پریشان نہ ہوں! آپ اپنی بالکل مفت \"API Key\" شامل کر کے بلا تاخیر اور بغیر کسی سبسکرپشن کے ایپ جاری رکھ سکتے ہیں۔",
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    lineHeight = 16.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ڈائریکٹ لنک (Direct Link)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onOpenGuide,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentPurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تصویری رہنما (Visual Guide)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                    OutlinedTextField(
                        value = keyText,
                        onValueChange = { keyText = it },
                        placeholder = { Text("اپنی اے پی آئی کی (API Key) درج کریں", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = {
                            TextButton(
                                onClick = {
                                    val clipText = clipboardManager.getText()?.text
                                    if (!clipText.isNullOrBlank()) {
                                        keyText = clipText.trim()
                                    }
                                }
                            ) {
                                Text("Paste", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                            }
                        }
                    )

                    Button(
                        onClick = {
                            if (keyText.isNotBlank()) {
                                onSaveKey(keyText.trim())
                                keyText = ""
                            }
                        },
                        enabled = keyText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("محفوظ کریں", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            // Bottom Accent Strip
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF3B82F6).copy(alpha = 0.3f),
                                Color(0xFF2563EB),
                                Color(0xFF1D4ED8).copy(alpha = 0.3f)
                            )
                        )
                    )
            )
        }
    }
}

