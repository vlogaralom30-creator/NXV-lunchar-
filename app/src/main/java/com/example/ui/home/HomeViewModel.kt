package com.example.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.LauncherDatabase
import com.example.data.local.PreferencesManager
import com.example.data.model.AppItem
import com.example.data.model.AppSortOrder
import com.example.data.model.HomeItem
import com.example.data.model.ThemeConfig
import com.example.data.repository.AppManagerRepository
import com.example.data.repository.LauncherLayoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.data.model.AnimeCharacter
import com.example.data.model.AnimeThemeState

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LauncherDatabase.getDatabase(application)
    private val preferencesManager = PreferencesManager(application)
    val appManagerRepository = AppManagerRepository(application)
    val launcherLayoutRepository = LauncherLayoutRepository(db.launcherDao())

    val themeConfig: StateFlow<ThemeConfig> = preferencesManager.themeConfig

    private val _animeThemeState = MutableStateFlow(AnimeThemeState())
    val animeThemeState: StateFlow<AnimeThemeState> = _animeThemeState.asStateFlow()

    val installedApps: StateFlow<List<AppItem>> = appManagerRepository.installedApps
    val recentApps: StateFlow<List<AppItem>> = appManagerRepository.recentApps

    val homeGridItems: StateFlow<List<HomeItem>> = launcherLayoutRepository.homeGridItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dockItems: StateFlow<List<HomeItem>> = launcherLayoutRepository.dockItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _appMap = MutableStateFlow<Map<String, AppItem>>(emptyMap())
    val appMap: StateFlow<Map<String, AppItem>> = _appMap.asStateFlow()

    init {
        viewModelScope.launch {
            appManagerRepository.refreshInstalledApps(themeConfig.value.appDrawerSortOrder)
        }

        viewModelScope.launch {
            installedApps.collect { apps ->
                _appMap.value = apps.associateBy { it.packageName }
                if (apps.isNotEmpty()) {
                    launcherLayoutRepository.seedDefaultLayoutIfEmpty(apps)
                }
            }
        }
    }

    fun launchApp(packageName: String) {
        appManagerRepository.launchApp(packageName)
    }

    fun openAppDetails(packageName: String) {
        appManagerRepository.openAppDetails(packageName)
    }

    fun requestUninstallApp(packageName: String) {
        appManagerRepository.requestUninstallApp(packageName)
    }

    fun removeItemFromHome(itemId: String) {
        viewModelScope.launch {
            launcherLayoutRepository.removeItemById(itemId)
        }
    }

    fun addAppToHome(app: AppItem, pageIndex: Int, row: Int, col: Int) {
        viewModelScope.launch {
            launcherLayoutRepository.addAppToHome(app, pageIndex, row, col)
        }
    }

    fun renameFolder(folder: HomeItem.Folder, newName: String) {
        viewModelScope.launch {
            launcherLayoutRepository.renameFolder(folder, newName)
        }
    }

    fun removeAppFromFolder(folder: HomeItem.Folder, packageName: String) {
        viewModelScope.launch {
            launcherLayoutRepository.removeAppFromFolder(folder, packageName)
        }
    }

    fun toggleSortOrder() {
        val currentOrder = themeConfig.value.appDrawerSortOrder
        val nextOrder = when (currentOrder) {
            AppSortOrder.ALPHABETICAL -> AppSortOrder.RECENTLY_USED
            AppSortOrder.RECENTLY_USED -> AppSortOrder.MOST_USED
            AppSortOrder.MOST_USED -> AppSortOrder.ALPHABETICAL
        }
        updateThemeConfig { it.copy(appDrawerSortOrder = nextOrder) }
        viewModelScope.launch {
            appManagerRepository.refreshInstalledApps(nextOrder)
        }
    }

    fun pinFavorite(packageName: String) {
        viewModelScope.launch {
            launcherLayoutRepository.toggleFavorite(packageName)
        }
    }

    fun updateThemeConfig(updateBlock: (ThemeConfig) -> ThemeConfig) {
        preferencesManager.updateThemeConfig(updateBlock)
    }

    fun selectAnimeCharacter(character: AnimeCharacter) {
        _animeThemeState.value = _animeThemeState.value.copy(selectedCharacter = character)
    }

    fun toggleNavbar(enabled: Boolean) {
        _animeThemeState.value = _animeThemeState.value.copy(isNavbarEnabled = enabled)
    }

    fun selectLanguage(lang: String) {
        _animeThemeState.value = _animeThemeState.value.copy(selectedLanguage = lang)
    }

    fun setCustomCharacterUri(uri: String?) {
        _animeThemeState.value = _animeThemeState.value.copy(customCharacterUri = uri)
    }

    fun toggleAnimeIconPack(enabled: Boolean) {
        _animeThemeState.value = _animeThemeState.value.copy(isAnimeIconPackEnabled = enabled)
    }

    fun updateWidgetCornerRadius(radiusDp: Int) {
        _animeThemeState.value = _animeThemeState.value.copy(widgetCornerRadiusDp = radiusDp)
    }

    fun updateWidgetOpacity(opacity: Float) {
        _animeThemeState.value = _animeThemeState.value.copy(widgetOpacity = opacity)
    }

    fun updateWidgetStyle(style: String) {
        _animeThemeState.value = _animeThemeState.value.copy(widgetStyle = style)
    }

    override fun onCleared() {
        super.onCleared()
        appManagerRepository.cleanup()
    }
}
