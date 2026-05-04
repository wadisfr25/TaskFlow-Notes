package com.example.taskflow

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskflow.data.TaskDatabaseHelper
import com.example.taskflow.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseHelper: TaskDatabaseHelper
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        databaseHelper = TaskDatabaseHelper(this)
        taskAdapter = TaskAdapter { task ->
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id)
            startActivity(intent)
        }

        setupRecyclerView()
        setupActions()
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }
    }

    private fun setupActions() {
        binding.fabAddTask.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }
    }

    private fun loadTasks() {
        val tasks = databaseHelper.getAllTasks()
        taskAdapter.submitList(tasks)
        binding.textEmptyState.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
    }
}
