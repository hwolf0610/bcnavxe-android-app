package com.crittermap.backcountrynavigator.xe.ui.help

import android.os.Bundle
import com.crittermap.backcountrynavigator.xe.R
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity
import kotlinx.android.synthetic.main.activity_bchelp.*

class BCHelpActivity : BCBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bchelp)
        setSupportActionBar(toolbar)
        configToolbar()
        makeStatusBarNotTransparent()
    }

    private fun configToolbar() {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setTitle(R.string.help_title)

            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }
}
