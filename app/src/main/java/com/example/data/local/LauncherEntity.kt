package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_items")
data class HomeItemEntity(
    @PrimaryKey val id: String,
    val itemType: String, // "APP", "FOLDER", "WIDGET"
    val packageName: String? = null,
    val className: String? = null,
    val customLabel: String? = null,
    val pageIndex: Int = 0,
    val row: Int = 0,
    val column: Int = 0,
    val spanX: Int = 1,
    val spanY: Int = 1,
    val isDockItem: Boolean = false,
    val dockIndex: Int = 0,
    // Folder specific
    val folderName: String? = null,
    val folderPackagesCsv: String? = null,
    // Widget specific
    val widgetId: Int? = null,
    val providerPackage: String? = null,
    val providerClass: String? = null
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val packageName: String,
    val pinOrder: Int = 0,
    val addedTime: Long = System.currentTimeMillis()
)
