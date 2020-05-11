package com.renelcuesta.chat_a_box.signin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.renelcuesta.chat_a_box.R;

public class Signin extends AppCompatActivity implements DataWarningDialog.NoticeDialogListener {

    public String TAG = "Chat-a-boxSignIn";

    FirebaseApp mApp;

    private DatabaseReference mDatabase;

    FirebaseAuth mAuth;

    FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseDatabase mFirebaseDatabase;

    EditText mEmailEdit;

    EditText mPasswordEdit;

    EditText mConfirmPasswordEdit;

    EditText mDisplayNameText;

    Button mLogonButton;

    TextView mRegisterText;

    String mDisplayName;

    boolean mLogonInProgress = false;

    boolean mRegisterInProgress = false;

    boolean mConsentGiven = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Log.e(TAG, "Starting Sign In Activity");

        initDisplayControls();
        initFirebase();
    }

    private void initDisplayControls() {
        mEmailEdit = (EditText) findViewById(R.id.emailEdit);
        mPasswordEdit = (EditText) findViewById(R.id.passwordEdit);
        mConfirmPasswordEdit = (EditText) findViewById(R.id.confirmPasswordEdit);
        mDisplayNameText = (EditText) findViewById(R.id.displayNameEdit);

        mLogonButton = (Button) findViewById(R.id.logonButton);
        mRegisterText = (TextView) findViewById(R.id.registerText);

        mEmailEdit.setVisibility(View.GONE);
        mPasswordEdit.setVisibility(View.GONE);
        mConfirmPasswordEdit.setVisibility(View.GONE);
        mDisplayNameText.setVisibility(View.GONE);

        mLogonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegisterInProgress = false;

                if (!mLogonInProgress) {
                    initLoginControls();
                } else {
                    startLogin();
                }
            }
        });

        mRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogonInProgress = false;

                if (!mConsentGiven) {
                    startDataWarning();
                } else {
                    if (mRegisterInProgress) {
                        startRegister();
                    }
                }
            }
        });
    }

    private void logonUser(String email, String password) {
        OnCompleteListener<AuthResult> success = new OnCompleteListener<AuthResult>() {
            //same listeners as registration
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Log.e(TAG, "SignIn : User logged on ");
                } else {
                    Log.e(TAG, "SignIn : User log on response, but failed ");
                }
            }
        };

        OnFailureListener fail = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "SignIn: Logon call failed");
            }
        };

        Log.e(TAG, "SignIn : Logging in : email [" + email + "] password [" + password + "]");
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(success).
                addOnFailureListener(fail);
    }

    private void initFirebase() {
        mApp = FirebaseApp.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance(mApp);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@androidx.annotation.NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    mFirebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = mFirebaseDatabase.getReference("Users");
                    final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(userID).getValue() != null) {
                                mDisplayName = dataSnapshot.child(userID).getValue().toString();
                            }

                            Log.e(TAG, mDisplayName + " from inside ValueEventListener");

                            Log.e(TAG, "Status Update : Valid current user logged on : email ["
                                    + user.
                                    getEmail() + "] display name [" + mDisplayName + "]");

                            finishActivity(mDisplayName);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "The read failed: " + databaseError.getCode());
                        }
                    });
                } else {
                    Log.e(TAG, "Status Update : No valid current user logged on");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void finishActivity(String mDisplayName) {
        Log.e(TAG, "Finishing Sign In Activity: Display Name: " + mDisplayName);

        Intent returningIntent = new Intent();
        returningIntent.putExtra("display name", mDisplayName);

        setResult(RESULT_OK, returningIntent);

        mAuth.removeAuthStateListener(mAuthListener);

        finish();
    }

    private void registerNewUser(final String email, String password, final String mDisplayName) {
        Log.e(TAG, "SignIn : Registration : email [" + email + "] password [" + password +
                "] display name [" + mDisplayName + "]");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        mAuth = FirebaseAuth.getInstance();
                        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        Log.e(TAG, "User ID is : " + userID);

                        mDatabase.child("Users").child(userID).setValue(mDisplayName);

                        Log.e(TAG, "Registered and set uid to displayname: " + mDisplayName);

                        finishActivity(mDisplayName);
                    }
                });
    }

    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        mConsentGiven = true;

        initRegisterControls();
    }

    public void onDialogNegativeClick(DialogFragment dialogFragment) {
        mConsentGiven = false;

        initDisplayControls();
    }

    public void initLoginControls() {
        mLogonInProgress = true;

        mEmailEdit.setVisibility(View.VISIBLE);
        mPasswordEdit.setVisibility(View.VISIBLE);
        mConfirmPasswordEdit.setVisibility(View.GONE);
        mDisplayNameText.setVisibility(View.GONE);

        mEmailEdit.setText("");
        mPasswordEdit.setText("");
        mConfirmPasswordEdit.setText("");
        mDisplayNameText.setText("");

        mConsentGiven = false;
    }

    private void initRegisterControls() {
        mRegisterInProgress = true;

        mEmailEdit.setVisibility(View.VISIBLE);
        mPasswordEdit.setVisibility(View.VISIBLE);
        mConfirmPasswordEdit.setVisibility(View.VISIBLE);
        mDisplayNameText.setVisibility(View.VISIBLE);

        mEmailEdit.setText("");
        mPasswordEdit.setText("");
        mConfirmPasswordEdit.setText("");
        mDisplayNameText.setText("");
    }

    private void startLogin() {
        String email = mEmailEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();

        if (!email.equals("") && !password.equals("")) {
            logonUser(email, password);
        }
    }


    private void startRegister() {
        String email = mEmailEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();
        String confirmPassword = mConfirmPasswordEdit.getText().toString();
        mDisplayName = mDisplayNameText.getText().toString();

        if (!email.equals("") && !password.equals("") && !mDisplayName.equals("")) {
            registerNewUser(email, password, mDisplayName);
        }
    }

    private void startDataWarning() {
        DataWarningDialog dialog = new DataWarningDialog();

        dialog.show(getSupportFragmentManager(), TAG);
    }
}