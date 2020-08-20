package com.imangazaliev.circlemenu.sample

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        val titles = arrayListOf("Favorite", "Search", "Alert", "Place", "Edit")
        circleMenu.setOnItemClickListener { buttonIndex ->
            Snackbar.make(snackbarContainer, titles[buttonIndex], Snackbar.LENGTH_LONG).show()
        }

        circleMenu.onMenuOpenAnimationStart {
            Log.d("CircleMenuStatus", "onMenuOpenAnimationStart")
        }

        circleMenu.onMenuOpenAnimationEnd() {
            Log.d("CircleMenuStatus", "onMenuOpenAnimationEnd")
        }

        circleMenu.onMenuCloseAnimationStart() {
            Log.d("CircleMenuStatus", "onMenuCloseAnimationStart")
        }

        circleMenu.onMenuCloseAnimationEnd() {
            Log.d("CircleMenuStatus", "onMenuCloseAnimationEnd")
        }

        circleMenu.onButtonClickAnimationStart {
            Log.d("CircleMenuStatus", "onButtonClickAnimationStart")
        }

        circleMenu.onButtonClickAnimationEnd {
            Log.d("CircleMenuStatus", "onButtonClickAnimationEnd")
        }
    }

}