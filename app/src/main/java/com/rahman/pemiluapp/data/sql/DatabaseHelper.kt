package com.rahman.pemiluapp.data.sql

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.rahman.pemiluapp.data.model.VoterDataModel

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "pemilih.db"
        private const val TABLE_NAME = "pemilih_table"
        private const val COLUMN_NIK = "nik"
        private const val COLUMN_NAMA = "nama"
        private const val COLUMN_NOHP = "nohp"
        private const val COLUMN_JK = "jk"
        private const val COLUMN_TANGGAL = "tanggal"
        private const val COLUMN_ALAMAT = "alamat"
        private const val COLUMN_GAMBAR = "gambar"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME " +
                "($COLUMN_NIK TEXT PRIMARY KEY, " +
                "$COLUMN_NAMA TEXT, " +
                "$COLUMN_NOHP TEXT, " +
                "$COLUMN_JK INTEGER, " +
                "$COLUMN_TANGGAL TEXT, " +
                "$COLUMN_ALAMAT TEXT, " +
                "$COLUMN_GAMBAR TEXT)")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertVoter(voterData: VoterDataModel): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NIK, voterData.nik)
        values.put(COLUMN_NAMA, voterData.nama)
        values.put(COLUMN_NOHP, voterData.nohp)
        values.put(COLUMN_JK, if (voterData.jk == true) 1 else 0)
        values.put(COLUMN_TANGGAL, voterData.tanggal)
        values.put(COLUMN_ALAMAT, voterData.alamat)
        values.put(COLUMN_GAMBAR, voterData.gambar)
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getVoterByID(nik: String): VoterDataModel? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NIK = ?"
        val cursor = db.rawQuery(query, arrayOf(nik))
        val voterData = if (cursor.moveToFirst()) {
            val gender = cursor.getInt(cursor.getColumnIndex(COLUMN_JK)) == 1
            VoterDataModel(
                cursor.getString(cursor.getColumnIndex(COLUMN_NIK)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAMA)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NOHP)),
                gender,
                cursor.getString(cursor.getColumnIndex(COLUMN_TANGGAL)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ALAMAT)),
                cursor.getString(cursor.getColumnIndex(COLUMN_GAMBAR))
            )
        } else {
            null
        }
        cursor.close()
        db.close()
        return voterData
    }

    @SuppressLint("Range")
    fun getAllVoter(): List<VoterDataModel> {
        val voterList = ArrayList<VoterDataModel>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val gender = cursor.getInt(cursor.getColumnIndex(COLUMN_JK)) == 1
                val voterData = VoterDataModel(
                    cursor.getString(cursor.getColumnIndex(COLUMN_NIK)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAMA)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOHP)),
                    gender,
                    cursor.getString(cursor.getColumnIndex(COLUMN_TANGGAL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_ALAMAT)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_GAMBAR))
                )
                voterList.add(voterData)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return voterList
    }

    fun deleteVoterByID(nik: String): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_NIK=?", arrayOf(nik))
        db.close()
        return result
    }
}