package co.bingleapp.bingle;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.Chip;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

import static co.bingleapp.bingle.Login.USER_PREFS;
import static co.bingleapp.bingle.slider.LOC_PREFS;


public class Profile_Fillup extends AppCompatActivity implements VerticalStepperForm {


    private VerticalStepperFormLayout verticalStepperForm;

    //Elements in layout
    public RadioGroup mRadioGroup;
    public DatePicker mdatePicker;
    public EditText phone;
    public ProgressDialog progressDialog;
    public RadioButton mCollege;
    public String[] labels;
    public NachoTextView mChips;
    String test;
    public RangeSeekBar rangeSeekBar;
    Calendar mCalendar;
    private com.wang.avi.AVLoadingIndicatorView formProgress;
   Float minage,maxage;
   private SharedPreferences prefs;
    private SharedPreferences loc_prefs;
    private DatabaseReference mDatabase;
    private  DatabaseReference mUser_Database;
// ...




    // Variables
    public String userGender;
    public EditText name;
    int dobday;
    int dobmonth;
    int dobyear;
    String college;
    private String userDOB;
    private String TAG = "retrieve-ruser";
    private List user_Hobbies;

    //Retrieve data
    private String rlocation;
    private String ruser_Name;
    private  String remail;
    private String rUID;

    public void stepComplete(){
        verticalStepperForm.setActiveStepAsCompleted();
        verticalStepperForm.goToNextStep();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fillup);

        prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        loc_prefs = getSharedPreferences(LOC_PREFS, MODE_PRIVATE);
        ruser_Name = prefs.getString("sharedName", null);
        rlocation = loc_prefs.getString("sharedCity", null);
        remail = prefs.getString("sharedEmail",null);
        rUID = prefs.getString("sharedUID",null);

        Toast.makeText(getApplicationContext(),ruser_Name,Toast.LENGTH_SHORT).show();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUser_Database = FirebaseDatabase.getInstance().getReference();


