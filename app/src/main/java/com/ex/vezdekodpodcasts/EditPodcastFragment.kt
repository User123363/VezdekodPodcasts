package com.ex.vezdekodpodcasts

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_create_new_podcast.*
import kotlinx.android.synthetic.main.fragment_edit_podcast.*
import kotlinx.android.synthetic.main.time_code_item.view.*

class EditPodcastFragment : Fragment() {

    lateinit var pref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_podcast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireContext().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)

        val v = layoutInflater.inflate(R.layout.custom_toolbar, null)
        edit_podcast_toolbar.addView(v)
        v.toolbar_title.text = "Редактирование"
        v.toolbar_back_btn.setOnClickListener {
            fragmentManager!!.popBackStack()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                fragmentManager!!.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        imageView4.setOnClickListener {
            Glide.with(this).load(R.drawable.ic_mp3_choosen).into(imageView4)
        }

        var countTimeCodes = 0

        add_tome_code_container.setOnClickListener {

            val viewTimeCode =
                LayoutInflater.from(context).inflate(R.layout.time_code_item, null)

            val paramsForItem = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            viewTimeCode.layoutParams = paramsForItem

            viewTimeCode.tag = countTimeCodes.toString()
            countTimeCodes++
            viewTimeCode.delete_time_code.setOnClickListener {
                container_for_time_codes.removeView(viewTimeCode)
            }

            container_for_time_codes.addView(viewTimeCode)

        }

        continue_btn.setOnClickListener {

        }
    }
}