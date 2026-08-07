import re

with open("app/src/main/java/com/example/ui/screens/LisanDailyScreen.kt", "r") as f:
    content = f.read()

# Replace Idiom state observation with Verb state observation
verb_states = """
    val dailyVerb by viewModel.dailyVerb.collectAsState()
    val isDailyVerbCompleted by viewModel.isDailyVerbCompleted.collectAsState()
    val isDailyVerbSaved by viewModel.isDailyVerbSaved.collectAsState()
    val dailyHelperChatHistory by viewModel.dailyHelperChatHistory.collectAsState()
    val isDailyHelperTyping by viewModel.isDailyHelperTyping.collectAsState()
"""
content = re.sub(r'val dailyIdiom by viewModel\.dailyIdiom\.collectAsState\(\)\n.*?val isDailyIdiomSaved by viewModel\.isDailyIdiomSaved\.collectAsState\(\)', verb_states, content, flags=re.DOTALL)

# Replace Idiom status card with Verb status card
content = content.replace('val isDailyIdiomCompleted = false // replace me later if needed', '')
content = content.replace('isDailyIdiomCompleted', 'isDailyVerbCompleted')
content = content.replace('Daily Idiom', 'Daily Verb')

# Find where to insert the Daily Verb UI
# "EditorialCard(" is used for cards. The first is Daily Challenge. We need to insert Daily Verb BEFORE Daily Challenge.
verb_ui = """
        // --- 1. Daily Verb Component ---
        dailyVerb?.let { verb ->
            EditorialCard(
                borderColor = if (isDailyVerbCompleted) Color(0xFFFFD54F).copy(alpha = 0.5f) else EditorialBorder
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DAILY VERB",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800),
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "آج کا فعل",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Giant Display Title
                Text(
                    text = verb.baseForm.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(6.dp))

                // English and Urdu Meanings
                Text(
                    text = "Urdu meaning:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextMuted
                )
                Text(
                    text = verb.urduMeaning,
                    fontSize = 16.sp,
                    color = EditorialPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )

                EditorialDivider()
                
                // Verb Forms Table
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EditorialContainer)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val forms = listOf(
                        "Base form" to verb.baseForm,
                        "Third-person singular" to verb.thirdPerson,
                        "Present participle" to verb.presentParticiple,
                        "Past simple" to verb.pastSimple,
                        "Past participle" to verb.pastParticiple
                    )
                    forms.forEach { (label, value) ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(label, fontSize = 13.sp, color = EditorialTextSecondary)
                            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                        }
                    }
                }
                
                EditorialDivider()

                // Contextual Examples
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Examples:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextMuted
                    )
                    verb.examples.forEach { example ->
                        Column {
                            Text(
                                text = example.english,
                                fontSize = 14.sp,
                                color = EditorialTextPrimary
                            )
                            Text(
                                text = example.urdu,
                                fontSize = 14.sp,
                                color = EditorialTextSecondary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFFF8E1),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(verb.usageNoteEn, fontSize = 13.sp, color = EditorialPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(verb.usageNoteUr, fontSize = 13.sp, color = EditorialTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons: Complete & Save/Bookmark
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Listen
                    IconButton(
                        onClick = { viewModel.playVerbAudio(verb.id) },
                        modifier = Modifier.size(48.dp).background(EditorialContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (currentlySpeaking == verb.id) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Listen",
                            tint = EditorialAccentPurple
                        )
                    }

                    // Save bookmark
                    IconButton(
                        onClick = { viewModel.toggleDailyVerbSaved() },
                        modifier = Modifier.size(48.dp).testTag("save_verb_button").background(EditorialContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isDailyVerbSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Verb",
                            tint = if (isDailyVerbSaved) EditorialAccentPurple else EditorialTextSecondary
                        )
                    }
                    
                    // Practise / Note placeholder (button takes up rest of space)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
"""
content = content.replace("// --- 1. Daily Challenge Component ---", verb_ui + "\n        // --- 2. Daily Challenge Component ---")

# Replace Idiom Component UI completely.
# Find where "// --- 2. Daily Idiom Component ---" starts and where "// --- 3. Spaced Repetition Revision Planner ---" starts
start_idx = content.find("// --- 2. Daily Idiom Component ---")
end_idx = content.find("// --- 3. Spaced Repetition Revision Planner ---")
if start_idx != -1 and end_idx != -1:
    content = content[:start_idx] + content[end_idx:]

# Also, below Daily Challenge, add the Daily Helper AI chat.
ai_helper_ui = """
        // --- 3. Daily Lisan Helper ---
        EditorialCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = null,
                    tint = EditorialAccentPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Daily Lisan Helper",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialAccentPurple,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "ڈیلی لسان مددگار",
                        fontSize = 12.sp,
                        color = EditorialTextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            // Chat history
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 300.dp)
                    .background(EditorialContainer, RoundedCornerShape(12.dp))
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dailyHelperChatHistory.forEach { msg ->
                    val isUser = msg.role == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (isUser) EditorialAccentPurple else Color.White,
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (isUser) 12.dp else 0.dp,
                                bottomEnd = if (isUser) 0.dp else 12.dp
                            ),
                            border = if (!isUser) BorderStroke(1.dp, EditorialBorder) else null,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.content,
                                modifier = Modifier.padding(10.dp),
                                color = if (isUser) Color.White else EditorialPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                if (isDailyHelperTyping) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp),
                            border = BorderStroke(1.dp, EditorialBorder)
                        ) {
                            Text("typing...", modifier = Modifier.padding(10.dp), color = EditorialTextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            var helperInput by remember { mutableStateOf("") }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = helperInput,
                    onValueChange = { helperInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask about today's task...") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = EditorialContainer,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = EditorialAccentPurple
                    )
                )
                IconButton(
                    onClick = {
                        if (helperInput.isNotBlank()) {
                            viewModel.sendDailyHelperMessage(helperInput)
                            helperInput = ""
                        }
                    },
                    modifier = Modifier.background(EditorialPrimary, CircleShape)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
"""
content = content.replace("// --- 3. Spaced Repetition Revision Planner ---", ai_helper_ui + "\n        // --- 4. Spaced Repetition Revision Planner ---")
content = content.replace("// --- 4. Discuss with Lisan AI Tutor Quick Link ---", "// --- 5. Discuss with Lisan AI Tutor Quick Link ---")
content = content.replace("// --- 5. Dynamic Achievements Ribbon List ---", "// --- 6. Dynamic Achievements Ribbon List ---")

with open("app/src/main/java/com/example/ui/screens/LisanDailyScreen.kt", "w") as f:
    f.write(content)
