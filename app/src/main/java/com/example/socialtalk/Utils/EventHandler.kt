package com.example.socialtalk.Utils


sealed interface EventHandlerForProfile {
    data class NameChange(val suUsername: String) : EventHandlerForProfile
    data class ImageChange(val imageUrl: String) : EventHandlerForProfile
    object Submit : EventHandlerForProfile
}

sealed interface EventHandlerForNewContact {
    data class UsernameChange(val ncUsername: String) : EventHandlerForNewContact
    data class ContactChange(val ncNumber: String) : EventHandlerForNewContact
    object Submit : EventHandlerForNewContact
}


sealed interface EventHandlerSearchBar {
    data class SearchBarOpen(val openSB: Boolean) : EventHandlerSearchBar
    data class SearchedData(val textToSearch: String) : EventHandlerSearchBar
    object getOriginalList : EventHandlerSearchBar
}

sealed interface EventHandlerForDialogNewContact {
    data class UsernameChange(val ncUsername: String) : EventHandlerForDialogNewContact
    data class ContactChange(val ncNumber: String) : EventHandlerForDialogNewContact
    data class DialogOpen(val openOrClose: Boolean) : EventHandlerForDialogNewContact
    data class DialogResult(val result: String) : EventHandlerForDialogNewContact
    data class Submit(val receiverID: String) : EventHandlerForDialogNewContact
}

sealed interface EventHandlerForNumber {
    data class NumberChange(val suNumber: String) : EventHandlerForNumber
    data class UsernameChange(val suusername: String) : EventHandlerForNumber
    data class ResendChange(val ResendState: Boolean) : EventHandlerForNumber
    data class LoadingState(val triggerLoading: Boolean) : EventHandlerForNumber
}

sealed interface EventHandlerForCode {
    data class CodeChange(val suCode: String) : EventHandlerForCode
    data class LoadingState(val triggerLoading: Boolean) : EventHandlerForCode
}

sealed interface EventHandlerForContacts {
    data class MessageChanged(val msgByUser: String) : EventHandlerForContacts
    data class MessageSent(
        val senderID: String,
        val receiverID: String,
        val msg: String,
    ) : EventHandlerForContacts
}