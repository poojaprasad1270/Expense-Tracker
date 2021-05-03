package uk.ac.tees.aad.W9520874;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import  androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    public static final int CAMERA_PERM_CODE = 101;




    StorageReference storageReference;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    TextView exp;
    TextView inc;
    TextView total;
    int income=0;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        exp = findViewById(R.id.expenditure);
        inc = findViewById(R.id.earningText);
        total = findViewById(R.id.totalText);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser =mAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar4);

        Button date = findViewById(R.id.monthly);

        storageReference = FirebaseStorage.getInstance().getReference();

        Button camera = findViewById(R.id.button3);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                askCameraPermissions();
            }
        });

        redirections();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uk.ac.tees.aad.W9520874.DatePicker mDatePickerDialogFragment;
                mDatePickerDialogFragment = new uk.ac.tees.aad.W9520874.DatePicker();
                mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference("incomes").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                income=0;
                for(DataSnapshot snap: snapshot.getChildren()){
                    income = income +  snap.getValue(Income.class).amount;
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
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view2);
        anyChartView.setChart(pie);
        progressBar.setVisibility(View.INVISIBLE);
    }


    private void setTextDashboard(int income, int Exp){
        exp.setText("Expenditure: "+Exp);
        inc.setText("Earnings: "+income);
        total.setText((income-Exp)+" ");
        if ((income-Exp)<0)
            total.setTextColor(Color.RED);
        else
            total.setTextColor(Color.GREEN);
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


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Intent intent = new Intent(getApplicationContext(),monthlyTrans.class);
        intent.putExtra("month",month+1);
        intent.putExtra("year",year);
        startActivity(intent);
    }




    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent,200);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == Activity.RESULT_OK){
            captureImageData(data);
        }
    }
    private void  captureImageData( Intent data)
    {
        Bitmap thumb = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumb.compress(Bitmap.CompressFormat.JPEG,90,bytes);
        byte bb[] = bytes.toByteArray();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "J" + timeStamp + "i.jpg";

       final StorageReference ref =  FirebaseStorage.getInstance().getReference(firebaseUser.getUid()).child(imageFileName);
        ref.putBytes(bb).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Toast.makeText(getApplicationContext(),"Image Uploded",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

}
