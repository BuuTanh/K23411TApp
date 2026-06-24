package com.tranlebuutanh.k23411tapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;

public class WeatherActivity extends AppCompatActivity {

    Spinner spinnerProvince;
    TextView txtLocation, txtDate, txtTemperature, txtStatus;
    TextView txtHighLow, txtHumidity, txtWind, txtSunrise, txtSunset;
    TextView txtUV, txtFeelsLike, txtLoading;

    LinkedHashMap<String, String> provinceMap = new LinkedHashMap<>();
    String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        initProvinces();
        setupSpinner();
    }

    private void addViews() {
        spinnerProvince = findViewById(R.id.spinnerProvince);
        txtLocation     = findViewById(R.id.txtLocation);
        txtDate         = findViewById(R.id.txtDate);
        txtTemperature  = findViewById(R.id.txtTemperature);
        txtStatus       = findViewById(R.id.txtStatus);
        txtHighLow      = findViewById(R.id.txtHighLow);
        txtHumidity     = findViewById(R.id.txtHumidity);
        txtWind         = findViewById(R.id.txtWind);
        txtSunrise      = findViewById(R.id.txtSunrise);
        txtSunset       = findViewById(R.id.txtSunset);
        txtUV           = findViewById(R.id.txtUV);
        txtFeelsLike    = findViewById(R.id.txtFeelsLike);
        txtLoading      = findViewById(R.id.txtLoading);
    }

    private void initProvinces() {
        // Danh sách tỉnh thành lấy từ API thanhnien.vn/ajax-get-item-weather.htm
        provinceMap.put("An Giang",          "2347719");
        provinceMap.put("Bình Dương",        "20070078");
        provinceMap.put("Bình Phước",        "20070086");
        provinceMap.put("Bình Thuận",        "2347731");
        provinceMap.put("Bình Định",         "2347730");
        provinceMap.put("Bạc Liêu",          "20070081");
        provinceMap.put("Bắc Giang",         "20070087");
        provinceMap.put("Bắc Kạn",           "20070084");
        provinceMap.put("Bắc Ninh",          "20070088");
        provinceMap.put("Bến Tre",           "2347703");
        provinceMap.put("Cao Bằng",          "2347704");
        provinceMap.put("Cà Mau",            "20070082");
        provinceMap.put("Cần Thơ",           "2347732");
        provinceMap.put("Điện Biên",         "28301718");
        provinceMap.put("Đà Nẵng",           "20070085");
        provinceMap.put("Đà Lạt",            "1252375");
        provinceMap.put("Đắk Lắk",           "2347720");
        provinceMap.put("Đắk Nông",          "28301719");
        provinceMap.put("Đồng Nai",          "2347721");
        provinceMap.put("Đồng Tháp",         "2347722");
        provinceMap.put("Gia Lai",           "2347733");
        provinceMap.put("Hà Nội",            "2347727");
        provinceMap.put("Hồ Chí Minh",       "2347728");
        provinceMap.put("Hà Giang",          "2347734");
        provinceMap.put("Hà Nam",            "2347741");
        provinceMap.put("Hà Tĩnh",           "2347736");
        provinceMap.put("Hòa Bình",          "2347737");
        provinceMap.put("Hưng Yên",          "20070079");
        provinceMap.put("Hải Dương",         "20070080");
        provinceMap.put("Hải Phòng",         "2347707");
        provinceMap.put("Hậu Giang",         "28301720");
        provinceMap.put("Khánh Hòa",         "2347738");
        provinceMap.put("Kiên Giang",        "2347723");
        provinceMap.put("Kon Tum",           "20070076");
        provinceMap.put("Lai Châu",          "2347708");
        provinceMap.put("Long An",           "2347710");
        provinceMap.put("Lào Cai",           "2347740");
        provinceMap.put("Lâm Đồng",          "2347709");
        provinceMap.put("Lạng Sơn",          "2347718");
        provinceMap.put("Nam Định",          "20070089");
        provinceMap.put("Nghệ An",           "2347742");
        provinceMap.put("Ninh Bình",         "2347743");
        provinceMap.put("Ninh Thuận",        "2347744");
        provinceMap.put("Phú Thọ",           "20070091");
        provinceMap.put("Phú Yên",           "2347745");
        provinceMap.put("Quảng Bình",        "2347746");
        provinceMap.put("Quảng Nam",         "2347711");
        provinceMap.put("Quảng Ngãi",        "20070077");
        provinceMap.put("Quảng Ninh",        "2347712");
        provinceMap.put("Quảng Trị",         "2347747");
        provinceMap.put("Sóc Trăng",         "2347748");
        provinceMap.put("Sơn La",            "2347713");
        provinceMap.put("Thanh Hóa",         "2347715");
        provinceMap.put("Thái Bình",         "2347716");
        provinceMap.put("Thái Nguyên",       "20070083");
        provinceMap.put("Thừa Thiên Huế",    "2347749");
        provinceMap.put("Tiền Giang",        "2347717");
        provinceMap.put("Trà Vinh",          "2347750");
        provinceMap.put("Tuyên Quang",       "2347751");
        provinceMap.put("Tây Ninh",          "2347714");
        provinceMap.put("Vĩnh Long",         "2347752");
        provinceMap.put("Vĩnh Phúc",         "20070090");
        provinceMap.put("Vũng Tàu",          "2347729");
        provinceMap.put("Yên Bái",           "2347753");
    }

    private void setupSpinner() {
        names = provinceMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(adapter);

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = provinceMap.get(names[position]);
                fetchWeather(selectedValue);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchWeather(String provinceId) {
        txtLoading.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                URL url = new URL("https://eth2.cnnd.vn/ajax/weatherinfo/" + provinceId + ".htm");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                JSONObject root     = new JSONObject(sb.toString());
                JSONObject dataInfo = root.getJSONObject("Data")
                                         .getJSONObject("data")
                                         .getJSONObject("datainfo");

                String location    = dataInfo.getString("location");
                String currentDate = dataInfo.getString("currentDate");
                int temperature    = dataInfo.getInt("temperature");
                int high           = dataInfo.getInt("high");
                int low            = dataInfo.getInt("low");
                String status      = dataInfo.getString("status");
                String humidity    = dataInfo.getString("humidity");
                String sunrise     = dataInfo.getString("sunrise");
                String sunset      = dataInfo.getString("sunset");
                int feelsLike      = dataInfo.getInt("feels_like");
                String windIndex   = dataInfo.getJSONObject("wind").getString("index");
                String windUnit    = dataInfo.getJSONObject("wind").getString("unit");
                String uvIndex     = dataInfo.getJSONObject("UV_index").getString("index");
                String uvStatus    = dataInfo.getJSONObject("UV_index").getString("status");

                runOnUiThread(() -> {
                    txtLoading.setVisibility(View.GONE);
                    txtLocation.setText("📍 " + location);
                    txtDate.setText("📅 " + currentDate);
                    txtTemperature.setText(temperature + "°C");
                    txtStatus.setText(status);
                    txtHighLow.setText("Cao: " + high + "° | Thấp: " + low + "°");
                    txtHumidity.setText(humidity);
                    txtWind.setText(windIndex + " " + windUnit);
                    txtSunrise.setText(sunrise);
                    txtSunset.setText(sunset);
                    txtUV.setText(uvIndex + " (" + uvStatus + ")");
                    txtFeelsLike.setText(feelsLike + "°C");
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    txtLoading.setVisibility(View.GONE);
                    txtLocation.setText("Không thể tải dữ liệu thời tiết");
                });
            }
        }).start();
    }
}
