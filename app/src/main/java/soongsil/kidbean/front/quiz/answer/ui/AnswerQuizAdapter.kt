package soongsil.kidbean.front.quiz.answer.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import soongsil.kidbean.front.databinding.ItemQuizBinding
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizMemberResponse
import soongsil.kidbean.front.quiz.word.ui.WordQuizAdapter
import soongsil.kidbean.front.quiz.word.ui.WordQuizShowActivity

class AnswerQuizAdapter(private val dataList: List<AnswerQuizMemberResponse>) :
    RecyclerView.Adapter<AnswerQuizAdapter.ViewHolder>() {

        private var quizId : Long = -1L

        inner class ViewHolder(private val binding: ItemQuizBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(position: Int) {
                binding.tvQuizTitle.text = dataList[position].title
                quizId = dataList[position].quizId
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val viewBinding = ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            return ViewHolder(viewBinding)
        }

        override fun onBindViewHolder(holder: AnswerQuizAdapter.ViewHolder, position: Int) {
            val currentQuiz = dataList[position]
            holder.bind(position)

            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, AnswerQuizShowActivity::class.java)
                intent.putExtra("quizId", currentQuiz.quizId)
                it.context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int = dataList.size
}