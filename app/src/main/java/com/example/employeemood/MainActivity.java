package com.example.employeemood;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView emojiTextView1;
    private TextView emojiTextView2;
    private TextView emojiTextView3;
    private TextView emojiTextView4;
    private TextView emojiTextView5;
    private TextView currentMoodTextView1;
    private TextView currentMoodTextView2;
    private LinearLayout linearLayout;

    private LineChart lineChart;
    private APIInterface apiInterface;
    private Retrofit retrofit;

    public String getEmoji(int unicode){
        return new String(Character.toChars(unicode));
    }


    public void setLineChart(List<Entry> entryList, ArrayList<String> dateList){
        //set line chart color
        LineDataSet lineDataSet = new LineDataSet(entryList, "Data set");
        lineDataSet.setColor(Color.GREEN);
        lineDataSet.setValueTextColor(Color.WHITE);

        //set chart values
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.setBorderColor(Color.GREEN);
        lineChart.invalidate();

        // set x-axis settings
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setLabelRotationAngle(-30f);
        xAxis.setLabelCount(dateList.size());
        xAxis.setAxisMinimum(2f);

        // set y-axis settings
        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        YAxis leftYAxis = lineChart.getAxis(YAxis.AxisDependency.LEFT);
        YAxis rightYAxis = lineChart.getAxis(YAxis.AxisDependency.RIGHT);
        rightYAxis.setTextColor(R.color.dark);
        leftYAxis.setTextSize(10f);
        leftYAxis.setTextColor(Color.WHITE);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);

        Description description = lineChart.getDescription();
        description.setEnabled(false);

        //formats label for x-axis
        IAxisValueFormatter iAxisValueFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dateList.get((int) value);
            }
        };
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(iAxisValueFormatter);
    }

    private void getDataFromAPI(){

        ArrayList<Entry> dataValues = new ArrayList<>();
        ArrayList<String> dateList = new ArrayList<>();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://api.reward-dragon.com:8000/customers/")
                .build();

        apiInterface = retrofit.create(APIInterface.class);

        apiInterface.getMoodData(getString(R.string.auth_key), 500).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("testing", "response: "+response.body()+"");

                JsonArray moodJsonArray = new JsonArray();
                moodJsonArray = response.body().getAsJsonObject().getAsJsonArray("moodalytics");

                for(int i=0;i<moodJsonArray.size();i++){

                    String dtStart = moodJsonArray.get(i).getAsJsonObject().get("updated_at").toString().substring(1,11);

                    dateList.add(dtStart.substring(8,10)+"/"+dtStart.substring(5,7)+"/"+dtStart.substring(0,4));
                    dataValues.add(new Entry(i,Integer.parseInt(moodJsonArray.get(i).getAsJsonObject().get("emoji_point").toString())));
                }
                setLineChart(dataValues, dateList);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.d("testing", "failed: "+t.toString());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emojiTextView1 = findViewById(R.id.emoji_textview1);
        emojiTextView2 = findViewById(R.id.emoji_textview2);
        emojiTextView3 = findViewById(R.id.emoji_textview3);
        emojiTextView4 = findViewById(R.id.emoji_textview4);
        emojiTextView5 = findViewById(R.id.emoji_textview5);
        currentMoodTextView1 = findViewById(R.id.current_mood_textview1);
        currentMoodTextView2 = findViewById(R.id.current_mood_textview2);
        lineChart = findViewById(R.id.line_chart);
        linearLayout = findViewById(R.id.quote_linear_layout);

        //set emoji values
        emojiTextView1.setText(getEmoji(0x1F603));
        emojiTextView2.setText(getEmoji(0x1F607));
        emojiTextView3.setText(getEmoji(0x1F611));
        emojiTextView4.setText(getEmoji(0x1F614));
        emojiTextView5.setText(getEmoji(0x1F621));
        currentMoodTextView1.setText(getEmoji(0x1F607));
        currentMoodTextView2.setText(getEmoji(0x1F607));

        getDataFromAPI();

        emojiTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Grinning Face", Toast.LENGTH_SHORT).show();
            }
        });

        emojiTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Smiling Face with Halo", Toast.LENGTH_SHORT).show();
            }
        });

        emojiTextView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Expressionless Face", Toast.LENGTH_SHORT).show();
            }
        });

        emojiTextView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Pensive Face", Toast.LENGTH_SHORT).show();
            }
        });

        emojiTextView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Enraged Face", Toast.LENGTH_SHORT).show();
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "The Team is feeling good today", Toast.LENGTH_SHORT).show();
            }
        });
    }
}