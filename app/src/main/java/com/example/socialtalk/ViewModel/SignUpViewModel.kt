package com.example.socialtalk.ViewModel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialtalk.Utils.EventHandlerForCode
import com.example.socialtalk.Utils.EventHandlerForNumber
import com.example.socialtalk.repository.authenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(private val authenticationRepository: authenticationRepository) :
    ViewModel() {

    var numAuthUiState by mutableStateOf(SignUpUIStateNumber())
        private set
    var mobileCodeState by mutableStateOf(mobileCode())
        private set

    fun codeEventOccurred(eventHandler: EventHandlerForCode) {
        mobileCodeState = when (eventHandler) {
            is EventHandlerForCode.CodeChange -> mobileCodeState.copy(verifyCode = eventHandler.suCode)

            is EventHandlerForCode.LoadingState -> mobileCodeState.copy(showLoadingState = eventHandler.triggerLoading)
        }
    }

    fun eventPerformed(eventHandler: EventHandlerForNumber) {
        numAuthUiState = when (eventHandler) {
            is EventHandlerForNumber.NumberChange -> numAuthUiState.copy(newNumber = eventHandler.suNumber)
            is EventHandlerForNumber.UsernameChange -> numAuthUiState.copy(newUsername = eventHandler.suusername)
            is EventHandlerForNumber.ResendChange -> numAuthUiState.copy(resendOrNot = eventHandler.ResendState)
            is EventHandlerForNumber.LoadingState -> numAuthUiState.copy(showLoadingState = eventHandler.triggerLoading)
        }
    }

    fun callRepoToSaveUser(userDataSaved: () -> Unit) {
        viewModelScope.launch {
            authenticationRepository.userInfoToDB("+92${numAuthUiState.newNumber}",numAuthUiState.newUsername,userDataSaved)
        }
    }

    data class SignUpUIStateNumber(
        val newUsername: String = "",
        val newNumber: String = "",
        val showLoadingState: Boolean = false,
        val resendOrNot: Boolean = false,
    )

    data class mobileCode(val verifyCode: String = "", val showLoadingState: Boolean = false)

}