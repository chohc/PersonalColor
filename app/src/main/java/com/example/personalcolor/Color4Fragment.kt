package com.example.personalcolor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment

class Color4Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_color4, container, false)
        val gridView: GridView = view.findViewById(R.id.color4GridView)
        val imageResources = intArrayOf(R.drawable.test_person, R.drawable.test_person, R.drawable.test_person, R.drawable.test_person)

        gridView.adapter = ImageAdapter(requireContext(), imageResources)
        gridView.setOnItemClickListener { _, _, _, _ ->
            startActivity(Intent(activity, ResultActivity::class.java))  // MainActivity로 이동
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 프로그레스 바 값을 4로 설정
        (activity as? Test2Activity)?.updateProgressBar(4)
    }
}