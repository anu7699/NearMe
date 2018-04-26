package com.wink.anu.nearme.login;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wink.anu.nearme.MainActivity;
import com.wink.anu.nearme.R;


public class LoginActivity extends AppCompatActivity {
    EditText email,pass;
    Button login ,register;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.hide();
        }
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        login=(Button)findViewById(R.id.bt_login);
        register=(Button)findViewById(R.id.bt_register);
        email=(EditText)findViewById(R.id.et_email);
        pass=(EditText)findViewById(R.id.et_password);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_text=email.getText().toString().trim();
                String pass_text=pass.getText().toString();
                //checking if email and passwords are empty

                if(TextUtils.isEmpty(email_text)){
                    Toast.makeText(getApplicationContext(),"Please enter email", Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(pass_text)){
                    Toast.makeText(getApplicationContext(),"Please enter password",Toast.LENGTH_LONG).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                mAuth.signInWithEmailAndPassword(email_text,pass_text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        if(task.isSuccessful()){
                            //start the profile activity
                            String uid = mAuth.getCurrentUser().getUid();
                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user=dataSnapshot.getValue(User.class);
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.putExtra("username", user.getUsername());
                                    i.putExtra("email", user.getEmail());
                                    i.putExtra("phone number", user.getPhone());
                                    i.putExtra("password", user.getPassword());
                                    Log.d("LoginActivity::",user.getUsername()+" "+user.getEmail());
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                    startActivity(i);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    throw databaseError.toException();
                                }
                            });
                            //i.putExtra("email",firebaseUser.getEmail());




                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Incorrect email or password", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this,SignupActivity.class);

                startActivity(i);
            }
        });




    }
}
