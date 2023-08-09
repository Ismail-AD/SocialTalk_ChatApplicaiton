package com.example.socialtalk.View

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.socialtalk.R
import com.example.socialtalk.Utils.ScreenRoutes
import com.example.socialtalk.ViewModel.SignUpViewModel
import com.example.socialtalk.Utils.EventHandlerForCode
import com.example.socialtalk.Utils.EventHandlerForNumber
import com.example.socialtalk.authscreens.OtpScreen
import com.example.socialtalk.authscreens.SignUp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// If you want to use dependency injection in Navigation Compose, you need to mark your Activity with @AndroidEntryPoint,
// because Hilt requires that all parent classes are also annotated with @AndroidEntryPoint.
@AndroidEntryPoint
class ActivtiyForCodeSend : ComponentActivity() {
    lateinit var verificationID: String
    lateinit var token: PhoneAuthProvider.ForceResendingToken
    @Inject
    lateinit var FBAuthObject: FirebaseAuth
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAuthentication()

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }

        setContent {
            // This method installs a splash screen on the given activity and returns
            // a SplashScreen instance that can be used to customize the splash screen behavior and appearance
            installSplashScreen()

            val rememberController = rememberNavController()
            NavHost(
                navController = rememberController,
                startDestination = ScreenRoutes.numberInput.route
            ) {
                composable(ScreenRoutes.numberInput.route) {
                    val numUiState = signUpViewModel.numAuthUiState
                    SignUp(
                        numUiState,
                        this@ActivtiyForCodeSend::codeToUserPhone,
                        signUpViewModel::codeEventOccurred,
                        signUpViewModel::eventPerformed,
                        signUpViewModel::callRepoToSaveUser,
                        rememberController
                    )
                }


                composable(ScreenRoutes.codeVerify.route) {
                    val otpState = signUpViewModel.mobileCodeState
                    OtpScreen(
                        otpState,
                        this@ActivtiyForCodeSend::generateCredential,
                        signUpViewModel::codeEventOccurred,
                        signUpViewModel::callRepoToSaveUser,
                    )
                }
            }


        }
    }

    private fun checkAuthentication() {
        if (FBAuthObject.currentUser != null) {
            //Intent to move to Other Activity (No need of else case after this function rest of activity code will execute after if block ignored )
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun authenticateNumber(
        phoneAuthCredential: PhoneAuthCredential,
        saveUserData: (() -> Unit) -> Unit,
        onCallingAuthenticate: () -> Unit,
    ) {
        // Authenticate whether entered OTP was legit or not and then Sign-In User
        FBAuthObject.signInWithCredential(phoneAuthCredential).addOnCompleteListener {
            if (it.isSuccessful) {
                saveUserData {
                    onCallingAuthenticate()
                    checkAuthentication()
                }
            } else {
                onCallingAuthenticate()
            }
        }.addOnFailureListener {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            onCallingAuthenticate()
        }
    }

    fun codeToUserPhone(
        phoneNumber: String,
        isResend: (EventHandlerForNumber) -> Unit,
        otpEventForLoading: (EventHandlerForCode) -> Unit,
        saveUserData: (() -> Unit) -> Unit,
        sentOrNot: Boolean,
        onCodeSend: () -> Unit,
        onVerificationFailed: (FirebaseException) -> Unit,
    ) {

        val callbacksAfterCompletion =
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(success: PhoneAuthCredential) {
                    //  It will trigger for some cases where verification code will be not necessary to be entered by user
                    //  to process verification rather user will receive otp with verification_ID (object) to avoid going through input process
                    isResend.invoke(EventHandlerForNumber.LoadingState(false))
                    authenticateNumber(
                        success,
                        saveUserData
                    ) {
                        otpEventForLoading.invoke(EventHandlerForCode.LoadingState(false))
                    }
                }

                override fun onVerificationFailed(failure: FirebaseException) {
                    isResend.invoke(EventHandlerForNumber.LoadingState(false))
                    isResend.invoke(EventHandlerForNumber.ResendChange(false))
                    onVerificationFailed.invoke(failure)
                }
                // It will trigger for cases where code is sent to user for input to complete verification
                override fun onCodeSent(
                    p0: String,
                    p1: PhoneAuthProvider.ForceResendingToken,
                ) {
                    super.onCodeSent(p0, p1)
                    isResend.invoke(EventHandlerForNumber.LoadingState(false))
                    isResend.invoke(EventHandlerForNumber.ResendChange(true))
                    // verification ID is used to tie the received verification code (OTP) to the ongoing verification session(ver ID).

                    // If the user does not receive the SMS of verification code,"Force Resending Token" is associated with
                    // the initial phone number verification request and serves as a reference
                    // to that request. Using this token, Firebase can re-trigger the OTP generation process for the same
                    // phone number verification session, making it easier to resend the OTP without needing the user to enter
                    // their phone number again or wait for timeouts.
                    onCodeSend()
                    verificationID = p0
                    token = p1
                }

            }
        val prepareRequest = PhoneAuthOptions.newBuilder(FBAuthObject)
            .setPhoneNumber(phoneNumber)
            .setTimeout(
                60L,
                TimeUnit.SECONDS
            ) // phone verification process should complete within 60 seconds. If it takes longer than that, the process will be canceled
            .setActivity(this)
            .setCallbacks(callbacksAfterCompletion)

        if (sentOrNot) {
            //In case of user has not receive message of verification code one can again ask for new code by using token generated
            PhoneAuthProvider.verifyPhoneNumber(
                prepareRequest.setForceResendingToken(token).build()
            )
        } else {
            PhoneAuthProvider.verifyPhoneNumber(prepareRequest.build())
        }

    }

    private fun generateCredential(
        verifyCode: String,
        saveUserData: (() -> Unit) -> Unit,
        onCallingAuthenticate: () -> Unit,
    ) {
        val phoneCredentials = PhoneAuthProvider.getCredential(verificationID, verifyCode)
        authenticateNumber(phoneCredentials, saveUserData, onCallingAuthenticate)
    }
}




