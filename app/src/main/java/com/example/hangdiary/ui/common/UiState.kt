package com.example.hangdiary.ui.common

/**
 * 统一的UI状态封装
 * 用于标准化所有界面的状态管理
 */
sealed class UiState<out T> {
    /**
     * 加载中状态
     */
    object Loading : UiState<Nothing>()
    
    /**
     * 成功状态
     * @param data 成功获取的数据
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * 错误状态
     * @param exception 错误信息
     * @param message 用户友好的错误消息
     */
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "未知错误"
    ) : UiState<Nothing>()
    
    /**
     * 空状态
     * @param message 空状态提示消息
     */
    data class Empty(val message: String = "暂无数据") : UiState<Nothing>()
    
    /**
     * 刷新状态（在有数据的基础上刷新）
     * @param data 当前数据
     */
    data class Refreshing<T>(val data: T) : UiState<T>()
}

/**
 * UiState扩展函数
 */
inline fun <T> UiState<T>.onSuccess(action: (T) -> Unit): UiState<T> {
    if (this is UiState.Success) action(data)
    return this
}

inline fun <T> UiState<T>.onError(action: (Throwable, String) -> Unit): UiState<T> {
    if (this is UiState.Error) action(exception, message)
    return this
}

inline fun <T> UiState<T>.onLoading(action: () -> Unit): UiState<T> {
    if (this is UiState.Loading) action()
    return this
}

inline fun <T> UiState<T>.onEmpty(action: (String) -> Unit): UiState<T> {
    if (this is UiState.Empty) action(message)
    return this
}

/**
 * 获取数据，无论状态如何
 */
fun <T> UiState<T>.getDataOrNull(): T? = when (this) {
    is UiState.Success -> data
    is UiState.Refreshing -> data
    else -> null
}

/**
 * 检查是否为加载状态
 */
fun <T> UiState<T>.isLoading(): Boolean = this is UiState.Loading || this is UiState.Refreshing<*>

/**
 * 检查是否有数据
 */
fun <T> UiState<T>.hasData(): Boolean = this is UiState.Success || this is UiState.Refreshing<*>