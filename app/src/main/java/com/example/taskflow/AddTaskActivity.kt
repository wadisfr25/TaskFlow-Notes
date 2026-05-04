package com.example.taskflow

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taskflow.data.Task
import com.example.taskflow.data.TaskDatabaseHelper
import com.example.taskflow.databinding.ActivityAddTaskBinding

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var databaseHelper: TaskDatabaseHelper
    private var taskId: Long = INVALID_TASK_ID
    private var createdAt: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        databaseHelper = TaskDatabaseHelper(this)
        taskId = intent.getLongExtra(EXTRA_TASK_ID, INVALID_TASK_ID)

        setupCategoryDropdown()
        setupScreenMode()
        setupActions()
    }

    private fun setupCategoryDropdown() {
        val categories = resources.getStringArray(R.array.task_categories)
        val categoryAdapter = ArrayAdapter(
            this,
            R.layout.item_category_dropdown,
            categories
        )
        binding.inputCategory.setAdapter(categoryAdapter)
    }

    private fun setupScreenMode() {
        if (taskId == INVALID_TASK_ID) {
            binding.textInputTitle.text = getString(R.string.input_header)
            binding.textInputSubtitle.text = getString(R.string.input_subheader)
            binding.buttonSaveTask.text = getString(R.string.save)
            return
        }

        val task = databaseHelper.getTaskById(taskId) ?: run {
            Toast.makeText(this, R.string.message_data_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        createdAt = task.createdAt
        binding.textInputTitle.text = getString(R.string.edit_header)
        binding.textInputSubtitle.text = getString(R.string.edit_subheader)
        binding.buttonSaveTask.text = getString(R.string.update)
        binding.inputTitle.setText(task.title)
        binding.inputDescription.setText(task.description)
        binding.inputCategory.setText(task.category, false)
    }

    private fun setupActions() {
        binding.buttonSaveTask.setOnClickListener {
            saveTask()
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }
    }

    private fun saveTask() {
        val title = binding.inputTitle.text?.toString()?.trim().orEmpty()
        val description = binding.inputDescription.text?.toString()?.trim().orEmpty()
        val category = binding.inputCategory.text?.toString()?.trim().orEmpty()

        var isValid = true

        if (title.isEmpty()) {
            binding.layoutTitle.error = getString(R.string.error_required)
            isValid = false
        } else {
            binding.layoutTitle.error = null
        }

        if (description.isEmpty()) {
            binding.layoutDescription.error = getString(R.string.error_required)
            isValid = false
        } else {
            binding.layoutDescription.error = null
        }

        if (category.isEmpty()) {
            binding.layoutCategory.error = getString(R.string.error_required)
            isValid = false
        } else {
            binding.layoutCategory.error = null
        }

        if (!isValid) {
            return
        }

        val task = Task(
            id = if (taskId == INVALID_TASK_ID) 0 else taskId,
            title = title,
            description = description,
            category = category,
            createdAt = createdAt
        )

        if (taskId == INVALID_TASK_ID) {
            createTask(task)
        } else {
            updateTask(task)
        }
    }

    private fun createTask(task: Task) {
        val insertedId = databaseHelper.insertTask(task)
        if (insertedId > 0) {
            Toast.makeText(this, R.string.message_save_success, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, R.string.message_save_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTask(task: Task) {
        val updatedRows = databaseHelper.updateTask(task)
        if (updatedRows > 0) {
            Toast.makeText(this, R.string.message_update_success, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, R.string.message_update_failed, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        private const val INVALID_TASK_ID = -1L
    }
}
