package co.bingleapp.bingle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;
import static co.bingleapp.bingle.Login.USER_PREFS;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileSettings.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileSettings extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static final int DEFAULT_BIO_LENGTH_LIMIT = 1000;
    Button changePasswordButton;
    Button editProfileButton;
    Button signOutButton;
    Button saveProfileButton;
    private TextView mNameEditTextView;
    private TextView mInterestsEditTextView;
    private TextView mEmailEditTextView;
    private TextView mGenderEditTextView;
    private TextView mDOBEditTextView;
    private TextView mEducationEditTextView;
    private TextView mMatchingAgeEditTextView;

    private EditText mNameEdit;
    private EditText mEmailEdit;
    private EditText mEducationEdit;
    private EditText mInterestsEdit;
    private EditText mMatchingAgeEdit;

    //firebase instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCollegeDatabaseReference;
    private DatabaseReference mInterestsDatabaseReference;
    private DatabaseReference mAgeDatabaseReference;

    //shared preferences variable
    public String displayname;

    private OnFragmentInteractionListener mListener;



    public ProfileSettings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileSettings.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileSettings newInstance(String param1, String param2) {
        ProfileSettings fragment = new ProfileSettings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile_settings, container, false);
        //Initialize firebase component
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mCollegeDatabaseReference = mFirebaseDatabase.getReference().child("College");
        mInterestsDatabaseReference = mFirebaseDatabase.getReference().child("Interests");
        mAgeDatabaseReference = mFirebaseDatabase.getReference().child("Age");

        //Initialize reference to views
        mNameEditTextView = (TextView) rootView.findViewById(R.id.textViewName);
        mInterestsEditTextView = (TextView) rootView.findViewById(R.id.textViewInterests);
        mEmailEditTextView = (TextView) rootView.findViewById(R.id.textViewEMail);
        mDOBEditTextView = (TextView) rootView.findViewById(R.id.textViewDOB);
        mGenderEditTextView = (TextView) rootView.findViewById(R.id.textViewGender);
        mEducationEditTextView = (TextView) rootView.findViewById(R.id.textViewEducation);
        mMatchingAgeEditTextView = (TextView) rootView.findViewById(R.id.textViewMatchingAgeRange);

        mNameEdit = (EditText) rootView.findViewById(R.id.editTextName);
        mEmailEdit = (EditText) rootView.findViewById(R.id.editTextEmail);
        mEducationEdit = (EditText) rootView.findViewById(R.id.editTextEducation);
        mInterestsEdit = (EditText) rootView.findViewById(R.id.editTextInterests);
        mMatchingAgeEdit = (EditText) rootView.findViewById(R.id.editTextMatchingAge);

        editProfileButton = (Button) rootView.findViewById(R.id.buttonEditProfile);
        saveProfileButton = (Button) rootView.findViewById(R.id.buttonSaveProfile);
        changePasswordButton = (Button) rootView.findViewById(R.id.buttonChangePassword);
        signOutButton = (Button) rootView.findViewById(R.id.buttonSignOut);

        //setting text to text views
        SharedPreferences preferences = this.getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        mNameEditTextView.setText(preferences.getString("sharedName", null));
        mEmailEditTextView.setText(preferences.getString("sharedEmail", null));
        mGenderEditTextView.setText(preferences.getString("sharedGender", null));
        mDOBEditTextView.setText(preferences.getString("sharedDob", null));
        mEducationEditTextView.setText(preferences.getString("sharedEducation", null));
        mInterestsEditTextView.setText(preferences.getString("sharedInterests", null));
        mMatchingAgeEditTextView.setText(preferences.getString("sharedAgeRange", null));





