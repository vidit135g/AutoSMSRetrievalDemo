package com.absolute.authenticator

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroCustomLayoutFragment.Companion.newInstance
import java.lang.Boolean


class SplashActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make sure you don't call setContentView!
        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        addSlide(newInstance(R.layout.intro_custom_layout1))
        addSlide(newInstance(R.layout.intro_custom_layout2))
        addSlide(newInstance(R.layout.intro_custom_layout3))

        showStatusBar(true)
        setStatusBarColorRes(R.color.infoPageBackgroundColor)
        setNavBarColorRes(R.color.infoPageBackgroundColor)
        setColorDoneText(R.color.black)
        setColorSkipButton(R.color.black)
        setDoneText("NEXT")
        setProgressIndicator()

    }

    override fun onResume() {
        super.onResume()
        val sharedpreferences = getSharedPreferences(getString(R.string.copy), Context.MODE_PRIVATE)
        if (!sharedpreferences.getBoolean(prevStarted, false)) {
            val editor = sharedpreferences.edit()
            editor.putBoolean(prevStarted, Boolean.TRUE)
            editor.apply()
        } else {
            finish()
        }
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
                finish ()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}