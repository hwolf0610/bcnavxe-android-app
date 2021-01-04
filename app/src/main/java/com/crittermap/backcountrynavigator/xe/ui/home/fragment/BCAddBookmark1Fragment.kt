package com.crittermap.backcountrynavigator.xe.ui.home.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crittermap.backcountrynavigator.xe.R

private const val BOOKMARK_ID = "bookmark_id"

class BCAddBookmark1Fragment : Fragment() {

    private var bookmarkId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookmarkId = it.getString(BOOKMARK_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_bookmark1, container, false)
    }

    companion object {
        const val TAG = "com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCAddBookmark1Fragment"

        @JvmStatic
        fun newInstance(bookmarkId: String) =
                BCAddBookmark1Fragment().apply {
                    arguments = Bundle().apply {
                        putString(BOOKMARK_ID, bookmarkId)
                    }
                }
    }
}
