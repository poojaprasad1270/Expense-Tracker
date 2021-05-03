package uk.ac.tees.aad.W9520874;

import  androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    TextView exp;
    TextView inc;
    TextView total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        exp = findViewById(R.id.expenditure);
        inc = findViewById(R.id.earningText);
        total = findViewById(R.id.totalText);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser =mAuth.getCurrentUser();




    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
    }

    private void setPiechart(int inc, int Exp){
        Pie pie = AnyChart.pie();
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Expenditure", inc));
        data.add(new ValueDataEntry("Income", Exp));
        pie.data(data);
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        anyChartView.setChart(pie);

    }
    private void setTextDashboard(int inc, int Exp){
        exp.setText("Expenditure: "+10000);
        inc.setText("Earnings: "+5000);
        total.setText("OverAll "+(10000-5000));
    }

    private void redirections(){

        Button logout = findViewById(R.id.button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        Button btn = findViewById(R.id.addExpText);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),addIncome.class));
            }
        });
    }
}
