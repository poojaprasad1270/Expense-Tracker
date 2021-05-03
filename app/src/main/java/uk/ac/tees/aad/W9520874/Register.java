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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    User user;
    TextView name;
    TextView email;
    TextView password;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

         name = findViewById(R.id.regName);
         email =  findViewById(R.id.regMail);
        password = findViewById(R.id.regPass);
        progressBar = findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.INVISIBLE);
        Button btn = findViewById(R.id.btnRegister);

       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String emailS = email.getText().toString();
               String pass = password.getText().toString();
               String nameS = name.getText().toString();

               if(nameS.length() <  4){
                   name.setError("Name must Be 3 letters");
                   name.findFocus();
                   return;
               }

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


               mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                       .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if (task.isSuccessful()) {
                                   FirebaseUser usertemp = mAuth.getCurrentUser();
                                   user = new User(name.getText().toString(),email.getText().toString(),password.getText().toString());
                                   FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                          progressBar.setVisibility(View.INVISIBLE);
                                           startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                       }
                                   });
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
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        finishAffinity();
    }
}
