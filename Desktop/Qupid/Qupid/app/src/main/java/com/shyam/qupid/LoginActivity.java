package com.shyam.qupid;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.qrcode.encoder.QRCode;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    public EditText phonenumber;
    public Button signup;
    public String verificationid;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthCredential credential;
    private EditText username;
    private boolean status = false;
    PhoneAuthCredential credit;
    String Qrcode, pushkey;
    DatabaseReference dref;
    FirebaseUser curuser;
    boolean noofpersons;
    String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phonenumber = (EditText) findViewById(R.id.number);
        signup = (Button) findViewById(R.id.signup);
        username = (EditText) findViewById(R.id.Username);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verify();

            }
        });

        //mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.

                Toast.makeText(getApplicationContext(), "verification completed", Toast.LENGTH_SHORT).show();


                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Toast.makeText(getApplicationContext(), "verification failed", Toast.LENGTH_SHORT).show();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                Toast.makeText(getApplicationContext(), "code sent", Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //   Log.d(TAG, "signInWithCredential:success");

                            curuser = task.getResult().getUser();
                            //  Toast.makeText(getApplicationContext(),user.getPhoneNumber().toString(),Toast.LENGTH_SHORT).show();
                            Intent returnIntent = new Intent(LoginActivity.this, MainActivity.class);
                            // String result=user.getPhoneNumber();

                            returnIntent.putExtra("phonenumber", curuser.getPhoneNumber().toString());
                            returnIntent.putExtra("userid", curuser.getUid().toString());
                            String uid = curuser.getUid();
                            String phone = curuser.getPhoneNumber();
                            dref = FirebaseDatabase.getInstance().getReference("users");
                            pushkey = dref.push().getKey();
                            dref.child("qrcodes").child(uid).setValue(pushkey);

                            dref.child("Qrdecodes").child(pushkey).setValue(uid);

                            dref.child(uid).child("username").setValue(username.getText().toString());

                            setResult(Activity.RESULT_OK, returnIntent);

                            // Toast.makeText(getApplicationContext(),"successful",Toast.LENGTH_SHORT).show();

                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //   Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "signin failed", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void verify() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phonenumber.getText().toString(),  // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks, mResendToken);
    }


}