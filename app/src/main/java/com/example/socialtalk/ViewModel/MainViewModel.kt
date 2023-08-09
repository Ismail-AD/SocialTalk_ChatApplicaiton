package com.example.socialtalk.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialtalk.ReturnedData.chatMateData
import com.example.socialtalk.Utils.ReturnedResult
import com.example.socialtalk.Utils.EventHandlerSearchBar
import com.example.socialtalk.repository.MainAndChat_Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainAndChatRepository: MainAndChat_Repository) :
    ViewModel() {

    var chatMatesUiState by mutableStateOf(DataUi())
    var searchBarState by mutableStateOf(TopBarState())

    fun eventHandlerForSB(eventHandlerSearchBar: EventHandlerSearchBar) {
        when (eventHandlerSearchBar) {
            is EventHandlerSearchBar.SearchBarOpen -> {
                searchBarState =
                    searchBarState.copy(isSearchBarVisible = eventHandlerSearchBar.openSB)
                searchBarState =
                    searchBarState.copy(listOfChatMates = chatMatesUiState.listData)
            }

            is EventHandlerSearchBar.SearchedData -> {
                searchBarState =
                    searchBarState.copy(searchedText = eventHandlerSearchBar.textToSearch)
                filterTheList(searchBarState.listOfChatMates, eventHandlerSearchBar.textToSearch)
            }

            EventHandlerSearchBar.getOriginalList -> chatMatesUiState =
                chatMatesUiState.copy(listData = searchBarState.listOfChatMates)
        }
    }

    private fun filterTheList(listOfChatMates: MutableList<chatMateData>, textToSearch: String) {
        val filteredList = listOfChatMates.filter {
            it.username!!.contains(textToSearch, ignoreCase = true)
        }
        chatMatesUiState = chatMatesUiState.copy(listData = filteredList.toMutableList())
    }


    fun getListData() = viewModelScope.launch {
        mainAndChatRepository.getChatMates().collect {
            chatMatesUiState = when (it) {
                is ReturnedResult.Failure -> chatMatesUiState.copy(
                    error = true,
                    loadingState = false
                )
                ReturnedResult.Loading -> chatMatesUiState.copy(loadingState = true)
                is ReturnedResult.Success -> {
                    chatMatesUiState.copy(listData = it.data, loadingState = false)
                }
            }
        }
    }

    data class DataUi(
        val listData: MutableList<chatMateData> = mutableListOf(),
        val error: Boolean = false,
        val loadingState: Boolean = false,
    )

    data class TopBarState(
        val isSearchBarVisible: Boolean = false,
        val searchedText: String = "",
        val listOfChatMates: MutableList<chatMateData> = mutableListOf(),
    )
}
