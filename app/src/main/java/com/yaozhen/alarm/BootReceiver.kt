package com.yaozhen.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // 设备重启后，重新加载闹钟设置
            // 这里可以添加重新设置AlarmManager的代码
        }
    }
}
