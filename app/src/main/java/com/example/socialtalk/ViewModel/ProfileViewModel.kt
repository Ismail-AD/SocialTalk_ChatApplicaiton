package com.example.socialtalk.ViewModel


import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.socialtalk.ReturnedData.UsersData
import com.example.socialtalk.repository.MainAndChat_Repository
import com.example.socialtalk.Utils.EventHandlerForProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val mainAndChatRepository: MainAndChat_Repository) :
    ViewModel() {

    var newDataUiState by mutableStateOf(UsersData())
    var userDataUiState by mutableStateOf(UsersData())
    var updateDataState by mutableStateOf(UnderProgress())

    init {
        getUserProfileInfo()
    }

    private fun getUserProfileInfo() {
        updateDataState = updateDataState.copy(loadingState = true)
        mainAndChatRepository.getUserInfo { message, usersData ->
            updateDataState = updateDataState.copy(loadingState = false)
            usersData?.let {
                userDataUiState = userDataUiState.copy(
                    username = it.username,
                    profileImage = it.profileImage, profileImageUrl = it.profileImageUrl
                )
                newDataUiState = newDataUiState.copy(
                    username = it.username,
                    profileImage = it.profileImage, profileImageUrl = it.profileImageUrl
                )
            }
            message?.let {
                updateDataState = updateDataState.copy(updateProcessResult = it)
            }
        }
    }

    fun handleOccurredEvent(eventHandler: EventHandlerForProfile) {
        when (eventHandler) {
            is EventHandlerForProfile.NameChange -> newDataUiState =
                newDataUiState.copy(username = eventHandler.suUsername)
            EventHandlerForProfile.Submit -> upDateUserData()
            is EventHandlerForProfile.ImageChange -> {
                mainAndChatRepository.saveImageUri(Uri.parse(eventHandler.imageUrl)) {
                    newDataUiState = newDataUiState.copy(profileImageUrl = eventHandler.imageUrl)
                }
            }
        }

    }

    private fun upDateUserData() {
        if (newDataUiState.username.isNotEmpty() && (newDataUiState.username.trim() != userDataUiState.username.trim() || newDataUiState.profileImage != userDataUiState.profileImage)) {
            userDataUiState = newDataUiState.copy()
            updateDataState = updateDataState.copy(loadingState = true)
            mainAndChatRepository.upDatePersonalInfo(userDataUiState.username) { DoneOrNot, Message ->
                updateDataState = if (DoneOrNot) {
                    updateDataState.copy(
                        updateProcessDone = true,
                        updateProcessResult = Message,
                        loadingState = false
                    )
                } else {
                    updateDataState.copy(
                        updateProcessDone = false,
                        updateProcessResult = Message,
                        loadingState = false
                    )
                }
            }
        }
    }

    data class UnderProgress(
        val loadingState: Boolean = false,
        val updateProcessDone: Boolean = false,
        val updateProcessResult: String = "",
    )
}

