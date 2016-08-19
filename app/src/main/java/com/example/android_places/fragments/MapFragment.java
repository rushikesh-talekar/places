package com.example.android_places.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android_places.R;
import com.example.android_places.adapter.PhotosListAdapter;
import com.example.android_places.utils.AttributedPhoto;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PLACE = "place_id";
    private final String TAG = MapFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String placeId;
    private Activity mActivity;
    private Place place;
    private RecyclerView recyclerView;


    public MapFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public MapFragment(Place place) {
        this.place = place;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        mActivity = getActivity();
        SupportMapFragment mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map_container, mSupportMapFragment).commit();
            mSupportMapFragment.getMapAsync(this);
        }

        // Photos List

        recyclerView = (RecyclerView)v.findViewById(R.id.fragment_map_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
         Marker marker;
        marker = googleMap.addMarker(new MarkerOptions()
                .position(place.getLatLng())
                .title(place.getName().toString()));
        marker.setTag(0);
        moveToCurrentLocation(googleMap,place.getLatLng());
        placePhotosTask();
    }

    private void placePhotosTask() {
        final String placeId = place.getId(); // Australian Cruise Group

        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        PhotoTask task = new PhotoTask(mActivity,100, 100);
        task.execute(placeId);
    }

    private void moveToCurrentLocation(GoogleMap googleMap,LatLng currentLocation)
    {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

    }

    private void saveImage(Bitmap finalBitmap) {
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(isSDPresent) {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root);
            myDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = place.getName()+"_"+ n + ".jpg";
            File file = new File(myDir, fname);
            try {
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                Toast.makeText(mActivity,mActivity.getResources().getString(R.string.img_saved),Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mActivity,mActivity.getResources().getString(R.string.sdcard_error),Toast.LENGTH_LONG).show();
        }
    }


    //**********************AsyncTask ******************************//
    public class PhotoTask extends AsyncTask<String, Void, ArrayList<AttributedPhoto>> {

        private final Context mContext;
        private int mHeight;

        private int mWidth;
        private GoogleApiClient mGoogleApiClient;
        private ProgressDialog dialog;

        public PhotoTask(Context context, int width, int height) {
            mContext = context;
            mHeight = height;
            mWidth = width;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mContext);
            dialog.setTitle(mActivity.getResources().getString(R.string.loading));
            dialog.setCancelable(false);
            dialog.show();
        }

        /**
         * Loads the first photo for a place id from the Geo Data API.
         * The place id must be the first (and only) parameter.
         */
        @Override
        protected ArrayList<AttributedPhoto> doInBackground(String... params) {
            ArrayList<AttributedPhoto> list= new ArrayList<>();
            if (params.length != 1) {
                return null;
            }
            final String placeId = params[0];
            mGoogleApiClient = new GoogleApiClient
                    .Builder(mContext)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
            mGoogleApiClient.connect();
            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    for (int i = 0; i <photoMetadataBuffer.getCount() ; i++) {
                        // Get the first bitmap and its attributions.
                        PlacePhotoMetadata photo = photoMetadataBuffer.get(i);
                        CharSequence attribution = photo.getAttributions();
                        // Load a scaled bitmap for this photo.
                        Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                                .getBitmap();

                        AttributedPhoto attributedPhoto = new AttributedPhoto(attribution, image);
                        list.add(attributedPhoto);
                    }
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<AttributedPhoto> list) {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
            mGoogleApiClient.disconnect();
            if(!list.isEmpty()) {
                addAdapter(list);
            }
        }
    }

    private void addAdapter(ArrayList<AttributedPhoto> list) {
        //Snakbar to guide user
        Snackbar snackbar = Snackbar
                .make(mActivity.findViewById(android.R.id.content), mActivity.getResources().getString(R.string.snakbar_text), Snackbar.LENGTH_LONG);

        snackbar.show();

        PhotosListAdapter photosListAdapter = new PhotosListAdapter(new PhotosListAdapter.OnItemClickListner() {
            @Override
            public void onItemClicked(AttributedPhoto attributedPhoto) {
                saveImage(attributedPhoto.bitmap);
            }
        },mActivity,list);
        recyclerView.setAdapter(photosListAdapter);
    }
}
