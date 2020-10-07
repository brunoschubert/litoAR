package com.vizlab.litoAr.DetectSample.renderer;

import android.content.Context;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Pose;

import java.io.IOException;

/** Renders an augmented image. */
public class AugmentedImageRenderer {
    private static final String TAG = "AugmentedImageRenderer";

    private static final float TINT_INTENSITY = 0.1f;
    private static final float TINT_ALPHA = 1.0f;
    private static final int[] TINT_COLORS_HEX = {
            0x000000, 0xF44336, 0xE91E63, 0x9C27B0, 0x673AB7, 0x3F51B5, 0x2196F3, 0x03A9F4, 0x00BCD4,
            0x009688, 0x4CAF50, 0x8BC34A, 0xCDDC39, 0xFFEB3B, 0xFFC107, 0xFF9800,
    };

    private final ObjectRenderer andyRenderer = new ObjectRenderer();

    private final ObjectRenderer mazeRenderer = new ObjectRenderer();

    // Loads the Frame object pieces
    private final ObjectRenderer imageFrameUpperLeft = new ObjectRenderer();
    private final ObjectRenderer imageFrameUpperRight = new ObjectRenderer();
    private final ObjectRenderer imageFrameLowerLeft = new ObjectRenderer();
    private final ObjectRenderer imageFrameLowerRight = new ObjectRenderer();

    public AugmentedImageRenderer() {}

    public void createOnGlThread(Context context) throws IOException {

        mazeRenderer.createOnGlThread(
                context, "models/green-maze/GreenMaze.obj", "models/frame_base.png");
        mazeRenderer.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

//        andyRenderer.createOnGlThread(
//                context, "models/andy.obj", "models/andy.png");
//        andyRenderer.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

//    imageFrameUpperLeft.createOnGlThread(
//        context, "models/frame_upper_left.obj", "models/frame_base.png");
//    imageFrameUpperLeft.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
//    imageFrameUpperLeft.setBlendMode(BlendMode.SourceAlpha);
//
//    imageFrameUpperRight.createOnGlThread(
//        context, "models/frame_upper_right.obj", "models/frame_base.png");
//    imageFrameUpperRight.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
//    imageFrameUpperRight.setBlendMode(BlendMode.SourceAlpha);
//
//    imageFrameLowerLeft.createOnGlThread(
//        context, "models/frame_lower_left.obj", "models/frame_base.png");
//    imageFrameLowerLeft.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
//    imageFrameLowerLeft.setBlendMode(BlendMode.SourceAlpha);
//
//    imageFrameLowerRight.createOnGlThread(
//        context, "models/frame_lower_right.obj", "models/frame_base.png");
//    imageFrameLowerRight.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
//    imageFrameLowerRight.setBlendMode(BlendMode.SourceAlpha);
    }


