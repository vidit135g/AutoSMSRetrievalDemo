package com.absolute.authenticator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity{
    private static final int RESOLVE_HINT = 101;
    CountryCodePicker ccp;
    EditText editTextCarrierNumber;
    FrameLayout send_otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        editTextCarrierNumber = (EditText) findViewById(R.id.name_edit_text);
        send_otp = findViewById(R.id.send_otp_button);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(this, "Already Logged In",
                    Toast.LENGTH_LONG).show();
            Intent intent =new  Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                // your code
                if (ccp.isValidFullNumber()) {
                    send_otp.setBackgroundColor(getResources().getColor(R.color.gblue));
                    send_otp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String number = ccp.getFormattedFullNumber();
                            // OnVerificationStateChangedCallbacks
                            Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                            intent.putExtra("number", number);
                            startActivity(intent);
                        }


                    });

                    // Construct a request for phone numbers and show the picker

                } else
                    send_otp.setBackgroundColor(getResources().getColor(R.color.ggray));
            }
        });


    }


}