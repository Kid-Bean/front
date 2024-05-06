package soongsil.kidbean.front.mypage.word.ui

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import soongsil.kidbean.front.databinding.MypageItemWordAndAnswerQuizBinding
import soongsil.kidbean.front.mypage.word.dto.response.SolvedWordQuizListResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SolvedWordQuizListAdapter(private val dataList: List<SolvedWordQuizListResponse.SolvedWordQuizInfo>) :
    RecyclerView.Adapter<SolvedWordQuizListAdapter.ViewHolder>() {

        private var solvedId : Long = -1L

        inner class ViewHolder(private val binding: MypageItemWordAndAnswerQuizBinding) :
            RecyclerView.ViewHolder(binding.root) {

            @RequiresApi(Build.VERSION_CODES.O)
            fun bind(position: Int) {
                val originalDateTime = LocalDateTime.parse(dataList[position].solvedTime, DateTimeFormatter.ISO_DATE_TIME)
                binding.tvDate.text = originalDateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))
                binding.tvTitle.text = dataList[position].title
                solvedId = dataList[position].solvedId

                binding.tvTitle.isSelected = true

                binding.btnDetail.setOnClickListener {
                    val intent = Intent(binding.root.context, SolvedWordQuizDetailActivity::class.java)
                    intent.putExtra("solvedId", dataList[position].solvedId)
                    binding.root.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val viewBinding = MypageItemWordAndAnswerQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            return ViewHolder(viewBinding)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: SolvedWordQuizListAdapter.ViewHolder, position: Int) {
            holder.bind(position)
        }

        override fun getItemCount(): Int = dataList.size
}