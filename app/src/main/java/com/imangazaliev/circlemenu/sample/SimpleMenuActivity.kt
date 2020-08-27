package com.imangazaliev.circlemenu.sample

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_simple_menu.*
import kotlinx.android.synthetic.main.activity_simple_menu.snackbarContainer

class SimpleMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_menu)

        val titles = arrayListOf("Favorite", "Search", "Alert", "Place", "Edit")
      //circleMenu.setOnItemClickListener { buttonIndex ->
      //    Snackbar.make(snackbarContainer, titles[buttonIndex], Snackbar.LENGTH_LONG).show()
      //}

      //circleMenu.onMenuOpenAnimationStart {
      //    Log.d("CircleMenuStatus", "onMenuOpenAnimationStart")
      //}

      //circleMenu.onMenuOpenAnimationEnd {
      //    Log.d("CircleMenuStatus", "onMenuOpenAnimationEnd")
      //}

      //circleMenu.onMenuCloseAnimationStart {
      //    Log.d("CircleMenuStatus", "onMenuCloseAnimationStart")
      //}

      //circleMenu.onMenuCloseAnimationEnd {
      //    Log.d("CircleMenuStatus", "onMenuCloseAnimationEnd")
      //}

      //circleMenu.onButtonClickAnimationStart { buttonIndex ->
      //    Log.d("CircleMenuStatus", "onButtonClickAnimationStart: $buttonIndex")
      //}

      //circleMenu.onButtonClickAnimationEnd { buttonIndex ->
      //    Log.d("CircleMenuStatus", "onButtonClickAnimationEnd: $buttonIndex")
      //}
    }

}