@file:Suppress("DEPRECATION")

package com.example.scanner

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Environment
import android.provider.BaseColumns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: ScannerDbHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var scannedDataAdapter: ScannedDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = ScannerDbHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        scannedDataAdapter = ScannedDataAdapter(this, dbHelper.readAllScans())

        // Set the adapter for the RecyclerView
        recyclerView.adapter = scannedDataAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val buttonScan = findViewById<Button>(R.id.button_scan)
        buttonScan.setOnClickListener {
            IntentIntegrator(this).initiateScan()
        }

        val buttonSaveToCsv = findViewById<Button>(R.id.button_save_to_csv)
        buttonSaveToCsv.setOnClickListener {
            saveDatabaseToCsv()
        }

        val buttonClearDatabase = findViewById<Button>(R.id.button_clear_database)
        buttonClearDatabase.setOnClickListener {
            clearDatabase()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Відмінено", Toast.LENGTH_LONG).show()
            } else {
                val scannedData = result.contents
                findViewById<TextView>(R.id.textView_result).text = scannedData
                insertScanToDatabase(scannedData)
            }
        }
    }

    private fun insertScanToDatabase(scannedData: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(ScannerContract.ScannerEntry.COLUMN_SCANNED_VALUE, scannedData)
            put(ScannerContract.ScannerEntry.COLUMN_SCAN_DATE_TIME, getCurrentDateTime())
        }

        // Insert the new scan into the database
        val newRowId = db.insert(ScannerContract.ScannerEntry.TABLE_NAME, null, values)

        // Update the RecyclerView with the new data
        scannedDataAdapter.swapCursor(dbHelper.readAllScans())

        Toast.makeText(this, "Інформацію просканвано з ID: $newRowId", Toast.LENGTH_LONG).show()
    }

    private fun saveDatabaseToCsv() {
        val scansCursor: Cursor = dbHelper.readAllScans()
        val csvFileName = "Scanner.csv"
        val downloadFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        try {
            val file = File(downloadFolder, csvFileName)
            val outputStream = FileOutputStream(file)
            val writer = OutputStreamWriter(outputStream)

            // Write CSV header
            writer.write("ID,Проскановне Значення,Дата та час\n")

            // Write database records to CSV
            while (scansCursor.moveToNext()) {
                val idColumnIndex = scansCursor.getColumnIndex(BaseColumns._ID)
                val scannedValueColumnIndex =
                    scansCursor.getColumnIndex(ScannerContract.ScannerEntry.COLUMN_SCANNED_VALUE)
                val scanDateTimeColumnIndex =
                    scansCursor.getColumnIndex(ScannerContract.ScannerEntry.COLUMN_SCAN_DATE_TIME)

                // Check if the indices are valid
                if (idColumnIndex >= 0 && scannedValueColumnIndex >= 0 && scanDateTimeColumnIndex >= 0) {
                    val id = scansCursor.getLong(idColumnIndex)
                    val scannedValue = scansCursor.getString(scannedValueColumnIndex)
                    val scanDateTime = scansCursor.getString(scanDateTimeColumnIndex)

                    // Write CSV row
                    writer.write("$id,$scannedValue,$scanDateTime\n")
                }
            }

            writer.close()
            outputStream.close()

            Toast.makeText(this, "Збережено в CSV", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Помилка при збереженні CSV", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } finally {
            scansCursor.close()
        }
    }

    private fun clearDatabase() {
        dbHelper.clearDatabase()

        // Update the RecyclerView with the new data
        scannedDataAdapter.swapCursor(dbHelper.readAllScans())

        Toast.makeText(this, "Базу даних очищено", Toast.LENGTH_LONG).show()
    }

    private fun getCurrentDateTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }
}
