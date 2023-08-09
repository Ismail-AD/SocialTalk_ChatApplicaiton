package com.example.socialtalk.repository

import com.example.socialtalk.ReturnedData.UsersData
import com.example.socialtalk.ReturnedData.chatMateData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class authenticationRepository @Inject constructor(
    private val FBAuthObject: FirebaseAuth,
    private val firebaseDB: FirebaseDatabase,
) {

    suspend fun userInfoToDB(
        num: String,
        username: String,
        userDataSaved: () -> Unit,
    ): Unit = withContext(Dispatchers.IO) {
        val reference = firebaseDB.reference.child("UsersData")
        val inDatabase = reference.orderByChild("num").equalTo(num).get().await()
        FBAuthObject.currentUser?.uid?.let {
            if (inDatabase.exists()) {
                reference.child(it).child("username").setValue(username).await()
            } else {
                val usersData = UsersData(
                    username = username,
                    num = num,
                    userid = it
                )
                reference.child(it).setValue(usersData).await()
            }
        }
        userDataSaved()
    }

    suspend fun userIsRegistered(
        contact: String,
        username: String,
        onComplete: (Boolean, String) -> Unit,
    ) = withContext(Dispatchers.IO) {

        // launching two child coroutines to run in-parallel
        // IF User Already Exists in my saved Contacts then don't do duplication
        val contactConfirmQuery =
            firebaseDB.reference.child("ChatMate").child(FBAuthObject.currentUser!!.uid)
                .orderByChild("userContact").equalTo(contact)

        val resultOfContact = async {
            contactConfirmQuery.get().await()
        }

        // Registered user of application or not
        val userReference =
            firebaseDB.reference.child("UsersData").orderByChild("num").equalTo(contact)
        val resultOfApp = async {
            userReference.get().await()
        }

        val resultOfBoth = awaitAll(resultOfApp, resultOfContact)
        val inApp = resultOfBoth[0]
        val inContact = resultOfBoth[1]

        if (inApp.exists()) {
            if (!inContact.exists()) {
                val mateUserid = inApp.children.first().child("userid").value as? String
                val chatMateData = chatMateData(
                    username = username.replaceFirstChar { it.uppercaseChar() },
                    userContact = contact,
                    userid = mateUserid
                )
                mateUserid?.let {
                    firebaseDB.reference.child("ChatMate").child(FBAuthObject.currentUser!!.uid)
                        .child(it).setValue(chatMateData).addOnSuccessListener {
                            onComplete.invoke(true, "Contact Saved Successfully")
                        }.addOnFailureListener {
                            onComplete.invoke(false, "Something Went Wrong")
                        }
                }

            } else {
                withContext(Dispatchers.Main) {
                    onComplete.invoke(false, "User Is Already in your Contacts")
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                onComplete.invoke(false, "User is not registered in application !")
            }
        }
    }

    val currentUserContact = FBAuthObject.currentUser?.phoneNumber
}