package com.example.digitalkhata.api
import com.example.digitalkhata.util.Constants.BASE_URL
import com.example.digitalkhata.util.Constants.BASE_URL_DIGITAL_KHATA
import com.google.gson.GsonBuilder
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val apiService: ApiService by lazy {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS").create()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_DIGITAL_KHATA)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofit.create(ApiService::class.java)
    }
}
