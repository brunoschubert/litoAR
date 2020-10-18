package com.vizlab.litoAr.DetectSample.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.vizlab.litoAr.DetectSample.renderer.AugmentedImageRenderer;
import com.vizlab.litoAr.DetectSample.renderer.BackgroundRenderer;
import com.vizlab.litoAr.DetectSample.renderer.ObjectRendererAlt;
import com.vizlab.litoAr.DetectSample.renderer.ObjectRendererAltA;
import com.vizlab.litoAr.DetectSample.utils.CameraPermissionUtils;
import com.vizlab.litoAr.DetectSample.utils.DisplayRotationUtils;
import com.vizlab.litoAr.DetectSample.utils.TrackingHelperUtils;
import com.vizlab.litoAr.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetectSampleFragment extends Fragment implements GLSurfaceView.Renderer{
    private static final String ERROR_TAG = DetectSampleFragment.class.getSimpleName();

    // Creates the Renderers and initialize once the GL surface is created.
    private GLSurfaceView surfaceView;

    // Rendering Classes
    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    //TODO:
    //private final AugmentedImageRenderer augmentedImageRenderer = new AugmentedImageRenderer();

    // Tracks display rotation.
    private DisplayRotationUtils displayRotationHelper;
    // Provides the user suggested actions to better track the image.
    private final TrackingHelperUtils trackingStateHelper = new TrackingHelperUtils(getActivity());

    // Sessions provide a mechanism to store user-visible groups of related stream data in a useful
    // and shareable manner, and allows for easy querying of the data in a detailed or
    // aggregated fashion
    private Session session;
    private boolean shouldConfigureSession = false;

    // Checks whether or not ARCore has been requested.
    private boolean installRequested;

    // Augmented image configuration and rendering.
    // Load a single image (true) or a pre-generated image database (false).
    // TODO: This loads the image database
    private final boolean useSingleImage = true;
    // Augmented image and its associated center pose anchor, keyed by index of the augmented image
    // in the database.
    private final Map<Integer, Pair<AugmentedImage, Anchor>> augmentedImageMap = new HashMap<>();

    View view;
    ImageView actionBar;
    Button browseFiles;

    //private static final String sampleAre = "arec/ARE.obj";

    private static final String sampleAre = "Carbonatonew/Amostra_ara.obj";
    private static final String sampleCarb = "Tabanew/ARE.obj";

    //private static final String sampleAre = "are/ARE.obj";
    // static final String sampleAre = "ara/Amostra_ara.obj";
    //private static final String sampleAre = "green-maze/GreenMaze.obj";
    //private static final String sampleAre = "Lexus/lexus_hs.obj";

    //private final ObjectRendererAlt sampleAreRender = new ObjectRendererAlt(sampleAre);
    private final ObjectRendererAltA sampleAreRender = new ObjectRendererAltA(sampleAre);
    private final ObjectRendererAltA sampleCarbRender = new ObjectRendererAltA(sampleCarb);


    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] anchorMatrix = new float[16];

    public DetectSampleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detect_sample, container, false);

        surfaceView = view.findViewById(R.id.GLSurfaceView);
        displayRotationHelper = new DisplayRotationUtils(getActivity().getApplicationContext());

        // Create and set up the renderer and render modes.
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setEGLContextClientVersion(2);
        // Alpha used for plane blending.
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        surfaceView.setRenderer(this);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        surfaceView.setWillNotDraw(false);

        installRequested = false;

        actionBar = view.findViewById(R.id.lower_action_bar);
        actionBar.setVisibility(View.GONE);

        browseFiles = view.findViewById(R.id.btn_browse_files);
        browseFiles.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(getActivity(), !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionUtils.hasCameraPermission(getActivity())) {
                    CameraPermissionUtils.requestCameraPermission(getActivity());
                    return;
                }

                session = new Session(getActivity().getApplicationContext());

            } catch (UnavailableArcoreNotInstalledException
                    | UnavailableUserDeclinedInstallationException e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update this app";
                exception = e;
            } catch (Exception e) {
                message = "This device does not support AR";
                exception = e;
            }

            if (message != null) {
                Toast.makeText(getActivity(), message,
                        Toast.LENGTH_LONG).show();
                exception.printStackTrace();
                return;
            }

            shouldConfigureSession = true;
        }

        if (shouldConfigureSession) {
            configureSession();
            shouldConfigureSession = false;
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            session.resume();
        } catch (CameraNotAvailableException e) {
            Toast.makeText(getActivity(), "Camera not available. Try restarting the app.",
                    Toast.LENGTH_LONG).show();
            session = null;
            return;
        }
        surfaceView.onResume();
        displayRotationHelper.onResume();

        //TODO: ImageView should be here?
        //fitToScanView.setVisibility(View.VISIBLE);
        actionBar.setVisibility(View.GONE);
        browseFiles.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            displayRotationHelper.onPause();
            surfaceView.onPause();
            session.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (!CameraPermissionUtils.hasCameraPermission(getActivity())) {
            Toast.makeText(getActivity(), "Camera permissions are needed to run this application",
                    Toast.LENGTH_LONG).show();
            if (!CameraPermissionUtils.shouldShowRequestPermissionRationale(getActivity())) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionUtils.launchPermissionSettings(getActivity());
            }
            getActivity().finish();
        }
    }



    // OPENGL OVERRIDEN METHODS ------------------------------------
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
        try {
            // Create the texture and pass it to ARCore session to be filled during update().
            backgroundRenderer.createOnGlThread(getActivity().getApplicationContext());
            //augmentedImageRenderer.createOnGlThread(getActivity().getApplicationContext());
            //TODO: Use AugmentedRender as Interface
            sampleCarbRender.createOnGlThread(getActivity().getApplicationContext());
            sampleCarbRender.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            sampleAreRender.createOnGlThread(getActivity().getApplicationContext());
            sampleAreRender.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
        } catch (IOException e) {
            Log.e("ERROAR", "Failed to read obj file.");
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        displayRotationHelper.onSurfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (session == null) {
            return;
        }
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session);

        try{
            session.setCameraTextureName(backgroundRenderer.getTextureId());

            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            Frame frame = session.update();
            Camera camera = frame.getCamera();

            // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
            trackingStateHelper.updateKeepScreenOnFlag(camera.getTrackingState());

            // If frame is ready, render camera preview image to the GL surface.
            backgroundRenderer.draw(frame);

            // Get projection matrix.
            float[] projmtx = new float[16];
            camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

            // Get camera matrix and draw.
            float[] viewmtx = new float[16];
            camera.getViewMatrix(viewmtx, 0);

            // Compute lighting from average intensity of the image.
            final float[] colorCorrectionRgba = new float[4];
            frame.getLightEstimate().getColorCorrection(colorCorrectionRgba, 0);

            //TODO: Should this be here
            // Compute lighting from average intensity of the image.
            final float lightIntensity = frame.getLightEstimate().getPixelIntensity();

            // Visualize augmented images.
            //TODO: Light estimate
            drawAugmentedImages(frame, projmtx, viewmtx, colorCorrectionRgba, lightIntensity);

        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(ERROR_TAG, "Exception on the OpenGL thread", t);
        }
    }
    // -------------------------------------------------------------
    // ARCore METHODS --------------------------------------------
    private void drawAugmentedImages(
            Frame frame, float[] projmtx, float[] viewmtx, float[] colorCorrectionRgba, float lightIntensity){
        Collection<AugmentedImage> updatedAugmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);

        // Iterate to update augmentedImageMap, remove elements we cannot draw.
        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            switch (augmentedImage.getTrackingState()) {
                case PAUSED:
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
                    String message = String.format("Detected Image %d", augmentedImage.getIndex());
//                    Toast.makeText(getActivity(), message,
//                            Toast.LENGTH_LONG).show();
                    Log.e("ERRORAR", message);
                    break;

                case TRACKING:
                    // Have to switch to UI Thread to update View.
                    Log.e("ERRORAR", "DETECTED");
                    getActivity().runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    //TODO: Do we need this image view?
                                    //fitToScanView.setVisibility(View.GONE);
                                    actionBar.setVisibility(View.VISIBLE);
                                    browseFiles.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            //TODO: Show the Image name being Tracked on the UI
                                            // So that we know what to Load.
                                            Log.e("ID", "Augmented ID is: " + augmentedImage.getIndex() + " // Name is: + " + augmentedImage.getName());
                                            Navigation.findNavController(view).navigate(R.id.action_detectSampleFragment_to_browseFilesFragment, null);
                                        }
                                    });
                                    browseFiles.setVisibility(View.VISIBLE);
                                }
                            });

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage.getIndex())) {
                        Anchor centerPoseAnchor = augmentedImage.createAnchor(augmentedImage.getCenterPose());
                        augmentedImageMap.put(
                                augmentedImage.getIndex(), Pair.create(augmentedImage, centerPoseAnchor));
                    }
                    break;

                case STOPPED:
                    augmentedImageMap.remove(augmentedImage.getIndex());
                    Log.e("ERRORAR", "REMOVED");
                    break;

                default:
                    break;
            }
        }

        // Draw all images in augmentedImageMap
        for (Pair<AugmentedImage, Anchor> pair : augmentedImageMap.values()) {
            AugmentedImage augmentedImage = pair.first;
            Anchor centerAnchor = augmentedImageMap.get(augmentedImage.getIndex()).second;
            switch (augmentedImage.getTrackingState()) {
                case TRACKING:
                    //TODO: Check matrix
//                    final float max_image_edge = Math.max(augmentedImage.getExtentX(), augmentedImage.getExtentZ());
//                    final float maze_edge_size = 492.65f;
//                    Pose anchorPose = centerAnchor.getPose();
//                    float mazsScaleFactor = max_image_edge / maze_edge_size;
//                    Pose mozeModelLocalOffset = Pose.makeTranslation(
//                            -251.3f * mazsScaleFactor,
//                            0.0f,
//                            129.0f * mazsScaleFactor);
//                    anchorPose.compose(mozeModelLocalOffset).toMatrix(anchorMatrix, 0);
//                    sampleAreRender.updateModelMatrix(anchorMatrix, 10f);
//                    sampleAreRender.draw(viewmtx, projmtx, lightIntensity);


//                    Pose anchorPose = centerAnchor.getPose();
//                    anchorPose.toMatrix(anchorMatrix, 0);

                    centerAnchor.getPose().toMatrix(anchorMatrix, 0);
                    if(augmentedImage.getIndex() == 0) {
                        sampleAreRender.updateModelMatrix(anchorMatrix, 1f);
                        sampleAreRender.draw(viewmtx, projmtx, lightIntensity);
                    }
                    if(augmentedImage.getIndex() == 1) {
                        sampleCarbRender.updateModelMatrix(anchorMatrix, 1f);
                        sampleCarbRender.draw(viewmtx, projmtx, lightIntensity);
                    }
                    //TODO: EndOf
//                    augmentedImageRenderer.draw(
//                            viewmtx, projmtx, augmentedImage, centerAnchor, colorCorrectionRgba);
                    Log.e("ERRORAR", "DRAW " + augmentedImage.getIndex());
                    break;

                default:
                    break;
            }
        }

    }
    // -------------------------------------------------------------
    // ACTIVITY METHODS --------------------------------------------

    private void configureSession() {
        Config config = new Config(session);
        config.setFocusMode(Config.FocusMode.AUTO);
        if (!setupAugmentedImageDatabase(config)) {
            Toast.makeText(getActivity(), "Could not setup augmented image database",
                    Toast.LENGTH_LONG).show();
        }
        session.configure(config);
    }

    private boolean setupAugmentedImageDatabase(Config config) {
        AugmentedImageDatabase augmentedImageDatabase;

        // There are two ways to configure an AugmentedImageDatabase:
        // 1. Add Bitmap to DB directly
        // 2. Load a pre-built AugmentedImageDatabase
        // Option 2) has
        // * shorter setup time
        // * doesn't require images to be packaged in apk.
        if (useSingleImage) {
            Bitmap augmentedImageBitmap = loadAugmentedImageBitmap();
            if (augmentedImageBitmap == null) {
                return false;
            }

            augmentedImageDatabase = new AugmentedImageDatabase(session);
            augmentedImageDatabase.addImage("image_name", augmentedImageBitmap);

            //TODO: ADD THE SECOND
            augmentedImageBitmap = loadSecondAugmentedImageBitmap();
            if (augmentedImageBitmap == null) {
                return false;
            }
            augmentedImageDatabase.addImage("image_qr2", augmentedImageBitmap);
            // If the physical size of the image is known, you can instead use:
            //     augmentedImageDatabase.addImage("image_name", augmentedImageBitmap, widthInMeters);
            // This will improve the initial detection speed. ARCore will still actively estimate the
            // physical size of the image as it is viewed from multiple viewpoints.
        } else {
            // This is an alternative way to initialize an AugmentedImageDatabase instance,
            // load a pre-existing augmented image database.
            try (InputStream is = getActivity().getAssets().open("sample_database.imgdb")) {
                augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, is);
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Could not setup augmented image database",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

        config.setAugmentedImageDatabase(augmentedImageDatabase);
        return true;
    }

    private Bitmap loadAugmentedImageBitmap() {
        // TODO: CRITICAL - Once you pass the BITMAP you also pass the Model.
        // CREATE THE RENDERS CLASSES FOR EACH MODEL
        // ADD INDEX TO RENDER CLASSES
        // RENDER INDEX EQUALS THE AUGMENTED IMAGE INDEX INSIDE THE DB
        try (InputStream is = getActivity().getAssets().open("qr1.png")) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Could not setup augmented image database",
                    Toast.LENGTH_LONG).show();
        }
        return null;
    }

    private Bitmap loadSecondAugmentedImageBitmap() {
        try (InputStream is = getActivity().getAssets().open("qr2.png")) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Could not setup augmented image database",
                    Toast.LENGTH_LONG).show();
        }
        return null;
    }

    // -------------------------------------------------------------
}