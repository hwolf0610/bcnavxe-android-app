package com.crittermap.backcountrynavigator.xe.ui.home.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crittermap.backcountrynavigator.xe.R

private const val BOOKMARK_ID = "bookmark_id"

class BCListBookmarkFragment : Fragment() {
    private var bookmarkId: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookmarkId = it.getString(BOOKMARK_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bclist_bookmark, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteractionCreateNewList()

        fun onFragmentInteractionOnPickFoler(folderId: String)
    }

    companion object {
        @JvmStatic
        fun newInstance(bookmarkId: String) =
                BCListBookmarkFragment().apply {
                    arguments = Bundle().apply {
                        putString(BOOKMARK_ID, bookmarkId)
                    }
                }
    }
}
