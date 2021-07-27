package com.example.milkandcookies.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.milkandcookies.R;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import okhttp3.Headers;

public class ProductActivity extends AppCompatActivity {

    private String barcode;
    private double percentProtein;
    private double percentFat;
    private double percentCarbs;
    private TextView tvIngredients;
    private TextView tvTitle;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        barcode = getIntent().getStringExtra("barcode");
        pieChart = findViewById(R.id.piechart);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvTitle = findViewById(R.id.tvTitle);

        getInfoSpoonacular();

    }

    private void getInfoSpoonacular() {
        StringBuilder urlBuilder = new StringBuilder("https://api.spoonacular.com/food/products/upc/");
        urlBuilder.append(barcode).append("?apiKey=").append(getString(R.string.spoonacular_key));
        String URL = urlBuilder.toString();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d("got information!", json.jsonObject.toString());
                parseInfoSpoonacular(json.jsonObject);
            }
            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d("failed", "error: " + throwable.toString());
            }
        });

    }

    private void parseInfoSpoonacular(JSONObject jsonObject) {
        try {
            JSONObject nutrition = jsonObject.getJSONObject("nutrition");
            JSONObject caloricBreakdown = nutrition.getJSONObject("caloricBreakdown");
            percentProtein = caloricBreakdown.getDouble("percentProtein");
            percentFat = caloricBreakdown.getDouble("percentFat");
            percentCarbs = caloricBreakdown.getDouble("percentCarbs");
            tvIngredients.setText(jsonObject.getString("ingredientList"));
            tvTitle.setText(jsonObject.getString("title"));
            createPieChart();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createPieChart() {
        Log.d("percentProtein", (float) percentProtein + "");
        pieChart.addPieSlice(
                new PieModel(
                        "Protein",
                        (float) percentProtein,
                        Color.parseColor("#FF8C6B")));
        pieChart.addPieSlice(
                new PieModel(
                        "Fat",
                        (float) percentFat,
                        Color.parseColor("#eab676")));
        pieChart.addPieSlice(
                new PieModel(
                        "Carbs",
                        (float) percentCarbs,
                        Color.parseColor("#84563C")));
        pieChart.startAnimation();
    }

}