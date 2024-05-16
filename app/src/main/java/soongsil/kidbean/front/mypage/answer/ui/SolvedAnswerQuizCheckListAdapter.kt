package soongsil.kidbean.front.mypage.answer.ui

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import soongsil.kidbean.front.databinding.MypageItemCheckListBinding
import soongsil.kidbean.front.mypage.answer.dto.response.SolvedAnswerQuizDetailResponse

class SolvedAnswerQuizCheckListAdapter(private val dataList: List<SolvedAnswerQuizDetailResponse.MorphemeCheckListResponse.MorphemeCheckListInfo>) :
    RecyclerView.Adapter<SolvedAnswerQuizCheckListAdapter.ViewHolder>() {

        private var solvedId : Long = -1L

        inner class ViewHolder(private val binding: MypageItemCheckListBinding) :
            RecyclerView.ViewHolder(binding.root) {

            @RequiresApi(Build.VERSION_CODES.O)
            fun bind(position: Int) {
                binding.checkbox.isChecked = dataList[position].isUsed
                binding.ageGroup.text = dataList[position].ageGroup
                binding.checkContent.text = dataList[position].checkInfoContent
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val viewBinding = MypageItemCheckListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            return ViewHolder(viewBinding)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: SolvedAnswerQuizCheckListAdapter.ViewHolder, position: Int) {
            holder.bind(position)
        }

        override fun getItemCount(): Int = dataList.size
}