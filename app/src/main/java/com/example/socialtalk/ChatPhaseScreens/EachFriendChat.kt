package com.example.socialtalk.ChatPhaseScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.socialtalk.Myfont
import com.example.socialtalk.R
import com.example.socialtalk.ReturnedData.ChatMessage
import com.example.socialtalk.ReturnedData.UsersData
import com.example.socialtalk.ViewModel.ChatViewModel
import com.example.socialtalk.Utils.EventHandlerForContacts
import com.example.socialtalk.Utils.EventHandlerForDialogNewContact
import com.example.socialtalk.regularPoppins
import com.example.socialtalk.ui.theme.DarkBlue
import com.example.socialtalk.ui.theme.lightBlue
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun EachMessage(senderID: String, eachMessage: ChatMessage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, end = 10.dp, start = 10.dp),
        horizontalAlignment = if (eachMessage.userID.equals(senderID)) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (eachMessage.userID.equals(senderID)) DarkBlue else lightBlue,
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            eachMessage.message?.let {
                Text(
                    text = it,
                    fontFamily = regularPoppins,
                    fontSize = 15.sp,
                    color = if (eachMessage.userID.equals(senderID)) Color.White else DarkBlue,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                )
            }
        }
        Text(
            text = toConvert(eachMessage.timeStamp),
            fontFamily = regularPoppins,
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}

fun toConvert(timeStamp: Long?): String {
    val spd = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return spd.format(Date(timeStamp!!))
}

@Composable
fun EachChat(
    receiverId: String?,
    loginUserId: String?,
    Username: String?,
    eventHandler: (EventHandlerForContacts) -> Unit,
    eachChatUiState: ChatViewModel.ChatMsgUiState,
    receivedMessages: Flow<MutableList<ChatMessage>>,
    dialogEventHandler: (EventHandlerForDialogNewContact) -> Unit,
    userInfoState: UsersData,
    dialogState: ChatViewModel.DialogContactSave,
    navController: NavHostController,
) {

    val scrollState = rememberLazyListState()
    val responseFromDB =
        receivedMessages.collectAsStateWithLifecycle(initialValue = mutableListOf<ChatMessage>())
    val context = LocalContext.current
    val focusM = LocalFocusManager.current

    LaunchedEffect(key1 = responseFromDB.value.size) {
        if (responseFromDB.value.size > 1)
            scrollState.animateScrollToItem(responseFromDB.value.size - 1)
    }
    LaunchedEffect(key1 = dialogState.resultMessage) {
        if (dialogState.resultMessage.trim().isNotEmpty())
            Toast.makeText(context, dialogState.resultMessage, Toast.LENGTH_SHORT)
                .show()
    }
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = Username!!,
                    fontSize = 18.sp,
                    fontFamily = Myfont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Start
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    focusM.clearFocus()
                    navController.popBackStack()
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowLeft,
                        "backIcon",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }, actions = {
                Username?.let {
                    if (it.contains("+92")) {
                        IconButton(onClick = {
                            dialogEventHandler.invoke(
                                EventHandlerForDialogNewContact.DialogOpen(
                                    true
                                )
                            )
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.addcontactresized),
                                "backIcon",
                                modifier = Modifier
                                    .size(36.dp)
                            )
                        }
                    }
                }
            },
            backgroundColor = Color.White,
            contentColor = Color.Black,
            elevation = 3.dp
        )
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.TopCenter, modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.91f)
            ) {
                LazyColumn(contentPadding = it, state = scrollState) {
                    items(responseFromDB.value) { eachMessage ->
                        EachMessage(loginUserId!!, eachMessage)
                    }
                }

            }
            Box(
                contentAlignment = Alignment.BottomCenter, modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                TextField(
                    value = eachChatUiState.message,
                    onValueChange = { eventHandler.invoke(EventHandlerForContacts.MessageChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth(0.97f)
                        .height(56.dp)
                        .padding(bottom = 3.dp)
                        .clip(
                            shape = RoundedCornerShape(
                                topEnd = 90f,
                                topStart = 90f,
                                bottomEnd = 90f,
                                bottomStart = 90f
                            )
                        ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = DarkBlue,
                        textColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = DarkBlue
                    ),
                    singleLine = false,
                    placeholder = {
                        Text(
                            text = "Input Message Here .....",
                            color = Color.LightGray
                        )
                    }, trailingIcon = {
                        IconButton(onClick = { //Pass id From Here
                            eventHandler.invoke(
                                EventHandlerForContacts.MessageSent(
                                    senderID = loginUserId!!,
                                    receiverID = receiverId!!,
                                    msg = eachChatUiState.message
                                )
                            )
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_send_24),
                                contentDescription = "Sent", tint = Color.White
                            )
                        }
                    }
                )
            }

        }
    }


    if (dialogState.dialogOpen == true) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Dialog(onDismissRequest = {
                dialogEventHandler.invoke(
                    EventHandlerForDialogNewContact.DialogOpen(false)
                )
            })
            {
                Surface(
                    color = Color.Transparent, modifier = Modifier
                        .fillMaxWidth(0.92f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 30.dp)
                                .background(Color.White, shape = RoundedCornerShape(percent = 5)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(44.dp))
                            Text(
                                text = "Securing Contact",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                fontFamily = regularPoppins
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedTextField(
                                value = userInfoState.username,
                                onValueChange = {
                                    dialogEventHandler.invoke(
                                        EventHandlerForDialogNewContact.UsernameChange(it)
                                    )
                                },
                                singleLine = true, label = {
                                    Text(text = "Name")
                                }, placeholder = {
                                    Text(text = "Name")
                                }, modifier = Modifier.fillMaxWidth(0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = Username!!,
                                onValueChange = { },
                                readOnly = true, label = {
                                    Text(text = "Contact Number")
                                }, modifier = Modifier.fillMaxWidth(0.8f)
                            )
                            Spacer(modifier = Modifier.height(40.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 10.dp, start = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                Button(
                                    onClick = {
                                        dialogEventHandler.invoke(
                                            EventHandlerForDialogNewContact.DialogOpen(false)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color(
                                            0xffEFEFF9
                                        )
                                    ),
                                    shape = RoundedCornerShape(percent = 20),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = "Cancel",
                                        fontFamily = regularPoppins,
                                        color = DarkBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Button(
                                    onClick = {
                                        if (userInfoState.username.trim().isNotEmpty()) {
                                            dialogEventHandler.invoke(
                                                EventHandlerForDialogNewContact.ContactChange(
                                                    Username
                                                )
                                            )
                                            dialogEventHandler.invoke(
                                                EventHandlerForDialogNewContact.Submit(receiverId!!)
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = DarkBlue
                                    ), shape = RoundedCornerShape(percent = 20),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = "Save",
                                        fontFamily = regularPoppins,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.addcontactresized),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(bottom = 100.dp)
                                .border(width = 2.dp, color = DarkBlue, shape = CircleShape)
                                .background(color = Color.White, shape = CircleShape)
                                .align(alignment = Alignment.TopCenter)
                                .size(60.dp)
                        )
                    }
                }
            }

        }
    }

}




