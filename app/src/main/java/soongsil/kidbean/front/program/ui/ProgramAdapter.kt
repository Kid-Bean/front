package soongsil.kidbean.front.program.ui

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ItemProgramBinding
import soongsil.kidbean.front.databinding.ItemQuizBinding
import soongsil.kidbean.front.program.dto.response.ProgramResponse
import soongsil.kidbean.front.quiz.image.ui.ImageQuizShowActivity

class ProgramAdapter(private val dataList: List<ProgramResponse>) :
    RecyclerView.Adapter<ProgramAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemProgramBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvProgramName.text = dataList[position].programTitle
            binding.tvDepartmentName.text = dataList[position].departmentName
            binding.tvProgramPlace.text = dataList[position].place

            //isStar 가 true -> 노란 별 이미지
            if (dataList[position].isStar) {
                binding.imgStar.setImageResource(R.drawable.star)
            }

            // API로 가져온 이미지 넣기
            val imageView: ImageView = binding.imgDepartment

            Glide.with(this.itemView.context)
                .load(dataList[position].departmentS3Url)
                .into(imageView)

            imageView.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = ItemProgramBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ProgramDetailActivity::class.java)
            intent.putExtra("programId", dataList[position].programId)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dataList.size
}