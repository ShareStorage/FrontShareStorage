package com.example.frontsharestorage.DTO

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.frontsharestorage.R
import com.example.frontsharestorage.User.ResponseDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VolunteerAdapter(
    private val context: Context?,
    private val volunteerList: MutableList<VolunteerData>
) : RecyclerView.Adapter<VolunteerAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null
    private var alertDialog: AlertDialog? = null
    private var retrofit = RetrofitManager.instance
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.item_volunteerTitle)
        val locationTextView: TextView = itemView.findViewById(R.id.item_volunterLocation)
        val dateTextView: TextView = itemView.findViewById(R.id.item_volunteerDate)
        val timeTextView: TextView = itemView.findViewById(R.id.item_volunteerTime)
        val deleteItemButton: ImageView = itemView.findViewById(R.id.delete_item)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_record_volunteer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = volunteerList[position]

        holder.titleTextView.text = data.title
        holder.locationTextView.text = data.location
        holder.dateTextView.text = data.date
        holder.timeTextView.text = data.startTime + "~" + data.endTime

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position)

            Log.d("clickID", data.recordID.toString())
            Log.d("clickTitle", data.title)
            Log.d("clickLocation", data.location)
            Log.d("clickDetail", data.detail)
            Log.d("clickDate", data.date)
            Log.d("clickTime", data.startTime + " ~ " + data.endTime)
            Log.d("clickApprove", data.approve.toString())

            showAlertDialog(data.title, data.location, data.detail, data.date, data.startTime, data.endTime)
        }

        // 삭제 버튼 클릭 리스너 추가
        holder.deleteItemButton.setOnClickListener {
            //removeItem(position)
            val recordID = volunteerList[position].recordID

            // API 서버에서 삭제
            val sendDeleteRecord = retrofit.apiService.deleteRecord(recordID)
            sendDeleteRecord.enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    val responseDto = response.body()

                    if (responseDto != null) {
                        if (responseDto.response) {
                            // 성공적으로 서버에서 삭제될 경우에만 RecyclerView에서도 삭제
                            Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show()
                            volunteerList.removeAt(position)
                            notifyItemRemoved(position)

                        } else {
                            Toast.makeText(context, "삭제 불가능!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("API TEST", "ERROR  = ${t.message}")
                    Toast.makeText(context, "삭제 불가능 네트워크 에러!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showAlertDialog(clickTitle : String, clickLocation : String, clickDetail : String, clickDate : String, clickStartTime : String, clickEndTime : String) {

        val builder = AlertDialog.Builder(context ?: return)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.click_volunteer_recycler, null)

        val volunteerDateTextViewAlert = dialogView.findViewById<TextView>(R.id.volunteerDateTextView)
        val volunteerStartTimeText = dialogView.findViewById<TextView>(R.id.volunteerStartTimeText)
        val volunteerEndTimeText = dialogView.findViewById<TextView>(R.id.volunteerEndTimeText)

        val volunteerTitle = dialogView.findViewById<EditText>(R.id.volunteerTitleEditText)
        val volunteerLocation = dialogView.findViewById<EditText>(R.id.volunteerLocationEditText)
        val volunteerDetail = dialogView.findViewById<EditText>(R.id.volunteerDetailEditText)
        val volunteerCloseButton = dialogView.findViewById<View>(R.id.closeButtonView)

        volunteerTitle.setText(clickTitle)
        volunteerLocation.setText(clickLocation)
        volunteerDetail.setText(clickDetail)
        volunteerDateTextViewAlert.text = clickDate
        volunteerStartTimeText.text = clickStartTime
        volunteerEndTimeText.text = clickEndTime

        volunteerCloseButton.setOnClickListener {
            alertDialog?.dismiss()
        }

        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.show()

    }
    override fun getItemCount(): Int {
        return volunteerList.size
    }

    fun removeItem(position: Int) {
        volunteerList.removeAt(position)
        notifyItemRemoved(position)
    }
}