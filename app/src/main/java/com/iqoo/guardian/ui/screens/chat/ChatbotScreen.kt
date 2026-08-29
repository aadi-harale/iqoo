package com.iqoo.guardian.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.iqoo.guardian.ui.components.AmbientBackground
import com.iqoo.guardian.ui.components.GuardianTopBar
import com.iqoo.guardian.ui.theme.GAccent
import com.iqoo.guardian.ui.theme.GBackground
import com.iqoo.guardian.ui.theme.GCardElevated
import com.iqoo.guardian.ui.theme.GTextMuted
import com.iqoo.guardian.ui.theme.GTextPrimary
import com.iqoo.guardian.ui.theme.GTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.iqoo.guardian.ui.components.WeeklyThreatChart
import com.iqoo.guardian.ui.components.PermissionsDonutChart

enum class ChatWidget { NONE, THREAT_CHART, PRIVACY_CHART }
data class ChatMessage(val text: String, val isUser: Boolean, val widget: ChatWidget = ChatWidget.NONE)

@Composable
fun ChatbotScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    val predefinedQAs = mapOf(
        "What is iQOO Guardian?" to ChatMessage("iQOO Guardian is an on-device, local-first intelligence engine that actively monitors your notifications, storage, and app activity for deceptive patterns, scams, and threats without ever sending your data to the cloud.", false),
        "How does local-first protection work?" to ChatMessage("Guardian runs highly optimized models directly on your phone's neural processor. This means it can instantly catch threats like UPI scams or fake storage warnings even in airplane mode, ensuring 100% data privacy.", false, ChatWidget.PRIVACY_CHART),
        "What are my current threat stats?" to ChatMessage("In the last 7 days, Guardian has successfully blocked 24 threats, analyzed over 14,000 background signals, and revoked permissions for 2 potentially risky apps. Here is your weekly breakdown:", false, ChatWidget.THREAT_CHART)
    )
    
    val initialMessage = ChatMessage("Hello! I am your Guardian Assistant. How can I help you understand your device's security today?", false)
    var messages by remember { mutableStateOf(listOf(initialMessage)) }
    var isTyping by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        AmbientBackground(accent = GAccent, bloomCenterYFraction = 0.5f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Spacer(Modifier.height(18.dp))
            GuardianTopBar(
                eyebrow = "GUARDIAN AI",
                title = "Security Assistant",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))
            
            // Chat History
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(message = msg)
                }
                if (isTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }
            
            // Predefined Questions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GCardElevated)
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Suggested Questions", style = MaterialTheme.typography.labelMedium, color = GTextMuted)
                predefinedQAs.keys.forEach { question ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GBackground)
                            .clickable {
                                if (!isTyping) {
                                    messages = messages + ChatMessage(question, true)
                                    isTyping = true
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(messages.size)
                                        delay(1500)
                                        messages = messages + predefinedQAs[question]!!
                                        isTyping = false
                                        delay(100)
                                        listState.animateScrollToItem(messages.size)
                                    }
                                }
                            }
                            .padding(14.dp)
                    ) {
                        Text(question, style = MaterialTheme.typography.bodyMedium, color = GTextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (message.isUser) GAccent.copy(alpha = 0.2f) else GCardElevated
    val textColor = if (message.isUser) GAccent else GTextPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (message.isUser) 40.dp else 0.dp,
                end = if (message.isUser) 0.dp else 40.dp
            ),
        contentAlignment = alignment
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (!message.isUser) {
                Icon(
                    imageVector = Icons.Rounded.SmartToy,
                    contentDescription = null,
                    tint = GAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    ))
                    .background(bgColor)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
                
                if (message.widget != ChatWidget.NONE) {
                    Spacer(Modifier.height(16.dp))
                    when (message.widget) {
                        ChatWidget.THREAT_CHART -> WeeklyThreatChart()
                        ChatWidget.PRIVACY_CHART -> PermissionsDonutChart()
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.SmartToy,
            contentDescription = null,
            tint = GAccent,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                .background(GCardElevated)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text("Analyzing...", style = MaterialTheme.typography.bodyMedium, color = GTextMuted)
        }
    }
}
