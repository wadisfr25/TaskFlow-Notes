package com.example.taskflow

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taskflow.data.Task
import com.example.taskflow.data.TaskDatabaseHelper
import com.example.taskflow.databinding.ActivityTaskDetailBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailBinding
    private lateinit var databaseHelper: TaskDatabaseHelper
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    private var taskId: Long = INVALID_TASK_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        databaseHelper = TaskDatabaseHelper(this)
        taskId = intent.getLongExtra(EXTRA_TASK_ID, INVALID_TASK_ID)

        setupActions()
    }

    override fun onResume() {
        super.onResume()
        loadTaskDetail()
    }

    private fun setupActions() {
        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonEdit.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra(AddTaskActivity.EXTRA_TASK_ID, taskId)
            startActivity(intent)
        }

        binding.buttonDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun loadTaskDetail() {
        if (taskId == INVALID_TASK_ID) {
            Toast.makeText(this, R.string.message_data_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val task = databaseHelper.getTaskById(taskId)
        if (task == null) {
            Toast.makeText(this, R.string.message_data_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        bindTask(task)
    }

    private fun bindTask(task: Task) {
        binding.textDetailTitle.text = task.title
        binding.textDetailCategory.text = task.category
        binding.textDetailDescription.text = task.description
        binding.textDetailDate.text = getString(
            R.string.detail_created_at,
            dateFormat.format(Date(task.createdAt))
        )
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_dialog_title)
            .setMessage(R.string.delete_dialog_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteTask()
            }
            .show()
    }

    private fun deleteTask() {
        val deletedRows = databaseHelper.deleteTask(taskId)
        if (deletedRows > 0) {
            Toast.makeText(this, R.string.message_delete_success, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, R.string.message_delete_failed, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        private const val INVALID_TASK_ID = -1L
    }
}