/*
        //Enable save button when there's text change
        mCollegeEditText.addTextChangedListener(watcher);
        mInterestsEditText.addTextChangedListener(watcher);
        mAgeEditText.addTextChangedListener(watcher);
*/
     //   mBioEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_BIO_LENGTH_LIMIT)});

        //Layout related code
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pushing values to database
               // mCollegeDatabaseReference.push().setValue(mCollegeEditText.getText().toString());
               // mInterestsDatabaseReference.push().setValue(mInterestsEditText.getText().toString());
               // mAgeDatabaseReference.push().setValue(mAgeEditText.getText().toString());

                //change visibility
                mNameEditTextView.setVisibility(View.INVISIBLE);
                mEmailEditTextView.setVisibility(View.INVISIBLE);
                mEducationEditTextView.setVisibility(View.INVISIBLE);
                mInterestsEditTextView.setVisibility(View.INVISIBLE);
                mMatchingAgeEditTextView.setVisibility(View.INVISIBLE);
                editProfileButton.setVisibility(View.INVISIBLE);

                //Change visibility
                mNameEdit.setVisibility(View.VISIBLE);
                mEmailEdit.setVisibility(View.VISIBLE);
                mEducationEdit.setVisibility(View.VISIBLE);
                mInterestsEdit.setVisibility(View.VISIBLE);
                mMatchingAgeEdit.setVisibility(View.VISIBLE);
                saveProfileButton.setVisibility(View.VISIBLE);

                //Set text to edit
                mNameEdit.setText(mNameEditTextView.getText().toString());
                mEmailEdit.setText(mEmailEditTextView.getText().toString());
                mEducationEdit.setText(mEducationEditTextView.getText().toString());
                mInterestsEdit.setText(mInterestsEditTextView.getText().toString());
                mMatchingAgeEdit.setText(mMatchingAgeEditTextView.getText().toString());


            }
        });
        final SharedPreferences pref = this.getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////change visibility
                mNameEditTextView.setVisibility(View.VISIBLE);
                mEmailEditTextView.setVisibility(View.VISIBLE);
                mEducationEditTextView.setVisibility(View.VISIBLE);
                mInterestsEditTextView.setVisibility(View.VISIBLE);
                mMatchingAgeEditTextView.setVisibility(View.VISIBLE);
                editProfileButton.setVisibility(View.VISIBLE);

                //Change visibility
                mNameEdit.setVisibility(View.INVISIBLE);
                mEmailEdit.setVisibility(View.INVISIBLE);
                mEducationEdit.setVisibility(View.INVISIBLE);
                mInterestsEdit.setVisibility(View.INVISIBLE);
                mMatchingAgeEdit.setVisibility(View.INVISIBLE);
                saveProfileButton.setVisibility(View.INVISIBLE);

                //set text to text views
                mNameEditTextView.setText(mNameEdit.getText().toString());
                mEmailEditTextView.setText(mEmailEdit.getText().toString());
                mEducationEditTextView.setText(mEducationEdit.getText().toString());
                mInterestsEditTextView.setText(mInterestsEdit.getText().toString());
                mMatchingAgeEditTextView.setText(mMatchingAgeEdit.getText().toString());

                //updating shared preferences
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("sharedName", mNameEdit.getText().toString());
                edit.putString("sharedEmail", mEmailEdit.getText().toString());
                edit.putString("sharedEducation", mEducationEdit.getText().toString());
                edit.putString("sharedInterests", mInterestsEdit.getText().toString());
                edit.putString("sharedAgeRange", mMatchingAgeEdit.getText().toString());
                edit.apply();

                //sending data to database
                //....

                mListener.onProfileSettingsSaveFragmentInteraction();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onProfileSettingsChangePasswordFragmentInteraction();
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onProfileSettingsSignOutFragmentInteraction();
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event-


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
           // if(s.toString().trim().length()>0){
             //   preferenceSaveButton.setEnabled(true);
          //  } else {
            //    preferenceSaveButton.setEnabled(false);
           // }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onProfileSettingsSaveFragmentInteraction();
        void onProfileSettingsChangePasswordFragmentInteraction();
        void onProfileSettingsSignOutFragmentInteraction();

    }


}
