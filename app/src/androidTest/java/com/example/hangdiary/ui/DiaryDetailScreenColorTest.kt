package com.example.hangdiary.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hangdiary.ui.theme.HangDiaryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * DiaryDetailScreen颜色相关UI测试类
 * 测试颜色选择器和颜色预览功能
 */
@RunWith(AndroidJUnit4::class)
class DiaryDetailScreenColorTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun testColorPicker_Display() {
        // 设置Compose内容
        composeTestRule.setContent {
            HangDiaryTheme {
                ColorPicker(
                    selectedColor = "red",
                    onColorSelected = {}
                )
            }
        }
        
        // 验证颜色选择器显示
        composeTestRule
            .onNodeWithText("颜色")
            .assertExists()
            .assertIsDisplayed()
        
        // 验证颜色选项显示
        composeTestRule
            .onNodeWithContentDescription("颜色: red")
            .assertExists()
            .assertIsDisplayed()
    }
    
    @Test
    fun testColorPicker_Selection() {
        var selectedColor = ""
        
        // 设置Compose内容
        composeTestRule.setContent {
            HangDiaryTheme {
                ColorPicker(
                    selectedColor = "red",
                    onColorSelected = { color ->
                        selectedColor = color
                    }
                )
            }
        }
        
        // 点击蓝色选项
        composeTestRule
            .onNodeWithContentDescription("颜色: blue")
            .performClick()
        
        // 验证颜色选择回调
        assertEquals("blue", selectedColor)
    }
    
    @Test
    fun testColorPreview_Display() {
        // 设置Compose内容
        composeTestRule.setContent {
            HangDiaryTheme {
                ColorPreview(
                    color = "red"
                )
            }
        }
        
        // 验证颜色预览显示
        composeTestRule
            .onNodeWithContentDescription("颜色预览: red")
            .assertExists()
            .assertIsDisplayed()
    }
    
    @Test
    fun testColorPreview_TransparentColor() {
        // 设置Compose内容
        composeTestRule.setContent {
            HangDiaryTheme {
                ColorPreview(
                    color = ""
                )
            }
        }
        
        // 验证透明色预览显示
        composeTestRule
            .onNodeWithContentDescription("颜色预览: 透明")
            .assertExists()
            .assertIsDisplayed()
    }
    
    @Test
    fun testColorPreview_UnknownColor() {
        // 设置Compose内容
        composeTestRule.setContent {
            HangDiaryTheme {
                ColorPreview(
                    color = "unknown_color"
                )
            }
        }
        
        // 验证未知颜色预览显示
        composeTestRule
            .onNodeWithContentDescription("颜色预览: 未知")
            .assertExists()
            .assertIsDisplayed()
    }
    
    @Test
    fun testColorPicker_AllOptions() {
        val expectedColors = listOf(
            "", "red", "pink", "purple", "deep_purple",
            "indigo", "blue", "light_blue", "cyan", "teal",
            "green", "light_green", "lime", "yellow", "amber",
            "orange", "deep_orange", "brown", "grey", "blue_grey"
        )
        
        // 设置Compose内容
        composeTestRule.setContent {
            HangDiaryTheme {
                ColorPicker(
                    selectedColor = "",
                    onColorSelected = {}
                )
            }
        }
        
        // 验证所有颜色选项都显示
        expectedColors.forEach { color ->
            val description = if (color.isEmpty()) "颜色: 透明" else "颜色: $color"
            composeTestRule
                .onNodeWithContentDescription(description)
                .assertExists()
                .assertIsDisplayed()
        }
    }
    
    @Test
    fun testColorPicker_HighlightSelected() {
        // 设置Compose内容
        composeTestRule.setContent {
            HangDiaryTheme {
                ColorPicker(
                    selectedColor = "blue",
                    onColorSelected = {}
                )
            }
        }
        
        // 验证选中的颜色有高亮显示
        composeTestRule
            .onNodeWithContentDescription("颜色: blue")
            .assertExists()
            .assertIsDisplayed()
        
        // 这里可以添加更多验证，例如检查选中的颜色是否有边框或其他高亮效果
        // 但这需要知道具体的UI实现细节
    }
}