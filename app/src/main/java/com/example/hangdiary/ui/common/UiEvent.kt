package com.example.hangdiary.ui.common

/**
 * 统一的UI事件封装
 * 用于标准化界面间的事件通信
 */
sealed class UiEvent {
    /**
     * 刷新事件
     */
    object Refresh : UiEvent()
    
    /**
     * 显示错误消息
     * @param message 错误消息
     * @param actionLabel 操作按钮文本
     * @param action 操作回调
     */
    data class ShowError(
        val message: String,
        val actionLabel: String? = null,
        val action: (() -> Unit)? = null
    ) : UiEvent()
    
    /**
     * 显示成功消息
     * @param message 成功消息
     */
    data class ShowSuccess(val message: String) : UiEvent()
    
    /**
     * 显示信息消息
     * @param message 信息内容
     */
    data class ShowInfo(val message: String) : UiEvent()
    
    /**
     * 导航事件
     * @param route 目标路由
     * @param popUpTo 弹出到指定路由
     * @param inclusive 是否包含popUpTo路由
     */
    data class Navigate(
        val route: String,
        val popUpTo: String? = null,
        val inclusive: Boolean = false
    ) : UiEvent()
    
    /**
     * 返回事件
     */
    object NavigateBack : UiEvent()
    
    /**
     * 显示加载对话框
     * @param message 加载消息
     */
    data class ShowLoading(val message: String = "加载中...") : UiEvent()
    
    /**
     * 隐藏加载对话框
     */
    object HideLoading : UiEvent()
    
    /**
     * 显示确认对话框
     * @param title 标题
     * @param message 消息内容
     * @param confirmText 确认按钮文本
     * @param cancelText 取消按钮文本
     * @param onConfirm 确认回调
     * @param onCancel 取消回调
     */
    data class ShowConfirmDialog(
        val title: String,
        val message: String,
        val confirmText: String = "确认",
        val cancelText: String = "取消",
        val onConfirm: () -> Unit,
        val onCancel: (() -> Unit)? = null
    ) : UiEvent()
    
    /**
     * 请求权限
     * @param permissions 权限列表
     * @param onGranted 授权成功回调
     * @param onDenied 授权失败回调
     */
    data class RequestPermissions(
        val permissions: List<String>,
        val onGranted: () -> Unit,
        val onDenied: (() -> Unit)? = null
    ) : UiEvent()
    
    /**
     * 分享内容
     * @param content 分享内容
     * @param title 分享标题
     */
    data class ShareContent(
        val content: String,
        val title: String = "分享"
    ) : UiEvent()
}

/**
 * UI事件处理器接口
 */
interface UiEventHandler {
    fun handleUiEvent(event: UiEvent)
}

/**
 * 默认的UI事件处理器实现
 */
class DefaultUiEventHandler : UiEventHandler {
    override fun handleUiEvent(event: UiEvent) {
        when (event) {
            is UiEvent.Refresh -> {
                // 默认刷新处理
            }
            is UiEvent.ShowError -> {
                // 默认错误显示处理
            }
            is UiEvent.ShowSuccess -> {
                // 默认成功消息处理
            }
            is UiEvent.ShowInfo -> {
                // 默认信息消息处理
            }
            is UiEvent.Navigate -> {
                // 默认导航处理
            }
            is UiEvent.NavigateBack -> {
                // 默认返回处理
            }
            is UiEvent.ShowLoading -> {
                // 默认加载显示处理
            }
            is UiEvent.HideLoading -> {
                // 默认加载隐藏处理
            }
            is UiEvent.ShowConfirmDialog -> {
                // 默认确认对话框处理
            }
            is UiEvent.RequestPermissions -> {
                // 默认权限请求处理
            }
            is UiEvent.ShareContent -> {
                // 默认分享处理
            }
        }
    }
}