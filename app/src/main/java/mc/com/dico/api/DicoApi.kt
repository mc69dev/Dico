package mc.com.dico.api

import android.app.Dialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import mc.com.tools.Dico
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder


class DicoApi(context : Context, progressbar_layout_id: Int, progressbar_show : Boolean, callback : JobDone)
            : AsyncTask<String, Int, String>() {
    private var dialog: Dialog? = null
    //private var progressbar_show: Boolean = true
    init{
       /* this.progressbar_show=progressbar_show
        if(progressbar_show)
            this.dialog = ProgressDialog.create(context, progressbar_layout_id)*/
    }

    //private val context : Context = activity
    private val callback : JobDone = callback
    override fun onPreExecute() {
       /* if(progressbar_show)
            dialog!!.show()*/
    }
    override fun onPostExecute(result: String) {
        /*if(progressbar_show)
            dialog!!.dismiss()*/

        var results = result.split("|+|")
        //Log.i("tests","onPostExecute : ${Arrays.toString(results.toTypedArray())}")

        val word = results[0]
        var html = if (results.size>1) results[1] else ""

        callback.notify(word, html)
    }

    override fun doInBackground(vararg urls : String?): String {
        val reader:BufferedReader
        var line : String?
        var html = StringBuilder()

        try{
            val word = urls[0].toString()
            val url = URL(Dico.DICO_API_URL+word)

            reader = BufferedReader(InputStreamReader(url.openStream()))
            do {
                line = reader.readLine()
                if (line == null)
                    break
                //Log.i("tests",line)
                html.append(line)
            } while (true)

            reader.close()

            return "$word|+|$html"
        } catch (e: Exception) {
            Log.e("tests" , "Error", e)
            return html.toString()
        }

    }
}