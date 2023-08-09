package com.example.socialtalk.repository


import android.net.Uri
import com.example.socialtalk.ReturnedData.ChatMessage
import com.example.socialtalk.ReturnedData.UsersData
import com.example.socialtalk.ReturnedData.chatMateData
import com.example.socialtalk.Utils.ReturnedResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainAndChat_Repository @Inject constructor(
    private val FBAuthObject: FirebaseAuth,
    private val RealtimeDBObject: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage,
) {

    fun getUserInfo(onComplete: (String?, UsersData?) -> Unit) {
        RealtimeDBObject.reference.child("UsersData").child(FBAuthObject.currentUser!!.uid)
            .get().addOnSuccessListener {
                it.getValue(UsersData::class.java)?.let { userData ->
                    onComplete("", userData)
                }
            }.addOnFailureListener {
                onComplete(it.localizedMessage!!, UsersData())
            }
    }

    fun saveImageUri(imageUri: Uri, onComplete: () -> Unit) {
        val storageRef = firebaseStorage.reference.child(FBAuthObject.currentUser!!.uid)
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    RealtimeDBObject.reference.child("UsersData")
                        .child(FBAuthObject.currentUser!!.uid).child("profileImageUrl")
                        .setValue(it.toString()).addOnSuccessListener {
                            onComplete()
                        }
                }
            }
    }

    fun upDatePersonalInfo(
        username: String, onComplete: (Boolean, String) -> Unit,
    ) {
        RealtimeDBObject.reference.child("UsersData").child(FBAuthObject.currentUser!!.uid)
            .child("username").setValue(username).addOnSuccessListener {
                onComplete(true, "Updated Successfully !")
            }.addOnFailureListener {
                onComplete(false, it.localizedMessage!!)
            }
    }

    fun upDateFriendName(
        username: String, friendID: String, onComplete: (Boolean, String) -> Unit,
    ) {
        RealtimeDBObject.reference.child("ChatMate").child(FBAuthObject.currentUser!!.uid)
            .child(friendID).child("username").setValue(username).addOnSuccessListener {
                onComplete(true, "Updated Successfully !")
            }.addOnFailureListener {
                onComplete(false, it.localizedMessage!!)
            }
    }

    suspend fun saveMessages(
        srUID: String,
        rSUID: String,
        message: String,
    ) =
        withContext(Dispatchers.IO) {

            val friendDBReference = RealtimeDBObject.reference.child("ChatMate")
            val dbReference = RealtimeDBObject.reference.child("Messages")
            val valuesToUpdate = HashMap<String, Any?>()

            val resultOfContact = async {
                val query = friendDBReference.child(rSUID)
                    .orderByChild("userContact").equalTo(FBAuthObject.currentUser?.phoneNumber)
                    .get()
                    .await()
                query.children.firstOrNull()
            }

            val messageToStore = ChatMessage(
                message = message,
                userID = FBAuthObject.currentUser?.uid,
                timeStamp = System.currentTimeMillis()
            )
            dbReference.child(srUID + rSUID).push().setValue(messageToStore)
            dbReference.child(rSUID + srUID).push().setValue(messageToStore)
            valuesToUpdate["lastMsg"] = messageToStore.message
            valuesToUpdate["chatTime"] = messageToStore.timeStamp

            friendDBReference.child(srUID).child(rSUID).updateChildren(valuesToUpdate)

            val friendOrNot =
                resultOfContact.await() //after saving message check that sender is friend/In-Contact of/with receiver or not
            friendOrNot?.let {
                friendDBReference.child(rSUID).child(srUID).updateChildren(valuesToUpdate)
            }
            if (friendOrNot == null) {
                val chatMateData = chatMateData(
                    username = FBAuthObject.currentUser?.phoneNumber,
                    userContact = FBAuthObject.currentUser?.phoneNumber,
                    userid = srUID,
                    lastMsg = valuesToUpdate["lastMsg"] as String?,
                    chatTime = valuesToUpdate["chatTime"] as Long?
                )
                RealtimeDBObject.reference.child("ChatMate").child(rSUID).child(srUID)
                    .setValue(chatMateData).await()
            }
        }

    // we will register a listener and when our work is done will unregister it so it will not keep listening
    // to data changes when we don't need and then close the channel as flow is completed or canceled.

    fun getMessages(srUID: String, rSUID: String): Flow<MutableList<ChatMessage>> = callbackFlow {
        val dbRef = RealtimeDBObject.reference.child("Messages").child(srUID + rSUID)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList =
                    mutableListOf<ChatMessage>() // we will create a new list with updated data
                for (snapshotData in snapshot.children) {
                    val singleMessage = snapshotData.getValue(ChatMessage::class.java)
                    singleMessage?.let {
                        messageList.add(it)
                    }
                }
                trySend(messageList)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        dbRef.addValueEventListener(listener)
        awaitClose {
            // unregister listener before closing channel (clean up resources)
            dbRef.removeEventListener(listener)
            close()
        }

    }

    fun getChatMates(): Flow<ReturnedResult<MutableList<chatMateData>>> = callbackFlow {
        trySend(ReturnedResult.Loading)
        val list: MutableList<chatMateData> = mutableListOf()
        val dataReference =
            RealtimeDBObject.reference.child("ChatMate").child(FBAuthObject.currentUser!!.uid)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear() //clear the list to avoid duplication
                for (dataFromDB in snapshot.children) {
                    val chatMateObject = dataFromDB.getValue(chatMateData::class.java)
                    chatMateObject?.let { chatMateData ->
                        RealtimeDBObject.reference.child("UsersData").child(chatMateData.userid!!)
                            .child("profileImageUrl").get().addOnSuccessListener { dataSnap ->
                                chatMateData.profileImageUrl = dataSnap.value.toString()
                                dataReference.child(chatMateData.userid).child("profileImageUrl")
                                    .setValue(chatMateData.profileImageUrl)
                            }
                        list.add(chatMateData)
                    }
                }
                trySend(ReturnedResult.Success(list))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(ReturnedResult.Failure(error.message))
            }
        }
        dataReference.addValueEventListener(listener)
        awaitClose {
            dataReference.removeEventListener(listener)
            close()
        }

    }
}