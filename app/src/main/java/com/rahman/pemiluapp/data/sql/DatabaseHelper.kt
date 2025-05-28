package com.rahman.pemiluapp.data.sql

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction
import com.rahman.pemiluapp.data.model.VoterDataModel

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

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

    fun importVoters(voterList: List<VoterDataModel>) {
        val db = this.writableDatabase
        db.transaction {
            try {
                val selectSql = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NIK = ?"
                val updateSql = """
                    UPDATE $TABLE_NAME SET
                    $COLUMN_NAMA = ?,
                    $COLUMN_NOHP = ?,
                    $COLUMN_JK = ?,
                    $COLUMN_TANGGAL = ?,
                    $COLUMN_ALAMAT = ?,
                    $COLUMN_GAMBAR = ?
                    WHERE $COLUMN_NIK = ?
                    """.trimIndent()

                val insertSql = """
                    INSERT INTO $TABLE_NAME
                    ($COLUMN_NIK, $COLUMN_NAMA, $COLUMN_NOHP, $COLUMN_JK, $COLUMN_TANGGAL, $COLUMN_ALAMAT, $COLUMN_GAMBAR)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """.trimIndent()

                val updateStmt = db.compileStatement(updateSql)
                val insertStmt = db.compileStatement(insertSql)

                for (voter in voterList) {
                    val cursor = db.rawQuery(selectSql, arrayOf(voter.nik))
                    val exists = cursor.moveToFirst()

                    val existingVoter = if (exists) {
                        VoterDataModel(
                            nik = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NIK)),
                            nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA)),
                            nohp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOHP)),
                            jk = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_JK)) == 1,
                            tanggal = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL)),
                            alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT)),
                            gambar = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR))
                        )
                    } else null
                    cursor.close()

                    if (existingVoter != null) {
                        val merged = existingVoter.copy(
                            nik = voter.nik,
                            nama = voter.nama?.takeIf { it.isNotEmpty() } ?: existingVoter.nama,
                            nohp = voter.nohp?.takeIf { it.isNotEmpty() } ?: existingVoter.nohp,
                            jk = voter.jk ?: existingVoter.jk,
                            tanggal = voter.tanggal?.takeIf { it.isNotEmpty() } ?: existingVoter.tanggal,
                            alamat = voter.alamat?.takeIf { it.isNotEmpty() } ?: existingVoter.alamat,
                            gambar = voter.gambar?.takeIf { it.isNotEmpty() } ?: existingVoter.gambar
                        )

                        updateStmt.clearBindings()

                        updateStmt.bindString(1, merged.nama)
                        updateStmt.bindString(2, merged.nohp)
                        updateStmt.bindLong(3, if (merged.jk == true) 1 else 0)
                        updateStmt.bindString(4, merged.tanggal)
                        updateStmt.bindString(5, merged.alamat)
                        if (merged.gambar != null) updateStmt.bindString(6, merged.gambar) else updateStmt.bindNull(6)
                        updateStmt.bindString(7, merged.nik)

                        updateStmt.executeUpdateDelete()
                    } else {
                        insertStmt.clearBindings()

                        insertStmt.bindString(1, voter.nik)
                        insertStmt.bindString(2, voter.nama ?: "")
                        insertStmt.bindString(3, voter.nohp ?: "")
                        insertStmt.bindLong(4, if (voter.jk == true) 1 else 0)
                        insertStmt.bindString(5, voter.tanggal ?: "")
                        insertStmt.bindString(6, voter.alamat ?: "")
                        if (voter.gambar != null) insertStmt.bindString(7, voter.gambar) else insertStmt.bindNull(7)

                        insertStmt.executeInsert()
                    }
                }

                updateStmt.close()
                insertStmt.close()
            } finally {
            }
        }

        db.close()
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

    @SuppressLint("Range")
    fun searchVoter(query: String): List<VoterDataModel> {
        val voterList = ArrayList<VoterDataModel>()
        val formattedQuery = "%$query%"
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NIK LIKE ? OR $COLUMN_NAMA LIKE ? OR $COLUMN_ALAMAT LIKE ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(formattedQuery, formattedQuery, formattedQuery))

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