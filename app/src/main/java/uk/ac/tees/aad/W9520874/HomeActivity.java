package uk.ac.tees.aad.W9520874;

import androidx.annotation.NonNull;
import  androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    TextView exp;
    TextView inc;
    TextView total;
    int income=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        exp = findViewById(R.id.expenditure);
        inc = findViewById(R.id.earningText);
        total = findViewById(R.id.totalText);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser =mAuth.getCurrentUser();

        redirections();

        FirebaseDatabase.getInstance().getReference("incomes").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                income=0;


                for(DataSnapshot snap: snapshot.getChildren()){
                    income = income+  snap.getValue(Income.class).amount;
                }

                FirebaseDatabase.getInstance().getReference("expenses").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        int out = 0;
                        for(DataSnapshot snap: snapshot.getChildren()){
                            out = out+  snap.getValue(Income.class).amount;
                        }
                        setPiechart(income,out);
                        setTextDashboard(income,out);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
    }
    private void setPiechart(int income, int Exp){
        Pie pie = AnyChart.pie();
        String[] colors = {"#ff3333","#3399ff"};
        pie.palette(colors);
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Expenditure", Exp));
        data.add(new ValueDataEntry("Income", income));
        pie.data(data);
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        anyChartView.setChart(pie);

    }


    private void setTextDashboard(int income, int Exp){
        exp.setText("Expenditure: "+Exp);
        inc.setText("Earnings: "+income);
        total.setText((income-Exp)+" ");
        if ((income-Exp)<0)
        {
            total.setTextColor(Color.RED);

        }else
        {
            total.setTextColor(Color.BLUE);
        }

    }

    @Override
    public void onBackPressed() {


       finishAffinity();
        finish();
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
        Button addExp = findViewById(R.id.addExpenditure);
        addExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),addExpenditure.class));
            }
        });
    }
}
