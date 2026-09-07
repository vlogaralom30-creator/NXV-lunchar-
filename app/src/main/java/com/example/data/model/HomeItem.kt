package com.example.data.model

import java.util.UUID

/**
 * Position on the home grid.
 */
data class GridPosition(
    val pageIndex: Int = 0,
    val row: Int = 0,
    val column: Int = 0,
    val spanX: Int = 1,
    val spanY: Int = 1
)

/**
 * Represents an item placed on the Home screen or Dock.
 */
sealed class HomeItem {
    abstract val id: String
    abstract val position: GridPosition
    abstract val isDockItem: Boolean

    data class App(
        override val id: String = UUID.randomUUID().toString(),
        val packageName: String,
        val className: String = "",
        val customLabel: String? = null,
        override val position: GridPosition = GridPosition(),
        override val isDockItem: Boolean = false,
        val dockIndex: Int = 0
    ) : HomeItem()

    data class Folder(
        override val id: String = UUID.randomUUID().toString(),
        val name: String = "Folder",
        val appPackages: List<String> = emptyList(),
        override val position: GridPosition = GridPosition(),
        override val isDockItem: Boolean = false,
        val dockIndex: Int = 0
    ) : HomeItem()

    data class Widget(
        override val id: String = UUID.randomUUID().toString(),
        val appWidgetId: Int,
        val providerPackage: String,
        val providerClass: String,
        override val position: GridPosition = GridPosition(),
        override val isDockItem: Boolean = false
    ) : HomeItem()
}
