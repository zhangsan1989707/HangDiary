package com.example.hangdiary.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.hangdiary.data.database.AppDatabase
import com.example.hangdiary.data.model.Diary
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

/**
 * DiaryDao单元测试类
 * 测试日记数据访问对象的功能，包括颜色相关的数据库操作
 */

class DiaryDaoTest {
    
    private lateinit var database: AppDatabase
    private lateinit var diaryDao: DiaryDao
    
    @Before
    fun setup() {
        // 创建内存数据库进行测试
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        diaryDao = database.diaryDao()
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun testInsertAndGetDiaryWithColor() = runBlocking {
        // 准备测试数据
        val testDiary = Diary(
            id = 0L,
            title = "测试日记",
            content = "测试内容",
            color = "red",
            imagePaths = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 插入日记
        val id = diaryDao.insert(testDiary)
        assertTrue(id > 0)
        
        // 获取日记
        val retrievedDiary = diaryDao.getById(id)
        assertNotNull(retrievedDiary)
        assertEquals("red", retrievedDiary?.color)
        assertEquals(testDiary.title, retrievedDiary?.title)
        assertEquals(testDiary.content, retrievedDiary?.content)
    }
    
    @Test
    fun testUpdateDiaryColor() = runBlocking {
        // 准备测试数据
        val testDiary = Diary(
            id = 0L,
            title = "测试日记",
            content = "测试内容",
            color = "red",
            imagePaths = listOf("path1", "path2"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 插入日记
        val id = diaryDao.insert(testDiary)
        assertTrue(id > 0)
        
        // 更新颜色
        val updateResult = diaryDao.updateDiaryColor(id, "blue")
        assertEquals(1, updateResult)
        
        // 验证更新结果
        val updatedDiary = diaryDao.getById(id)
        assertNotNull(updatedDiary)
        assertEquals("blue", updatedDiary?.color)
    }
    
    @Test
    fun testBatchUpdateDiaryColors() = runBlocking {
        // 准备测试数据
        val diary1 = Diary(
            id = 0L,
            title = "日记1",
            content = "内容1",
            color = "red",
            imagePaths = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val diary2 = Diary(
            id = 0L,
            title = "日记2",
            content = "内容2",
            color = "green",
            imagePaths = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val diary3 = Diary(
            id = 0L,
            title = "日记3",
            content = "内容3",
            color = "yellow",
            imagePaths = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 插入日记
        val id1 = diaryDao.insert(diary1)
        val id2 = diaryDao.insert(diary2)
        val id3 = diaryDao.insert(diary3)
        
        assertTrue(id1 > 0)
        assertTrue(id2 > 0)
        assertTrue(id3 > 0)
        
        // 批量更新颜色
        val diaryIds = listOf(id1, id2, id3)
        val updateResult = diaryDao.updateDiariesColor(diaryIds, "purple")
        assertEquals(3, updateResult)
        
        // 验证更新结果
        val updatedDiary1 = diaryDao.getById(id1)
        val updatedDiary2 = diaryDao.getById(id2)
        val updatedDiary3 = diaryDao.getById(id3)
        
        assertEquals("purple", updatedDiary1?.color)
        assertEquals("purple", updatedDiary2?.color)
        assertEquals("purple", updatedDiary3?.color)
    }
    
    @Test
    fun testUpdateDiary() = runBlocking {
        // 准备测试数据
        val testDiary = Diary(
            id = 0L,
            title = "测试日记",
            content = "测试内容",
            color = "red",
            imagePaths = listOf("path1", "path2"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 插入日记
        val id = diaryDao.insert(testDiary)
        assertTrue(id > 0)
        
        // 获取并更新日记
        val retrievedDiary = diaryDao.getById(id)
        assertNotNull(retrievedDiary)
        
        val updatedDiary = retrievedDiary?.copy(
            title = "更新日记",
            content = "更新内容",
            color = "blue",
            updatedAt = LocalDateTime.now()
        )
        
        if (updatedDiary != null) {
            val updateResult = diaryDao.update(updatedDiary)
            assertEquals(1, updateResult)
            
            // 验证更新结果
            val finalDiary = diaryDao.getById(id)
            assertNotNull(finalDiary)
            assertEquals("更新日记", finalDiary?.title)
            assertEquals("更新内容", finalDiary?.content)
            assertEquals("blue", finalDiary?.color)
        }
    }
    
    @Test
    fun testDeleteDiary() = runBlocking {
        // 准备测试数据
        val testDiary = Diary(
            id = 0L,
            title = "测试日记",
            content = "测试内容",
            color = "red",
            imagePaths = listOf("path1", "path2"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 插入日记
        val id = diaryDao.insert(testDiary)
        assertTrue(id > 0)
        
        // 验证日记存在
        var retrievedDiary = diaryDao.getById(id)
        assertNotNull(retrievedDiary)
        
        // 删除日记
        val diaryToDelete = retrievedDiary!!
        diaryDao.delete(diaryToDelete)
        
        // 验证日记已删除
        retrievedDiary = diaryDao.getById(id)
        assertNull(retrievedDiary)
    }
    
    @Test
    fun testGetAllDiaries() = runBlocking {
        // 准备测试数据
        val diary1 = Diary(
            id = 0L,
            title = "日记1",
            content = "内容1",
            color = "red",
            imagePaths = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val diary2 = Diary(
            id = 0L,
            title = "日记2",
            content = "内容2",
            color = "blue",
            imagePaths = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 插入日记
        diaryDao.insert(diary1)
        diaryDao.insert(diary2)
        
        // 获取所有日记 - 由于getAllDiaries返回Flow，我们需要收集第一个值
        val allDiariesFlow = diaryDao.getAllDiaries()
        val allDiaries = allDiariesFlow.first()
        assertEquals(2, allDiaries.size)
        
        // 验证颜色信息
        assertTrue(allDiaries.any { it.color == "red" })
        assertTrue(allDiaries.any { it.color == "blue" })
    }
}