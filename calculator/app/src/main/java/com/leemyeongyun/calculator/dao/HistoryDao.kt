package com.leemyeongyun.calculator.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.leemyeongyun.calculator.model.History

@Dao //조회, 삭제 등 관리기능
interface HistoryDao {

    @Query("SELECT * FROM history") // 모든 history 가져옴
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history: History)// history 하나를 삽입

    @Query("DELETE FROM history")// history 전부삭제
    fun deleteAll()

    /*
    //하나만 삭제하고싶다면
    @Delete
    fun delete(history: History)

    //모든 result가 인자로 들어온 result를 가져와서 History로 반환
    @Query("SELECT * FROM history WHERE result LIKE :result")
    fun findByResult(result: String) : List<History>

    //만약 하나만 반환하고싶다면 LIMIT 1을 취함
    @Query("SELECT * FROM history WHERE result LIKE :result LIMIT 1")
    fun findByResult(result: String) : History
     */
}