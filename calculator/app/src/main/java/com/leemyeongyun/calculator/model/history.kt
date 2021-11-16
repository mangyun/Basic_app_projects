package com.leemyeongyun.calculator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity //gradle에서 implementation설정을 하고옴

//각 변수들의 저장 표시
data class History(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name = "expression") val expression: String?,
    @ColumnInfo(name = "result") val result: String?
)