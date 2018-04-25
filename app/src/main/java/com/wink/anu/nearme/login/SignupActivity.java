package com.wink.anu.nearme.login;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wink.anu.nearme.MainActivity;
import com.wink.anu.nearme.R;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
     EditText username,email,pass,c_pass,phone;
     TextView tv_tnc;
     Button signup;
     CheckBox tnc;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    boolean DataOk=false;

    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";
    private static final String EMAIL_MSG = "invalid email";
    private static final String PHONE_MSG = "+##-##########";


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
        setContentView(R.layout.activity_signup);

        mAuth= FirebaseAuth.getInstance();
        username=findViewById(R.id.et_reg_name);
        email=findViewById(R.id.et_reg_email);
        pass=findViewById(R.id.et_reg_pass);
        c_pass=findViewById(R.id.et_reg_cpass);
        phone=findViewById(R.id.et_reg_phone);
        tnc=(CheckBox)findViewById(R.id.chbx_tnc);
        signup=findViewById(R.id.bt_signup);
        tv_tnc=findViewById(R.id.tv_tnc);
        tv_tnc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                //startActivity(new Intent(SignupActivity.this,TncActivity.class));
                return false;
            }
        });



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String UsernameText=username.getText().toString();
                String EmailText=email.getText().toString();
                String passText=pass.getText().toString();
                String cpassText=c_pass.getText().toString();
                String phoneText=phone.getText().toString();
                DataOk=validate(UsernameText,EmailText,passText,cpassText,phoneText);
                if(DataOk) {
                    final User user = new User(EmailText, passText, UsernameText, phoneText);
                    mAuth.createUserWithEmailAndPassword(EmailText, passText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.v("write in db::",UsernameText);
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                FirebaseUser fUser = mAuth.getCurrentUser();
                                mDatabase.child("users").child(fUser.getUid()).setValue(user);
                                Intent i = new Intent(SignupActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.putExtra("username", user.getUsername());
                                i.putExtra("email", user.getEmail());
                                i.putExtra("phone number", user.getPhone());
                                i.putExtra("password", user.getPassword());
                                finish();
                                startActivity(i);
                            }
                        }
                    });
                }




            }
        });

    }

    private boolean validate(String usernameText, String emailText, String passText, String cpassText, String phoneText) {
        if((!emailText.contains("@"))||(!emailText.contains(".com"))||!isValid(email,EMAIL_REGEX,EMAIL_MSG,true))
        {
            email.setText("");
            Toast.makeText(getApplicationContext(),"Invalid Email",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!isValid(phone,PHONE_REGEX,PHONE_MSG,true))
        {
            phone.setText("");
            Toast.makeText(getApplicationContext(),"Invalid Phone Number",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!cpassText.equals(passText))
        {
            pass.setText("");
            c_pass.setText("");
            Toast.makeText(getApplicationContext(),"Password in the confirmation field does not match",Toast.LENGTH_SHORT).show();
            return false;

        }
        else if(passText.length()<8||passText.length()>30)
        {
            pass.setText("");
            c_pass.setText("");
            Toast.makeText(getApplicationContext(),"Password must contain 8 to 30 characters ",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!tnc.isChecked())
        {

            Toast.makeText(getApplicationContext(),"Please agree to the Terms & Conditions to proceed",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;

    }
    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        };

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {

            return false;
        }

        return true;
    }


}
