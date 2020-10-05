package com.aryosatria.whatsappxs.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aryosatria.whatsappxs.R
import com.aryosatria.whatsappxs.adapter.ConversationAdapter
import com.aryosatria.whatsappxs.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_conversation.*

class ConversationActivity : AppCompatActivity() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val conversationAdapter = ConversationAdapter(arrayListOf(), userId)
    private val firebaseDb = FirebaseFirestore.getInstance()
    private var chatId: String? = null
    private var imageUrl: String? = null
    private var otherUserId: String? = null
    private var chatName: String? = null
    private var phone: String? = null

    companion object {
        private val PARAM_CHAT_ID = "Chat_id"
        private val PARAM_IMAGE_URL = "Image_url"
        private val PARAM_OTHER_USER_ID = "Other_user_id"
        private val PARAM_CHAT_NAME = "Chat_name"

        fun newIntent(context: Context?, chatId: String?, imageUrl: String?, otherUserId: String?,
                      chatName: String?): Intent {
            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra(PARAM_CHAT_ID, chatId)
            intent.putExtra(PARAM_IMAGE_URL, imageUrl)
            intent.putExtra(PARAM_OTHER_USER_ID, otherUserId)
            intent.putExtra(PARAM_CHAT_NAME, chatName)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        setSupportActionBar(toolbar_conversation)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_conversation.setNavigationOnClickListener { onBackPressed() }

        chatId = intent.extras?.getString(PARAM_CHAT_ID)
        imageUrl = intent.extras?.getString(PARAM_IMAGE_URL)
        chatName = intent.extras?.getString(PARAM_CHAT_NAME)
        otherUserId = intent.extras?.getString(PARAM_OTHER_USER_ID)

        if (chatId.isNullOrEmpty() || userId.isNullOrEmpty()) {
            Toast.makeText(this, "Chat Room Error", Toast.LENGTH_SHORT).show()
            finish()
        }
        populateImage(this, imageUrl, img_toolbar, R.drawable.ic_user)
        txt_toolbar.text = chatName
        rv_message.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = conversationAdapter
        }

        firebaseDb.collection(DATA_USERS).document(userId!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                phone = user?.phone
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                finish()
            }
        firebaseDb.collection(DATA_CHATS)
            .document(chatId!!)
            .collection(DATA_CHAT_MESSAGE)
            .orderBy(DATA_CHAT_MESSAGE_TIME)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    firebaseFirestoreException.printStackTrace()
                    return@addSnapshotListener
                } else {
                    if (querySnapshot != null)
                        for (change in querySnapshot.documentChanges) {
                            when (change.type) {
                                DocumentChange.Type.ADDED -> {
                                    val message = change.document.toObject(Message::class.java)
                                    if (message != null) {
                                        conversationAdapter.addMessage(message)
                                        rv_message.post {
                                            rv_message.smoothScrollToPosition(conversationAdapter.itemCount - 1)
                                        }
                                    }
                                }
                            }
                        }
                }
            }

        btn_send.setOnClickListener {
            if (!edt_message.text.isNullOrEmpty()) {
                val message =
                    Message(userId, edt_message.text.toString(), System.currentTimeMillis())

                firebaseDb.collection(DATA_CHATS)
                    .document(chatId!!)
                    .collection(DATA_CHAT_MESSAGE)
                    .document()
                    .set(message)
                edt_message.setText("", TextView.BufferType.EDITABLE)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_conversation,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.action_call -> {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}