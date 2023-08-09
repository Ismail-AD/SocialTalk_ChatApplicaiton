package com.example.socialtalk.ChatPhaseScreens


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.socialtalk.Myfont
import com.example.socialtalk.R
import com.example.socialtalk.ReturnedData.UsersData
import com.example.socialtalk.Utils.ScreenRoutes
import com.example.socialtalk.Utils.TriggerProgressBar
import com.example.socialtalk.ViewModel.ProfileViewModel
import com.example.socialtalk.Utils.EventHandlerForProfile
import com.example.socialtalk.ui.theme.DarkBlue


@Composable
fun SettingsHere(
    navController: NavHostController,
    userNewInfoState: UsersData,
    oldDataState: UsersData,
    eventOccurred: (EventHandlerForProfile) -> Unit,
    progressState: ProfileViewModel.UnderProgress,
) {
    val focusManager = LocalFocusManager.current
    if (progressState.loadingState)
        TriggerProgressBar()
    val context = LocalContext.current

    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            it?.let {
                eventOccurred.invoke(EventHandlerForProfile.ImageChange(it.toString()))
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontSize = 20.sp,
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
                            popUpTo(ScreenRoutes.profile.route) {
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
        }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .fillMaxHeight()
                .background(DarkBlue),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(userNewInfoState.profileImageUrl)
                            .error(R.drawable.person_outline)
                            .crossfade(500)
                            .placeholder(R.drawable.person_outline)
                            .build(),
                        contentDescription = "pImage",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(
                                CircleShape
                            )
                    )
                }
                IconButton(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    }, modifier = Modifier
                        .padding(top = 136.dp, start = 100.dp)
                        .background(color = DarkBlue, shape = CircleShape)
                        .align(Alignment.Center)
                        .size(35.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                        contentDescription = "",
                        tint = Color.White
                    )
                }

            }
            Spacer(modifier = Modifier.height(38.dp))
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp)),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(20.dp))
                    //USERNAME
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth(0.75f)
                    ) {
                        Text(
                            text = "Username",
                            color = Color.Gray.copy(0.7f),
                            textAlign = TextAlign.Start, fontSize = 18.sp
                        )

                        BasicTextField(
                            value = userNewInfoState.username,
                            onValueChange = {
                                eventOccurred.invoke(
                                    EventHandlerForProfile.NameChange(
                                        it
                                    )
                                )
                            },
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black.copy(0.75f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.padding(
                                        end = 2.dp,
                                        top = 7.dp,
                                        bottom = 7.dp
                                    )
                                ) {
                                    innerTextField()
                                }
                            })
                        Divider(
                            color = Color.Gray.copy(0.7f),
                            thickness = 1.dp
                        )
                    }


                    Spacer(modifier = Modifier.size(20.dp))
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            eventOccurred.invoke(EventHandlerForProfile.Submit)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = DarkBlue
                        ),
                        enabled = userNewInfoState.username.trim() != oldDataState.username.trim() && userNewInfoState.username.trim()
                            .isNotEmpty()
                    ) {
                        Text(
                            text = "Save",
                            style = TextStyle(
                                fontSize = 18.sp,
                                letterSpacing = 0.sp,
                                fontFamily = Myfont
                            ),
                            color = if (userNewInfoState.username.trim() != oldDataState.username.trim()) Color.White else Color.Black.copy(
                                0.7f
                            )
                        )
                    }
                }
            }
        }
    }
}

