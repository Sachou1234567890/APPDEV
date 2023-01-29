package com.example.rsdev

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.rsdev.data.CourseRVModal
import java.util.*
import kotlin.collections.ArrayList

class TestActivity : AppCompatActivity() {

    // on below line we are creating variables for our swipe
    // to refresh layout, recycler view, adapter and list.
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var courseRV: RecyclerView
    lateinit var courseRVAdapter: CourseRVAdapter
    lateinit var courseList: ArrayList<CourseRVModal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // on below line we are initializing our views with their ids.
        swipeRefreshLayout = findViewById(R.id.container)
        courseRV = findViewById(R.id.idRVCourses)

        // on below line we are initializing our list
        courseList = ArrayList()

        // on below line we are initializing our adapter
        courseRVAdapter = CourseRVAdapter(courseList, this)

        // on below line we are setting adapter to our recycler view.
        courseRV.adapter = courseRVAdapter

        // on below line we are adding data to our list
        courseList.add(CourseRVModal("Android Development", R.drawable.t_android))
        courseList.add(CourseRVModal("C++ Development", R.drawable.t_c))
        courseList.add(CourseRVModal("Java Development", R.drawable.t_java))
        courseList.add(CourseRVModal("Python Development", R.drawable.t_python))
        courseList.add(CourseRVModal("JavaScript Development", R.drawable.t_javascript))

        // on below line we are notifying adapter
        // that data has been updated.
        courseRVAdapter.notifyDataSetChanged()

        // on below line we are adding refresh listener
        // for our swipe to refresh method.
        swipeRefreshLayout.setOnRefreshListener {

            // on below line we are setting is refreshing to false.
            swipeRefreshLayout.isRefreshing = false

            // on below line we are shuffling our list using random
            Collections.shuffle(courseList, Random(System.currentTimeMillis()))

            // on below line we are notifying adapter
            // that data has changed in recycler view.
            courseRVAdapter.notifyDataSetChanged()
        }
    }
}
