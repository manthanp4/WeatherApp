package com.example.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("current.json")
    Call<WeatherResponse> getWeather(

            @Query("key") String apiKey,

            @Query("q") String city,

            @Query("aqi") String aqi

    );

}