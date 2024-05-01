package soongsil.kidbean.front.mypage.image.ui

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import soongsil.kidbean.front.databinding.MypageItemQuizBinding
import soongsil.kidbean.front.mypage.image.dto.response.SolvedImageListResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class QuizListAdapter(private val dataList: List<SolvedImageListResponse.SolvedListInfo>) :
    RecyclerView.Adapter<QuizListAdapter.ViewHolder>() {

        private var solvedId : Long = -1L

        inner class ViewHolder(private val binding: MypageItemQuizBinding) :
            RecyclerView.ViewHolder(binding.root) {

            @RequiresApi(Build.VERSION_CODES.O)
            fun bind(position: Int) {
                val originalDateTime = LocalDateTime.parse(dataList[position].solvedTime, DateTimeFormatter.ISO_DATE_TIME)
                binding.tvDate.text = originalDateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))
                binding.tvAnswer.text = dataList[position].answer
                solvedId = dataList[position].solvedId

                binding.btnDetail.setOnClickListener {
                    val intent = Intent(binding.root.context, ImageQuizSolvedDetailActivity::class.java)
                    intent.putExtra("solvedId", dataList[position].solvedId)
                    binding.root.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val viewBinding = MypageItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            return ViewHolder(viewBinding)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: QuizListAdapter.ViewHolder, position: Int) {
            holder.bind(position)
        }

        override fun getItemCount(): Int = dataList.size
}