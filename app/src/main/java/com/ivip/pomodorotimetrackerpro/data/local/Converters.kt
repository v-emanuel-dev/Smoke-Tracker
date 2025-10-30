package com.ivip.pomodorotimetrackerpro.data.local

import androidx.room.TypeConverter
import com.ivip.pomodorotimetrackerpro.domain.model.SessionType

class Converters {
    @TypeConverter
    fun fromSessionType(value: SessionType): String = value.name

    @TypeConverter
    fun toSessionType(value: String): SessionType = SessionType.valueOf(value)
}
