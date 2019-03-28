package mc.com.tools

import android.util.Log

class Dom {

    companion object Factory {
        fun getTags(tagName: String, pattern: String, html: String): List<String> {
            var tagHeader: String? = null
            var tagContent: String? = null
            var searchStart = 0
            var tagEndIndex: Int
            var found = false

            val tags = ArrayList<String>()

            var tagStartIndex = html.indexOf("<$tagName")
            while (tagStartIndex > 0) {
                tagEndIndex = html.indexOf(">", tagStartIndex + 1)
                tagHeader = html.substring(tagStartIndex, tagEndIndex + 1)

                found = if(pattern=="*") true else tagHeader.matches(pattern.toRegex())
                if (found) {
                    tagContent = "\n" + tagHeader
                    tagContent += "\n" + tagContent(tagName, html.substring(tagEndIndex + 1))
                    tagContent += "\n</$tagName>"
                    //break

                    tags.add(tagContent)
                }
                searchStart = tagEndIndex + 1
                tagStartIndex = html.indexOf("<$tagName", searchStart)
            }
            return tags
        }

        fun getTag(tagName: String, pattern: String, html: String): String? {
            var tagHeader: String? = null
            var tagContent: String? = null
            var searchStart = 0
            var tagEndIndex: Int
            var found = false

            var tagStartIndex = html.indexOf("<$tagName")
            while (tagStartIndex > 0) {
                tagEndIndex = html.indexOf(">", tagStartIndex + 1)
                tagHeader = html.substring(tagStartIndex, tagEndIndex + 1)

                found = if(pattern=="*") true else tagHeader.matches(pattern.toRegex())
                if (found) {
                    tagContent = "\n" + tagHeader
                    tagContent += "\n" + tagContent(tagName, html.substring(tagEndIndex + 1))
                    tagContent += "\n</$tagName>"
                    break
                }
                searchStart = tagEndIndex + 1
                tagStartIndex = html.indexOf("<$tagName", searchStart)
            }

            return tagContent
        }

        fun tagContent(tagName: String, html: String): String {
            var opened = 1
            var closed = 0

            var begin = 0
            var end = 1
            while (closed < opened) {
                end = html.indexOf("</$tagName>", end)

                if (end > 0) {
                    closed++
                    end++
                    //Log.i("tests", end.toString()+" ==> "+closed)
                    if (closed == opened)
                        break
                    begin = html.substring(begin, end).indexOf("<$tagName")
                    if (begin > 0) {
                        opened++
                        begin++
                        //Log.i("tests", begin.toString()+" ==> "+opened)
                    }
                }
            }
            return html.substring(0, end - 1)
        }
        fun innerTag(tagName: String, tag: String): String {
            var begin =  tag.indexOf(">")
            var end =  tag.indexOf("</$tagName>")
            return tag.substring(begin+1,end).trim()
        }
    }
}