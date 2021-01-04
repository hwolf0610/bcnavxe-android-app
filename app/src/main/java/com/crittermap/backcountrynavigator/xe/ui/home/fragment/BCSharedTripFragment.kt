package com.crittermap.backcountrynavigator.xe.ui.home.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.crittermap.backcountrynavigator.xe.R

class BCSharedTripFragment : AppCompatDialogFragment() {
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bcshared_trip, container, false)
        view.findViewById<Button>(R.id.btn_discard).setOnClickListener {
            mListener?.onDiscardSharedTrip()
        }
        view.findViewById<Button>(R.id.btn_save).setOnClickListener {
            mListener?.onSaveSharedTrip()
        }
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnWaypointSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun onDiscardSharedTrip()
        fun onSaveSharedTrip()
    }

    companion object {
        const val TAG = "BCSharedTripFragment"
        fun newInstance(): BCSharedTripFragment {
            return BCSharedTripFragment()
        }
    }
}
