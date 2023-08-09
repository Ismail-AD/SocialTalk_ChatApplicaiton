package com.example.socialtalk.navigationCompose

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.socialtalk.ChatPhaseScreens.EachChat
import com.example.socialtalk.ChatPhaseScreens.MasterScreen
import com.example.socialtalk.ChatPhaseScreens.SettingsHere
import com.example.socialtalk.ChatPhaseScreens.newContact
import com.example.socialtalk.Utils.ScreenRoutes
import com.example.socialtalk.ViewModel.ChatViewModel
import com.example.socialtalk.ViewModel.MainViewModel
import com.example.socialtalk.ViewModel.NewContactViewModel
import com.example.socialtalk.ViewModel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun NavigationCompose() {

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ScreenRoutes.Main.route) {
        //Profile
        composable(
            ScreenRoutes.profile.route, enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(600)
                )
            }, exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(600)
                )
            }, popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(600)
                )
            }, popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(600)
                )
            }
        ) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            SettingsHere(
                navController,
                profileViewModel.newDataUiState,
                profileViewModel.userDataUiState,
                profileViewModel::handleOccurredEvent,
                profileViewModel.updateDataState
            )
        }

        // MAIN-SCREEN
        composable(
            ScreenRoutes.Main.route,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = {
                fadeOut(tween(300))
            },
            popEnterTransition = {
                fadeIn(tween(200))
            }) {
            val mainViewModel: MainViewModel = hiltViewModel()
            MasterScreen(
                mainViewModel.chatMatesUiState,
                mainViewModel.searchBarState,
                mainViewModel::eventHandlerForSB,
                mainViewModel::getListData,
                navController
            )
        }

        // NEW-CHAT
        // If a composable require arguments then concat with its route the required arguments....Whenever we call each chat
        // we have to pass require arguments or it will crash.

        composable(
            ScreenRoutes.eachChat.route + "/{userid}/{username}", enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(600)
                )
            }, exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(600)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(600)
                )
            }, popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(600)
                )
            }) {
            val chatViewModel: ChatViewModel = hiltViewModel()
            EachChat(
                receiverId = it.arguments?.getString("userid"),
                loginUserId = firebaseAuth.currentUser?.uid,
                Username = it.arguments?.getString("username"),
                chatViewModel::handleEventMethods,
                chatViewModel.messageByUser, chatViewModel.getMessages(
                    firebaseAuth.currentUser?.uid ?: "",
                    it.arguments?.getString("userid") ?: ""
                ),
                chatViewModel::dialogEventHandler,
                chatViewModel.userInfoState,
                chatViewModel.dialogState,
                navController
            )
        }

        // NEW-CONTACT
        composable(ScreenRoutes.newUser.route, enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(600)
            )
        }, exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(600)
            )
        }, popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(600)
            )
        }, popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(600)
            )
        }) {
            val contactViewModel: NewContactViewModel = hiltViewModel()
            newContact(
                navController,
                contactViewModel.existingUserStateValues,
                contactViewModel::eventHandler
            )
        }
    }
}






