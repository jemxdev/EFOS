package com.example.efos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "EfosHistory.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_SCANS = "scans"
        const val COLUMN_ID = "id"
        const val COLUMN_DISEASE = "disease"
        const val COLUMN_DATE = "date"
        const val COLUMN_SEVERITY = "severity"
        const val COLUMN_IMAGE_PATH = "image_path" // We store the file path, not the actual image
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_SCANS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_DISEASE TEXT,"
                + "$COLUMN_DATE TEXT,"
                + "$COLUMN_SEVERITY TEXT,"
                + "$COLUMN_IMAGE_PATH TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SCANS")
        onCreate(db)
    }

    // Function to add a new scan to the database
    fun insertScan(disease: String, date: String, severity: String, imagePath: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DISEASE, disease)
            put(COLUMN_DATE, date)
            put(COLUMN_SEVERITY, severity)
            put(COLUMN_IMAGE_PATH, imagePath)
        }
        val id = db.insert(TABLE_SCANS, null, values)
        db.close()
        return id
    }

    // Function to get all scans, sorted newest first
    fun getAllScans(): List<HistoryItem> {
        val scanList = mutableListOf<HistoryItem>()
        val selectQuery = "SELECT * FROM $TABLE_SCANS ORDER BY $COLUMN_ID DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val disease = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISEASE))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val severity = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEVERITY))
                val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))

                // We temporarily pass the imagePath inside the disease string or handle it in the next step
                // For now, we will add imagePath to our HistoryItem data class in the next step
                scanList.add(HistoryItem(disease, date, severity, imagePath = imagePath))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return scanList
    }

    // Function to get just the total count for the Home Page
    fun getScanCount(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_SCANS", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }
}