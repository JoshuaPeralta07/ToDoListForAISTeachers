package com.assignment.todolistforaisteachers

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.assignment.todolistforaisteachers.databinding.ActivityMainBinding
import com.assignment.todolistforaisteachers.databinding.FragmentNewTaskSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText


class NewTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment()
{
    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var db: DatabaseHelper


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        // Initialization of database
        db = DatabaseHelper(activity)

        //Add Task and Edit Task shares the same fragement
        if(taskItem != null)
        {
            binding.tvTaskTitle.text = "Edit Task"
            val editable = Editable.Factory.getInstance()
            binding.taskName.text = editable.newEditable(taskItem!!.name)
            binding.taskDescription.text = editable.newEditable(taskItem!!.desc)
        }

        else
        {
            binding.tvTaskTitle.text = "Add Task"
        }

        binding.saveButton.setOnClickListener {
            saveAction()
        }
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View{
        binding = FragmentNewTaskSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun saveAction() {
        // Get the task name and description
        val name = binding.taskName.text.toString()
        val desc = binding.taskDescription.text.toString()

        // Input validation (make sure name and description aren't empty)
        if (name.isEmpty() || desc.isEmpty()) {
            Toast.makeText(context, "Please fill in both name and description", Toast.LENGTH_SHORT).show()
            return
        }

        // Add new task if taskItem is empty
        if (taskItem == null){
           val newTask = TaskItem(0,name,desc,false)

            // Add task in the db
            db.addTask(newTask)

            Toast.makeText(context, "Task added successfully", Toast.LENGTH_SHORT).show()
        }

        else{
            //taskViewModel.updateTaskItem(taskItem!!.id,name, desc)
        }

        // Clear input fields after saving
        binding.taskName.setText("")
        binding.taskDescription.setText("")

        // Dismiss Dialog after saving
        dismiss()
    }

}