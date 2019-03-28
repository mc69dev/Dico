package mc.com.dico.db

import android.content.Context
import android.util.Log

class DicoManager(context: Context) {
    companion object {
        private var dao: WordDao? = null
    }
    init {
        if (dao == null)
            dao = DatabaseClient.getInstance(context).appDatabase.wordDao()

        Log.i("tests","Dao :  $dao")
    }

    fun add(word: Word) {
        dao!!.insert(word)
    }

    fun add(words: List<Word>) {
        words.forEach { word ->
            if (!exists(word.title!!))
                dao!!.insert(word)
        }
    }

    fun delete(word: Word) {
        dao!!.delete(word)
    }

    fun delete() { //All!
        list().forEach { w -> delete(w) }
    }

    fun update(word: Word) {
        dao!!.update(word)
    }

    fun list(): List<Word> {
        return dao!!.all
    }

    operator fun get(id: Int): Word {
        return dao!!.findById(id)
    }

    fun exists(title: String): Boolean {
        return dao!!.findByTitle(title) == null
    }


}
