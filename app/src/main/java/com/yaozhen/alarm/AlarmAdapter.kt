package com.yaozhen.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class AlarmAdapter(
    private val alarms: List<AlarmItem>,
    private val onToggle: (Int, Boolean) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.findViewById(R.id.tv_time)
        val switch: Switch = view.findViewById(R.id.switch_alarm)
        val deleteButton: MaterialButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.alarm_item, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        val timeStr = String.format("%02d:%02d", alarm.hour, alarm.minute)
        holder.timeText.text = timeStr
        holder.switch.isChecked = alarm.enabled

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            onToggle(position, isChecked)
        }

        holder.deleteButton.setOnClickListener {
            onDelete(position)
        }
    }

    override fun getItemCount() = alarms.size
}
