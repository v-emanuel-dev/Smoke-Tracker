package com.timetracker.data.local

import androidx.room.TypeConverter
import com.timetracker.domain.model.SessionType

class Converters {
    @TypeConverter
    fun fromSessionType(value: SessionType): String = value.name

    @TypeConverter
    fun toSessionType(value: String): SessionType = SessionType.valueOf(value)
}
