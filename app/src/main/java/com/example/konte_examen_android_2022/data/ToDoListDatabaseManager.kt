package com.example.konte_examen_android_2022.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.konte_examen_android_2022.model.ToDoItem
import java.util.*

class ToDoListDatabaseManager(context: Context?, factory:SQLiteDatabase.CursorFactory?) :  SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION)  {


    override fun onCreate(db: SQLiteDatabase?) {
        val query = ("CREATE TABLE $TABLE_NAME ($TODO_ID INTEGER PRIMARY KEY,$TASK_TEXT TEXT, $TASK_DATE TEXT, $IS_DONE INTEGER, $PRIORITY_ID INTEGER)")
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addToDo(text : String, priority : Int){
        val c = Calendar.getInstance()
        val date = c.get(Calendar.DAY_OF_YEAR).and(Calendar.YEAR).toString()
        val values = ContentValues()
        values.put(TASK_TEXT, text)
        values.put(TASK_DATE, date)
        values.put(IS_DONE, 0)
        values.put(PRIORITY_ID, priority)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)

        db.close();
    }

    fun getToDo(done : Int) : Cursor{
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $IS_DONE LIKE $done LIMIT 1", null)
    }

    fun modifyIsDoneState(todo : String, isDone : Int){
        val db = this.writableDatabase
        Log.i("modifyIsDoneState", isDone.toString())
        val done = isDone xor 1 //xor operation to flip the "bool" int
        Log.i("modifyIsDoneState", done.toString())
        db.execSQL("UPDATE $TABLE_NAME SET $IS_DONE = $done WHERE $TASK_TEXT LIKE \"$todo\"")
        db.close()

    }
    fun modifyPriority(todo : String, priority : Int){
        val db = this.writableDatabase
        db.execSQL("UPDATE $TABLE_NAME SET $PRIORITY_ID = $priority WHERE $TASK_TEXT LIKE \"$todo\"")
        db.close()
    }
    fun deleteToDo(todo : String){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME WHERE $TASK_TEXT LIKE \"$todo\"")
        db.close()
    }

    fun moveToDoToTomorrow(todo : String){
        val db = this.writableDatabase
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, 1)
        val date = c.get(Calendar.DAY_OF_YEAR).and(Calendar.YEAR).toString()
        db.execSQL("UPDATE $TABLE_NAME SET $TASK_DATE = $date WHERE $TASK_TEXT LIKE \"$todo\"")
        db.close()
    }

    fun getTodaysTodo():Cursor{
        val c = Calendar.getInstance()
        val date = c.get(Calendar.DAY_OF_YEAR).and(Calendar.YEAR).toString()

        val db = this.readableDatabase

        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $TASK_DATE LIKE $date AND $IS_DONE LIKE 0", null)
    }

    fun getTomorrowsToDo():Cursor{
        val c = Calendar.getInstance()
        val date = c.get(Calendar.DAY_OF_YEAR +1).and(Calendar.YEAR).toString()

        val db = this.readableDatabase

        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $TASK_DATE LIKE $date", null)
    }

    companion object{
        private val DATABASE_NAME = "TODO_LIST_DATABASE"
        private val DATABASE_VERSION = 4
        private val TABLE_NAME = "TODOS"
        private val TODO_ID = "TODO_ID"
        private val TASK_TEXT = "TASK_TEXT"
        private val TASK_DATE = "TASK_DATE"
        private val IS_DONE = "IS_DONE"
        private val PRIORITY_ID = "PRIORITY_ID"

    }
}

