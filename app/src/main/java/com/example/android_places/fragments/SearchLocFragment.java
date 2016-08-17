package com.example.android_places.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android_places.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchLocFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchLocFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchLocFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private CardView cvSelectLoc;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private final String TAG = SearchLocFragment.class.getSimpleName();
    private Activity mActivity;
    private TextView tvLocation;
    private Button btnProceed;
    private Place place = null;

    public static SearchLocFragment newInstance() {
        SearchLocFragment fragment = new SearchLocFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_loc, container, false);
        initView(v);
        setListners();
        return v;
    }

    private void setListners() {
        cvSelectLoc.setOnClickListener(this);
        btnProceed.setOnClickListener(this);
    }

    private void initView(View v) {
        place = null;
        mActivity = getActivity();
        cvSelectLoc = (CardView)v.findViewById(R.id.fragment_search_card_view);
        tvLocation = (TextView)v.findViewById(R.id.fragment_search__location);
        btnProceed = (Button)v.findViewById(R.id.fragment_search__btn_proceed);
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

    public void loadAutocompleteView() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(mActivity);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_search_card_view:
                loadAutocompleteView();
                break;
            case R.id.fragment_search__btn_proceed:
                redirectToMap(place);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == mActivity.RESULT_OK) {
                    place = PlaceAutocomplete.getPlace(mActivity, data);
                    tvLocation.setText(place.getAddress());
                    Log.i(TAG, "Place: " + place.getName());
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(mActivity, data);
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == mActivity.RESULT_CANCELED) {
                }
                break;
        }
    }

    private void redirectToMap(Place place) {
        if(place != null)
             mListener.onFragmentInteraction(place);
        else
            Toast.makeText(mActivity,mActivity.getResources().getString(R.string.select_location_first),Toast.LENGTH_SHORT).show();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Place place);
    }
}
