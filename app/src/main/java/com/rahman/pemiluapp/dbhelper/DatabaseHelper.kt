package com.rahman.pemiluapp.dbhelper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.rahman.pemiluapp.model.Pemilih

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

    fun tambahPemilih(pemilih: Pemilih): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NIK, pemilih.nik)
        values.put(COLUMN_NAMA, pemilih.nama)
        values.put(COLUMN_NOHP, pemilih.nohp)
        values.put(COLUMN_JK, if (pemilih.jk == true) 1 else 0)
        values.put(COLUMN_TANGGAL, pemilih.tanggal)
        values.put(COLUMN_ALAMAT, pemilih.alamat)
        values.put(COLUMN_GAMBAR, pemilih.gambar)
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getPemilih(nik: String): Pemilih? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NIK = ?"
        val cursor = db.rawQuery(query, arrayOf(nik))
        val pemilih = if (cursor.moveToFirst()) {
            val jeniskelamin = cursor.getInt(cursor.getColumnIndex(COLUMN_JK)) == 1
            Pemilih(
                cursor.getString(cursor.getColumnIndex(COLUMN_NIK)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAMA)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NOHP)),
                jeniskelamin,
                cursor.getString(cursor.getColumnIndex(COLUMN_TANGGAL)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ALAMAT)),
                cursor.getString(cursor.getColumnIndex(COLUMN_GAMBAR))
            )
        } else {
            null
        }
        cursor.close()
        db.close()
        return pemilih
    }

    @SuppressLint("Range")
    fun getAllPemilih(): List<Pemilih> {
        val pemilihList = ArrayList<Pemilih>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val jeniskelamin = cursor.getInt(cursor.getColumnIndex(COLUMN_JK)) == 1
                val pemilih = Pemilih(
                    cursor.getString(cursor.getColumnIndex(COLUMN_NIK)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAMA)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOHP)),
                    jeniskelamin,
                    cursor.getString(cursor.getColumnIndex(COLUMN_TANGGAL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_ALAMAT)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_GAMBAR))
                )
                pemilihList.add(pemilih)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return pemilihList
    }

    fun deletePemilihByNik(nik: String): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_NIK=?", arrayOf(nik))
        db.close()
        return result
    }
}