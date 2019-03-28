package mc.com.tools

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater

class ProgressDialog {
    companion object {
        fun create(context: Context, layout_id: Int): Dialog{
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(layout_id, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            return dialog
        }
    }
}

