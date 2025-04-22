package Mechanisms.Scoring;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class SpecimenLift { // Prefix for commands
    private static DcMotor lift; // Init Motor Var
    private static double pos = 0.0; // starting Position
    private static final double encoderTicks = -537.7; // calculate your own ratio // negative to invert values
    public static double minPos = 0.0; // all the way down
    public static double maxPos = 3.5; // GOOD and working
    public static double minDrop = 2.7; // lower drop pos in rotations
    public static double maxDrop = 2.9; // higher drop pos in rotations
    private static OpMode opmode; // opmode var init
    private static boolean downButtonCurrentlyPressed = false;
    private static boolean downButtonPreviouslyPressed = false;
    private static boolean upButtonCurrentlyPressed = false;
    private static boolean upButtonPreviouslyPressed = false;
    public static DcMotor.RunMode encoderMode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
    public static DcMotor.RunMode motorMode = DcMotor.RunMode.RUN_TO_POSITION; // set motor mode

    public static void initLift(OpMode opmode) { // init motors
        pos = 0.0;
        lift = opmode.hardwareMap.get(DcMotor.class, "specimenLift"); // config name
        lift.setTargetPosition((int) (pos * encoderTicks));
        lift.setMode(encoderMode); // reset encoder
        lift.setMode(motorMode); // enable motor mode
        SpecimenLift.opmode = opmode;
    }

    public static void initLiftAfterAuto(OpMode opmode) { // init motors
        lift = opmode.hardwareMap.get(DcMotor.class, "specimenLift"); // config name
        lift.setMode(motorMode); // enable motor mode
        SpecimenLift.opmode = opmode;
    }

    public static void updateLift() {
        // can change controls
        downButtonCurrentlyPressed = opmode.gamepad2.left_bumper;
        upButtonCurrentlyPressed = opmode.gamepad2.right_bumper;

        if (!(downButtonCurrentlyPressed && upButtonCurrentlyPressed)) {
            if (downButtonCurrentlyPressed && !downButtonPreviouslyPressed) {
                pos = minPos;
            }
            if (upButtonCurrentlyPressed && !upButtonPreviouslyPressed && SpecimenClaw.state.equals("Closed")) {
                pos = maxPos;
            }
        }

        checkDrop();

        downButtonPreviouslyPressed = downButtonCurrentlyPressed;
        upButtonPreviouslyPressed = upButtonCurrentlyPressed;

        // pos += currentSpeed
        lift.setPower(0.7); // turn motor on
        lift.setTargetPosition((int) (pos * encoderTicks));
        opmode.telemetry.addData("Specimen Lift target position", pos); // Set telemetry
    }

    private static void checkDrop() {
        if ((maxDrop > lift.getCurrentPosition() / encoderTicks) && (lift.getCurrentPosition() / encoderTicks > minDrop) && (lift.getTargetPosition() / encoderTicks == minPos)) {
            SpecimenClaw.open();
        }
    }
}