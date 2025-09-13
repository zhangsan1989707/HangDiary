package com.example.hangdiary.data.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 缓存管理器
 * 提供内存缓存功能，提升应用性能
 */
@Singleton
class CacheManager @Inject constructor() {
    
    private val cache = ConcurrentHashMap<String, CacheEntry<*>>()
    private val mutex = Mutex()
    
    /**
     * 获取缓存的Flow数据
     * @param key 缓存键
     * @param loader 数据加载器
     * @param ttl 缓存生存时间（毫秒）
     * @return 缓存的数据流
     */
    suspend fun <T> getCachedFlow(
        key: String,
        ttl: Long = DEFAULT_TTL,
        loader: () -> Flow<T>
    ): Flow<T> {
        return mutex.withLock {
            val entry = cache[key] as? CacheEntry<Flow<T>>
            
            if (entry != null && !entry.isExpired()) {
                entry.data
            } else {
                val flow = loader()
                cache[key] = CacheEntry(flow, System.currentTimeMillis() + ttl)
                flow
            }
        }
    }
    
    /**
     * 获取缓存的单个数据
     * @param key 缓存键
     * @param loader 数据加载器
     * @param ttl 缓存生存时间（毫秒）
     * @return 缓存的数据
     */
    suspend fun <T> getCached(
        key: String,
        ttl: Long = DEFAULT_TTL,
        loader: suspend () -> T
    ): T {
        return mutex.withLock {
            val entry = cache[key] as? CacheEntry<T>
            
            if (entry != null && !entry.isExpired()) {
                entry.data
            } else {
                val data = loader()
                cache[key] = CacheEntry(data, System.currentTimeMillis() + ttl)
                data
            }
        }
    }
    
    /**
     * 设置缓存
     * @param key 缓存键
     * @param data 缓存数据
     * @param ttl 缓存生存时间（毫秒）
     */
    suspend fun <T> put(key: String, data: T, ttl: Long = DEFAULT_TTL) {
        mutex.withLock {
            cache[key] = CacheEntry(data, System.currentTimeMillis() + ttl)
        }
    }
    
    /**
     * 获取缓存数据
     * @param key 缓存键
     * @return 缓存的数据，如果不存在或已过期则返回null
     */
    suspend fun <T> get(key: String): T? {
        return mutex.withLock {
            val entry = cache[key] as? CacheEntry<T>
            if (entry != null && !entry.isExpired()) {
                entry.data
            } else {
                cache.remove(key)
                null
            }
        }
    }
    
    /**
     * 移除缓存
     * @param key 缓存键
     */
    suspend fun remove(key: String) {
        mutex.withLock {
            cache.remove(key)
        }
    }
    
    /**
     * 清空所有缓存
     */
    suspend fun clear() {
        mutex.withLock {
            cache.clear()
        }
    }
    
    /**
     * 清理过期缓存
     */
    suspend fun cleanExpired() {
        mutex.withLock {
            val expiredKeys = cache.entries
                .filter { it.value.isExpired() }
                .map { it.key }
            
            expiredKeys.forEach { cache.remove(it) }
        }
    }
    
    /**
     * 获取缓存统计信息
     */
    suspend fun getStats(): CacheStats {
        return mutex.withLock {
            val totalEntries = cache.size
            val expiredEntries = cache.values.count { it.isExpired() }
            val validEntries = totalEntries - expiredEntries
            
            CacheStats(
                totalEntries = totalEntries,
                validEntries = validEntries,
                expiredEntries = expiredEntries,
                hitRate = 0.0 // TODO: 实现命中率统计
            )
        }
    }
    
    companion object {
        const val DEFAULT_TTL = 5 * 60 * 1000L // 5分钟
        const val SHORT_TTL = 1 * 60 * 1000L   // 1分钟
        const val LONG_TTL = 30 * 60 * 1000L   // 30分钟
        
        // 预定义的缓存键
        const val KEY_ALL_DIARIES = "all_diaries"
        const val KEY_RECENT_DIARIES = "recent_diaries"
        const val KEY_PINNED_DIARIES = "pinned_diaries"
        const val KEY_ALL_TAGS = "all_tags"
        const val KEY_ALL_TODOS = "all_todos"
        const val KEY_SETTINGS = "settings"
    }
}

/**
 * 缓存条目
 */
private data class CacheEntry<T>(
    val data: T,
    val expiryTime: Long
) {
    fun isExpired(): Boolean = System.currentTimeMillis() > expiryTime
}

/**
 * 缓存统计信息
 */
data class CacheStats(
    val totalEntries: Int,
    val validEntries: Int,
    val expiredEntries: Int,
    val hitRate: Double
)

/**
 * 缓存策略
 */
enum class CacheStrategy {
    CACHE_FIRST,    // 优先使用缓存
    NETWORK_FIRST,  // 优先使用网络
    CACHE_ONLY,     // 仅使用缓存
    NETWORK_ONLY    // 仅使用网络
}

/**
 * 智能缓存管理器
 * 根据不同策略管理缓存
 */
@Singleton
class SmartCacheManager @Inject constructor(
    private val cacheManager: CacheManager
) {
    
    /**
     * 根据策略获取数据
     */
    suspend fun <T> getData(
        key: String,
        strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
        networkLoader: suspend () -> T,
        ttl: Long = CacheManager.DEFAULT_TTL
    ): T {
        return when (strategy) {
            CacheStrategy.CACHE_FIRST -> {
                cacheManager.get<T>(key) ?: run {
                    val data = networkLoader()
                    cacheManager.put(key, data, ttl)
                    data
                }
            }
            CacheStrategy.NETWORK_FIRST -> {
                try {
                    val data = networkLoader()
                    cacheManager.put(key, data, ttl)
                    data
                } catch (e: Exception) {
                    cacheManager.get<T>(key) ?: throw e
                }
            }
            CacheStrategy.CACHE_ONLY -> {
                cacheManager.get<T>(key) ?: throw IllegalStateException("No cached data available")
            }
            CacheStrategy.NETWORK_ONLY -> {
                networkLoader()
            }
        }
    }
}