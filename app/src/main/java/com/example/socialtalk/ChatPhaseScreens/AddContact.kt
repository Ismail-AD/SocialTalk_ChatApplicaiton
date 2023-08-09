package com.example.socialtalk.ChatPhaseScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.socialtalk.Myfont
import com.example.socialtalk.Utils.ScreenRoutes
import com.example.socialtalk.ViewModel.NewContactViewModel
import com.example.socialtalk.Utils.EventHandlerForNewContact
import com.example.socialtalk.ui.theme.DarkBlue


@Composable
fun newContact(
    navController: NavHostController,
    contactUiState: NewContactViewModel.ExistingUserState,
    eventHandler: (EventHandlerForNewContact) -> Unit,
) {
    val contextObject = LocalContext.current
    val focusManager = LocalFocusManager.current

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "Add Contact",
                    fontSize = 18.sp,
                    fontFamily = Myfont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.75f)
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    focusManager.clearFocus()
                    navController.navigate(ScreenRoutes.Main.route) {
                        launchSingleTop = true
                        popUpTo(ScreenRoutes.newUser.route) {
                            inclusive = true
                        }
                    }
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowLeft,
                        "backIcon",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }, backgroundColor = DarkBlue,
            contentColor = Color.White,
            elevation = 0.dp
        )
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xff1C2E46))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .offset(y = 20.dp)
                    .padding(paddingValues)
                    .background(
                        Color.White, shape = RoundedCornerShape(
                            topStart = 40.dp,
                            topEnd = 40.dp
                        )
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Spacer(modifier = Modifier.height(25.dp))
                    OutlinedTextField(
                        value = contactUiState.euUsername,
                        onValueChange = {
                            eventHandler.invoke(
                                EventHandlerForNewContact.UsernameChange(
                                    it
                                )
                            )
                        },
                        singleLine = true,
                        label = {
                            Text(
                                text = "Name"
                            )
                        },
                        placeholder = { Text(text = "Name") },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    OutlinedTextField(
                        value = contactUiState.euNum,
                        onValueChange = {
                            eventHandler.invoke(
                                EventHandlerForNewContact.ContactChange(
                                    it
                                )
                            )
                        },
                        singleLine = true,
                        label = {
                            Text(
                                text = "Contact Number"
                            )
                        },
                        placeholder = { Text(text = "Contact Number") },
                        leadingIcon = { Text(text = "+92", color = Color.Black) },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    if (contactUiState.LoadingState)
                        CircularProgressIndicator(color = DarkBlue)
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.95f)
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            eventHandler.invoke(EventHandlerForNewContact.Submit)
                        }, modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                            .align(Alignment.BottomCenter),
                        colors = ButtonDefaults.buttonColors(backgroundColor = DarkBlue),
                        enabled = contactUiState.euUsername.trim()
                            .isNotEmpty() && contactUiState.euNum.trim().isNotEmpty()
                    ) {
                        Text(
                            text = "Save",
                            fontSize = 18.sp,
                            fontFamily = Myfont,
                            letterSpacing = 0.sp,
                            color = if (contactUiState.euUsername.trim()
                                    .isNotEmpty() && contactUiState.euNum.trim().isNotEmpty()
                            ) Color.White else Color.Black.copy(0.7f), textAlign = TextAlign.Center
                        )
                    }
                }

                LaunchedEffect(
                    key1 = contactUiState.contactSaved,
                    key2 = contactUiState.resultMessage
                ) {
                    if (contactUiState.resultMessage.isNotEmpty())
                        Toast.makeText(
                            contextObject,
                            contactUiState.resultMessage,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    if (contactUiState.contactSaved) {
                        navController.navigate(ScreenRoutes.Main.route) {
                            launchSingleTop = true
                            popUpTo(ScreenRoutes.newUser.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
    }

}
