package com.example.memories.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memories.R
import com.example.memories.adapter.MemoryAdapter
import com.example.memories.db.Repository
import com.example.memories.util.Constants
import com.example.memories.util.RunTimePermission
import com.example.memories.util.TrackingLocation
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.home.*

class HomeView : AppCompatActivity() {

    val runTimePermission = RunTimePermission(
        this, Constants.LOCATION_PERMISSION_CODE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    lateinit var trackingLocation: TrackingLocation
    lateinit var memoriesListViewModel: Home

    lateinit var repository: Repository
    lateinit var memoryAdapter: MemoryAdapter
    lateinit var textViewRecyclerState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        preExecute()
        checkForLocationPermission()
        initViews()
        initMemoriesRecyclerView()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (runTimePermission.manageResponse(requestCode, permissions, grantResults, packageName))
            memoriesListViewModel.startLocationService()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.LOCATION_PERMISSION_CODE &&
            runTimePermission.isPermissionGranted()
        )
            memoriesListViewModel.startLocationService()
        else if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_ADD_MEMORY -> {
                    memoryAdapter.refillMemoriesList(repository.getAllMemoriesWithPhotos())
                    memoryAdapter.notifyDataSetChanged()
                    showHideTextViewRecyclerState(false)
                }
                Constants.REQUEST_UPDATE_MEMORY -> {
                    memoryAdapter.refillMemoriesList(repository.getAllMemoriesWithPhotos())
                    memoryAdapter.notifyItemChanged(
                        data?.getIntExtra(Constants.EXTRA_MEMORY_POSITION, -1)!!
                    )
                }
            }
        }
    }

    private fun preExecute() {
        trackingLocation = TrackingLocation(this)
        memoriesListViewModel = Home(this)
        repository = Repository(this)
        memoryAdapter = MemoryAdapter(repository.getAllMemoriesWithPhotos())
    }

    private fun checkForLocationPermission() {
        if (runTimePermission.isPermissionGranted())
            return
        else
            runTimePermission.requestPermission(getString(R.string.permission_desc))
    }

    private fun initViews() {

        val editTextSearch = edit_text_search
        val floatingActionButtonAdd = floating_action_button_add
        textViewRecyclerState = text_view_recycler_state

        memoryAdapter.memories?.run {
            if (isNotEmpty()) showHideTextViewRecyclerState(false)
        }

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                memoryAdapter.filterList(text.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        floatingActionButtonAdd.setOnClickListener {

            startActivityForResult(
                Intent(this, AddEditMemoryView::class.java)
                    .putExtra(Constants.EXTRA_MEMORY_COORDINATES, DoubleArray(2)),
                Constants.REQUEST_ADD_MEMORY
            )
        }
    }

    private fun initMemoriesRecyclerView() {
        memoryAdapter.onItemClickListener = object : MemoryAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val inte = Intent(this@HomeView, AddEditMemoryView::class.java)
                    .putExtra(
                        Constants.EXTRA_MEMORY_ID,
                        memoryAdapter.memories?.get(position)?.memory?.id
                    )
                    .putExtra(
                        Constants.EXTRA_MEMORY_LAST_TIME_MODIFIED,
                        repository.getLastTimeNotifiedById(
                            memoryAdapter.memories?.get(position)?.memory?.id
                        )
                    )
                    .putExtra(Constants.EXTRA_MEMORY_POSITION, position)
                startActivityForResult(
                    inte,
                    Constants.REQUEST_UPDATE_MEMORY
                )
            }

            override fun onItemLongClick(view: View) {
                Snackbar.make(view, getString(R.string.delete_all_memories), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.delete), View.OnClickListener {
                        val itemCount = memoryAdapter.itemCount
                        repository.deleteAllMemories()
                        memoryAdapter.refillMemoriesList(repository.getAllMemoriesWithPhotos())
                        memoryAdapter.notifyItemRangeRemoved(0, itemCount)
                        showHideTextViewRecyclerState(true)
                    })
                    .show()
            }
        }
        val recyclerViewMemories = recycler_view_memories
        recyclerViewMemories.apply {
            adapter = memoryAdapter
            layoutManager = LinearLayoutManager(this@HomeView)
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    memoryAdapter.memories?.let {
                        repository.deleteMemory(it.get(position).memory)
                        memoryAdapter.refillMemoriesList(repository.getAllMemoriesWithPhotos())
                        memoryAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                        memoryAdapter.memories?.run {
                            if (isEmpty()) showHideTextViewRecyclerState(true)
                        }
                    }

                }
            }
        }).attachToRecyclerView(recyclerViewMemories)
    }

    private fun showHideTextViewRecyclerState(isRecyclerEmpty: Boolean) {
        if (isRecyclerEmpty)
            textViewRecyclerState.visibility = View.VISIBLE
        else
            textViewRecyclerState.visibility = View.GONE
    }

}
