package com.ex.vezdekodpodcasts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ex.vezdehodapp.utils.showFragment
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_list_podcasts.*

class ListPodcastsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_podcasts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val v = layoutInflater.inflate(R.layout.custom_toolbar, null)
        podcasts_list_toolbar.addView(v)
        v.toolbar_title.visibility = View.GONE
        v.toolbar_back_btn.visibility = View.GONE
        v.toolbar_separator.visibility = View.GONE

        create_podcast_btn.setOnClickListener {
            showFragment(CreateNewPodcastFragment(), requireFragmentManager())
        }
    }
}