import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.localization.PoseUpdater;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.util.Constants;
import com.pedropathing.util.DashboardPoseTracker;
import com.pedropathing.util.Drawing;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "Test Auto")
public class TestAuto extends OpMode {

    private Follower follower;

    private PoseUpdater poseUpdater;
    private DashboardPoseTracker dashboardPoseTracker;

    private Telemetry telemetryA;

    @Override
    public void init() {
        poseUpdater = new PoseUpdater(hardwareMap, FConstants.class, LConstants.class);
        telemetryA = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        dashboardPoseTracker = new DashboardPoseTracker(poseUpdater);
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        follower.setStartingPose(new Pose(11,90,0));
        Drawing.drawRobot(follower.getPose().getAsFTCStandardCoordinates(), "#4CAF50");
        Drawing.sendPacket();
        buidPaths();
    }


    private int pathState = 0;

    public static PathChain line2;
    public static PathChain line1;
    public void buidPaths() {
        line1 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(11.000, 55.000, Point.CARTESIAN),
                                new Point(11.000, 90.000, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();

        line2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(11.000, 90.000, Point.CARTESIAN),
                                new Point(11.000, 55.000, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0: // Move from start to scoring position
                follower.followPath(line1);
                pathState = 1;
                break;

            case 1: // Wait until the robot is near the scoring position
                if (!follower.isBusy()) {
                    follower.followPath(line2);
                    pathState = 0;
                }
                break;
        }
    }
    @Override
    public void loop() {
        follower.update();
        poseUpdater.update();
        autonomousPathUpdate();
        telemetryA.addData("x:", follower.getPose().getX());
        telemetryA.addData("y:", follower.getPose().getY());
        telemetryA.addData("state: ", pathState);
        telemetryA.update();
        Drawing.drawPoseHistory(dashboardPoseTracker, "#4CAF50");
        Drawing.drawRobot(follower.getPose().getAsFTCStandardCoordinates(), "#4CAF50");
        Drawing.drawRobot(follower.getClosestPose().getAsFTCStandardCoordinates(), "#0000FF");
        Drawing.drawPath(follower.getCurrentPath(), "#000000");
        Drawing.sendPacket();
    }
}
