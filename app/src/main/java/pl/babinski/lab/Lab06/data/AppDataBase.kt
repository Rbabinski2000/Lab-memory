package pl.babinski.lab.Lab06.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import pl.babinski.lab.Lab06.ListData.Priority
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Database(entities = [TodoTaskEntity::class], version = 1)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun taskDao(): TodoTaskDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "app_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
class LocalDateConverter {
    companion object {
        const val pattern = "yyyy-MM-dd"

        fun fromMillis(millis: Long): LocalDate {
            return Instant
                .ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }

        fun toMillis(date: LocalDate): Long {
            return Instant.ofEpochSecond(date.toEpochDay() * 24 * 60 * 60).toEpochMilli()
        }
    }

    @TypeConverter
    fun fromDateTime(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern(pattern))
    }

    @TypeConverter
    fun fromDateTime(str: String): LocalDate {
        return LocalDate.parse(str, DateTimeFormatter.ofPattern(pattern))
    }
    @TypeConverter
    fun fromPriority(priority: Priority):String{
        return priority.toString();
    }@TypeConverter
    fun toPriority(name: String):Priority{
        return Priority.valueOf(name)
    }
}