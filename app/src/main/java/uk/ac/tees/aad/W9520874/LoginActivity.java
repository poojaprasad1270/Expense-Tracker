package uk.ac.tees.aad.W9520874;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
{

    FirebaseUser user;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        final TextView email = findViewById(R.id.loginEmail);
        final TextView password =  findViewById(R.id.loginPassword);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        Button btn = findViewById(R.id.btnLogin);
        TextView create = findViewById(R.id.createacc);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(getApplicationContext(),Register.class);
                startActivity(i);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailS = email.getText().toString();
                final String pass = password.getText().toString();

                if(emailS.isEmpty()){
                    email.setError("Enter Correct Format");
                    email.findFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(emailS).matches()){
                    email.setError("Enter Correct Format");
                    email.findFocus();
                    return;
                }
                if(pass.length() < 6)
                {
                    password.setError("password must be 6 charecters");
                    password.findFocus();
                    return;

                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(emailS,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isComplete()){

                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Incorrect Details",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });






    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        finishAffinity();
    }
}
