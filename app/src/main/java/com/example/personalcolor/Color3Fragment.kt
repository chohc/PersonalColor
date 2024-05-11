package com.example.personalcolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment

class Color3Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 레이아웃 인플레이션
        val view = inflater.inflate(R.layout.fragment_color3, container, false)
        // GridView 찾기
        val gridView: GridView = view.findViewById(R.id.color3GridView)
        // 이미지 리소스 설정
        val imageResources = intArrayOf(R.drawable.test_person, R.drawable.test_person, R.drawable.test_person, R.drawable.test_person)

        // 어댑터 설정
        gridView.adapter = ImageAdapter(requireContext(), imageResources)
        // 아이템 클릭 리스너 설정
        gridView.setOnItemClickListener { _, _, _, _ ->
            val test2Activity = activity as? Test2Activity
            test2Activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, Color4Fragment())?.commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 프로그레스 바 값을 3로 설정
        (activity as? Test2Activity)?.updateProgressBar(3)
    }
}