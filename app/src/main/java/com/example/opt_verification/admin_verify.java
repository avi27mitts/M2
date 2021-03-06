package com.example.opt_verification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class admin_verify extends AppCompatActivity {

    TextView tvname , tvnum ;

    EditText otp ;

    Button btn_verify ;

    String sent_code ;

    FirebaseAuth fbauth ;

    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_verify);

        tvname = findViewById(R.id.tv_na) ;
        tvnum = findViewById(R.id.tv_num) ;

        otp = findViewById(R.id.et_otp) ;

        btn_verify = findViewById(R.id.btn_verify_admin) ;

        fbauth = FirebaseAuth.getInstance() ;

        Intent intent = getIntent() ;

        tvname.setText(intent.getStringExtra(admin_display.adminname)) ;
        tvnum.setText(intent.getStringExtra(admin_display.adminnum)) ;

        progressDialog = new ProgressDialog( admin_verify.this ) ;

        sendotp() ;

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifycode() ;
            }
        });
    }


    private void sendotp() {

        progressDialog.setMessage( "Sending otp." );
        progressDialog.show() ;

        String num = "+91" + tvnum.getText().toString() ;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                num,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText( getApplicationContext() , "Code sent successfully. " , Toast.LENGTH_LONG).show() ;

            sent_code = s ;

            progressDialog.dismiss() ;

        }

    } ;

    private void verifycode() {
        String code = otp.getText().toString() ;

        if (code.isEmpty()){
            otp.setError( " Please enter a valid otp. " ) ;
            otp.requestFocus() ;
            return ;
        }

        try {
            progressDialog.setMessage( "Verifying....Please wait." );
            progressDialog.show() ;

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(sent_code, code) ;

            signInWithPhoneAuthCredential(credential) ;

        }
        catch (Exception e){
            Toast.makeText(getApplicationContext() , "Wrong code." , Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // if verification is successful

                            progressDialog.dismiss() ;

                            Toast.makeText( getApplicationContext() , "Verification approved." , Toast.LENGTH_LONG).show() ;

                            otp.setText("");

                            finish() ;

                            Intent intent = new Intent( getApplicationContext() , admin_options.class) ;
                            startActivity(intent);

                        } else { // if verification fails

                            Toast.makeText( getApplicationContext() , "Verification failed." , Toast.LENGTH_LONG).show() ;

                        }
                    }
                });
    }

}
