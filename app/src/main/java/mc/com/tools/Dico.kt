package mc.com.tools

import android.graphics.Color
import android.util.Log
import android.view.View
import android.webkit.WebView
import java.lang.Exception
import java.util.function.Consumer

class Dico {

    companion object {
        const val DICO_API_URL="https://www.dicolink.com/mots/"
        var current_word:String?=""
        var current_defs_html:String?=""
        var current_word_html:String?=""

        fun getDefinition(html: String, save : Boolean): String?{
            val tagName = "div"
            val classNames = "word-module module-definitions"
            val pattern = "(.*) class=\"(.*)$classNames(.*)\"(.*)"

            var definitionTag :String? =""
            try {
                var tagBodyStartIndex: Int = html.indexOf("<body")
                var tagBodyEndIndex: Int = html.indexOf("</body")

                var content: String = html.substring(tagBodyStartIndex, tagBodyEndIndex)

                if(save)
                    current_word_html=content
                definitionTag = Dom.getTag(tagName, pattern, content)
            }catch (e:Exception) {}

            return definitionTag
        }

        fun parseDefsHtml(html: String, webview: WebView) {
            var tag = getDefinition(html,true)
            if (tag.isNullOrEmpty())
                tag = "<h2 style='color:white;'>:(</h2>"

            current_defs_html=tag
            display(tag, webview)
        }
        fun parseRelatedHtml(webview: WebView) {
            if(current_word_html!!.isEmpty())
                return

            val tagName = "div"
            val classNames = "related-group-content"
            val pattern = "(.*) class=\"(.*)$classNames(.*)\"(.*)"

            var tag = Dom.getTag(tagName, pattern, current_word_html!!)
            if (tag.isNullOrEmpty())
                tag = "<h2 style='color:white;'>:(</h2>"
            display(tag, webview)
        }

        fun display(tag: String, webview: WebView) {
            var html = tag.replace("<h3 class=\"source\"", "<h3 class=\"source\" style=\"color:white;\"")
            html = html.replace("<h2>", "<h2 style=\"visibility:hidden;\">")
            html = html.replace("<li>", "<li style=\"color:white;\">")

            webview.setBackgroundColor(Color.TRANSPARENT)
            webview.visibility = View.VISIBLE //TODO : + animation..

            webview.settings.javaScriptEnabled = true
            webview.loadDataWithBaseURL("", html, "text/html", "UTF-8", "")
        }

        fun relatedWords(): List<String> {
            val tagName = "div"
            val classNames = "related-group-content"
            val pattern = "(.*) class=\"(.*)$classNames(.*)\"(.*)"
            var words = mutableListOf<String>()

            if(!current_word_html!!.isEmpty()) {
                var tag = Dom.getTag(tagName, pattern, current_word_html!!)

                if (!tag.isNullOrEmpty()) {
                    var listHtml=Dom.tagContent("ol", tag)
                    var itemsHtml = Dom.getTags("li", "*", listHtml)
                    itemsHtml.forEach(Consumer {
                        var wordTag = Dom.getTag("a", "*", it)
                        var wordText = Dom.innerTag("a",wordTag!!)
                        words.add(wordText)
                    })
                    Log.i("tests","relatedWords => words=$words")
                }
            }
            return words.toList()
        }
    }
}