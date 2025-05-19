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

@Autonomous(name = "1 Tile Back and Forth")
public class SampleAuto extends OpMode {

    private final Pose startPose = new Pose(0,72, Math.toRadians(0));
    private final Pose endPose = new Pose(24, 72, Math.toRadians(0));

    private Follower follower;
    private PathChain fowardOneTile;
    private PathChain backwardOneTile;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;

    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        generatePath();

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
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(fowardOneTile);
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
                    follower.followPath(backwardOneTile, true);
                    setPathState(2);
                }
                break;
            case 2:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the end position */
                if(!follower.isBusy()) {
                    /* Set the state to a Case we won't use or define, so it just stops running an new paths */
                    setPathState(-1);
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
        follower.setStartingPose(startPose);

        fowardOneTile = follower.pathBuilder()
            .addPath(new BezierLine(new Point(startPose), new Point(endPose)))
            .setConstantHeadingInterpolation(Math.toRadians(0.0))
            .build();

        backwardOneTile = follower.pathBuilder()
            .addPath(new BezierLine(new Point(endPose), new Point(startPose)))
            .setConstantHeadingInterpolation(Math.toRadians(0.0))
            .build();
    }
}