package com.example.socialtalk.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialtalk.ReturnedData.ChatMessage
import com.example.socialtalk.ReturnedData.UsersData
import com.example.socialtalk.Utils.EventHandlerForContacts
import com.example.socialtalk.Utils.EventHandlerForDialogNewContact
import com.example.socialtalk.repository.MainAndChat_Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val mainRepository: MainAndChat_Repository) :
    ViewModel() {
    var messageByUser by mutableStateOf(ChatMsgUiState())
    var dialogState by mutableStateOf(DialogContactSave())
    var userInfoState by mutableStateOf(UsersData())

    fun dialogEventHandler(eventHandler: EventHandlerForDialogNewContact) {
        when (eventHandler) {
            is EventHandlerForDialogNewContact.ContactChange -> {
                userInfoState =
                    userInfoState.copy(num = eventHandler.ncNumber)
            }

            is EventHandlerForDialogNewContact.DialogOpen -> dialogState =
                dialogState.copy(dialogOpen = eventHandler.openOrClose)

            is EventHandlerForDialogNewContact.Submit -> {
                userInfoState = userInfoState.copy(userid = eventHandler.receiverID)
                saveDialogContact(userInfoState.username, userInfoState.userid)
            }

            is EventHandlerForDialogNewContact.UsernameChange -> userInfoState =
                userInfoState.copy(username = eventHandler.ncUsername)


            is EventHandlerForDialogNewContact.DialogResult -> dialogState =
                dialogState.copy(resultMessage = eventHandler.result)

        }
    }

    private fun saveDialogContact(username: String, receiverId: String) {
        mainRepository.upDateFriendName(username, receiverId) { isSuccess, Message ->
            dialogState = if (isSuccess) {
                dialogState.copy(dialogOpen = false, resultMessage = Message)
            } else {
                dialogState.copy(resultMessage = Message)
            }
        }
    }


    fun handleEventMethods(eventHandler: EventHandlerForContacts) {
        when (eventHandler) {
            is EventHandlerForContacts.MessageSent -> {
                if (eventHandler.msg.trim().isNotEmpty()) {
                    messageByUser = messageByUser.copy(message = "")
                    saveMessageToDB(
                        eventHandler.senderID,
                        eventHandler.receiverID,
                        eventHandler.msg
                    )
                }
            }

            is EventHandlerForContacts.MessageChanged -> {
                messageByUser = messageByUser.copy(message = eventHandler.msgByUser)
            }
        }
    }

    fun getMessages(srUID: String, rSID: String): Flow<MutableList<ChatMessage>> {
        return mainRepository.getMessages(srUID, rSID)
    }

    private fun saveMessageToDB(sRID: String, rSID: String, msg: String) = viewModelScope.launch {
        mainRepository.saveMessages(sRID, rSID, msg)
    }

    data class ChatMsgUiState(val message: String = "")

    data class DialogContactSave(
        val dialogOpen: Boolean = false,
        val resultMessage: String = "",
    )
}