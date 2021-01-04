package com.crittermap.backcountrynavigator.xe.ui.bookmark

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.crittermap.backcountrynavigator.xe.R
import com.crittermap.backcountrynavigator.xe.service.BCApiService
import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack
import com.crittermap.backcountrynavigator.xe.service.bookmark.BCBookmarkData
import com.crittermap.backcountrynavigator.xe.service.bookmark.BCBookmarkListResponse
import com.crittermap.backcountrynavigator.xe.share.BCUtils
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity
import com.crittermap.backcountrynavigator.xe.ui.bookmark.adapter.BCBookmarkAdapter

/**
 * A placeholder fragment containing a simple view.
 */
class BCBookmarkActivityFragment : Fragment(), BCBookmarkAdapter.OnBookmarkAdapterListener {
    private lateinit var adapter: BCBookmarkAdapter
    private lateinit var rv_bookmark: RecyclerView
    private var listener: OnBookmarkFragmentListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_bcbookmark, container, false)
        val currentUser = BCUtils.getCurrentUser()
        adapter = BCBookmarkAdapter(ArrayList(), context!!, this)
        rv_bookmark = v.findViewById(R.id.rv_bookmark)
        rv_bookmark.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        rv_bookmark.adapter = adapter
        val webServiceCallBack = object : WebServiceCallBack<BCBookmarkListResponse> {
            override fun onSuccess(data: BCBookmarkListResponse) {
                //After save successfully, call fragment
                adapter.arrayData = data.bookmarkInfo
                rv_bookmark.adapter.notifyDataSetChanged()
                val activity = activity as BCBaseActivity
                activity.dismissProgress()
            }

            override fun onFailed(errorMessage: String) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        BCApiService.getInstance().doLoadBookmarkByUser(currentUser.userName, webServiceCallBack)

        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnBookmarkFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnBookmarkFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onBookmarkItemClicked(bookmark: BCBookmarkData) {
        listener?.onBookmarkItemClicked(bookmark)
    }

    interface OnBookmarkFragmentListener {
        fun onBookmarkItemClicked(bookmark: BCBookmarkData)
    }
}
