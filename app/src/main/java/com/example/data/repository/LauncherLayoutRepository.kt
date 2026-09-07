package com.example.data.repository

import com.example.data.local.FavoriteEntity
import com.example.data.local.HomeItemEntity
import com.example.data.local.LauncherDao
import com.example.data.model.AppItem
import com.example.data.model.GridPosition
import com.example.data.model.HomeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class LauncherLayoutRepository(
    private val launcherDao: LauncherDao
) {

    val homeGridItems: Flow<List<HomeItem>> = launcherDao.getHomeGridItems().map { entities ->
        entities.map { it.toHomeItem() }
    }

    val dockItems: Flow<List<HomeItem>> = launcherDao.getDockItems().map { entities ->
        entities.map { it.toHomeItem() }
    }

    val favorites: Flow<List<FavoriteEntity>> = launcherDao.getFavorites()

    suspend fun seedDefaultLayoutIfEmpty(installedApps: List<AppItem>) = withContext(Dispatchers.IO) {
        if (installedApps.isEmpty()) return@withContext

        val itemsToInsert = mutableListOf<HomeItemEntity>()

        // Find standard app package names or available apps
        val appMap = installedApps.associateBy { it.packageName }

        // Find popular app packages if available, otherwise take first few
        val preferredDockPackages = listOf(
            "com.nxv.phone",
            "com.android.dialer",
            "com.google.android.dialer",
            "com.nxv.messages",
            "com.android.mms",
            "com.google.android.apps.messaging",
            "com.nxv.browser",
            "com.android.chrome",
            "com.nxv.camera",
            "com.nxv.settings",
            "com.android.settings"
        )

        val dockApps = mutableListOf<AppItem>()
        for (pkg in preferredDockPackages) {
            appMap[pkg]?.let {
                if (!dockApps.contains(it) && dockApps.size < 5) dockApps.add(it)
            }
        }
        // Fill dock to at least 4 items if possible
        for (app in installedApps) {
            if (dockApps.size >= 4) break
            if (!dockApps.contains(app)) dockApps.add(app)
        }

        // Add dock items
        dockApps.forEachIndexed { idx, app ->
            itemsToInsert.add(
                HomeItemEntity(
                    id = UUID.randomUUID().toString(),
                    itemType = "APP",
                    packageName = app.packageName,
                    className = app.className,
                    customLabel = app.label,
                    isDockItem = true,
                    dockIndex = idx
                )
            )
        }

        // Populate home grid page 0 (under clock header: starting row 2 or 3)
        var row = 2
        var col = 0
        val maxCols = 4

        val homeGridApps = installedApps.filter { app -> !dockApps.any { it.packageName == app.packageName } }.take(8)

        homeGridApps.forEach { app ->
            itemsToInsert.add(
                HomeItemEntity(
                    id = UUID.randomUUID().toString(),
                    itemType = "APP",
                    packageName = app.packageName,
                    className = app.className,
                    customLabel = app.label,
                    pageIndex = 0,
                    row = row,
                    column = col,
                    isDockItem = false
                )
            )
            col++
            if (col >= maxCols) {
                col = 0
                row++
            }
        }

        if (itemsToInsert.isNotEmpty()) {
            launcherDao.insertAllItems(itemsToInsert)
        }
    }

    suspend fun saveItem(item: HomeItem) = withContext(Dispatchers.IO) {
        launcherDao.insertItem(item.toEntity())
    }

    suspend fun addAppToHome(app: AppItem, pageIndex: Int = 0, targetRow: Int = 0, targetCol: Int = 0) = withContext(Dispatchers.IO) {
        val newItem = HomeItem.App(
            id = UUID.randomUUID().toString(),
            packageName = app.packageName,
            className = app.className,
            customLabel = app.label,
            position = GridPosition(pageIndex = pageIndex, row = targetRow, column = targetCol),
            isDockItem = false
        )
        launcherDao.insertItem(newItem.toEntity())
    }

    suspend fun addAppToDock(app: AppItem, dockIndex: Int) = withContext(Dispatchers.IO) {
        val newItem = HomeItem.App(
            id = UUID.randomUUID().toString(),
            packageName = app.packageName,
            className = app.className,
            customLabel = app.label,
            isDockItem = true,
            dockIndex = dockIndex
        )
        launcherDao.insertItem(newItem.toEntity())
    }

    suspend fun createFolderFromApps(
        app1: HomeItem.App,
        app2: HomeItem.App,
        folderName: String = "Folder"
    ) = withContext(Dispatchers.IO) {
        // Remove app1 and app2 from home
        launcherDao.deleteItemById(app1.id)
        launcherDao.deleteItemById(app2.id)

        val folderItem = HomeItem.Folder(
            id = UUID.randomUUID().toString(),
            name = folderName,
            appPackages = listOf(app1.packageName, app2.packageName),
            position = app1.position,
            isDockItem = app1.isDockItem,
            dockIndex = app1.dockIndex
        )

        launcherDao.insertItem(folderItem.toEntity())
    }

    suspend fun addAppToFolder(folder: HomeItem.Folder, appPackageName: String) = withContext(Dispatchers.IO) {
        if (!folder.appPackages.contains(appPackageName)) {
            val updatedPackages = folder.appPackages + appPackageName
            val updatedFolder = folder.copy(appPackages = updatedPackages)
            launcherDao.insertItem(updatedFolder.toEntity())
        }
    }

    suspend fun removeAppFromFolder(folder: HomeItem.Folder, appPackageName: String) = withContext(Dispatchers.IO) {
        val updatedPackages = folder.appPackages.filter { it != appPackageName }
        if (updatedPackages.isEmpty()) {
            launcherDao.deleteItemById(folder.id)
        } else {
            val updatedFolder = folder.copy(appPackages = updatedPackages)
            launcherDao.insertItem(updatedFolder.toEntity())
        }
    }

    suspend fun renameFolder(folder: HomeItem.Folder, newName: String) = withContext(Dispatchers.IO) {
        val updatedFolder = folder.copy(name = newName)
        launcherDao.insertItem(updatedFolder.toEntity())
    }

    suspend fun removeItemById(itemId: String) = withContext(Dispatchers.IO) {
        launcherDao.deleteItemById(itemId)
    }

    suspend fun toggleFavorite(packageName: String) = withContext(Dispatchers.IO) {
        val favEntity = FavoriteEntity(packageName = packageName)
        launcherDao.insertFavorite(favEntity)
    }

    suspend fun removeFavorite(packageName: String) = withContext(Dispatchers.IO) {
        launcherDao.deleteFavorite(packageName)
    }

    private fun HomeItemEntity.toHomeItem(): HomeItem {
        return when (itemType) {
            "FOLDER" -> HomeItem.Folder(
                id = id,
                name = folderName ?: "Folder",
                appPackages = folderPackagesCsv?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
                position = GridPosition(pageIndex, row, column, spanX, spanY),
                isDockItem = isDockItem,
                dockIndex = dockIndex
            )
            "WIDGET" -> HomeItem.Widget(
                id = id,
                appWidgetId = widgetId ?: -1,
                providerPackage = providerPackage ?: "",
                providerClass = providerClass ?: "",
                position = GridPosition(pageIndex, row, column, spanX, spanY),
                isDockItem = isDockItem
            )
            else -> HomeItem.App(
                id = id,
                packageName = packageName ?: "",
                className = className ?: "",
                customLabel = customLabel,
                position = GridPosition(pageIndex, row, column, spanX, spanY),
                isDockItem = isDockItem,
                dockIndex = dockIndex
            )
        }
    }

    private fun HomeItem.toEntity(): HomeItemEntity {
        return when (this) {
            is HomeItem.App -> HomeItemEntity(
                id = id,
                itemType = "APP",
                packageName = packageName,
                className = className,
                customLabel = customLabel,
                pageIndex = position.pageIndex,
                row = position.row,
                column = position.column,
                spanX = position.spanX,
                spanY = position.spanY,
                isDockItem = isDockItem,
                dockIndex = dockIndex
            )
            is HomeItem.Folder -> HomeItemEntity(
                id = id,
                itemType = "FOLDER",
                folderName = name,
                folderPackagesCsv = appPackages.joinToString(","),
                pageIndex = position.pageIndex,
                row = position.row,
                column = position.column,
                spanX = position.spanX,
                spanY = position.spanY,
                isDockItem = isDockItem,
                dockIndex = dockIndex
            )
            is HomeItem.Widget -> HomeItemEntity(
                id = id,
                itemType = "WIDGET",
                widgetId = appWidgetId,
                providerPackage = providerPackage,
                providerClass = providerClass,
                pageIndex = position.pageIndex,
                row = position.row,
                column = position.column,
                spanX = position.spanX,
                spanY = position.spanY,
                isDockItem = isDockItem
            )
        }
    }
}
