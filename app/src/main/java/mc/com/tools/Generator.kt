package mc.com.tools

class Generator {
     companion object {
        var exceptions = mutableListOf<String>()
        private var letters : Array<String> = emptyArray()
        private var listOfWords = mutableMapOf<Int, MutableList<String>>()

        fun generate(entries : Array<String>) : MutableList<String> {
            letters = entries

            if(listOfWords.isNotEmpty())
                listOfWords.clear()

            var words = mutableListOf<String>()
            var result = mutableListOf<String>()

            for (word_length in 2..letters.size)
            {
                listOfWords[word_length] = mutableListOf<String>()
                for (index in letters.indices) {
                    result = combine(
                        index,
                        letters[index],
                        word_length
                    )
                    listOfWords[word_length]!!.addAll(result)

                    words.addAll(result)
                }
                listOfWords[word_length] = listOfWords[word_length]!!.distinct().toMutableList()
            }

            /*Log.i("tests", "*****************************")
            Log.i("tests", "letters : ${Arrays.toString(words.toTypedArray())}")
            Log.i("tests", "*****************************")*/

            return words
        }



        private fun combine(index_current: Int, letter_current: String, word_length: Int): MutableList<String> {
            var combines = mutableListOf<String>()
            var others = mutableListOf<String>()

            if(word_length==2) {
                for (index in letters.indices)
                    if (index_current != index)
                        others.add(letters[index])
            }else{
                others = listOfWords[word_length-1]!!
            }

            others.forEach {
                if(!combines.contains("$letter_current$it"))
                    combines.add("$letter_current$it")
            }
            return combines
        }
    }
}