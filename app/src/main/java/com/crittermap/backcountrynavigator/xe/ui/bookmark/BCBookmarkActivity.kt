package com.crittermap.backcountrynavigator.xe.ui.bookmark

import android.content.Intent
import android.os.Bundle
import com.crittermap.backcountrynavigator.xe.R
import com.crittermap.backcountrynavigator.xe.service.bookmark.BCBookmarkData
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity
import kotlinx.android.synthetic.main.activity_bcbookmark.*

class BCBookmarkActivity : BCBaseActivity(), BCBookmarkActivityFragment.OnBookmarkFragmentListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bcbookmark)
        setSupportActionBar(toolbar)
        configToolbar()
        makeStatusBarNotTransparent()
        showProgress("Loading bookmarks")
    }

    private fun configToolbar() {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setTitle(R.string.bookmar_title)

            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }

    override fun onBookmarkItemClicked(bookmark: BCBookmarkData) {
        val intent = Intent()
        intent.putExtra("bookmark", bookmark)
        setResult(RESULT_OK, intent)
        finish()
    }
}
