package co.bingleapp.bingle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;


public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        ProfileSettings.OnFragmentInteractionListener, FindDate.OnFragmentInteractionListener,
        FixedDate.OnFragmentInteractionListener, Notifications.OnFragmentInteractionListener,
        Settings.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChildEventListener mChildEventListener;
    private String mUsername;
    public static final String ANONYMOUS = "anonymous";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //load settings fragment by default
        loadFragment(new Settings());

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //user is signed in
                    onSignedInInitialze(user.getDisplayName());
                }
                else{
                    //user is signed out
                    onSignedOutCleanup();
                }
            }
        };

    }

    private boolean loadFragment(Fragment fragment) {
        if(fragment != null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_settings:
                fragment = new Settings();
                break;

            case R.id.navigation_find_date:
                fragment = new FindDate();
                break;

            case R.id.navigation_fixed_date:
                fragment = new FixedDate();
                break;

            case R.id.navigation_notifications:
                fragment = new Notifications();
                break;

            case R.id.navigation_profile_settings:
                fragment = new ProfileSettings();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public void onFindDateFragmentInteraction() {
        Toast.makeText(MainActivity.this, "We will notify you when your date is available!", Toast.LENGTH_LONG).show();
        //rest of the code here for find date fragment
    }

    @Override
    public void onSettingsFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFixedDateFragmentInteraction(Uri uri){

    }

    @Override
    public void onProfileSettingsEditFragmentInteraction() {

        Intent mSwitchToProfileFillUp = new Intent(MainActivity.this, Profile_Fillup.class);
        startActivity(mSwitchToProfileFillUp);

    }
    @Override
    public void onProfileSettingsChangePasswordFragmentInteraction(){

        Intent mSwitchToResetPassword = new Intent(MainActivity.this, Reset_Password.class);
        startActivity(mSwitchToResetPassword);

    }

    @Override
    public void onProfileSettingsSignOutFragmentInteraction(){

        mAuth.signOut();

    }


    @Override
    public void onNotificationsFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void onSignedInInitialze(String username)
    {
        mUsername = username;
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup()
    {
        mUsername = ANONYMOUS;
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
           // mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null)
        {
         //   mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }


}
