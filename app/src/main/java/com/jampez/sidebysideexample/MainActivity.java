package com.jampez.sidebysideexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jampez.sidebyside.SideBySideView;

public class MainActivity extends AppCompatActivity {

    SideBySideView names, contactInfo, passwords, authCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView text = findViewById(R.id.text);
        names = findViewById(R.id.names);
        contactInfo = findViewById(R.id.contact_info);
        passwords = findViewById(R.id.password);
        authCode = findViewById(R.id.authorisation_code);
        names.setLeftInputListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                text.setText(s.toString());
            }
        });

        Button validate = findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean namesValid = names.isValid();
                boolean contactInfoValid = contactInfo.isValid();
                boolean passwordsValid = passwords.isValid();
                boolean authCodeValid = authCode.isValid();

                if(namesValid && contactInfoValid && passwordsValid && authCodeValid){
                    text.setText("Validated!");
                }else{
                    text.setText("Not Validated!");
                }
            }
        });
    }
}