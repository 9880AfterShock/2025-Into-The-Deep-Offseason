package Autos;

import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "1 Tile")
public class SampleAuto extends OpMode {

    private final Pose startPose = new Pose(0,72, Math.toRadians(0));
    private Follower follower;
    private PathChain tile;

    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        generatePath();
    }

    public void loop() {
        follower.update();
    }

    public void generatePath() {
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        follower.setStartingPose(startPose);

        tile = follower.pathBuilder()
            .addPath(
                    // Line 1
                    new BezierLine(
                            new Point(0.000, 72.000, Point.CARTESIAN),
                            new Point(24.000, 72.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(0.0))
            .build();

        follower.followPath(tile);
    }
}