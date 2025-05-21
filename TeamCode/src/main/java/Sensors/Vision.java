package Sensors;

import android.util.Size;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.SortOrder;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ImageRegion;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import java.util.ArrayList;
import java.util.List;

import Mechanisms.Pickup.Swivel;

public class Vision { //Prefix for commands

    public static ColorBlobLocatorProcessor colorLocatorRed;
    public static ColorBlobLocatorProcessor colorLocatorBlue;
    public static ColorBlobLocatorProcessor colorLocatorYellow;
    public static OpMode opmode; //opmode var innit

    private static boolean alignSwivelButtonCurrentlyPressed = false;
    private static boolean alignSwivelButtonPreviouslyPressed = false;

    public static VisionPortal portal;
    public static int[] pointsOverTime;

    public static double angle = 180.0; //sample for now
    public static final int exposureMillis = 5;
    public static double testTelemetry = 0.0;
    public static double degreeAngle = 0.0;

    public static void initVision(OpMode opmode) { //init motors

        colorLocatorYellow = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.YELLOW) // use a predefined color match
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY) // exclude blobs inside blobs
                .setRoi(
                        ImageRegion.asUnityCenterCoordinates(
                                -0.8,
                                0.9,
                                0.8,
                                -0.9
                        )
                ) // search central main area of camera view
                .setDrawContours(true) // Show contours on the Stream Preview
                .setBlurSize(5) // Smooth the transitions between different colors in image
                .build();

        colorLocatorBlue = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.BLUE) // use a predefined color match
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY) // exclude blobs inside blobs
                .setRoi(
                        ImageRegion.asUnityCenterCoordinates(
                                -0.8,
                                0.9,
                                0.8,
                                -0.9
                        )
                ) // search central main area of camera view
                .setDrawContours(true) // Show contours on the Stream Preview
                .setBlurSize(5) // Smooth the transitions between different colors in image
                .build();

        colorLocatorRed = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.RED) // use a predefined color match
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY) // exclude blobs inside blobs
                .setRoi(
                        ImageRegion.asUnityCenterCoordinates(
                                -0.8,
                                0.9,
                                0.8,
                                -0.9
                        )
                ) // search central main area of camera view
                .setDrawContours(true) // Show contours on the Stream Preview
                .setBlurSize(5) // Smooth the transitions between different colors in image
                .build();

        portal = new VisionPortal.Builder()
                .addProcessor(colorLocatorYellow)
                .addProcessor(colorLocatorBlue)
                .addProcessor(colorLocatorRed)
                .setCameraResolution(new Size(960, 720))
                .setCamera(opmode.hardwareMap.get(WebcamName.class, "Webcam"))
                .build();

        portal.setProcessorEnabled(colorLocatorYellow, true);
        portal.setProcessorEnabled(colorLocatorRed, true);
        portal.setProcessorEnabled(colorLocatorBlue, true);

        pointsOverTime = new int[0];

        Vision.opmode = opmode;
    }

    public static void updateVision() {

        alignSwivelButtonCurrentlyPressed = opmode.gamepad2.left_stick_button; //can change controls

        opmode.telemetry.addData("preview on/off", "... Camera Stream\n");

        // Read the current list
        List blobsYellow = colorLocatorYellow.getBlobs();
        List blobsRed = colorLocatorRed.getBlobs();
        List blobsBlue = colorLocatorBlue.getBlobs();
        List allBlobs = new ArrayList();
        allBlobs.addAll(blobsYellow);
        allBlobs.addAll(blobsRed);
        allBlobs.addAll(blobsBlue);

        ColorBlobLocatorProcessor.Util.filterByArea(
                100.0,
                15000.0,
                allBlobs
        ); // filter out very small or large blobs.

        ColorBlobLocatorProcessor.Util.sortByArea(
                SortOrder.DESCENDING,
                allBlobs
        );

        opmode.telemetry.addLine(" Area Density Aspect  Center");

        if (!allBlobs.isEmpty()) {
            RotatedRect boxFit = ((ColorBlobLocatorProcessor.Blob) allBlobs.get(0)).getBoxFit();

            Point leftMost = getMostLeftPoint(((ColorBlobLocatorProcessor.Blob) allBlobs.get(0)).getContourPoints());
            Point rightMost = getMostRightPoint(((ColorBlobLocatorProcessor.Blob) allBlobs.get(0)).getContourPoints());

            if (leftMost != null) {
                if (rightMost != null) {
                    degreeAngle = leftMost.y < rightMost.y ? //angled this way //
                            boxFit.angle + 90 : //angled this way \\
                            boxFit.angle;
                }
            }
            opmode.telemetry.addData("real angle ======", 7.0 / 1800.0 * (degreeAngle % 180) + 0.15);
        }

        if (alignSwivelButtonCurrentlyPressed && !alignSwivelButtonPreviouslyPressed) {
            pointsOverTime = new int[0];
            if (!allBlobs.isEmpty()) {
                Swivel.restingState = 7.0 / 1800.0 * (degreeAngle - 90 % 180) + 0.15;  //add -90 for new wrist rotation, might need to be +90
                //Swivel.restingState = 7.0/1800.0*(allBlobs.get(0).boxFit.angle%180.0)+0.15 //box of best fit
//                Swivel.restingState = 7.0/1800.0*((Math.atan(calculateSlope(((ColorBlobLocatorProcessor.Blob)allBlobs.get(0)).contourPoints))*180/Math.PI)%180.0)+0.15 //line of best fit of all points

            }
        }

        //yoinked from example code, might not work
        if (!allBlobs.isEmpty()) {
            RotatedRect boxFit = ((ColorBlobLocatorProcessor.Blob) allBlobs.get(0)).getBoxFit();
            Rect myHorizontalBoxFit = boxFit.boundingRect();
            org.opencv.core.Size angleOfSample = myHorizontalBoxFit.size();
            opmode.telemetry.addData("angles rotated size", angleOfSample);

            opmode.telemetry.addData("top left X", myHorizontalBoxFit.x);
            opmode.telemetry.addData("top left Y", myHorizontalBoxFit.y);
            opmode.telemetry.addData("width", myHorizontalBoxFit.width);
            opmode.telemetry.addData("height", myHorizontalBoxFit.height);
            //pointsOverTime (angleOfSample)

        }
//        if (alignSwivelButtonPreviouslyPressed && !alignSwivelButtonCurrentlyPressed) {
//            if (pointsOverTime.length != 0) {
//                //Swivel.restingState = 7.0/1800.0*((Math.atan(anglesOverTime.average())*180/Math.PI)%180.0)+0.15
//                //testTelemetry = 7.0/1800.0*((Math.atan(calculateSlope(pointsOverTime))*180/Math.PI)%180.0)+0.15
//                testTelemetry = linearRegression(pointsOverTime);
//            }
//        }

        alignSwivelButtonPreviouslyPressed = alignSwivelButtonCurrentlyPressed;

        opmode.telemetry.addData("avg over time", testTelemetry);
        if (!allBlobs.isEmpty()) {
            opmode.telemetry.addData("angles raw", ((ColorBlobLocatorProcessor.Blob) allBlobs.get(0)).getBoxFit().angle); //need to adjust
            opmode.telemetry.addData("angles", 7.0 / 1800.0 * (((ColorBlobLocatorProcessor.Blob) allBlobs.get(0)).getBoxFit().angle % 180) + 0.15);
            opmode.telemetry.update();
        } else {
            //opmode.telemetry.addData("none", "all angles");
        }

//        opmode.telemetry.update();

    }

    public static void stopVision() {
        portal.close();
    }


    private static Point getHighestPoint(Point[] points) {
        Point highest = null;
        for (Point pt : points) {
            if (pt != null) {
                if (highest == null || pt.y > highest.y) {
                    highest = pt;
                }
            }
        }
        return highest;
    }

    private static Point getMostLeftPoint(Point[] points) {
        Point leftMost = null;
        for (Point pt : points) {
            if (pt != null) {
                if (leftMost == null || pt.x < leftMost.x) {
                    leftMost = pt;
                }
            }
        }
        return leftMost;
    }

    private static Point getMostRightPoint(Point[] points) {
        Point rightMost = null;
        for (Point pt : points) {
            if (pt != null) {
                if (rightMost == null || pt.x > rightMost.x) {
                    rightMost = pt;
                }
            }
        }
        return rightMost;
    }
}
