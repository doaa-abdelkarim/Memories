package com.example.memories.ui

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.memories.R
import com.example.memories.adapter.PhotoAdapter
import com.example.memories.db.Repository
import com.example.memories.model.Memory
import com.example.memories.model.MemoryWithPhotos
import com.example.memories.model.Photo
import com.example.memories.util.Constants
import com.example.memories.util.UserLocation
import com.example.memories.util.Utility
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.add_edit_memory.*
import java.util.*

class AddEditMemoryView : AppCompatActivity() {

    var memoryId: Long = -1L
    var lastTimeNotified: Long = -1L
    var photos = arrayListOf<Photo>()
    val userLocation = UserLocation()
    val onSuccessListener = OnSuccessListener<Location> {
        val latitude = it?.latitude
        val longitude = it?.longitude

        saveEditMemory(latitude, longitude)
    }

    lateinit var repository: Repository
    lateinit var addEditMemory: AddEditMemory
    lateinit var photoAdapter: PhotoAdapter
    lateinit var editTextMemoryDesc: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_edit_memory)

        preExecute()
        initWinAndViews()
        initPhotosRecyclerView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_TAKE_PHOTO -> {
                    photos.add(0, Photo(uri = addEditMemory.photoURI.toString()))
                    photoAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun preExecute() {
        memoryId = intent.getLongExtra(Constants.EXTRA_MEMORY_ID, -1)
        lastTimeNotified = intent.getLongExtra(
            Constants.EXTRA_MEMORY_LAST_TIME_MODIFIED,
            -1
        )

        repository = Repository(this)
        addEditMemory = AddEditMemory(this)
        photoAdapter = PhotoAdapter(photos)
        photoAdapter.onItemClickListener = object : PhotoAdapter.OnItemClickListener {
            override fun onDelete(position: Int) {
                photos.removeAt(position)
                photoAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initWinAndViews() {
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val textViewSave = text_view_save
        val textViewCancel = text_view_cancel
        editTextMemoryDesc = edit_text_memory_desc
        val imageViewTakePhoto: ImageView = image_view_take_photo
        val textViewTime = text_view_time

        textViewTime.text = Utility.formatTime(Date(Utility.getCurrentTime()))

        if (memoryId != -1L) {
            textViewSave.text = getString(R.string.edit)
            editTextMemoryDesc.requestFocus()
            val memoryWithPhotos = repository.getMemoryWithPhotosById(memoryId)
            memoryWithPhotos?.let {
                editTextMemoryDesc.setText(it.memory.description)
                photoAdapter.refillPhotosList(it.photos as ArrayList<Photo>)
            }
        }

        imageViewTakePhoto.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)?.also {
                addEditMemory.dispatchTakePictureIntent()
            }
        }

        textViewSave.setOnClickListener {
            if (editTextMemoryDesc.text.trim().isEmpty()) {
                Toast
                    .makeText(this, getString(R.string.define_memory), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            userLocation.getUserLocation(this, onSuccessListener)
        }
        textViewCancel.setOnClickListener { finish() }
    }

    private fun initPhotosRecyclerView() {
        val recyclerViewPhotos: RecyclerView = recycler_view_photos
        recyclerViewPhotos.apply {
            adapter = photoAdapter
            layoutManager = LinearLayoutManager(
                this@AddEditMemoryView,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
        }
        LinearSnapHelper().attachToRecyclerView(recyclerViewPhotos)
    }

    private fun saveEditMemory(latitude: Double?, longitude: Double?) {
        if (memoryId == -1L) {
            repository.insertMemoryWithPhotos(
                MemoryWithPhotos(
                    Memory(
                        description = editTextMemoryDesc.text.toString(),
                        time = Utility.getCurrentTime(),
                        latitude = latitude,
                        longitude = longitude
                    ),
                    photos
                )
            )
            setResult(Activity.RESULT_OK)
        } else {
            repository.updateMemoryWithPhotos(
                MemoryWithPhotos(
                    Memory(
                        memoryId,
                        editTextMemoryDesc.text.toString(),
                        Utility.getCurrentTime(),
                        latitude = latitude,
                        longitude = longitude,
                        lastTimeNotified = lastTimeNotified
                    ),
                    photos
                )
            )
            setResult(
                Activity.RESULT_OK,
                Intent()
                    .putExtra(
                        Constants.EXTRA_MEMORY_POSITION,
                        intent.getIntExtra(Constants.EXTRA_MEMORY_POSITION, -1)
                    )
            )
        }
        finish()
    }
}