    public void draw(
            float[] viewMatrix,
            float[] projectionMatrix,
            AugmentedImage augmentedImage,
            Anchor centerAnchor,
            float[] colorCorrectionRgba) {
        float[] tintColor =
                convertHexToColor(TINT_COLORS_HEX[augmentedImage.getIndex() % TINT_COLORS_HEX.length]);

        final float maze_edge_size = 492.65f; // Magic number of maze size
        final float max_image_edge = Math.max(augmentedImage.getExtentX(), augmentedImage.getExtentZ()); // Get largest detected image edge size

        Pose anchorPose = centerAnchor.getPose();

        float mazsScaleFactor = max_image_edge / maze_edge_size; // scale to set Maze to image size
        float[] modelMatrix = new float[16];

        // OpenGL Matrix operation is in the order: Scale, rotation and Translation
        // So the manual adjustment is after scale
        // The 251.3f and 129.0f is magic number from the maze obj file
        // We need to do this adjustment because the maze obj file
        // is not centered around origin. Normally when you
        // work with your own model, you don't have this problem.
        Pose mozeModelLocalOffset = Pose.makeTranslation(
                -251.3f * mazsScaleFactor,
                0.0f,
                129.0f * mazsScaleFactor);
        anchorPose.compose(mozeModelLocalOffset).toMatrix(modelMatrix, 0);
        mazeRenderer.updateModelMatrix( modelMatrix, mazsScaleFactor, mazsScaleFactor/10.0f, mazsScaleFactor);
        mazeRenderer.draw(viewMatrix, projectionMatrix, colorCorrectionRgba, tintColor);

        // In draw() function, at the end add code to display the Andy, standing on top of the maze
//        Pose andyModelLocalOffset = Pose.makeTranslation(
//                0.0f,
//                0.1f,
//                0.0f);
//        anchorPose.compose(andyModelLocalOffset).toMatrix(modelMatrix, 0);
//        andyRenderer.updateModelMatrixAndy(modelMatrix, 0.05f); // 0.05f is a Magic number to scale
//        andyRenderer.draw(viewMatrix, projectionMatrix, colorCorrectionRgba, tintColor);

    }

//  public void draw(
//      float[] viewMatrix,
//      float[] projectionMatrix,
//      AugmentedImage augmentedImage,
//      Anchor centerAnchor,
//      float[] colorCorrectionRgba) {
//    float[] tintColor =
//        convertHexToColor(TINT_COLORS_HEX[augmentedImage.getIndex() % TINT_COLORS_HEX.length]);
//
//    Pose[] localBoundaryPoses = {
//      Pose.makeTranslation(
//          -0.5f * augmentedImage.getExtentX(),
//          0.0f,
//          -0.5f * augmentedImage.getExtentZ()), // upper left
//      Pose.makeTranslation(
//          0.5f * augmentedImage.getExtentX(),
//          0.0f,
//          -0.5f * augmentedImage.getExtentZ()), // upper right
//      Pose.makeTranslation(
//          0.5f * augmentedImage.getExtentX(),
//          0.0f,
//          0.5f * augmentedImage.getExtentZ()), // lower right
//      Pose.makeTranslation(
//          -0.5f * augmentedImage.getExtentX(),
//          0.0f,
//          0.5f * augmentedImage.getExtentZ()) // lower left
//    };
//
//    Pose anchorPose = centerAnchor.getPose();
//    Pose[] worldBoundaryPoses = new Pose[4];
//    for (int i = 0; i < 4; ++i) {
//      worldBoundaryPoses[i] = anchorPose.compose(localBoundaryPoses[i]);
//    }
//
//    float scaleFactor = 1.0f;
//    float[] modelMatrix = new float[16];
//
//    worldBoundaryPoses[0].toMatrix(modelMatrix, 0);
//    imageFrameUpperLeft.updateModelMatrix(modelMatrix, scaleFactor);
//    imageFrameUpperLeft.draw(viewMatrix, projectionMatrix, colorCorrectionRgba, tintColor);
//
//    worldBoundaryPoses[1].toMatrix(modelMatrix, 0);
//    imageFrameUpperRight.updateModelMatrix(modelMatrix, scaleFactor);
//    imageFrameUpperRight.draw(viewMatrix, projectionMatrix, colorCorrectionRgba, tintColor);
//
//    worldBoundaryPoses[2].toMatrix(modelMatrix, 0);
//    imageFrameLowerRight.updateModelMatrix(modelMatrix, scaleFactor);
//    imageFrameLowerRight.draw(viewMatrix, projectionMatrix, colorCorrectionRgba, tintColor);
//
//    worldBoundaryPoses[3].toMatrix(modelMatrix, 0);
//    imageFrameLowerLeft.updateModelMatrix(modelMatrix, scaleFactor);
//    imageFrameLowerLeft.draw(viewMatrix, projectionMatrix, colorCorrectionRgba, tintColor);
//  }

    private static float[] convertHexToColor(int colorHex) {
        // colorHex is in 0xRRGGBB format
        float red = ((colorHex & 0xFF0000) >> 16) / 255.0f * TINT_INTENSITY;
        float green = ((colorHex & 0x00FF00) >> 8) / 255.0f * TINT_INTENSITY;
        float blue = (colorHex & 0x0000FF) / 255.0f * TINT_INTENSITY;
        return new float[] {red, green, blue, TINT_ALPHA};
    }
}
