package co.bingleapp.bingle;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextUtils;
import android.util.Log;
import android.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class SignUp extends AppCompatActivity {

    // UI references
    private EditText mSignUpEmail;
    private EditText mSignUpPassword;
    private EditText mSignUpConfirmPassword;
    private com.wang.avi.AVLoadingIndicatorView SignUpProgress;


    // Firebase instance variable
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Linked elements in layout to Java code
        TextView mLoginSwitch = findViewById(R.id.Login_TextViewButton);
        mSignUpEmail = findViewById(R.id.SignUpEmail_Field);
        mSignUpPassword = findViewById(R.id.SignUpPass_Field);
        mSignUpConfirmPassword = findViewById(R.id.ConfirmPass_Field);
        SignUpProgress = findViewById(R.id.SignUp_progressBar);


        // Keyboard Sign-in Action
        mSignUpConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == R.integer.register_form_complete || i == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }


                return false;

            }
        });

        // Instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Switch to Login activity
        mLoginSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mSwitchtoLogin = new Intent(SignUp.this,Login.class);
                startActivity(mSwitchtoLogin);
                finish();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void SignUpClick(View view){
        if (isNetworkAvailable() == true)
        {
            attemptRegistration();
        }
        else
            Toasty.warning(getApplicationContext(), "Not connected to internet!", Toast.LENGTH_SHORT, true).show();

    }


    // Attempt Registration
    private void attemptRegistration() {

        // Reset errors displayed in the form.
        mSignUpEmail.setError(null);
        mSignUpPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mSignUpEmail.getText().toString();
        String password = mSignUpPassword.getText().toString();
        String confirmpassword = mSignUpConfirmPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !CheckPassword(password)) {
            mSignUpPassword.setError(getString(R.string.error_invalid_password));
            focusView = mSignUpPassword;
            cancel = true;
        }

        // Check for a valid confirm password, if the user entered one.
        if (!confirmpassword.equals(password)){
            mSignUpConfirmPassword.setError(getString(R.string.error_incorrect_password));
            focusView = mSignUpConfirmPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mSignUpEmail.setError(getString(R.string.error_field_required));
            focusView = mSignUpEmail;
            cancel = true;
        } else if (!CheckEmail(email)) {
            mSignUpEmail.setError(getString(R.string.error_invalid_email));
            focusView = mSignUpEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Call create FirebaseUser() here
            createFirebaseUser();

        }
    }

    // Check if email is valid
    private boolean CheckEmail(String email) {
        return email.contains("@");
    }

    // Check if password is valid
    private boolean CheckPassword(String password) {

        return (password.length() > 5);

    }

    // Create Firebase User
    private void createFirebaseUser(){
        String email = mSignUpEmail.getText().toString();
        String password = mSignUpPassword.getText().toString();
        SignUpProgress.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Bingle", "createUser onComplete:" + task.isSuccessful());
                SignUpProgress.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()){
                    showSuccessDialog("Sign up successful");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent mSignUpSuccess = new Intent( SignUp.this, Login.class);
                            finish();
                            startActivity(mSignUpSuccess);
                        }
                    },3000);

                }

                if(!task.isSuccessful()){
                    showErrorDialog("Sign up attempt failed");
                }

            }
        });
    }

    // Alert dialog in case of registration failed
    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .show();
    }

    private void showSuccessDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Complete")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .show();
    }
}
