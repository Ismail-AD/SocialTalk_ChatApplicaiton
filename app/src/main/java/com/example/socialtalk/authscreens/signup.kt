package com.example.socialtalk.authscreens


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.socialtalk.Myfont
import com.example.socialtalk.R
import com.example.socialtalk.Utils.EventHandlerForCode
import com.example.socialtalk.Utils.EventHandlerForNumber
import com.example.socialtalk.Utils.ScreenRoutes
import com.example.socialtalk.Utils.TriggerProgressBar
import com.example.socialtalk.ViewModel.SignUpViewModel
import com.example.socialtalk.ui.theme.blusih
import com.google.firebase.FirebaseException



@Composable
fun SignUp(
    numUiState: SignUpViewModel.SignUpUIStateNumber,
    codeToUserPhone: (String, (EventHandlerForNumber) -> Unit, (EventHandlerForCode) -> Unit, (() -> Unit) -> Unit, Boolean, () -> Unit, (FirebaseException) -> Unit) -> Unit,
    otpEventForLoading: (EventHandlerForCode) -> Unit,
    eventToBePerformed: (EventHandlerForNumber) -> Unit,
    saveUserData: (() -> Unit) -> Unit,
    rememberController: NavHostController,
) {

    if (numUiState.showLoadingState)
        TriggerProgressBar()

    val focusManager = LocalFocusManager.current

    val contextObject =
        LocalContext.current  // composition local will provide context object associated with this composable
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(start = 10.dp, end = 10.dp, bottom = 30.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.otpscreen),
                    modifier = Modifier.size(100.dp),
                    contentDescription = "Sign Up Image"
                )
                Spacer(modifier = Modifier.padding(35.dp))
                Text(
                    text = "Otp Verification",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontFamily = Myfont),
                    fontSize = 30.sp
                )
                Spacer(modifier = Modifier.padding(20.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = numUiState.newUsername,
                        onValueChange = {
                            eventToBePerformed.invoke(EventHandlerForNumber.UsernameChange(it))
                        },
                        singleLine = true,
                        label = {
                            Text(text = "Name")
                        },
                        placeholder = { Text(text = "Name") },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    OutlinedTextField(
                        value = numUiState.newNumber,
                        onValueChange = {
                            eventToBePerformed.invoke(EventHandlerForNumber.NumberChange(it))
                        },
                        singleLine = true,
                        label = {
                            Text(
                                text = "Number"
                            )
                        }, leadingIcon = { Text(text = "+92", color = Color.Black) },
                        placeholder = { Text(text = "Phone Number") },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    Spacer(modifier = Modifier.padding(20.dp))
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (numUiState.newNumber.isEmpty() || numUiState.newUsername.isEmpty() || numUiState.equals(
                                    "+92"
                                )
                            ) {
                                Toast.makeText(
                                    contextObject,
                                    "Complete Missing Fields !",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                eventToBePerformed.invoke(EventHandlerForNumber.LoadingState(true))
                                codeToUserPhone.invoke("+92${numUiState.newNumber}", eventToBePerformed,
                                    otpEventForLoading, saveUserData, numUiState.resendOrNot, {
                                        eventToBePerformed.invoke(
                                            EventHandlerForNumber.LoadingState(
                                                false
                                            )
                                        )
                                        Toast.makeText(
                                            contextObject,
                                            "You will receive the One-Time Password (OTP) shortly!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        rememberController.navigate(ScreenRoutes.codeVerify.route) {
                                            launchSingleTop = true
                                            popUpTo(ScreenRoutes.numberInput.route) {
                                                inclusive = false
                                            }
                                        }
                                    }, {
                                        Toast.makeText(
                                            contextObject,
                                            it.localizedMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    })
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = blusih)
                    ) {
                        Text(
                            text = if (numUiState.resendOrNot) "Resend OTP" else "Send OTP",
                            fontSize = 18.sp,
                            fontFamily = Myfont,
                            letterSpacing = 0.sp,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.padding(16.dp))
                }

            }
        }
