package com.isaquesoft.despesas.utils

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.isaquesoft.despesas.R
import java.lang.ref.WeakReference

class CustomToast(context: Context, private val text: String) : Toast(context) {
    private val weakContext = WeakReference(context)
    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.toast_custom, null)
        view.findViewById<TextView>(R.id.custom_toast_text).text = text
        setGravity(Gravity.CENTER, 0, 0)
        duration = Toast.LENGTH_LONG
        view.alpha = 0f
        setView(view)
    }

    override fun show() {
        val view = view
        val anim = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
        anim.duration = 500
        anim.start()
        super.show()
    }

    override fun cancel() {
        val view = view
        val anim = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f)
        anim.duration = 1700
        anim.start()
        Handler(Looper.getMainLooper()).postDelayed({
            weakContext.get()?.let { super.cancel() }
        }, anim.duration)
    }
}
