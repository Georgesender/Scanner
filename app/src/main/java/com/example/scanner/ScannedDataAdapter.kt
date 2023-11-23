package com.example.scanner

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScannedDataAdapter(context: Context, cursor: Cursor) :
    RecyclerView.Adapter<ScannedDataAdapter.ScannedDataViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var cursor: Cursor? = null

    init {
        this.cursor = cursor
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannedDataViewHolder {
        val itemView = inflater.inflate(R.layout.item_scanned_data, parent, false)
        return ScannedDataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ScannedDataViewHolder, position: Int) {
        if (cursor?.moveToPosition(position) == true) {
            val scannedValue = cursor?.getString(cursor?.getColumnIndexOrThrow(ScannerContract.ScannerEntry.COLUMN_SCANNED_VALUE)!!)
            val scanDateTime = cursor?.getString(cursor?.getColumnIndexOrThrow(ScannerContract.ScannerEntry.COLUMN_SCAN_DATE_TIME)!!)

            holder.scannedValueTextView.text = "Scanned Value: $scannedValue"
            holder.scanDateTimeTextView.text = "Scan Date and Time: $scanDateTime"
        }
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    fun swapCursor(newCursor: Cursor?) {
        cursor?.close()
        cursor = newCursor
        notifyDataSetChanged()
    }

    inner class ScannedDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scannedValueTextView: TextView = itemView.findViewById(R.id.scannedValueTextView)
        val scanDateTimeTextView: TextView = itemView.findViewById(R.id.scanDateTimeTextView)
    }
}
