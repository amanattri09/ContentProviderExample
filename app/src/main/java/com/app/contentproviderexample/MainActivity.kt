package com.app.contentproviderexample

import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //inserting data into content provider
        val tuple = ContentValues()
        tuple.put(Constants.TEXT, "Constants.TEXT_DATA")
        contentResolver.insert(Constants.URL, tuple)
        //reading from content provider
        val cols = arrayOf(Constants.ID, Constants.TEXT)
        val u = Constants.URL
        val c = contentResolver.query(u, cols, null, null, null)
        if (c!!.moveToLast()) {
            if (c.getColumnIndex(Constants.TEXT) != -1) {
                findViewById<TextView>(R.id.tvDb).text = "Data read from content provider: " +
                        c.getString(c.getColumnIndex(Constants.TEXT))
            } else {
                findViewById<TextView>(R.id.tvDb).text = "No record found"
            }
        } else {
            findViewById<TextView>(R.id.tvDb).text == "Access Denied"
        }

    }

}