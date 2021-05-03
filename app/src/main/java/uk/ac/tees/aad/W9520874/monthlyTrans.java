package uk.ac.tees.aad.W9520874;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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

public class monthlyTrans extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    int income=0;
    String str;
    TextView exp,inc,month,totalView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_trans);

        exp = findViewById(R.id.exptext);
        inc = findViewById(R.id.inctext);
        month = findViewById(R.id.monthtext);
        totalView = findViewById(R.id.totaltext);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        str = getIntent().getExtras().get("month").toString()+"-"+getIntent().getExtras().get("year").toString();


        FirebaseDatabase.getInstance().getReference("incomes").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                income=0;
                for(DataSnapshot snap: snapshot.getChildren()){
                    if(snap.getValue(Income.class).date.equals(str)){
                        income = income +  snap.getValue(Income.class).amount;
                    }
                }
                FirebaseDatabase.getInstance().getReference("expenses").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int out = 0;
                        for(DataSnapshot snap: snapshot.getChildren()){
                            if(snap.getValue(Income.class).date.equals(str))
                            {
                            out = out+  snap.getValue(Income.class).amount;
                            }
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


    private void setTextDashboard(int income, int Exp)
    {
        exp.setText("Expenditure: "+Exp);
        inc.setText("Earnings: "+income);
        totalView.setText((income-Exp)+" ");
        if ((income-Exp)<0)
            totalView.setTextColor(Color.RED);
        else
            totalView.setTextColor(Color.GREEN);

        month.setText(str);
    }

    private void setPiechart(int income, int Exp){
        Pie pie = AnyChart.pie();
        String[] colors = {"#ff3333","#3399ff"};
        pie.palette(colors);
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Expenditure", Exp));
        data.add(new ValueDataEntry("Income", income));
        pie.data(data);
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view2);
        anyChartView.setChart(pie);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
