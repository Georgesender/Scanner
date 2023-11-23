package com.example.scanner

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class ScannerDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
        // This method is called when the database needs to be upgraded.
    }

    fun clearDatabase() {
        val db = writableDatabase
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun readAllScans(): Cursor {
        val db = readableDatabase
        return db.query(
            ScannerContract.ScannerEntry.TABLE_NAME,  // The table to query
            null,  // The array of columns to return (null to get all)
            null,  // The columns for the WHERE clause
            null,  // The values for the WHERE clause
            null,  // don't group the rows
            null,  // don't filter by row groups
            null   // The sort order
        )
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Scanner.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${ScannerContract.ScannerEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${ScannerContract.ScannerEntry.COLUMN_SCANNED_VALUE} TEXT," +
                    "${ScannerContract.ScannerEntry.COLUMN_SCAN_DATE_TIME} TEXT)"

        private const val SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS ${ScannerContract.ScannerEntry.TABLE_NAME}"
    }

    object ScannerContract {
        // Define the table contents
        class ScannerEntry : BaseColumns {
            companion object {
                const val TABLE_NAME = "scans"
                const val COLUMN_SCANNED_VALUE = "scanned_value"
                const val COLUMN_SCAN_DATE_TIME = "scan_date_time"
            }
        }
    }
}
