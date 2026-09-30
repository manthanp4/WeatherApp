package com.example.weatherapp;

import com.example.weatherapp.BuildConfig;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView tvTitle, tvStatus, tvCity, tvTemperature, tvCondition, tvHumidity;
    EditText edtCity;
    ProgressBar prgrsBar;
    Button btnShowWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTitle = findViewById(R.id.tvTitle);
        tvStatus = findViewById(R.id.tvStatus);

        tvCity = findViewById(R.id.tvCity);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvCondition = findViewById(R.id.tvCondition);
        tvHumidity = findViewById(R.id.tvHumidity);
        edtCity = findViewById(R.id.etCity);
        prgrsBar = findViewById(R.id.progressBar);
        btnShowWeather = findViewById(R.id.btnShowWeather);

        btnShowWeather.setOnClickListener(v -> {
            String cityName = edtCity.getText().toString().trim();
            if (cityName.isEmpty()) {
                edtCity.setError(getString(R.string.please_enter_city));
                return;
            }

            // Clear Previous values
            tvCity.setText("");
            tvTemperature.setText("");
            tvCondition.setText("");
            tvHumidity.setText("");
            tvStatus.setText(getString(R.string.requesting));
            prgrsBar.setVisibility(View.VISIBLE);

            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            Call<WeatherResponse> call =
                    apiService.getWeather(BuildConfig.WEATHER_API_KEY, cityName, "no");
            call.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    prgrsBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        WeatherResponse weatherResponse = response.body();

                        tvCity.setText(getString(R.string.city) + weatherResponse.getLocation().getName());
                        tvTemperature.setText(getString(R.string.temperature_value) + weatherResponse.getCurrent().getTemp_c() + "°C");
                        tvCondition.setText(getString(R.string.weather_condition) + weatherResponse.getCurrent().getCondition().getText());
                        tvHumidity.setText(getString(R.string.humidity) + weatherResponse.getCurrent().getHumidity() + "%");
                        tvStatus.setText(getString(R.string.success));

                        Toast.makeText(MainActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                    } else {
                        tvStatus.setText(getString(R.string.error) + " " + response.code());
                        Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    prgrsBar.setVisibility(View.GONE);
                    tvCity.setText("");
                    tvTemperature.setText("");
                    tvCondition.setText("");
                    tvHumidity.setText("");
                    tvStatus.setText(getString(R.string.error) + " " + t.getMessage());
                    Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
}