package com.app.contentproviderexample

import android.net.Uri

object Constants {
    val AUTHORITY = "com.app.contentproviderexample"
    val URL = Uri.parse("content://$AUTHORITY/t1")
    val CONTENT_TYPE = "contentproviderlab.t12"
    val ID = "_ID"
    val TEXT = "MESSAGE"
    val TEXT_DATA="Hello Content Providers !"
}