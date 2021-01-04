package com.crittermap.backcountrynavigator.xe.ui.help

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.crittermap.backcountrynavigator.xe.R

/**
 * A placeholder fragment containing a simple view.
 */
class BCHelpActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bchelp, container, false)
        try {
            val packageName = activity.packageName
            val pInfo = activity.applicationContext.packageManager.getPackageInfo(packageName, 0)
            view.findViewById<TextView>(R.id.tv_app_version_value).text = pInfo.versionName
        } catch (e: Exception) {

        }

        return view
    }
}
