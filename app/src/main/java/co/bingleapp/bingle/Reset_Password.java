package co.bingleapp.bingle;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Reset_Password extends AppCompatActivity {

    private EditText mForgotEmail;
    private FirebaseAuth mAuth;
    private com.wang.avi.AVLoadingIndicatorView ResetProgress;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        mForgotEmail = findViewById(R.id.ForgotEmail_Field);
        mAuth = FirebaseAuth.getInstance();
        ResetProgress = findViewById(R.id.ResetPassword_progressBar);

        mForgotEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if( i == R.integer.forgot_password_field_complete || i == EditorInfo.IME_NULL){
                    AttemptForgotPassword();
                    return true;
                }

                return false;
            }
        });



    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void ForgotPasswordClick(View view){
        if (isNetworkAvailable() == true)
        {
            AttemptForgotPassword();
        }
        else
            showErrorDialog("Not connected to Internet");
    }

    private void AttemptForgotPassword(){
        String email = mForgotEmail.getText().toString();

        if (email.equals("")) return;
        ResetProgress.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                ResetProgress.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()){
                    showSuccessDialog("Password reset email is sent to your email. Please check inbox!");
                    Log.d("Bingle", " Email sent");
                }
                if (!task.isSuccessful()){
                    showErrorDialog("The entered email is not registered to any account");
                    Log.d("Bingle", " Failed");
                }

            }
        });


    }

    private void showSuccessDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Reset email sent")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }


}

