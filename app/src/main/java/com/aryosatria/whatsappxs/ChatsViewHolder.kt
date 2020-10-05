package com.aryosatria.whatsappxs

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aryosatria.whatsappxs.listener.ChatClickListener
import com.aryosatria.whatsappxs.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chats.*

class ChatsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),LayoutContainer {

    private val firebaseDb = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var partnerId: String? = null
    private var chatName: String? = null
    private var chatImageUrl: String? = null
    fun bindItem(chatId: String, listener: ChatClickListener?) {
        progress_layout.visibility = View.VISIBLE
        progress_layout.setOnTouchListener { v, event -> true }

        firebaseDb.collection(DATA_CHATS)
            .document(chatId)
            .get()
            .addOnSuccessListener {
                val chatParticipants = it[DATA_CHAT_PARTICIPANTS]
                if (chatParticipants !=null){
                    for (participant in chatParticipants as ArrayList<String>) {
                        if (participant != null&& !participant.equals(userId)){
                            partnerId = participant
                            firebaseDb.collection(DATA_USERS).document(partnerId!!).get()
                                .addOnSuccessListener {
                                    val user = it.toObject(User::class.java)
                                    chatImageUrl = user?.imageUrl
                                    chatName = user?.name
                                    txt_chats.text = user?.name
                                    populateImage(img_chats.context,user?.imageUrl,
                                        img_chats,R.drawable.ic_user)
                                    progress_layout.visibility = View.GONE
                                }
                                .addOnFailureListener { e ->
                                    e.printStackTrace()
                                    progress_layout.visibility = View.GONE
                                }
                        }
                    }
                }
                progress_layout.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                progress_layout.visibility = View.GONE
            }
        itemView.setOnClickListener {
            listener?.onChatClicked(chatId, userId, chatImageUrl, chatName)
        }
    }
}