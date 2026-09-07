package com.example.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.LruCache
import com.example.data.model.AppItem
import com.example.data.model.AppSortOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppManagerRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager

    // LruCache for app icons (up to 30MB)
    private val iconCache = object : LruCache<String, Bitmap>(30 * 1024 * 1024) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount
        }
    }

    private val _installedApps = MutableStateFlow<List<AppItem>>(emptyList())
    val installedApps: StateFlow<List<AppItem>> = _installedApps.asStateFlow()

    private val _recentApps = MutableStateFlow<List<AppItem>>(emptyList())
    val recentApps: StateFlow<List<AppItem>> = _recentApps.asStateFlow()

    private var packageReceiver: BroadcastReceiver? = null

    init {
        registerPackageChangeReceiver()
    }

    suspend fun refreshInstalledApps(sortOrder: AppSortOrder = AppSortOrder.ALPHABETICAL) = withContext(Dispatchers.IO) {
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(mainIntent, 0)
        }

        val appList = mutableListOf<AppItem>()

        for (resolveInfo in resolveInfos) {
            val packageName = resolveInfo.activityInfo.packageName
            val className = resolveInfo.activityInfo.name

            // Skip self launcher from app drawer list if preferred, or keep it
            val label = resolveInfo.loadLabel(packageManager).toString().trim().ifEmpty { packageName }
            val isSystemApp = (resolveInfo.activityInfo.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0

            val iconBitmap = loadAppIconBitmap(packageName, resolveInfo, label)

            appList.add(
                AppItem(
                    packageName = packageName,
                    className = className,
                    label = label,
                    iconBitmap = iconBitmap,
                    isSystemApp = isSystemApp
                )
            )
        }

        // Merge built-in asset apps if not already present in installed apps list
        val builtInApps = AppIconPackManager.getBuiltInApps(context)
        for (builtIn in builtInApps) {
            val exists = appList.any {
                it.packageName == builtIn.packageName || it.label.equals(builtIn.label, ignoreCase = true)
            }
            if (!exists) {
                appList.add(builtIn)
            }
        }

        val sortedList = when (sortOrder) {
            AppSortOrder.ALPHABETICAL -> appList.sortedBy { it.label.lowercase() }
            AppSortOrder.RECENTLY_USED -> appList.sortedByDescending { it.lastUsedTime }
            AppSortOrder.MOST_USED -> appList.sortedByDescending { it.launchCount }
        }

        _installedApps.value = sortedList
    }

    fun getAppIcon(packageName: String): Bitmap {
        val cached = iconCache.get(packageName)
        if (cached != null) return cached

        val customIcon = AppIconPackManager.getCustomIconBitmap(context, packageName, "")
        if (customIcon != null) {
            iconCache.put(packageName, customIcon)
            return customIcon
        }

        return try {
            val drawable = packageManager.getApplicationIcon(packageName)
            val bitmap = drawableToBitmap(drawable)
            iconCache.put(packageName, bitmap)
            bitmap
        } catch (e: Exception) {
            val fallback = createFallbackBitmap(packageName)
            iconCache.put(packageName, fallback)
            fallback
        }
    }

    private fun loadAppIconBitmap(packageName: String, resolveInfo: ResolveInfo, label: String): Bitmap {
        val cached = iconCache.get(packageName)
        if (cached != null) return cached

        val customIcon = AppIconPackManager.getCustomIconBitmap(context, packageName, label)
        if (customIcon != null) {
            iconCache.put(packageName, customIcon)
            return customIcon
        }

        return try {
            val drawable = resolveInfo.loadIcon(packageManager)
            val bitmap = drawableToBitmap(drawable)
            iconCache.put(packageName, bitmap)
            bitmap
        } catch (e: Exception) {
            val fallback = createFallbackBitmap(packageName)
            iconCache.put(packageName, fallback)
            fallback
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 128
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 128
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun createFallbackBitmap(packageName: String): Bitmap {
        val size = 128
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#334155")
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 54f
            textAlign = Paint.Align.CENTER
        }
        val firstChar = packageName.takeLastWhile { it != '.' }.take(1).uppercase()
        val initial = if (firstChar.isNotEmpty()) firstChar else "A"
        val yPos = (size / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(initial, size / 2f, yPos, textPaint)

        return bitmap
    }

    fun launchApp(packageName: String) {
        try {
            if (AppIconPackManager.launchBuiltInApp(context, packageName)) {
                markAppUsed(packageName)
                return
            }

            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                markAppUsed(packageName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openAppDetails(packageName: String) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun requestUninstallApp(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
                data = android.net.Uri.fromParts("package", packageName, null)
                putExtra(Intent.EXTRA_RETURN_RESULT, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun markAppUsed(packageName: String) {
        val currentList = _installedApps.value.toMutableList()
        val index = currentList.indexOfFirst { it.packageName == packageName }
        if (index != -1) {
            val item = currentList[index]
            val updated = item.copy(
                launchCount = item.launchCount + 1,
                lastUsedTime = System.currentTimeMillis()
            )
            currentList[index] = updated
            _installedApps.value = currentList

            val recents = _recentApps.value.toMutableList()
            recents.removeAll { it.packageName == packageName }
            recents.add(0, updated)
            _recentApps.value = recents.take(10)
        }
    }

    private fun registerPackageChangeReceiver() {
        packageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Clear cache on package changes and update installed app list
                iconCache.evictAll()
                CoroutineScope(Dispatchers.IO).launch {
                    refreshInstalledApps()
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        context.registerReceiver(packageReceiver, filter)
    }

    fun cleanup() {
        packageReceiver?.let {
            runCatching { context.unregisterReceiver(it) }
        }
    }
}