        String[] mySteps = {"What should we call you?", "I am","My birthday is on", "Studying in", "Things you love?", "Matches I would prefer in between"};
        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);

        // Finding the view
        verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);

        // Setting up and initializing the form
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, mySteps, this, this)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true) // It is true by default, so in this case this line is not necessary
                .init();


    }

    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        switch (stepNumber) {
            case 0:
                view = createNameStep();
                break;
            case 1:
                view = createGenderStep();
                break;
            case 2:
                view = createDOBStep();
                break;
            case 3:
                view = createEducationStep();
                break;
            case 4:
                view = createHobbiesStep();
                break;
            case 5:
                view = createAgeRangeStep();
                break;
        }
        return view;
    }

    private View createNameStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout

        if(ruser_Name!=null)
        {
            name = new EditText(this);
            name.setText(ruser_Name);
            name.setSingleLine(true);
            name.setHint("Your name");
        }
        else {
            name = new EditText(this);
            name.setSingleLine(true);
            name.setHint("Your name");
        }
        //setting already provided name
        SharedPreferences preferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        name.setText(preferences.getString("sharedName", null));

        return name;
    }

    private View createGenderStep() {
        // In this case we generate the view by inflating a XML file
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout genderLayoutContent = (LinearLayout) inflater.inflate(R.layout.activity_gender, null, false);

        mRadioGroup = findViewById(R.id.radiogroup);

        return genderLayoutContent;

    }

    private View createDOBStep() {
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout dobLayoutContent = (LinearLayout) inflater.inflate(R.layout.dobpicker, null, false);

        mdatePicker = (findViewById(R.id.dobdatePicker));

        return dobLayoutContent;
    }

    private View createEducationStep() {
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout educationLayoutContent = (LinearLayout) inflater.inflate(R.layout.selectcollege, null, false);

        mCollege = findViewById(R.id.collegeSelect_radioButton);

        return educationLayoutContent;
    }

    private View createAgeRangeStep() {
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout agerangeLayoutContent = (LinearLayout) inflater.inflate(R.layout.ageselector, null, false);

        rangeSeekBar = findViewById(R.id.seekbar);

        return agerangeLayoutContent;
    }

    private View createHobbiesStep() {
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout hobbiesLayoutContent = (LinearLayout) inflater.inflate(R.layout.interests, null, false);

        mChips = findViewById(R.id.nacho_text_view);




        return hobbiesLayoutContent;
    }

    @Override
    public void onStepOpening(int stepNumber) {
        mRadioGroup = findViewById(R.id.radiogroup);
        mdatePicker = (findViewById(R.id.dobdatePicker));
        mCollege = findViewById(R.id.collegeSelect_radioButton);

        mChips = findViewById(R.id.nacho_text_view);
        Resources res = getResources();
        String[] userHobbies = res.getStringArray(R.array.Hobbies);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, userHobbies);
        mChips.setAdapter(adapter);

        rangeSeekBar = findViewById(R.id.seekbar);
        rangeSeekBar.setMinProgress(18);//set min
        rangeSeekBar.setMaxProgress(35);//set max
        rangeSeekBar.setRangeInterval(1);
        rangeSeekBar.getLeftSeekBar().setTypeface(Typeface.DEFAULT);
        rangeSeekBar.getRightSeekBar().setTypeface(Typeface.DEFAULT);
        rangeSeekBar.setValue(18, 22);

        rangeSeekBar.setIndicatorTextDecimalFormat("0");
        com.jaygoo.widget.SeekBar leftSeekBar = rangeSeekBar.getLeftSeekBar();
        com.jaygoo.widget.SeekBar rightSeekBar = rangeSeekBar.getRightSeekBar();
        leftSeekBar.setIndicatorShowMode(com.jaygoo.widget.SeekBar.INDICATOR_MODE_ALWAYS_SHOW);
        rightSeekBar.setIndicatorShowMode(com.jaygoo.widget.SeekBar.INDICATOR_MODE_ALWAYS_SHOW);





    }

    @Override
    public void onContinue(int stepNumber) {
        switch (stepNumber) {
            case 0:
                checkName();
                break;
            case 1:
                checkGender();
                break;
            case 2:
                checkDOB();
                break;
            case 3:
                checkEducation();
                break;
            case 4:
                checkHobbies();
                break;
            case 5:
                checkAgeRange();
                break;



        }
        }



    private void checkName() {
        if(name.length() >= 3 && name.length() <= 40) {

            SharedPreferences pref = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("sharedName", name.getText().toString());
            edit.apply();

            stepComplete();
        } else {
            // This error message is optional (use null if you don't want to display an error message)
            String errorMessage = "The name must have between 3 and 40 characters";
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }
    }

    private void checkGender() {
        if (mRadioGroup.getCheckedRadioButtonId() == -1) {
            String errorMessage = "Please select your gender";
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        } else {
            int selectId = mRadioGroup.getCheckedRadioButtonId();
            final RadioButton selectedGender = findViewById(selectId);
            userGender = selectedGender.getText().toString();

            SharedPreferences pref = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("sharedGender", userGender);
            edit.apply();

            stepComplete();
        }
    }

    private void checkDOB() {
        dobday = mdatePicker.getDayOfMonth();
        dobmonth = 1+mdatePicker.getMonth();
        dobyear = mdatePicker.getYear();
        String date = dobyear + "-" + dobmonth + "-" + dobday;

        SharedPreferences pref = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("sharedDob", date);
        edit.apply();

        mCalendar = Calendar.getInstance();
        int day;
        int month;
        int year;
        day = mCalendar.get(Calendar.DAY_OF_MONTH);
        month = 1 + mCalendar.get(Calendar.MONTH);
        year = mCalendar.get(Calendar.YEAR);
        if(year - dobyear > 18)
        {
            stepComplete();
        }
        else if(year - dobyear == 18 && month > dobmonth)
        {
            stepComplete();
        }
        else if(year - dobyear == 18 && month == dobmonth && day >= dobday)
        {
            stepComplete();
        }
        else
        {
            String errorMessage = "You must be 18 years of age to Sign In !";
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }
    }

    private void checkEducation(){
        if (mCollege.isChecked()) {
            college = mCollege.getText().toString();

            SharedPreferences pref = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("sharedEducation", college);
            edit.apply();

            stepComplete();
        }
        else {
            String errorMessage = "Please select your college";
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }
    }

    public void checkHobbies(){
            // Do something with the text of each chip
            user_Hobbies = mChips.getChipValues();

        SharedPreferences pref = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("sharedInterests", test);
        edit.apply();
        stepComplete();
    }

    private void checkAgeRange(){

        rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                rangeSeekBar.setIndicatorText((int)min+"");
                minage = min;
                maxage = max;
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });
        String ageRange = minage.toString() + "-" + maxage.toString();
        SharedPreferences pref = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("sharedAgeRange", ageRange);
        edit.apply();
        stepComplete();

    }





    private void writeNewUser() {
        mDatabase.child("Location").child(rlocation).child(rUID).child("name").setValue(ruser_Name);
        mDatabase.child("Location").child(rlocation).child(rUID).child("email").setValue(remail);
        mDatabase.child("Location").child(rlocation).child(rUID).child("gender").setValue(userGender);
        mDatabase.child("Location").child(rlocation).child(rUID).child("dateofbirth").setValue(userDOB);
        mDatabase.child("Location").child(rlocation).child(rUID).child("education").setValue(college);
        mDatabase.child("Location").child(rlocation).child(rUID).child("interests").setValue(user_Hobbies);

    }






    @Override
    public void sendData() {
        writeNewUser();


       formProgress.setVisibility(View.VISIBLE);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                formProgress.setVisibility(View.INVISIBLE);
                Intent mSwitchtoContentmain = new Intent(Profile_Fillup.this, MainActivity.class);
                startActivity(mSwitchtoContentmain);
            }

        }, 1000L);

    }
}