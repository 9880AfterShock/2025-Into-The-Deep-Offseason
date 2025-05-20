package Autos;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.localization.PoseUpdater;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Drawing;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "caden test auto")
public class TestAuto extends OpMode {

    private Telemetry telemetryA;

    public static PathChain line1;

    public static PathChain line2;

    public static PathChain line3;

    public static PathChain line4;

    public static PathChain line5;

    public static PathChain line6;
    private Follower follower;
    private PathChain fowardOneTile;
    private PathChain backwardOneTile;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;

    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        generatePath();
        telemetryA = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        Drawing.drawRobot(follower.getPose().getAsFTCStandardCoordinates(), "#4CAF50");
        Drawing.sendPacket();
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
    }

    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    public void loop() {
        follower.update();
        autonomousPathUpdate();
        telemetryA.addData("x:", follower.getPose().getX());
        telemetryA.addData("y:", follower.getPose().getY());
        telemetryA.addData("state: ", pathState);
        telemetryA.update();
        Drawing.drawRobot(follower.getPose().getAsFTCStandardCoordinates(), "#4CAF50");
        Drawing.drawRobot(follower.getClosestPose().getAsFTCStandardCoordinates(), "#0000FF");
        Drawing.drawPath(follower.getCurrentPath(), "#000000");
        Drawing.sendPacket();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(line1);
                setPathState(1);
                break;
            case 1:

                /* You could check for
                - Follower State: "if(!follower.isBusy() {}"
                - Time: "if(pathTimer.getElapsedTimeSeconds() > 1) {}"
                - Robot Position: "if(follower.getPose().getX() > 36) {}"
                */

                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the tile's position */
                if (!follower.isBusy()) {
                    follower.followPath(line2);
                    setPathState(2);
                }
                break;
            case 2:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the end position */
                if(!follower.isBusy()) {
                    follower.followPath(line3);
                    setPathState(3);
                }
                break;
            case 3:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the end position */
                if(!follower.isBusy()) {
                    follower.followPath(line4);
                    setPathState(4);
                }
                break;
            case 4:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the end position */
                if(!follower.isBusy()) {
                    follower.followPath(line5);
                    setPathState(5);
                }
                break;
            case 5:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the end position */
                if(!follower.isBusy()) {
                    follower.followPath(line6);
                    setPathState(6);
                }
                break;
        }
    }
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    public void generatePath() {
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        follower.setStartingPose(new Pose(8.370, 56.732,Math.toRadians(-90)));

        line1 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(8.370, 56.732, Point.CARTESIAN),
                                new Point(6.820, 31.311, Point.CARTESIAN),
                                new Point(83.703, 35.186, Point.CARTESIAN),
                                new Point(76.728, 29.761, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(-90))
                .build();

        line2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(76.728, 29.761, Point.CARTESIAN),
                                new Point(18.911, 24.646, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(-90))
                .build();

        line3 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(18.911, 24.646, Point.CARTESIAN),
                                new Point(74.713, 20.461, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(-90))
                .build();

        line4 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(74.713, 20.461, Point.CARTESIAN),
                                new Point(19.221, 15.501, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(-90))
                .build();

        line5 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(19.221, 15.501, Point.CARTESIAN),
                                new Point(77.813, 9.455, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(-90))
                .build();


        line6 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(77.813, 9.455, Point.CARTESIAN),
                                new Point(19.531, 10.075, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(-90))
                .build();
    }
}