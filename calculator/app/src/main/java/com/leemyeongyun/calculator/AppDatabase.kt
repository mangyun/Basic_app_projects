package com.leemyeongyun.calculator

import androidx.room.Database
import androidx.room.RoomDatabase
import com.leemyeongyun.calculator.dao.HistoryDao
import com.leemyeongyun.calculator.model.History

@Database(entities = [History::class], version = 1) //버전을 명시해서 테이블구조를 유지
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}