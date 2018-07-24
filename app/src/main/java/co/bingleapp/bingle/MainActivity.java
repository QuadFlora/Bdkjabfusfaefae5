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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static co.bingleapp.bingle.Login.USER_PREFS;
import static co.bingleapp.bingle.slider.LOC_PREFS;


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
    private DatabaseReference mPairing;
    private DatabaseReference mDatabase;
    private DatabaseReference mPairDetails;
    private DatabaseReference switchToActive;
    private String pairedName;
    private String rUID;
    private SharedPreferences prefs;
    private SharedPreferences loc_prefs;
    private String rlocation;
    private String userGender;
    private  String matchedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();



        prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        loc_prefs = getSharedPreferences(LOC_PREFS, MODE_PRIVATE);
        rlocation = loc_prefs.getString("sharedCity", null);
        rUID = prefs.getString("sharedUID",null);
        userGender = prefs.getString("sharedGender",null);
        if(userGender.equals("Male")) {
            mPairing = FirebaseDatabase.getInstance().getReference("Location").child(rlocation).child("Active").child("ActiveFemale");
        }
        if(userGender.equals("Female")) {
            mPairing = FirebaseDatabase.getInstance().getReference("Location").child(rlocation).child("Active").child("ActiveMale");
        }
        mPairDetails = FirebaseDatabase.getInstance().getReference("Location").child(rlocation).child("Users");



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
                    onSignedInInitialize(user.getDisplayName());
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

        addToActive();

        mPairing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //rest of the code here for find date fragment
    }

    @Override
    public void onSettingsFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFixedDateFragmentInteraction(Uri uri){

    }

    @Override
    public void onProfileSettingsSaveFragmentInteraction() {

        Toast.makeText(MainActivity.this,"Profile Updated!", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onProfileSettingsChangePasswordFragmentInteraction(){

        Intent mSwitchToResetPassword = new Intent(MainActivity.this, Reset_Password.class);
        startActivity(mSwitchToResetPassword);

    }

    @Override
    public void onProfileSettingsSignOutFragmentInteraction(){

        signOut();

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

    private void onSignedInInitialize(String username)
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

    private void signOut()
    {
        mAuth.signOut();
        Toast.makeText(this, "User Sign out!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, Login.class));
        finish();
    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            pairedName = ds.getKey();
            getpartner();




        }

    }

    private void getpartner(){
        mPairDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                matchedName = dataSnapshot.child(pairedName).child("name").getValue(String.class);
                Toast.makeText(getApplicationContext(),matchedName,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addToActive(){
        if(userGender.equals("Male")){
            switchToActive = FirebaseDatabase.getInstance().getReference("Location").child(rlocation).child("IdleMale").child(rUID);
        }
        if(userGender.equals("Female")){
            switchToActive = FirebaseDatabase.getInstance().getReference("Location").child(rlocation).child("IdleFemale").child(rUID);
        }

        switchToActive.removeValue();
        if(userGender.equals("Male")){
            mDatabase.child("Location").child(rlocation).child("Active").child("ActiveMale").child(rUID).setValue("user_Active");
        }

        if(userGender.equals("Female")){
            mDatabase.child("Location").child(rlocation).child("Active").child("ActiveFemale").child(rUID).setValue("user_Active");
        }



    }


}
