package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherDao {

    @Query("SELECT * FROM home_items WHERE isDockItem = 0 ORDER BY pageIndex ASC, row ASC, column ASC")
    fun getHomeGridItems(): Flow<List<HomeItemEntity>>

    @Query("SELECT * FROM home_items WHERE isDockItem = 1 ORDER BY dockIndex ASC")
    fun getDockItems(): Flow<List<HomeItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: HomeItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllItems(items: List<HomeItemEntity>)

    @Query("DELETE FROM home_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: String)

    @Query("DELETE FROM home_items")
    suspend fun clearAllItems()

    @Query("SELECT * FROM favorites ORDER BY pinOrder ASC")
    fun getFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE packageName = :packageName")
    suspend fun deleteFavorite(packageName: String)
}
