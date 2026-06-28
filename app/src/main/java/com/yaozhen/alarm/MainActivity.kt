package com.yaozhen.alarm

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

data class AlarmItem(
    val id: String = UUID.randomUUID().toString(),
    var hour: Int,
    var minute: Int,
    var enabled: Boolean = true
)

class MainActivity : AppCompatActivity() {

    private lateinit var alarmAdapter: AlarmAdapter
    private val alarms = mutableListOf<AlarmItem>()
    private val gson = Gson()
    private val PREFS_NAME = "yaozhen_alarms"
    private lateinit var digitalClock: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val clockRunnable = object : Runnable {
        override fun run() {
            updateClock()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        digitalClock = findViewById<TextView>(R.id.tv_digital_clock)
        val recyclerView = findViewById<RecyclerView>(R.id.alarm_list)
        val addButton = findViewById<Button>(R.id.btn_add_alarm)

        loadAlarms()

        alarmAdapter = AlarmAdapter(
            alarms,
            onToggle = { position, enabled ->
                alarms[position].enabled = enabled
                saveAlarms()
            },
            onDelete = { position ->
                alarms.removeAt(position)
                alarmAdapter.notifyItemRemoved(position)
                saveAlarms()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = alarmAdapter

        addButton.setOnClickListener {
            showTimePicker()
        }

        updateClock()
        handler.post(clockRunnable)
    }

    private fun updateClock() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val timeStr = String.format("%02d:%02d:%02d", hour, minute, second)
        digitalClock.text = timeStr
    }

    private fun showTimePicker() {
        val dialog = android.app.Dialog(this)
        dialog.setContentView(R.layout.time_picker_dialog)
        
        val timePicker = dialog.findViewById<TimePicker>(R.id.time_picker)
        val btnConfirm = dialog.findViewById<Button>(R.id.btn_confirm)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)

        timePicker.setIs24HourView(true)

        btnConfirm.setOnClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            
            val alarm = AlarmItem(hour = hour, minute = minute)
            alarms.add(alarm)
            saveAlarms()
            alarmAdapter.notifyItemInserted(alarms.size - 1)
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveAlarms() {
        val json = gson.toJson(alarms)
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putString("alarms", json)
            .apply()
    }

    private fun loadAlarms() {
        val json = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getString("alarms", "[]")
        val type = object : TypeToken<List<AlarmItem>>() {}.type
        val loaded: List<AlarmItem> = gson.fromJson(json, type)
        alarms.clear()
        alarms.addAll(loaded)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(clockRunnable)
    }
}
