package com.example.midtermproject_24125072.data.local

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

class Converters {
  @TypeConverter
  fun fromZonedDateTime(value: ZonedDateTime): Long = value.toInstant().toEpochMilli()

  @TypeConverter
  fun toZonedDateTime(value: Long): ZonedDateTime =
    Instant.ofEpochMilli(value).atZone(ZoneOffset.UTC)
}
