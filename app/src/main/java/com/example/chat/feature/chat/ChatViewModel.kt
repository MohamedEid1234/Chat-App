package com.example.chat.feature.chat

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.chat.R
import com.example.chat.model.Message
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.BucketApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


@HiltViewModel
class ChatViewModel @Inject constructor(@ApplicationContext val context: Context) : ViewModel() {


    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val message = _messages.asStateFlow()
    private val db = Firebase.database

    // Supabase client initialization
    val supabaseClient = createSupabaseClient(
        supabaseUrl = "https://syiqosnnlnxukodesqdy.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InN5aXFvc25ubG54dWtvZGVzcWR5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzc1OTQzMDUsImV4cCI6MjA1MzE3MDMwNX0.TvoEotd3gPfAuYw7wyn0TVcaApSAa4e9iIdq7RFeWwI"
    ) {
        install(Postgrest)//database
        install(Storage)//Storage
        install(Auth) //Auth
    }






    fun sendMessage(channelID: String, messageText: String?, image: String? = null) {
        val message = Message(
            db.reference.push().key ?: UUID.randomUUID().toString(),
            Firebase.auth.currentUser?.uid ?: "",
            messageText,
            System.currentTimeMillis(),
            Firebase.auth.currentUser?.displayName ?: "",
            null,
            image
        )

        db.reference.child("messages").child(channelID).push().setValue(message)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    postNotificationToUsers(channelID, message.senderName, messageText ?: "")
                }
            }
    }


    fun uploadImageToSupabase(
        uri: Uri,
        fileName: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val storage = supabaseClient.storage
        val bucket: BucketApi = storage["Images"] // Specify your bucket name here

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val byteArray = context.contentResolver.openInputStream(uri)?.readBytes()
                    ?: throw Exception("Failed to read file")

                // Upload the file with metadata
                bucket.upload(
                    fileName,
                    byteArray
                )
                Log.d("ChatViewModel", "File uploaded successfully: $fileName")


// Get the public URL of the uploaded file
                val fileUrl = bucket.publicUrl(fileName)



               /* val fileUrl = bucket.createSignedUrl(fileName, expiresIn = 7.days)*/
                Log.d("ChatViewModel", "Generated Signed URL: $fileUrl")

                onSuccess(fileUrl)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Failed to upload image: ${e.message}", e)
                onError(e.message ?: "Unknown error")
            }
        }
    }


    fun sendImageMessage(uri: Uri, channelID: String) {
        val fileName = "image_${System.currentTimeMillis()}.jpg"

        uploadImageToSupabase(uri, fileName,
            onSuccess = { fileUrl ->
                sendMessage(channelID, messageText = null, image = fileUrl)
                Log.d("ChatViewModel", "Generated Image URL: $fileUrl")

            },
            onError = { error ->
                Log.e("ChatViewModel", "Image upload failed: $error")
            }
        )
    }


    fun listenForMessages(channelID: String) {
        db.getReference("messages").child(channelID).orderByChild("createdAt")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Message>()
                    snapshot.children.forEach { data ->
                        val message = data.getValue(Message::class.java)
                        message?.let {
                            list.add(it)
                        }
                    }
                    _messages.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        subscribeForNotification(channelID)
        registerUserIdtoChannel(channelID)
    }

    fun getAllUserEmails(channelID: String, callback: (List<String>) -> Unit) {
        val ref = db.reference.child("channels").child(channelID).child("users")
        val userIds = mutableListOf<String>()
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    userIds.add(it.value.toString())
                }
                callback.invoke(userIds)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.invoke(emptyList())
            }
        })
    }

    fun registerUserIdtoChannel(channelID: String) {
        val currentUser = Firebase.auth.currentUser
        val ref = db.reference.child("channels").child(channelID).child("users")
        ref.child(currentUser?.uid ?: "").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        ref.child(currentUser?.uid ?: "").setValue(currentUser?.email)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
        )

    }

    private fun subscribeForNotification(channelID: String) {
        FirebaseMessaging.getInstance().subscribeToTopic("group_$channelID")
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("ChatViewModel", "Subscribed to topic: group_$channelID")
                } else {
                    Log.d("ChatViewModel", "Failed to subscribe to topic: group_$channelID")
                    // Handle failure
                }
            }
    }

    private fun postNotificationToUsers(
        channelID: String,
        senderName: String,
        messageContent: String
    ) {
        val fcmUrl = "https://fcm.googleapis.com/v1/projects/chat-2a0f0/messages:send"
        val jsonBody = JSONObject().apply {
            put("message", JSONObject().apply {
                put("topic", "group_$channelID")
                put("notification", JSONObject().apply {
                    put("title", "New message in $channelID")
                    put("body", "$senderName: $messageContent")
                })
            })
        }

        val requestBody = jsonBody.toString()

        val request = object : StringRequest(Method.POST, fcmUrl, Response.Listener {
            Log.d("ChatViewModel", "Notification sent successfully")
        }, Response.ErrorListener {
            Log.e("ChatViewModel", "Failed to send notification")
        }) {
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${getAccessToken()}"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }

    private fun getAccessToken(): String {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.chatter_key)
            val googleCreds = GoogleCredentials.fromStream(inputStream)
                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
            googleCreds.refreshAccessToken().tokenValue
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Failed to get access token", e)
            ""
        }
    }


}





