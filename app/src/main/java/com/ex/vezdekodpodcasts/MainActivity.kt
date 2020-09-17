package com.ex.vezdekodpodcasts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ex.vezdehodapp.utils.showFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showFragment(ListPodcastsFragment(), supportFragmentManager)

    }
}