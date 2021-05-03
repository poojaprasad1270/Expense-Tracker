package uk.ac.tees.aad.W9520874;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class addIncome extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    TextView amount ;
    TextView date;
    FirebaseUser user;
    FirebaseAuth mAuth;
    TextView tview;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        tview = findViewById(R.id.addExpText);

        amount = findViewById(R.id.amountInc);
        date = findViewById(R.id.dateInc);
        Button btn = findViewById(R.id.submitbtnInc);


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uk.ac.tees.aad.W9520874.DatePicker mDatePickerDialogFragment;
                mDatePickerDialogFragment = new uk.ac.tees.aad.W9520874.DatePicker();
                mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
            }
        });

        FirebaseDatabase.getInstance().getReference("incomes").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int out = 0;
                for(DataSnapshot snap: snapshot.getChildren()){
                    out = out+  snap.getValue(Income.class).amount;
                }
                tview.setText(out+" ");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("incomes").child(user.getUid()).push();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Income inc = new Income(Integer.parseInt(amount.getText().toString()),"hai" +
                        "");

                FirebaseDatabase.getInstance().getReference("incomes").child(user.getUid()).push().setValue(inc);


            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalender = Calendar.getInstance();
        mCalender.set(Calendar.YEAR, year);
        mCalender.set(Calendar.MONTH, month);
        mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalender.getTime());
        date.setText(selectedDate);
    }
}
