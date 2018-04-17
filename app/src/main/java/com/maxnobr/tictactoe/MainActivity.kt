package com.maxnobr.tictactoe

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ToggleButton

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {

    var matrix = Array(3,{IntArray(3,{0})})
    var player1 = true
    private var mSensorManager : SensorManager ?= null
    private var mAccelerometer : Sensor ?= null
    var shakes = 0
    var maxShakes = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        fab.setOnClickListener {
            resetTable()
        }
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // focus in accelerometer
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
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

    fun tableClick(view: View)
    {
        (view as ToggleButton).text = if(player1) "X" else "O"
        view.isClickable = false
        val value = if(player1) 1 else -1
        when(view.id)
        {
            R.id.btn_1_1-> matrix[0][0] = value
            R.id.btn_1_2-> matrix[0][1] = value
            R.id.btn_1_3-> matrix[0][2] = value
            R.id.btn_2_1-> matrix[1][0] = value
            R.id.btn_2_2-> matrix[1][1] = value
            R.id.btn_2_3-> matrix[1][2] = value
            R.id.btn_3_1-> matrix[2][0] = value
            R.id.btn_3_2-> matrix[2][1] = value
            R.id.btn_3_3-> matrix[2][2] = value
        }

        calculateWin()
        player1 = !player1
    }

    private fun calculateWin()
    {
        var str = " "
        matrix.iterator().forEach {  str += "\n";it.iterator().forEach { str += " "+it.toString() } }
        Log.i("tag",str)
        var result = 0
        var done = false
        var x = 0
        var y = 0

        while(!done && x < 3) {
            result = 0
            for (y in 0..2) {
                result += matrix[x][y]
                if (abs(result) > 2) done = true
            }
            x++
        }
        x = 0

        while(!done && y < 3) {
            result = 0
            for (x in 0..2) {
                result += matrix[x][y]
                if (abs(result) > 2) done = true
            }
            y++
        }
        y = 0

        if(!done) {
            result = 0
            while (y < 3) {
                result += matrix[y][y]
                if (abs(result) > 2) done = true
                y++
            }
        }
        y = 0

        if(!done) {
            result = 0
            while (y < 3) {
                result += matrix[2 - y][y]
                if (abs(result) > 2) done = true
                y++
            }
        }

        if(done) {
            player1 = true
            Table.visibility = View.GONE
            textView.text = if(result > 0)"Player X WON" else "Player O WON"
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && (event.values[0]+event.values[1]+event.values[2]) > 40) {
            shakes++
            (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(100)

            if (shakes > maxShakes)
            {
                resetTable()
                shakes = 0
            }
        }
    }

    private fun resetTable() {
        Table.visibility = View.VISIBLE

        for (x in 0..2)
            for (y in 0..2)
                matrix[x][y] = 0

        btn_1_1.isClickable = true
        btn_1_2.isClickable = true
        btn_1_3.isClickable = true
        btn_2_1.isClickable = true
        btn_2_2.isClickable = true
        btn_2_3.isClickable = true
        btn_3_1.isClickable = true
        btn_3_2.isClickable = true
        btn_3_3.isClickable = true

        btn_1_1.text = " "
        btn_1_2.text = " "
        btn_1_3.text = " "
        btn_2_1.text = " "
        btn_2_2.text = " "
        btn_2_3.text = " "
        btn_3_1.text = " "
        btn_3_2.text = " "
        btn_3_3.text = " "

        player1 = true
        textView.text = " "
    }

    override fun onResume() {
        super.onResume()
        mSensorManager!!.registerListener(this,mAccelerometer,
                SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager!!.unregisterListener(this)
    }
}
