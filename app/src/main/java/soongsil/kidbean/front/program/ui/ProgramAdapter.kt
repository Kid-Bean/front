package soongsil.kidbean.front.program.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ItemProgramBinding
import soongsil.kidbean.front.databinding.ItemQuizBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.program.dto.response.ProgramResponse
import soongsil.kidbean.front.program.dto.response.ProgramResponseList
import soongsil.kidbean.front.program.dto.response.StarResponse
import soongsil.kidbean.front.program.presentation.ProgramController
import soongsil.kidbean.front.quiz.image.ui.ImageQuizShowActivity
import soongsil.kidbean.front.util.ApiClient

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

            ApiClient.init(this.itemView.context)

            imageView.visibility = View.VISIBLE

            binding.imgStar.setOnClickListener {
                val programController = ApiClient.getApiClient().create(ProgramController::class.java)
                programController.postStar(dataList[position].programId).enqueue(object :
                    Callback<ResponseTemplate<StarResponse>> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(
                        call: Call<ResponseTemplate<StarResponse>>,
                        response: Response<ResponseTemplate<StarResponse>>,
                    ) {
                        if (response.isSuccessful) {
                            // 정상적으로 통신이 성공된 경우
                            Log.d("post", "onResponse 성공: " + response.body().toString())

                            val status = response.body()?.results?.starStatus

                            if (status == "save") {
                                binding.imgStar.setImageResource(R.drawable.star)
                            } else if (status == "delete") {
                                binding.imgStar.setImageResource(R.drawable.empty_star)
                            }
                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("post", "onResponse 실패 + ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<ResponseTemplate<StarResponse>>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("post", "onFailure 에러: " + t.message.toString())
                    }
                })
            }
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