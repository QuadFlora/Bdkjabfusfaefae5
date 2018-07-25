package co.bingleapp.bingle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.CallbackManager;
import com.facebook.login.LoginResult;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity  {

    // UI references
    private EditText mSignUpEmail;
    private EditText mSignUpPassword;
    private FirebaseAuth mAuth;
    private com.wang.avi.AVLoadingIndicatorView LoginProgress;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1000;
    private static final String DEBUG_TAG= "Glogin";
    public static final String USER_PREFS = "mPrefsFile";
    private String user_Name;
    private String user_Email;
    private SharedPreferences.Editor editor;
    private FirebaseUser currentFirebaseUser;
    private String user_UID;


    boolean isPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editor = getSharedPreferences(USER_PREFS, MODE_PRIVATE).edit();


        AppEventsLogger.activateApp(getApplication());
        callbackManager = CallbackManager.Factory.create();
        com.facebook.login.widget.LoginButton loginButton = (com.facebook.login.widget.LoginButton) findViewById(R.id.facebook_login_button);
        com.google.android.gms.common.SignInButton Google_login = findViewById(R.id.google_signin_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                signInWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        // Linked elements in layout to Java code
        TextView mSignUpSwitch = findViewById(R.id.SignUp_TxtViewButton);
        TextView mForgotPasswordSwitch = findViewById(R.id.ForgotPass_TxtView);
        mSignUpEmail = findViewById(R.id.Email_Field);
        mSignUpPassword = findViewById(R.id.Password_Field);
        LoginProgress = findViewById(R.id.Login_progressBar);

        //Google Signin
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                google_signIn();
            }
        });

        // Keyboard Sign in action
        mSignUpPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if( i == R.integer.login_form_complete || i == EditorInfo.IME_NULL){
                    attemplogin();
                    return true;
                }

                return false;
            }
        });

        //Switch to Sign up activity
        mSignUpSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mSwitchtoSignUp = new Intent(Login.this, SignUp.class);
                startActivity(mSwitchtoSignUp);
            }
        });

        //Switch to Forgot Password Activity
        mForgotPasswordSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mSwitchtoForgotPassword = new Intent(Login.this, Reset_Password.class);
                startActivity(mSwitchtoForgotPassword);
            }
        });

        // Instance of Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }





    public void googlelogin_click(View view){

            google_signIn();

    }

    private void google_signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                // ...
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toasty.success(getApplicationContext(), "Success", Toast.LENGTH_SHORT, true).show();

                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent mSwitchtoMainactivity = new Intent(Login.this, Profile_Fillup.class);
                            startActivity(mSwitchtoMainactivity);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toasty.error(getApplicationContext(),"Authentication Failed", Toast.LENGTH_SHORT,true).show();

                        }

                        // ...
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            user_Name = acct.getDisplayName();
            editor.putString("sharedName", user_Name);
            String email = acct.getEmail();
            editor.putString("sharedEmail", email);
            editor.apply();

        }
    }

    private void signInWithFacebook(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful());
                        Toasty.success(getApplicationContext(), "Success", Toast.LENGTH_SHORT, true).show();
                        Intent mSwitchtoMainactivity = new Intent(Login.this, Profile_Fillup.class);
                        startActivity(mSwitchtoMainactivity);


                        if (!task.isSuccessful()) {
                            Toasty.error(getApplicationContext(),"Authentication Failed", Toast.LENGTH_SHORT,true).show();
                        }
                        }


                    });
                };



// Check for internet connection 
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void LoginClick(View view){
        if (isNetworkAvailable() == true)
        {
            attemplogin();
        }
        else
            Toasty.warning(getApplicationContext(), "Not connected to internet!", Toast.LENGTH_SHORT, true).show();
        }




    private void attemplogin() {
        String email = mSignUpEmail.getText().toString();
        editor.putString("sharedEmail", email);
        editor.apply();
        String password = mSignUpPassword.getText().toString();


        if (email.equals("") || password.equals("")) return;


        LoginProgress.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                LoginProgress.setVisibility(View.INVISIBLE);

                if (!task.isSuccessful()) {
                    showErrorDialog("Invalid email or password");
                }
                else {
                    currentFirebaseUser = mAuth.getInstance().getCurrentUser();
                    user_UID = currentFirebaseUser.getUid();
                    editor.putString("sharedUID", user_UID);
                    editor.apply();
                    Toasty.success(getApplicationContext(), "Success", Toast.LENGTH_SHORT, true).show();

                    Intent mSwitchToMainActivity = new Intent(Login.this, Profile_Fillup.class);
                    startActivity(mSwitchToMainActivity);
                }

            }
        });
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

}
