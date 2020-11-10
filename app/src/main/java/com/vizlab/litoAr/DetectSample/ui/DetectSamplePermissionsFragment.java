package com.vizlab.litoAr.DetectSample.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vizlab.litoAr.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetectSamplePermissionsFragment extends Fragment {
    // Arbitrary constant used as an Identifier to the permissions requests.
    private static final int DETECT_SAMPLE_PERMISSIONS_REQUEST_CODE = 42;
    // Array of camera permissions needed as specified in the manifest.
    private static final String[] DETECT_SAMPLE_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Holds an instance of the View.
    View view;

    public DetectSamplePermissionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        view = inflater.inflate(R.layout.fragment_detect_sample_permissions, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Makes sure the view is initialized before checking permissions.
        checkPermissions();
    }

    /**
     * Check if app already has camera access permission.
     * In case it doesn't, request it.
     * Then sets a lifecycle callback in onRequestPermissionsResult() to enable or disable camera.
     */
    private void checkPermissions() {
        if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

            // Navigates to the Sample Detector Fragment.
            //Navigation.findNavController(view).navigate(R.id.action_detectSamplePermissionsFragment_to_detectSampleFragment, null);
            Navigation.findNavController(view).navigate(R.id.action_detectSamplePermissionsFragment_to_dummyFragment, null);
        } else {
            // Requests Sample Collector permissions.
            requestPermissions(DETECT_SAMPLE_PERMISSIONS, DETECT_SAMPLE_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * In case the system send a request for permissions, calls this function and evaluates its result.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case DETECT_SAMPLE_PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                // Also checks whether all permissions have been granted.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permissions granted.", Toast.LENGTH_SHORT).show();

                    // Navigates to the Sample Collector's Home.
                    //Navigation.findNavController(view).navigate(R.id.action_detectSampleFragment_to_browseFilesFragment, null);
                    //Closes the activity.
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(),
                            "Permissions not granted.",
                            Toast.LENGTH_SHORT).show();

                    //Closes the activity.
                    getActivity().finish();
                }
            }
        }
    }
}