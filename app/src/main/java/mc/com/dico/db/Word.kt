package mc.com.dico.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

import java.io.Serializable

@Entity(tableName = "words")
class Word(
    @field:ColumnInfo(name = "title")
    var title: String?, @field:ColumnInfo(name = "desc")
    var desc: String?
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

