package com.emmajson.nbackapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.emmajson.nbackapp.R
import com.emmajson.nbackapp.navigation.Screen
import com.emmajson.nbackapp.ui.screencomponents.GridView
import com.emmajson.nbackapp.ui.screencomponents.ProgressBar
import com.emmajson.nbackapp.ui.theme.NBack_CImplTheme
import com.emmajson.nbackapp.ui.viewmodels.FakeVM
import com.emmajson.nbackapp.ui.viewmodels.GameType
import com.emmajson.nbackapp.ui.viewmodels.GameViewModel

@Composable
fun NBackScreen(vm: GameViewModel, navController: NavController) {
    NBack_CImplTheme {
        val gameState by vm.gameState.collectAsState()
        val gameType = gameState.gameType
        val currentscore by vm.score.collectAsState()
        val currentIndex by vm.currentIndex.collectAsState()
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val highlightedIndex = gameState.eventValue - 1

        Scaffold(
            snackbarHost = {}
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Placing SnackbarHost at the top of the Box so it overlays other content
                SnackbarHost(
                    hostState = snackBarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 200.dp),  // Add padding if needed
                    snackbar = { snackbarData ->
                        Snackbar(
                            snackbarData = snackbarData,
                            containerColor = Color.Blue,   // Custom background color
                            contentColor = Color.White     // Custom text color
                        )
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        Modifier
                            .align(Alignment.Start)
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Button(
                            modifier = Modifier
                                .height(75.dp)
                                .weight(0.2f),
                            shape = MaterialTheme.shapes.medium,
                            onClick = {
                                vm.stopGame()
                                navController.navigate(Screen.HomeScreen.route)
                            }
                        ) {
                            Text(
                                text = "Back",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(Modifier.width(100.dp))

                        Button(
                            modifier = Modifier
                                .height(75.dp)
                                .weight(0.2f),
                            shape = MaterialTheme.shapes.medium,
                            onClick = vm::startGame
                        ) {
                            Text(
                                text = "Start",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    // Wrapping the LazyGrid in another view (Box in this case)
                    Box(
                        modifier = Modifier
                            .padding(20.dp, 0.dp, 20.dp, 0.dp)
                            .fillMaxWidth()
                    ) {
                        ProgressBar(currentIndex, gameState.gameLength - 1)
                    }

                    GridView(vm = vm, highlightedIndex = highlightedIndex)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp,0.dp,20.dp,60.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Points: $currentscore",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (gameType != GameType.Visual) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { if (gameType == GameType.Visual) false else true },
                                shape = RectangleShape,
                                onClick = {
                                    if (vm.checkMatchAudio(vm.currentIndex.value)) {
                                        scope.launch {
                                            snackBarHostState.currentSnackbarData?.dismiss()
                                            snackBarHostState.showSnackbar(
                                                message = "Audio match! You scored a point!",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    } else {
                                        scope.launch {
                                            snackBarHostState.currentSnackbarData?.dismiss()
                                            snackBarHostState.showSnackbar(
                                                message = "It was not a match :(",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.sound_on),
                                    contentDescription = "Sound",
                                    modifier = Modifier
                                        .height(48.dp)
                                        .aspectRatio(3f / 2f)
                                )
                            }
                        }

                        if (gameType == GameType.AudioVisual ) Spacer(Modifier.width(4.dp))

                        if (gameType != GameType.Audio) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                shape = RectangleShape,
                                onClick = {
                                    if (vm.checkMatchPlacement(vm.currentIndex.value)) {
                                        scope.launch {
                                            snackBarHostState.currentSnackbarData?.dismiss()
                                            snackBarHostState.showSnackbar(
                                                message = "Visual match! You scored a point!",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    } else {
                                        scope.launch {
                                            snackBarHostState.currentSnackbarData?.dismiss()
                                            snackBarHostState.showSnackbar(
                                                message = "It was not a match :(",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }

                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.visual),
                                    contentDescription = "Visual",
                                    modifier = Modifier
                                        .height(48.dp)
                                        .aspectRatio(3f / 2f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NBackScreenPreview() {
    // Mock ViewModel or pass a fake ViewModel for preview purposes
    val fakeVm = FakeVM() // You can create a FakeVM class or just pass a mock instance
    NBackScreen(vm = fakeVm, rememberNavController())
}