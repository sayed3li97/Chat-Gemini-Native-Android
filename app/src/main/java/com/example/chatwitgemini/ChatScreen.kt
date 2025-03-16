package com.example.chatwitgemini


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.Image // Ensure this import is correct
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.draw.clip // Ensure this import is correct
import androidx.compose.ui.draw.shadow


// Removed images and imageDescriptions arrays

data class ChatMessage(val text: String? = null, val imageBitmap: Bitmap? = null, val isUser: Boolean)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = viewModel()
) {
    val selectedModel by chatViewModel.selectedModel.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val modelOptions = listOf("gemini-1.5-flash", "gemini-2.0-flash", "gemma-3-27b-it")

    // Removed selectedImage state
    var prompt by rememberSaveable { mutableStateOf("") }
    var chatMessages by remember { mutableStateOf(mutableListOf<ChatMessage>()) }
    val uiState by chatViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                selectedImageBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
            }
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.app_title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(2.dp)) // Add spacing between title and model text
                        Text(
                            text = "Model: $selectedModel",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), // Lighter color
                            style = MaterialTheme.typography.bodySmall, // Smaller text size
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                },
                navigationIcon = { // Example: Add an Icon to the start
                    IconButton(onClick = { /*TODO: Handle navigation*/ }) {
                        Icon(Icons.Filled.Menu, "Menu") // Example: Menu icon
                    }
                },
                actions = { // Example: Add an Icon to the end
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.Settings, "Settings") // Example: Settings icon
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        modelOptions.forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model) },
                                onClick = {
                                    chatViewModel.updateModel(model)
                                    expanded = false
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.shadow(elevation = 2.dp) // Subtle TopAppBar shadow
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row( // Removed the outer Column, using Row directly
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) {
                        Icon(
                            Icons.Filled.Image,
                            contentDescription = "Pick Image",
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    if (selectedImageBitmap != null) {
                        Box(modifier = Modifier.padding(start = 8.dp)) { // Container for image preview and remove button
                            Image(
                                bitmap = selectedImageBitmap!!.asImageBitmap(),
                                contentDescription = "Selected Image Preview",
                                modifier = Modifier
                                    .size(36.dp) // Smaller preview size
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                                    .padding(4.dp) // Add padding inside the background
                                    .clip(RoundedCornerShape(4.dp)), // Apply clip modifier HERE to the modifier!
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { selectedImageBitmap = null },
                                modifier = Modifier
                                    .padding(0.dp)
                                    .size(20.dp) // Smaller button
                                    .align(Alignment.TopEnd) // Position close button on top-end
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    "Remove Image",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant, // Adjust tint if needed
                                    modifier = Modifier.size(16.dp) // Smaller icon inside button
                                )
                            }
                        }
                    }

                    TextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp), // Add horizontal padding to TextField
                        placeholder = { Text("Enter your prompt...") },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    IconButton(onClick = {
                        if (prompt.isNotEmpty() || selectedImageBitmap != null) {
                            chatMessages.add(ChatMessage(text = prompt.takeIf { it.isNotEmpty() }, imageBitmap = selectedImageBitmap, isUser = true)) // Pass both text and image
                            val imageToSend = selectedImageBitmap // Image to send can now be null
                            val textToSend = prompt.takeIf { it.isNotEmpty() } // Text to send might be null if only image
                            chatViewModel.sendPrompt(imageToSend, textToSend)
                            prompt = ""
                            selectedImageBitmap = null
                        }
                    }) {
                        Icon(
                            Icons.Filled.Send,
                            contentDescription = "Send",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(chatMessages) { message ->
                    ChatMessageItem(message = message)
                }
                if (uiState is UiState.Loading) {
                    item { Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        DotsTypingAnimation()
                    } }
                }
            }

            LaunchedEffect(uiState) {
                if (uiState is UiState.Error) {
                    chatMessages =
                        (chatMessages + ChatMessage(text = (uiState as UiState.Error).errorMessage, isUser = false)).toMutableList() // Error messages are text-only
                } else if (uiState is UiState.Success) {
                    chatMessages =
                        (chatMessages + ChatMessage(text = (uiState as UiState.Success).outputText, isUser = false)).toMutableList() // Success messages are text-only
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(horizontalAlignment = if (message.isUser != null) Alignment.End else Alignment.Start) { // Align Column content
            message.imageBitmap?.let { imageBitmap -> // If there's an image, display it
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "Sent Image",
                    modifier = Modifier
                        .size(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp)), // Correctly apply clip modifier here
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp)) // Add some space between image and text
            }
            message.text?.let { text ->
                Text(
                    text = text,
                    modifier = Modifier
                        .background(
                            color = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary, // Changed background colors here
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                        .fillMaxWidth(),
                    color = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, // Changed text colors here
                    textAlign = if (message.isUser == true) TextAlign.End else TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun CenteredCircularProgressIndicator() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showSystemUi = true)
@Composable
fun BakingScreenPreview() {
    ChatScreen()
}

@Composable
fun DotsTypingAnimation(
    dotSize: Dp = 10.dp, // Slightly larger dots
    dotColor: Color = MaterialTheme.colorScheme.tertiary, // Theme-consistent color
    delayUnit: Int = 400 // Slightly faster animation
) {
    val dots = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )

    dots.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = delayUnit * dots.size
                        0f at 0 with LinearOutSlowInEasing // for 0-1
                        1f at delayUnit * index with LinearOutSlowInEasing
                        0f at delayUnit * (index + 1) with LinearOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        dots.forEach { animatable ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .background(
                        color = dotColor.copy(alpha = animatable.value),
                        shape = CircleShape
                    )
            )
        }
    }
}