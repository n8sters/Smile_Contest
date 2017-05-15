package com.apps.ncpow.smile_contest;

/**
 * Created by ncpow on 5/12/2017.
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

class Winner {

    private static final float CROWN_SCALE_FACTOR = .9f;

    /**
     * Method for detecting faces in a bitmap, and drawing crown depending on the facial
     * expression.
     */
    static Bitmap detectFacesandOverlayWinner(Context context, Bitmap picture) {

        // Create the face detector, disable tracking and enable classifications
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(picture).build();

        // Detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        // Initialize result bitmap to original picture
        Bitmap resultBitmap = picture;

        // If there are no faces detected, show a Toast message
        if (faces.size() == 0) {
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        } else {

            // init winning face
            Face winner = getWinningFace(faces);

            // overlay crown
            Bitmap winnerBitmap;
            winnerBitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.crown);

            // Add the winnerBitmap to the proper position in the original image
            resultBitmap = addBitmapToFace(resultBitmap, winnerBitmap, winner);

        }

        // Release the detector
        detector.release();

        return resultBitmap;
    }

    /**
     * This method takes in the array of faces and selects the face with the highest
     * smile probability, and returns that face.
     * */
    private static Face getWinningFace(SparseArray<Face> faces ) {
        Face temp = faces.valueAt(0);
        for ( int i = 0; i < faces.size(); i++ ) {

            // if the current face has a higher smiling probablity, then it is now the winner
            if ( temp.getIsSmilingProbability() < faces.valueAt(i).getIsSmilingProbability()) {
                temp = faces.valueAt(i);
            }
        }
        return temp;
    }


     // Combines the original picture with the crown bitmap
    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap winnerBitmap, Face face) {

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the crown so it looks better on the face
        float scaleFactor = CROWN_SCALE_FACTOR;

        // Determine the size of the crown to match the width of the face and preserve aspect ratio
        int newWinnerWidth = (int) (face.getWidth() * scaleFactor);
        int newWinnerHeight = (int) (winnerBitmap.getHeight() *
                newWinnerWidth / winnerBitmap.getWidth() * scaleFactor);


        // Scale the crown
        winnerBitmap = Bitmap.createScaledBitmap(winnerBitmap, newWinnerWidth, newWinnerHeight, false);

        // Determine the crown position so it best lines up with the face
        float crownPositionX =
                (face.getPosition().x + face.getWidth() / 2) - winnerBitmap.getWidth() / 2;
        float crownPositionY =
                (face.getPosition().y + face.getHeight() / 3) - winnerBitmap.getHeight() / 1;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(winnerBitmap, crownPositionX, crownPositionY, null);

        return resultBitmap;
    }

}