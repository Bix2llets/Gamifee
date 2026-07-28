package com.example.midtermproject_24125072.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
  entities = [
    UserLoyaltyEntity::class,
    RewardEntryEntity::class,
    UserInformationEntity::class,
    CartItemEntity::class,
    OrderItemEntity::class,
    OrderCartItemEntity::class,
    CouponItemEntity::class,
  ],
  version = 1,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userLoyaltyDao(): UserLoyaltyDao
  abstract fun userInformationDao(): UserInformationDao
  abstract fun cartItemDao(): CartItemDao
  abstract fun orderItemDao(): OrderItemDao
  abstract fun couponItemDao(): CouponItemDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "app_database.db"
        )
          .fallbackToDestructiveMigration()
          .build()
          .also { INSTANCE = it }
      }
    }
  }
}
