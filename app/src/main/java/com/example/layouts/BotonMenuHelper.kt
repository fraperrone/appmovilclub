package com.example.layouts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView

object BotonMenuHelper {
    fun configurarBotonMenu(context: Context, rootView: View) {
        val btn = rootView.findViewById<ImageView>(R.id.btnMenuPrincipal)
        btn?.setOnClickListener {
            val intent = Intent(context, MenuPrincipalActivity::class.java)
            context.startActivity(intent)

            if (context is Activity) {
                context.finish()
            }


        }
    }
}
