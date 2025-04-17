package com.rahman.pemiluapp.data.sql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "user_db"
        private const val TABLE_USERS = "users"
        private const val COLUMN_NIK = "nik"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_USERS " +
                "($COLUMN_NIK TEXT PRIMARY KEY, " +
                "$COLUMN_PASSWORD TEXT)")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(nik: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NIK, nik)
        values.put(COLUMN_PASSWORD, password)
        val id = db.insert(TABLE_USERS, null, values)
        db.close()
        return id
    }

    fun getUser(nik: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_NIK = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(nik, password))
        val result = cursor.count > 0
        cursor.close()
        db.close()
        return result
    }

    fun isNikExist(nik: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_NIK = ?"
        val cursor = db.rawQuery(query, arrayOf(nik))
        val result = cursor.count > 0
        cursor.close()
        db.close()
        return result
    }
}