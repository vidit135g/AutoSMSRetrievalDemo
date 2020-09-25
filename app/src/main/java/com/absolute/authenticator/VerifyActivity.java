package com.absolute.authenticator;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;


public class VerifyActivity extends AppCompatActivity  {

    TextView main_num;
    OtpView otp;
    TextView timer;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    FrameLayout loginbutton;
    private String otpnN0;

    final CountDownTimer countDownTimer=   new CountDownTimer(180000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            timer.setText(millisecondsToTime(millisUntilFinished));
        }
        @Override
        public void onFinish() {
            timer.setText("");
        }
    };
    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(otpnN0!=null&&otpnN0.equals("1234")){
                //otp.showSuccess();
                countDownTimer.cancel();
            }
            else {
                //otp.showError();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        main_num=findViewById(R.id.introdesc);
        otp=findViewById(R.id.otp_view);
        loginbutton=findViewById(R.id.check_otp);
        timer=findViewById(R.id.timer);

        String no=getIntent().getStringExtra("number");
        if(no!=null){
            main_num.setText(no);
        }
        sendVerificationCode(no);


        countDownTimer.start();

    }

    private void sendVerificationCode(String no) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                no,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            final String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                otp.setText(code);
                if(otp.getText().length()==6){
                    loginbutton.setBackgroundColor(getResources().getColor(R.color.gblue));
                    loginbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            verifyVerificationCode(code);
                        }
                    });
                }
                //verifying the code

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent=new Intent(VerifyActivity.this,MainActivity.class);
            startActivity(intent);
        }
        else{
            mVerificationId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }


                        }
                    }
                });
    }



    @Override
    protected void onDestroy() {

        if (countDownTimer != null) {
            countDownTimer.onFinish();
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.onFinish();
            countDownTimer.cancel();
        }
    }
    private String millisecondsToTime(long milliseconds) {

        return "Time remaining " + String.format("%d : %d ", TimeUnit.MILLISECONDS.toMinutes(milliseconds), TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

}