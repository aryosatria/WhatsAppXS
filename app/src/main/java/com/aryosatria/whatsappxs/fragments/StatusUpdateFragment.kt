package com.aryosatria.whatsappxs.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.aryosatria.whatsappxs.R
import com.aryosatria.whatsappxs.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_status_update.*

class StatusUpdateFragment : Fragment() {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var imageUrl = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_status_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress_layout.setOnTouchListener { v, event -> true }
        fab_status.setOnClickListener { onUpdate() }
        populateImage(context,imageUrl,img_status_update)

        lay_status.setOnClickListener {
            if (isAdded) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_CODE_PHOTO)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            storageImage(data?.data)
        }
    }

    private fun storageImage(imageUri: Uri?) {
        if (imageUri!=null&& userId !=null) {
            Toast.makeText(activity, "Uploading...", Toast.LENGTH_SHORT).show()
            progress_layout.visibility = View.VISIBLE

            val filePath = firebaseStorage.child(DATA_IMAGES).child("${userId}_status")

            filePath.putFile(imageUri)
                .addOnSuccessListener {
                    filePath.downloadUrl
                        .addOnSuccessListener { taskSnapShot ->
                            val url = taskSnapShot.toString()

                            firebaseDB.collection(DATA_USERS)
                                .document(userId)
                                .update(DATA_USER_STATUS,url)
                                .addOnSuccessListener {
                                    imageUrl = url
                                    populateImage(context,imageUrl,img_status_update)
                                }
                            progress_layout.visibility = View.GONE
                        }
                        .addOnFailureListener { onUploadFailure() }
                }
                .addOnFailureListener { onUploadFailure() }
        }
    }

    fun onUpdate() {
        progress_layout.visibility = View.VISIBLE
        val map = HashMap<String,Any>()
        map[DATA_USER_STATUS] = edt_status_update.text.toString()
        map[DATA_USER_STATUS_URL] = imageUrl
        map[DATA_USER_STATUS_TIME] = getTime()

        firebaseDB.collection(DATA_USERS)
            .document(userId!!)
            .update(map)
            .addOnSuccessListener {
                progress_layout.visibility = View.GONE
                Toast.makeText(activity,"Status updated.", Toast.LENGTH_SHORT).show()
            }

            .addOnFailureListener {
                progress_layout.visibility = View.GONE
                Toast.makeText(activity,"Status update failed.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onUploadFailure() {
        Toast.makeText(activity,"Image upload failed. Please try again later",
        Toast.LENGTH_SHORT).show()
        progress_layout.visibility = View.GONE
    }
}