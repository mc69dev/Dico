package mc.com

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.WebView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_dico.*
import mc.com.adapters.McRecyclerViewAdapter
import mc.com.dico.api.DicoApi
import mc.com.dico.api.JobDone
import mc.com.dico.db.DicoManager
import mc.com.dico.db.Word
import mc.com.fragments.FragmentFactory
import mc.com.fragments.OnFragmentViewCreated
import android.os.AsyncTask
import android.support.design.internal.NavigationMenu
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.*
import mc.com.tools.Dico
import mc.com.tools.Generator
import java.util.*
import java.nio.file.Files.size



class MainActivity : AppCompatActivity(), OnFragmentViewCreated{
    private var fragment : Int =1
    private var waiting_search_word :String =""
    private var context = this@MainActivity

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
           R.id.navigation_home -> fragment=1
           R.id.navigation_dashboard -> fragment=2
           R.id.navigation_notifications -> fragment=3
        }

        attachFragment(fragment)
        return@OnNavigationItemSelectedListener true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        supportActionBar!!.hide()

        navigation.selectedItemId = R.id.navigation_home

        dbGetAll()
    }

    private fun attachFragment( num: Int) { //savedInstanceState: Bundle?,
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, FragmentFactory.Instance(this@MainActivity, num))
            .commitNow()
    }

    private var webView : WebView? = null
    private var edtSearch : EditText? = null
    private var txtCurrentWord : TextView?=null

    override fun getView(view: View) {
        Log.i("tests","fragment : $fragment ==> view : $view")
        when (fragment) {
            1 -> initSearch(view)
            2 -> initInfo(view)
            else -> initScrabble(view)
        }
    }
    private var btnSearch : Button? = null

   /* private var btnScrabble : Button? = null

    private var l1 : EditText?=null
    private var l2 : EditText?=null
    private var l3 : EditText?=null
    private var l4 : EditText?=null
    private var l5 : EditText?=null
    private var l6 : EditText?=null*/

    private fun initInfo(view: View) {

        Log.i("tests","===> Infos : $fragment ==> view : $view")

        txtCurrentWord = view.findViewById(R.id.txt_current_word)
        txtCurrentWord!!.text = Dico.current_word

        var recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView!!.setHasFixedSize(true)

        //val layoutManager = LinearLayoutManager(context)
        var viewLayoutManager = GridLayoutManager(context, 3)

        recyclerView!!.layoutManager = viewLayoutManager!!       //layoutManager
        //recyclerView!!.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        val data  = Dico.relatedWords()
        var adapter = McRecyclerViewAdapter(
            data.toTypedArray(),
            object : McRecyclerViewAdapter.OnItemClickListener {
                override fun onItemClick(postion:Int, item: String) {
                   Toast.makeText(context, "Click on $item", Toast.LENGTH_SHORT).show()
                   waiting_search_word = item
                   navigation.selectedItemId = R.id.navigation_home
                }
            }
        )
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            context, R.anim.layout_fall_down_animation
        )
    }
    private fun initSearch(view: View) {
        Log.i("tests","===> Search : $fragment ==> view : $view")

        btnSearch = view.findViewById(R.id.btn_search)
        webView = view.findViewById(R.id.webview)

        edtSearch = view.findViewById(R.id.edt_search)
        if(!waiting_search_word.isEmpty()){
            edtSearch!!.setText(waiting_search_word)
            waiting_search_word=""
            Dico.current_word=""

            searchInDico()
        }

        if(!Dico.current_word!!.isEmpty()) {
            edtSearch!!.setText(Dico.current_word!!)
            Dico.display(Dico.current_defs_html!!, webView!!)
        }

        if (btnSearch != null)
            btnSearch!!.setOnClickListener(View.OnClickListener {
                Log.i("tests","===> Search - btnSearch Click : "+edtSearch!!.text)
                if (edtSearch!!.text.isEmpty())
                    return@OnClickListener

                if(edtSearch!!.text.toString()!= Dico.current_word!!) {
                    Dico.current_word = edtSearch!!.text.toString()
                    searchInDico()
                }else{
                    Dico.display(Dico.current_defs_html!!, webView!!)
                }
            })
    }

    private fun searchInDico() {
        Log.i("tests","===> Search - Call API! : "+edtSearch!!.text)

        var search_word=edtSearch!!.text
        var call = DicoApi(context, R.layout.progress_dialog,true, callback)
        call.execute(search_word.toString())
    }
    private val callback = object : JobDone {
        override fun notify(word: String, html: String) = Dico.parseDefsHtml(html, webView!!)
    }

    /**
     * Scrabble
     */

    private var TaskExecuting : Boolean =false

    private var scrabble : Scrabble? = null
    private fun initScrabble(view: View) {
        if(scrabble==null)
           scrabble = Scrabble(context, view)
        scrabble!!.load()
    }

    internal inner class Scrabble(context : Context,view: View) {
        private var context : Context = context
        private var view: View = view

        lateinit var progress : ProgressBar
        lateinit var start : Button
        lateinit var stop : Button
        lateinit var message : TextView
        lateinit var edtLetters : MutableList<EditText>

        var interrupted : Boolean =false
        var recyclerView: RecyclerView?=null
        //var adapter : McRecyclerViewAdapter?=null

        var found_words : MutableList<String> = mutableListOf()
        var _progress=0
        var _save : MutableList<String> = mutableListOf()
        var _AsyncTaks : MutableList<AsyncTask<String, Int, String>> = mutableListOf()

       fun load() {
            Log.i("tests","Scrabble Load..")

            progress = view.findViewById(R.id.progressBar)
            start = view.findViewById(R.id.btn_start_scrabble)
            stop = view.findViewById(R.id.btn_stop_scrabble)
            message=view.findViewById(R.id.title)

            setLettersListener()
            start.setOnClickListener { Search() }
            stop.setOnClickListener {
                _AsyncTaks.forEach {
                    Log.i("tests","canceling..")
                    it.cancel(true)
                }
                progress.progress=0
                setState(true)
            }
            setRecyclerView()
        }

        private fun setRecyclerView() {
            var viewLayoutManager = GridLayoutManager(context, 3) //LinearLayoutManager(this)
            var viewAdapter = McRecyclerViewAdapter(
                found_words.toTypedArray(),
                object : McRecyclerViewAdapter.OnItemClickListener {
                    override fun onItemClick(postion:Int, item: String) {
                        //Toast.makeText(context, "Click on $item", Toast.LENGTH_SHORT).show()
                        waiting_search_word = item
                        navigation.selectedItemId = R.id.navigation_home
                    }
                }
            )

            recyclerView = view.findViewById<RecyclerView>(R.id.list_words).apply {
                setHasFixedSize(true)
                layoutManager = viewLayoutManager!!
                adapter = viewAdapter
            }

            recyclerView!!.layoutAnimation = AnimationUtils.loadLayoutAnimation(
                context, R.anim.layout_fall_down_animation
            )
        }
        private fun setLettersListener(){
            var container = view.findViewById<LinearLayout>(R.id.letters)
            edtLetters= mutableListOf()

            for (i in 0..(container.childCount-1)){
                val edt = container.getChildAt(i) as EditText
                edtLetters.add(edt)

                edt.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        //Log.i("tests","${edt.id} => : "+s.toString())
                        if(!s.toString().isEmpty()) {
                            val nextID =  (edtLetters.indexOf(edt)+1)%edtLetters.size
                            setFocusOn(edtLetters[nextID])
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })
            }
        }
        private fun setFocusOn(edt: EditText) {
            edt.requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
        private fun getLetters(): MutableList<String> {
            var letters = mutableListOf<String>()
            edtLetters.forEach {
                if(!it.text.isEmpty())
                    letters.add(it.text.toString())
            }
            return letters
        }

        private fun enableBottomBar(enable: Boolean) {
            for (i in 0 until navigation.menu.size()) {
                navigation.menu.getItem(i).isEnabled = enable
            }
        }
        private fun setState(finished : Boolean){

            TaskExecuting =!finished
            enableBottomBar(finished)


            interrupted=finished

            start.isEnabled=finished
            stop.isEnabled=!finished

            var container=view.findViewById<LinearLayout>(R.id.letters)
            for (i in 0..(container.childCount-1))
                container.getChildAt(i).isEnabled=finished

            message.visibility = if(!finished) View.VISIBLE else View.GONE

        }
        private fun Search() {
            var letters= getLetters().toTypedArray()
            if(letters.isEmpty())
                return

            setState(false)
            val runnable = Runnable {

                found_words.clear()
                _progress=1

                var words =  Generator.generate(letters)
                var listwords = words.toTypedArray().distinct()

                _save=listwords.toMutableList()

                Log.i("tests", "*****************************")
                Log.i("tests", "words [${listwords.size}]:  ${Arrays.toString(listwords.toTypedArray())}")
                Log.i("tests", "*****************************")

                //SystemClock.sleep(2000)
                progress.max=listwords.size
                val max=listwords.size-1

                var i=0
                var nbfound=0
                var word :String
                while(!interrupted && i<=max){
                    if(i==listwords.size) //i>max ||
                        break

                    word=listwords[i]

                    if(dbExists(word)){
                        Log.i("tests", "$word from..database!")
                        updateList(word)
                        nbfound++

                        updateProgress(word, nbfound)

                    }else {
                        var task = DicoApi(
                            context,
                            0,
                            false,
                            object : JobDone {
                                override fun notify(word: String, html: String) {

                                    var definition = Dico.getDefinition(html, false)
                                    var correct = !definition.isNullOrEmpty()

                                    display(word, correct)
                                    if (correct) {
                                        Log.i("tests", "Found : $word from..website!")

                                        updateList(word)
                                        dbAdd(Word(word,definition))

                                        nbfound++
                                    }
                                    updateProgress(word, nbfound)
                                }
                            })
                        task.execute(word)
                        _AsyncTaks.add(task)
                    }
                    i++
                }
            }
            Thread(runnable).start()
        }

        private fun updateProgress(word: String, nbfound: Int) {
            (context as Activity).runOnUiThread {
                progress.post {
                    progress.progress = _progress++
                    message.text = word
                }
            }

            _save.remove(word)
            if (_save.size == 0) {
                Toast.makeText(
                    context,
                    "Finished! $nbfound words found!",
                    Toast.LENGTH_SHORT
                ).show()
                setState(true)
                display("", false)
            }
        }


        private fun updateList(word: String) {
            (context as Activity).runOnUiThread {
                found_words.add(word)
                (recyclerView!!.adapter as McRecyclerViewAdapter).updateDataSet(found_words.toTypedArray())
                recyclerView!!.adapter!!.notifyDataSetChanged()
            }
        }
       private fun display(word : String, correct : Boolean) {
            message.text = word
            message.setTextColor(if(correct) Color.WHITE else context.resources.getColor(R.color.colorPrimaryDark,null))
            //SystemClock.sleep(10)
        }

        fun start00(){
            /*var viewLayoutManager = GridLayoutManager(context, 3) //LinearLayoutManager(this)

            var viewAdapter = McRecyclerViewAdapter(
                found_words.toTypedArray(),
                object : McRecyclerViewAdapter.OnItemClickListener {
                    override fun onItemClick(postion:Int, item: String) {
                        //Toast.makeText(context, "Click on $item", Toast.LENGTH_SHORT).show()
                        waiting_search_word = item
                        navigation.selectedItemId = R.id.navigation_home
                    }
                }
            )

            var recyclerView = this.view.findViewById<RecyclerView>(R.id.list_words).apply {
                setHasFixedSize(true)
                layoutManager = viewLayoutManager
                adapter = viewAdapter
            }

            recyclerView!!.layoutAnimation = AnimationUtils.loadLayoutAnimation(
                context, R.anim.layout_fall_down_animation
            )

            var _letters = arrayOf(
                l1!!.text.toString(),
                l2!!.text.toString(),
                l3!!.text.toString(),
                l4!!.text.toString(),
                l5!!.text.toString(),
                l6!!.text.toString()
            )
            var listOfWords = Generator.generate(_letters)

            //var db = DicoManager(context)
            listOfWords.forEach {

                it.value.forEach { word ->
                    if(dbExists(word)){
                        found_words.add(word)

                        (recyclerView!!.adapter as McRecyclerViewAdapter).updateDataSet(found_words.toTypedArray())
                        recyclerView!!.adapter!!.notifyDataSetChanged()

                        Log.i("tests", "$word from..database!")
                    }else {
                        DicoApi(
                            context,
                            0,
                            false,
                            object : JobDone {
                                override fun notify(result: String) {
                                    var definition= Dico.getDefinition(result, false)
                                    if (!definition.isNullOrEmpty()) {

                                        Log.i("tests", "$word from..website!")
                                        found_words.add(word)

                                        dbAdd(Word(word,definition))

                                        (recyclerView!!.adapter as McRecyclerViewAdapter).updateDataSet(found_words.toTypedArray())
                                        recyclerView!!.adapter!!.notifyDataSetChanged()
                                    }
                                }
                            }).execute(Dico.DICO_API_URL + word)
                    }
                }
            }
            btnScrabble!!.isEnabled=true*/
        }
    }

    /**
     * Database
     */
    private var wordsFromDb = listOf<Word>()
    private fun dbExists(word: String): Boolean {
        //Log.i("tests","word in DB ? $word ..")
        wordsFromDb.forEach {
            if(it.title==word) {
                Log.i("tests","$word in DB!")
                return true
            }
        }
        return false
    }
    private fun dbAdd(word: Word) {
        AsyncTask.execute {
            var db = DicoManager(context)
            db.add(word)
            Log.i("tests","word ${word.title} added in DB!")
        }
    }
    private fun dbGetAll() {
        AsyncTask.execute {
            var db = DicoManager(context)
            wordsFromDb = db.list()
            Log.i("tests","words in DB : ${wordsFromDb.toTypedArray()}")
        }
    }

}
