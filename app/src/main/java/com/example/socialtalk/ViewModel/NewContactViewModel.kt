package com.example.socialtalk.ViewModel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialtalk.Utils.EventHandlerForNewContact
import com.example.socialtalk.repository.authenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewContactViewModel @Inject constructor(private val authenticationRepository: authenticationRepository) :
    ViewModel() {
    var existingUserStateValues by mutableStateOf(ExistingUserState())
        private set
    var getPath by mutableStateOf(false)

    fun eventHandler(eventHandler: EventHandlerForNewContact) {
        when (eventHandler) {
            is EventHandlerForNewContact.ContactChange -> existingUserStateValues =
                existingUserStateValues.copy(euNum = eventHandler.ncNumber)

            EventHandlerForNewContact.Submit -> callRepoToValidateUser()

            is EventHandlerForNewContact.UsernameChange -> existingUserStateValues =
                existingUserStateValues.copy(euUsername = eventHandler.ncUsername)

        }
    }

    private fun callRepoToValidateUser() = viewModelScope.launch {
        if ("+92${existingUserStateValues.euNum}" != authenticationRepository.currentUserContact) {
            try {
                existingUserStateValues = existingUserStateValues.copy(LoadingState = true)
                authenticationRepository.userIsRegistered(
                    "+92${existingUserStateValues.euNum}",
                    existingUserStateValues.euUsername
                ) { isSucceeded, message ->
                    if (isSucceeded) {
                        existingUserStateValues =
                            existingUserStateValues.copy(
                                resultMessage = message,
                                contactSaved = true
                            )
                    } else {
                        getPath = false
                        existingUserStateValues =
                            existingUserStateValues.copy(resultMessage = message)
                    }
                }
            } catch (e: Exception) {
                getPath = false
                existingUserStateValues =
                    existingUserStateValues.copy(resultMessage = e.localizedMessage!!)
            } finally {
                existingUserStateValues = existingUserStateValues.copy(LoadingState = false)
            }
        } else {
            existingUserStateValues =
                existingUserStateValues.copy(resultMessage = "Don't re-enter your Contact Number !")
        }
    }

    data class ExistingUserState(
        val euUsername: String = "",
        val euNum: String = "",
        val LoadingState: Boolean = false,
        val resultMessage: String = "",
        val contactSaved: Boolean = false,
    )
}