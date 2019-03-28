package mc.com.dico.db

import android.arch.persistence.room.*

@Dao
interface WordDao {
    @get:Query("SELECT * FROM words ORDER BY title")
    val all: List<Word>

    @Query("SELECT * FROM words WHERE title LIKE :title ORDER BY title")
    fun listByTitle(title: String): List<Word>

    @Query("SELECT * FROM words WHERE id=:id")
    fun findById(id: Int): Word

    @Query("SELECT * FROM words WHERE title=:title")
    fun findByTitle(title: String): Word

    @Insert
    fun insert(word: Word)

    @Delete
    fun delete(word: Word)

    @Update
    fun update(word: Word)
}