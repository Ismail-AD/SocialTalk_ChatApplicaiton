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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialtalk.Myfont
import com.example.socialtalk.R
import com.example.socialtalk.Utils.EventHandlerForCode
import com.example.socialtalk.Utils.TriggerProgressBar
import com.example.socialtalk.ViewModel.SignUpViewModel
import com.example.socialtalk.ui.theme.blusih

@Composable
fun OtpScreen(
    otpState: SignUpViewModel.mobileCode,
    generateCredential: (String, (() -> Unit) -> Unit, () -> Unit) -> Unit,
    eventPerformed: (EventHandlerForCode) -> Unit,
    saveUserData: (() -> Unit) -> Unit,
) {

    if (otpState.showLoadingState)
        TriggerProgressBar()

    val contextObject = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(start = 10.dp, end = 10.dp, bottom = 30.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.otpscreen2),
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
                value = otpState.verifyCode,
                onValueChange = {
                    eventPerformed.invoke(EventHandlerForCode.CodeChange(it))
                },
                singleLine = true,
                label = {
                    Text(text = "Verification Code")
                },
                placeholder = { Text(text = "Verification Code") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.padding(10.dp))
            Button(
                onClick = {
                    if (otpState.verifyCode.isEmpty()) {
                        Toast.makeText(
                            contextObject,
                            "Fill both Fields !",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        eventPerformed.invoke(EventHandlerForCode.LoadingState(true))
                        generateCredential(otpState.verifyCode, saveUserData) {
                            eventPerformed.invoke(EventHandlerForCode.LoadingState(false))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = blusih)
            ) {
                Text(
                    text = "Verify OTP",
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
