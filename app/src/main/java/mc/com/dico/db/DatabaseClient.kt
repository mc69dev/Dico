package mc.com.dico.db

import android.arch.persistence.room.Room
import android.content.Context

class DatabaseClient private constructor(private val mCtx: Context) {
    val appDatabase: AppDatabase

    init {
        appDatabase = Room.databaseBuilder(mCtx, AppDatabase::class.java, "MyDico").build()
    }

    companion object {
        private var mInstance: DatabaseClient? = null
        @Synchronized
        fun getInstance(mCtx: Context): DatabaseClient {
            if (mInstance == null) {
                mInstance = DatabaseClient(mCtx)
            }
            return mInstance!!
        }
    }
}
