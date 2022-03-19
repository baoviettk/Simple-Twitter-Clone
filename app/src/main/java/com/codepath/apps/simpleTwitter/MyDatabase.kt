package com.codepath.apps.simpleTwitter

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codepath.apps.simpleTwitter.models.SampleModel
import com.codepath.apps.simpleTwitter.models.SampleModelDao

@Database(entities = [SampleModel::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun sampleModelDao(): SampleModelDao?

    companion object {
        // Database name to be used
        const val NAME = "MyDatabase"
    }
}