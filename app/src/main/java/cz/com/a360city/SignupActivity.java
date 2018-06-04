package cz.com.a360city;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    EditText edt_username, edt_mail, edt_password, edt_repassword;
    Button btn_register;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-zA-Z0-9._-]+";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String package_name = "cz.com.a360city";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(SignupActivity.this);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPreferences = getSharedPreferences(package_name , Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initilizeViews();

        /*---------------------- initilize view -------------------------------*/
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fomeValidation()){
                    registerOnFirebase();
                }
            }
        });


        /*------------------------------ email validation ------------------------------------*/
        edt_mail .addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (edt_mail.getText().toString().trim().matches(emailPattern) && s.length() > 0){}
                else {
                    edt_mail.setError("invalid email");
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });
    }

    /*--------------------------------------- initilize view -----------------------------------------------*/
    public void initilizeViews(){
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_mail = (EditText) findViewById(R.id.edt_mail);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_repassword = (EditText) findViewById(R.id.edt_repassword);
        btn_register = (Button) findViewById(R.id.btn_register);
    }
    /*-------------------------------------------- End -----------------------------------------------*/

    /*---------------------------------------- initilize view -----------------------------------------------*/
    public boolean fomeValidation()
    {
        boolean flag = true;
        if(edt_username.getText().toString().equals("") || edt_mail.getText().toString().equals("") || edt_password.getText().toString().equals("")){
            flag = false;
            Toast.makeText(this, "Please fill all fields..!", Toast.LENGTH_SHORT).show();
        }
        else if(!edt_password.getText().toString().equals(edt_repassword.getText().toString())) {
            flag = false;
            Toast.makeText(this, "Please enter correct password..!", Toast.LENGTH_SHORT).show();
        }
        else {
            Pattern pattern;
            Matcher matcher;
            final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(edt_mail.getText().toString());
            if(!matcher.matches()){
                Toast.makeText(SignupActivity.this, "Please enter correct email..!", Toast.LENGTH_SHORT).show();
            }
            flag = matcher.matches();
        }
        return flag ;
    }
    /*---------------------------------------------- END -------------------------------------------------*/

    /*----------------------------------------  -----------------------------------------------*/
    public void registerOnFirebase()
    {

        final String var_email  = edt_mail.getText().toString().trim();
        final String var_username = edt_username.getText().toString();
        final String var_password = edt_password.getText().toString();

        final ProgressDialog pd = new ProgressDialog(SignupActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Users");

        myRef.orderByChild("email").equalTo(var_email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pd.dismiss();
                if(!dataSnapshot.hasChildren()){
                    String key = myRef.push().getKey();
                    myRef.child(key).child("email").setValue(var_email);
                    myRef.child(key).child("username").setValue(var_username);
                    myRef.child(key).child("password").setValue(var_password);

                    editor.putString("email", var_email);
                    editor.putString("username", var_username);
                    editor.putString("userkey", key);
                    editor.putString("remember_login", "1");
                    editor.commit();
                    onBackPressed();
                } else{
                    Toast.makeText(SignupActivity.this, "Register Failed! Registered user existed already.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pd.dismiss();
                Toast.makeText(SignupActivity.this, "Register Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
