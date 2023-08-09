package com.example.socialtalk.ChatPhaseScreens


import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.socialtalk.Myfont
import com.example.socialtalk.R
import com.example.socialtalk.ReturnedData.chatMateData
import com.example.socialtalk.Utils.ScreenRoutes
import com.example.socialtalk.Utils.TriggerProgressBar
import com.example.socialtalk.ViewModel.MainViewModel
import com.example.socialtalk.Utils.EventHandlerSearchBar
import com.example.socialtalk.ui.theme.DarkBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun ListRowData(
    chatMateData: chatMateData,
    navController: NavHostController,
    eHandlerForSearchBar: (EventHandlerSearchBar) -> Unit,
    topBarState: MainViewModel.TopBarState,
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(
                Color.White
            )
            .padding(5.dp, 0.dp)
            .clickable {
                if (topBarState.isSearchBarVisible) {
                    eHandlerForSearchBar.invoke(EventHandlerSearchBar.SearchBarOpen(false))
                    eHandlerForSearchBar.invoke(EventHandlerSearchBar.SearchedData(""))
                }
                navController.navigate(ScreenRoutes.eachChat.route + "/${chatMateData.userid}/${chatMateData.username}") {
                    launchSingleTop = true
                    popUpTo(ScreenRoutes.Main.route) {
                        inclusive = false
                    }
                }
            }) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(chatMateData.profileImageUrl).error(R.drawable.person_outline)
                .placeholder(R.drawable.person_outline).build(),
            contentDescription = "pImage",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(75.dp)
                .padding(10.dp)
                .clip(CircleShape)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            chatMateData.username?.let {
                Text(
                    text = it,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
            chatMateData.lastMsg?.let {
                Text(
                    text = it,
                    fontSize = 15.sp,
                    color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (chatMateData.chatTime!! > 0) {
            Text(
                text = toConvertTime(chatMateData.chatTime),
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }
}

fun toConvertTime(chatTime: Long?): String {
    val spd = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return spd.format(Date(chatTime!!))
}

@Composable
fun SearchAppBar(
    topBarState: MainViewModel.TopBarState,
    eventHandlerForSearchBar: (EventHandlerSearchBar) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBlue), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = topBarState.searchedText,
            onValueChange = { eventHandlerForSearchBar.invoke(EventHandlerSearchBar.SearchedData(it)) },
            textStyle = TextStyle(fontSize = 17.sp),
            leadingIcon = {
                IconButton(onClick = {
                    eventHandlerForSearchBar.invoke(EventHandlerSearchBar.SearchedData(""))
                    eventHandlerForSearchBar.invoke(
                        EventHandlerSearchBar.SearchBarOpen(
                            false
                        )
                    )
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "back button Icon",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            placeholder = {
                Text(
                    text = "Search....",
                    color = Color.White.copy(alpha = ContentAlpha.medium),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            trailingIcon = {
                if (topBarState.searchedText.trim().isNotEmpty())
                    IconButton(onClick = {
                        eventHandlerForSearchBar.invoke(EventHandlerSearchBar.SearchedData(""))
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close Icon",
                            tint = Color.White
                        )
                    }
                else
                    eventHandlerForSearchBar.invoke(EventHandlerSearchBar.getOriginalList)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                cursorColor = Color.White,
                backgroundColor = DarkBlue,
                textColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(0.94f)
        )
    }
}


@Composable
fun MasterScreen(
    mainUiState: MainViewModel.DataUi,
    topBarState: MainViewModel.TopBarState,
    eHandlerForSearchBar: (EventHandlerSearchBar) -> Unit,
    getUsersData: () -> Unit,
    navController: NavHostController
) {
    LaunchedEffect(key1 = Unit) {
            getUsersData.invoke()
    }
    Scaffold(
        topBar = {
            Crossfade(targetState = topBarState.isSearchBarVisible, animationSpec = tween(450)) {
                if (it) {
                    SearchAppBar(topBarState, eHandlerForSearchBar)
                } else {
                    TopAppBar(navController = navController) {
                        eHandlerForSearchBar.invoke(EventHandlerSearchBar.SearchBarOpen(true))
                    }
                }
            }
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(ScreenRoutes.newUser.route) {
                        launchSingleTop = true
                        popUpTo(ScreenRoutes.Main.route) {
                            inclusive = false
                        }
                    }
                },
                backgroundColor = DarkBlue
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "BTN",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }, floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->

        if (mainUiState.loadingState)
            TriggerProgressBar()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xff1C2E46))
        ) {
            Box(
                contentAlignment = Alignment.TopCenter, modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 20.dp)
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 40.dp,
                            topEnd = 40.dp
                        )
                    )
                    .fillMaxHeight()
                    .background(Color.White)
            ) {
                if (mainUiState.listData.isNotEmpty())
                    LazyColumn(
                        contentPadding = paddingValues,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(mainUiState.listData,
                            key = { eachContact -> eachContact.userid!! }) { eachContact ->
                            ListRowData(
                                chatMateData = eachContact,
                                navController, eHandlerForSearchBar, topBarState
                            )
                        }
                    }
            }
        }
    }
}


@Composable
fun TopAppBar(navController: NavHostController, makeSearchBarVisible: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Messages",
                fontSize = 20.sp,
                fontFamily = Myfont,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.88f)
            )
        }, navigationIcon = {
            IconButton(onClick = {
                navController.navigate(ScreenRoutes.profile.route) {
                    launchSingleTop = true
                    popUpTo(ScreenRoutes.Main.route) {
                        inclusive = false
                    }
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.menu),
                    "backIcon",
                    modifier = Modifier.size(28.dp)
                )
            }
        }, actions = {
            IconButton(onClick = {
                makeSearchBarVisible()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.sarch),
                    "backIcon",
                    modifier = Modifier.size(20.dp)
                )
            }
        }, backgroundColor = DarkBlue,
        contentColor = Color.White,
        elevation = 0.dp,
        modifier = Modifier.background(DarkBlue)
    )
}




