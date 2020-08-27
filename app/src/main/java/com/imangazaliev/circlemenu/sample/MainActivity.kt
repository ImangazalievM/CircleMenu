package com.imangazaliev.circlemenu.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        simpleMenu.setOnClickListener {
            openScreen(SimpleMenuActivity::class)
        }
        bottomBarMenu.setOnClickListener {
            openScreen(BottomBarMenuActivity::class)
        }
        fabMenu.setOnClickListener {
            openScreen(FabMenuActivity::class)
        }
    }

    private fun openScreen(activity: KClass<out Activity>) {
        startActivity(Intent(this, activity.java))
    }

}