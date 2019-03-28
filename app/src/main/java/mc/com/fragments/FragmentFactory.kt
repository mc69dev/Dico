package mc.com.fragments
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mc.com.R

class FragmentFactory : Fragment() {
    companion object {
        private var parentActivity: OnFragmentViewCreated? = null
        private var layout_fragment: Int = 0
        //private var num_fragment: Int = 1
        private val fragments_layouts = listOf(
            R.layout.fragment_dico,
            R.layout.fragment_infos,
            R.layout.fragment_scrabble)

  /*      fun Instance(num: Int): Fragment {
            layout_fragment = fragments_layouts[num-1]
            num_fragment = num
            return FragmentFactory()
        }*/

        fun Instance(activity: OnFragmentViewCreated, num: Int): Fragment {
            parentActivity=activity
            layout_fragment = fragments_layouts[num-1]
            //num_fragment = num
            return FragmentFactory()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentActivity!!.getView(view) //send view to parent activity!

       /* if(num_fragment ==1)
           initSearch(view)
        else if(num_fragment==2)
           initInfos(view)*/
    }

    /*private fun initInfos(view: View) {
        txtCurrentWord = view.findViewById(R.id.txt_current_word)
        txtCurrentWord!!.text = Dico.current_word

        recyclerView = view.findViewById(R.id.list)
        recyclerView!!.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager=layoutManager
        recyclerView!!.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        val data  = Dico.relatedWords()
        adapter = McRecyclerViewAdapter(
            data.toTypedArray(),
            object : McRecyclerViewAdapter.OnItemClickListener {
                override fun onItemClick(item: String) {
                   // Toast.makeText(context, "Click on $item", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            context, R.anim.layout_fall_down_animation
        )
    }
    private fun initSearch(view: View) {
        btnSearch = view.findViewById(R.id.btn_search)
        webView = view.findViewById(R.id.webview)
        edtSearch = view.findViewById(R.id.edt_search)


        if(!Dico.current_word!!.isEmpty()) {
            edtSearch!!.setText(Dico.current_word!!)
            Dico.display(Dico.current_defs_html!!, webview)
        }

        if (btnSearch != null)
            btnSearch!!.setOnClickListener(View.OnClickListener {
                if (edtSearch!!.text.isEmpty())
                    return@OnClickListener

                if(edtSearch!!.text.toString()!=Dico.current_word!!) {
                    Dico.current_word = edtSearch!!.text.toString()

                    var call = DicoApi(activity as Activity, R.layout.progress_dialog, callback)
                    call.execute(Dico.DICO_API_URL + edtSearch!!.text)
                }else{
                    Dico.display(Dico.current_defs_html!!, webview)
                }
            })
    }
    private val callback = object : JobDone {
        override fun notify(result: String) = Dico.parseDefsHtml(result, webview)
    }
*/

}
