package com.example.audiovolumeview

import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import indi.hubi.viewlab.AudioVolumeView

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        var avv: AudioVolumeView = findViewById(R.id.avv)
        var avv1: AudioVolumeView = findViewById(R.id.avv1)
        var avv2: AudioVolumeView = findViewById(R.id.avv2)
        //没10ms设置一次
        var timer = object: CountDownTimer(60 * 1000, 30) {
            override fun onTick(millisUntilFinished: Long) {
                var v = (0..60).random()
                avv.setVolume(40f + v)
                avv1.setVolume(40f + v)
                avv2.setVolume(40f + v)
            }
            override fun onFinish() {
                avv.pause()
                avv1.pause()
                avv2.pause()
            }
        }
        timer.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
