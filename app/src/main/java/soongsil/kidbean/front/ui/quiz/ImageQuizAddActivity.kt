package soongsil.kidbean.front.ui.quiz

import android.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import soongsil.kidbean.front.databinding.ActivityImageQuizAddBinding


class ImageQuizAddActivity : AppCompatActivity() {
    private lateinit var binding : ActivityImageQuizAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityImageQuizAddBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            // 그림 문제 목록 화면으로 이동
            //val intent = Intent(this, ImageQuizListActivity::class.java)
            startActivity(intent)
        }

        // API로 가져온 제목 넣기
        binding.tvTitle.text = "제목"

        // 카테고리 세팅
        categorySetting()

        // API로 가져온 정답 넣기
        binding.tvCorrect.text = "정답"

        // 수정 버튼 눌렀을 때 수정 화면으로 이동
        binding.btnEdit.setOnClickListener {
            // 그림 문제 목록 화면으로 이동
            //val intent = Intent(this, ImageQuizEditActivity::class.java)
            startActivity(intent)
        }

        // 삭제 버튼 눌렀을 때 팝업 띄우기
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("그림 문제 추가 삭제")
                setMessage("작성한 문제를 삭제하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(this@ImageQuizAddActivity, "삭제를 취소하였습니다.", Toast.LENGTH_SHORT).show()
                }
                setPositiveButton("삭제") { _, _ ->
                    Toast.makeText(this@ImageQuizAddActivity, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }.create().show()

            finish()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun categorySetting() {
        // 스피너에 표시될 데이터 생성
        val categories: MutableList<String> = ArrayList()
        categories.add("동물")
        categories.add("식물")
        categories.add("음식")

        // 어댑터 생성 및 데이터 설정
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 스피너에 어댑터 설정
        val spinner = binding.tvCategory
        spinner.adapter = adapter

        // 스피너 아이템 선택 이벤트 처리
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // 선택된 아이템의 텍스트 가져오기
                val selectedCategory = parent.getItemAtPosition(position).toString()
                // 선택된 아이템에 대한 작업 수행 (예: 토스트 메시지 표시)
                Toast.makeText(this@ImageQuizAddActivity, "선택된 카테고리: $selectedCategory", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때 처리할 작업 (필요에 따라 구현)
            }
        }
    }
}