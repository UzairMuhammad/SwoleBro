package com.swolebro.model

import androidx.lifecycle.lifecycleScope
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import org.json.JSONObject

@OptIn(DelicateCoroutinesApi::class)
fun BodyMassIndexCalc(height: String, weight: String, onResult: (String) -> Unit){
    GlobalScope.launch(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://body-mass-index-bmi-calculator.p.rapidapi.com/metric?weight=$weight&height=$height")
                .get()
                .addHeader("X-RapidAPI-Key", "19c0be0c36msh46196512dc17076p182118jsn278f6573deb9")
                .addHeader("X-RapidAPI-Host", "body-mass-index-bmi-calculator.p.rapidapi.com")
                .build()

            val response = client.newCall(request).execute()
            val jsonObject = response.body()?.string()?.let { JSONObject(it) }
            val bmi = jsonObject?.getDouble("bmi")?.roundToTwoDecimalPlaces().toString()
            withContext(Dispatchers.Main){
                onResult(bmi)
            }
        }
        catch (e: Exception){
            Log.e("BMI", "Error calculating BMI", e)
            withContext(Dispatchers.Main){
                onResult("")
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
suspend fun BodyMassIndexCategory(bmi: String): String = withContext(Dispatchers.IO){
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://body-mass-index-bmi-calculator.p.rapidapi.com/weight-category?bmi=$bmi")
                .get()
                .addHeader("X-RapidAPI-Key", "19c0be0c36msh46196512dc17076p182118jsn278f6573deb9")
                .addHeader("X-RapidAPI-Host", "body-mass-index-bmi-calculator.p.rapidapi.com")
                .build()

            val response = client.newCall(request).execute()
            val jsonObject = response.body()?.string()?.let { JSONObject(it) }
            val category = jsonObject?.getString("weightCategory").toString()
            Log.d("BMI", "category: $category")
            category
        }
        catch (e: Exception){
            Log.e("BMI", "Error calculating BMI", e)
            ""
        }
}

fun Double.roundToTwoDecimalPlaces() = "%.2f".format(this).toDouble()
