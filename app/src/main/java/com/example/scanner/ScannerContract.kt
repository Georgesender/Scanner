package com.example.scanner

import android.provider.BaseColumns

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
