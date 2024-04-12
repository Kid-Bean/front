package soongsil.kidbean.front.quiz.image.ui

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import soongsil.kidbean.front.databinding.ItemQuizBinding
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizMemberResponse


class ImageQuizAdapter(private val dataList: List<ImageQuizMemberResponse>) :
    RecyclerView.Adapter<ImageQuizAdapter.ViewHolder>() {

    private var quizId : Long = -1L

    inner class ViewHolder(private val binding: ItemQuizBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvQuizTitle.text = dataList[position].title
            quizId = dataList[position].quizId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

        holder.itemView.setOnClickListener(View.OnClickListener { v ->
            var intent = Intent(v.context, ImageQuizShowActivity::class.java)
            intent.putExtra("quizId", quizId)
            Log.d("imagequiz", quizId.toString())
            v.context.startActivity(intent)
        })
    }

    override fun getItemCount(): Int = dataList.size
}