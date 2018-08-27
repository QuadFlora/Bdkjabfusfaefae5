package co.bingleapp.bingle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import co.bingleapp.bingle.Model.OrderStatus;
import co.bingleapp.bingle.Model.Orientation;
import co.bingleapp.bingle.Model.TimeLineModel;
import co.bingleapp.bingle.Utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Notifications.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Notifications#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Notifications extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    public final static String EXTRA_ORIENTATION = "EXTRA_ORIENTATION";
    public final static String EXTRA_WITH_LINE_PADDING = "EXTRA_WITH_LINE_PADDING";

    /*
    @BindView(R.id.verticalTimeLineButton)
    Button mVerticalTimeLineButton;
    @BindView(R.id.verticalTimeLineButtonWPadding)
    Button mVerticalTimeLineButtonWPadding;
    @BindView(R.id.horizontalTimeLineButton)
    Button mHorizontalTimeLineButton;
    @BindView(R.id.horizontalTimeLineButtonWPadding)
    Button mHorizontalTimeLineButtonWPadding;
    */


    private OnFragmentInteractionListener mListener;

    public Notifications() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Notifications.
     */
    // TODO: Rename and change types and number of parameters
    public static Notifications newInstance(String param1, String param2) {
        Notifications fragment = new Notifications();
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
        View rootView =  inflater.inflate(R.layout.fragment_notifications, container, false);
        mOrientation = (Orientation) getActivity().getIntent().getSerializableExtra(Notifications.EXTRA_ORIENTATION);
        mWithLinePadding = getActivity().getIntent().getBooleanExtra(Notifications.EXTRA_WITH_LINE_PADDING, false);

      //  setTitle(mOrientation == Orientation.HORIZONTAL ? getResources().getString(R.string.horizontal_timeline) : getResources().getString(R.string.vertical_timeline));

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewNotifications);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        if(((AppCompatActivity)getActivity()).getSupportActionBar()!=null)
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        ButterKnife.bind(this);

        mVerticalTimeLineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(Orientation.VERTICAL, false);
            }
        });

        mHorizontalTimeLineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(Orientation.HORIZONTAL, false);
            }
        });

        mVerticalTimeLineButtonWPadding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(Orientation.VERTICAL, true);
            }
        });

        mHorizontalTimeLineButtonWPadding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(Orientation.HORIZONTAL, true);
            }
        });
        */

        initView();
        return rootView;
    }

    /*
    private void onButtonClick(Orientation orientation, boolean withLinePadding) {
        Intent intent = new Intent(this, TimeLineActivity.class);
        intent.putExtra(EXTRA_ORIENTATION, orientation);
        intent.putExtra(EXTRA_WITH_LINE_PADDING, withLinePadding);
        startActivity(intent);
    }
    */

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onNotificationsFragmentInteraction(uri);
        }
    }

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
        void onNotificationsFragmentInteraction(Uri uri);
    }

    private LinearLayoutManager getLinearLayoutManager() {
        if (mOrientation == Orientation.HORIZONTAL) {
            return new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        } else {
            return new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        }
    }

    private void initView() {
        setDataListItems();
        mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }

    private void setDataListItems(){
        mDataList.add(new TimeLineModel("Item successfully delivered", "", OrderStatus.INACTIVE));
        mDataList.add(new TimeLineModel("Courier is out to delivery your order", "2017-02-12 08:00", OrderStatus.ACTIVE));
        mDataList.add(new TimeLineModel("Item has reached courier facility at New Delhi", "2017-02-11 21:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Item has been given to the courier", "2017-02-11 18:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Item is packed and will dispatch soon", "2017-02-11 09:30", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order is being readied for dispatch", "2017-02-11 08:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order processing initiated", "2017-02-10 15:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order confirmed by seller", "2017-02-10 14:30", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order placed successfully", "2017-02-10 14:00", OrderStatus.COMPLETED));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Menu
        switch (item.getItemId()) {
            //When home is clicked
            case android.R.id.home:
                this.getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if(mOrientation!=null)
            savedInstanceState.putSerializable(Notifications.EXTRA_ORIENTATION, mOrientation);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Notifications.EXTRA_ORIENTATION)) {
                mOrientation = (Orientation) savedInstanceState.getSerializable(Notifications.EXTRA_ORIENTATION);
            }
        }
        super.onViewStateRestored(savedInstanceState);
    }
}
