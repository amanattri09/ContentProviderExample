package com.app.contentproviderexample

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log.i
import com.app.contentproviderexample.Constants.AUTHORITY

class MyContentProvider : ContentProvider(){

    private val DATABASE_NAME="table.db"
    private val DATA_BASE_VERSION=2
    private val DATABASE_TABLE_NAME="t1"
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    private val DATUM=1
    private val DATUM_ID=2
    private var projMap = mutableMapOf<String,String>()
    private var dbHelper : DBHelper?=null

    init {
        uriMatcher.addURI(AUTHORITY,DATABASE_TABLE_NAME,DATUM)
        uriMatcher.addURI(AUTHORITY, "$DATABASE_TABLE_NAME/#",DATUM)
        projMap.put(Constants.ID,Constants.ID)
        projMap.put(Constants.TEXT,Constants.TEXT)
    }


    override fun onCreate(): Boolean {
        dbHelper=DBHelper(context,DATABASE_NAME,null,DATA_BASE_VERSION)
        return true
    }

    override fun query(
        uri: Uri,
        p: Array<out String>?,
        s: String?,
        args: Array<out String>?,
        sort: String?
    ): Cursor? {
        val qb = SQLiteQueryBuilder()
        qb.tables = DATABASE_TABLE_NAME
        qb.projectionMap=projMap
        var s1= s
        if(uriMatcher.match(uri)!=DATUM){
            if(uriMatcher.match(uri)==DATUM_ID){
                s1 = s + "_id = " + uri?.lastPathSegment
            }else
                throw IllegalArgumentException("unknown URI$uri")
        }
        val db = dbHelper?.readableDatabase
        val c = qb.query(db,p,s1,args,null,null,sort)
        c.setNotificationUri(context!!.contentResolver,uri)
        return c
    }

    override fun getType(uri: Uri): String? {
        if(uriMatcher.match(uri)==DATUM){
            return Constants.CONTENT_TYPE
        }
        else {
            throw IllegalArgumentException("Unknown URI $uri")
        }
    }

    override fun insert(uri: Uri, cv: ContentValues?): Uri? {
        if(uriMatcher.match(uri)!=DATUM){
            throw IllegalArgumentException("unknown URI $uri")
        }
        val v : ContentValues
        if (cv != null)
            v = ContentValues(cv)
        else
            v = ContentValues()
        val db = dbHelper?.writableDatabase
        val rId = db?.insert(DATABASE_TABLE_NAME,Constants.TEXT,v)
        if(rId!=null){
            if(rId>0){
                val uri = ContentUris.withAppendedId(Constants.URL,rId)
                context?.contentResolver?.notifyChange(uri,null)
                return uri
            }
        }else{
            throw SQLException("Failed to insert row into $uri")
        }
        return null
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return 1
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 1
    }

    inner class DBHelper(context: Context?,
                         name: String?,
                         factory: SQLiteDatabase.CursorFactory?,
                         version: Int) : SQLiteOpenHelper(context, name, factory, version){
        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL("CREATE TABLE " +
                    DATABASE_TABLE_NAME +
                    " (" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + Constants.TEXT + " VARCHAR(20)" + ")")
        }

        override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
            db?.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME)
            onCreate(db)
        }

    }

}