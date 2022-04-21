package com.gouhar.ad340

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gouhar.ad340.Details.ForecastDetailsActivity

class MainActivity : AppCompatActivity() {

    private val forecastRepository = ForecastRepository()
    private lateinit var tempDisplaySettingManager: TempDisplaySettingManager
// Region Setup Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    tempDisplaySettingManager = TempDisplaySettingManager(this)


    val zipcodeEditText: EditText = findViewById(R.id.zipcodeEditText)
    val enterButton: Button = findViewById(R.id.enterButton)

    enterButton.setOnClickListener {
        val zipcode: String = zipcodeEditText.text.toString()

        if(zipcode.length !=5) {
            Toast.makeText(this,R.string.zipcode_entry_error, Toast.LENGTH_SHORT).show()
        }else{
            forecastRepository.loadForecast()
        }
    }

    val forecastList: RecyclerView = findViewById(R.id.forecastList)
    forecastList.layoutManager = LinearLayoutManager(this)
    val dailyForecastAdapter = DailyForecastAdapter(tempDisplaySettingManager) { forecast ->
        showForecastDetails(forecast)
    }
    forecastList.adapter = dailyForecastAdapter

    val weeklyForecastObserver = Observer<List<DailyForecast>> { forecastItems ->
        // Update out list adapter
        dailyForecastAdapter.submitList(forecastItems)
    }
    forecastRepository.weeklyForecast.observe(this, weeklyForecastObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.setting_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId){
            R.id.displaySettingUnits -> {
                showTempDisplaySettingDialog(this, tempDisplaySettingManager)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



     private fun showForecastDetails(forecast: DailyForecast) {
         val forecastDetailsIntent = Intent(this, ForecastDetailsActivity::class.java)
         forecastDetailsIntent.putExtra("key_temp",forecast.temp)
         forecastDetailsIntent.putExtra("key_description",forecast.description)
         startActivity(forecastDetailsIntent)
     }
}
