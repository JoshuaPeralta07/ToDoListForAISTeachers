package com.assignment.todolistforaisteachers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Declaration of constant values of table name and columns
    companion object {
        private const val DATABASE_NAME = "ToDoList.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_CONFIRMPASSWORD = "confirmpassword"
        private const val TABLE_TASKS = "tasks"
        private const val COLUMN_TASKID = "id"
        private const val COLUMN_TASKNAME = "taskname"
        private const val COLUMN_TASKDESC = "taskdescription"
        private const val COLUMN_IS_COMPLETED =
            "is_completed"  // Assuming you want this column for completion status
    }

    // Function for users and tasks table creation
    override fun onCreate(db: SQLiteDatabase?) {
        // Query for users table creation
        val createUsersTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
                $COLUMN_USERNAME TEXT, 
                $COLUMN_PASSWORD TEXT, 
                $COLUMN_CONFIRMPASSWORD TEXT
            )
        """

        // Query for tasks table creation (including is_completed column)
        val createTasksTableQuery = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_TASKID INTEGER PRIMARY KEY AUTOINCREMENT, 
                $COLUMN_TASKNAME TEXT, 
                $COLUMN_TASKDESC TEXT,
                $COLUMN_IS_COMPLETED INTEGER DEFAULT 0
            )
        """

        // Executes query for table creation
        db?.execSQL(createUsersTableQuery)
        db?.execSQL(createTasksTableQuery)
    }

    // Function to add users in the database
    fun addUser(username: String, password: String, confirmpassword: String): Long {

        // Holds the user details to be inserted into the database
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_CONFIRMPASSWORD, confirmpassword)
        }

        // Writable instance of the database for writing data
        val db = writableDatabase

        return db.insert(TABLE_USERS, null, values)
    }

    // Function to check username and password matches
    fun checkUsernamePassword(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val queryArgs = arrayOf(username, password)
        val cursor = db.query(TABLE_USERS, null, query, queryArgs, null, null, null, null)

        val userExists = cursor.count > 0

        cursor.close()

        return userExists

    }

    // Function to check if a user already exists in the database
    fun checkUserExists(username: String): Boolean {
        val db = readableDatabase

        // Query to check if a user with the given username exists
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?"
        val cursor = db.rawQuery(query, arrayOf(username))

        // If cursor has any results, that means the user exists
        val userExists = cursor.count > 0

        // Close the cursor to avoid memory leaks
        cursor.close()

        return userExists
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop the tables if they exist
        val dropUsersTableQuery = "DROP TABLE IF EXISTS $TABLE_USERS"
        db?.execSQL(dropUsersTableQuery)

        val dropTasksTableQuery = "DROP TABLE IF EXISTS $TABLE_TASKS"
        db?.execSQL(dropTasksTableQuery)

        // Recreate Tables
        onCreate(db)
    }

    // Function to add task to the database
    fun addTask(taskItem: TaskItem) {
        val values = ContentValues().apply {
            put(COLUMN_TASKNAME, taskItem.name)
            put(COLUMN_TASKDESC, taskItem.desc)
            put(
                COLUMN_IS_COMPLETED,
                if (taskItem.isCompleted) 1 else 0
            )  // Assuming isCompleted is a boolean
        }

        val db = writableDatabase
        db.insert(TABLE_TASKS, null, values)
        db.close()
    }

    // Function to show all tasks from the database
    fun showTask(): MutableList<TaskItem> {
        val taskList = mutableListOf<TaskItem>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_TASKS"
        val cursor = db.rawQuery(query, null)

        // Iterating through all rows to retrieve data
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASKID))
            val taskName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASKNAME))
            val taskDesc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASKDESC))
            val isCompleted =
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1  // Convert to boolean

            // Create TaskItem instance
            val task = TaskItem(id, taskName, taskDesc, isCompleted)
            taskList.add(task)
        }

        cursor.close()
        db.close()

        return taskList
    }

    // Function to get a task by ID
    fun getTaskById(taskId: Int): TaskItem? {
        val db = readableDatabase
        var taskItem: TaskItem? = null

        // Query to fetch task by ID
        val cursor = db.query(
            TABLE_TASKS,
            null,
            "$COLUMN_TASKID = ?",
            arrayOf(taskId.toString()),
            null,
            null,
            null
        )

        // Check if a task is found and create TaskItem
        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASKID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASKNAME))
            val desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASKDESC))
            val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1

            taskItem = TaskItem(id, name, desc, isCompleted)
        }

        cursor?.close()
        db.close()

        return taskItem
    }

    // Function to delete a task from the database
    fun deleteTask(taskId: Int): Int {
        val db = writableDatabase
        val whereClause = "$COLUMN_TASKID = ?"
        val whereArgs = arrayOf(taskId.toString())

        val rowsAffected = db.delete(TABLE_TASKS, whereClause, whereArgs)
        db.close()

        return rowsAffected
    }
}
