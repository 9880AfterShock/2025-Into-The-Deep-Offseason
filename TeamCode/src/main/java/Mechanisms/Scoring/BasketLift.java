package Mechanisms.Scoring;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class BasketLift { // Prefix for commands
    private static DcMotor lift; // Init Motor Var
    private static double pos = 0.0; // starting Position
    private static final double encoderTicks = 537.7; // calculate your own ratio // negative to invert values
    public static double minPos = 0.0; // all the way down
    public static double midPos = 3.5; //for low basket
    public static double maxPos = 6.5; // need to change
    private static OpMode opmode; // opmode var init
    private static boolean downButtonCurrentlyPressed = false;
    private static boolean downButtonPreviouslyPressed = false;
    private static boolean upButtonCurrentlyPressed = false;
    private static boolean upButtonPreviouslyPressed = false;
    private static boolean midButtonCurrentlyPressed = false;
    private static boolean midButtonPreviouslyPressed = false;
    public static DcMotor.RunMode encoderMode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
    public static DcMotor.RunMode motorMode = DcMotor.RunMode.RUN_TO_POSITION; // set motor mode

    public static void initLift(OpMode opmode) { // init motors
        pos = 0.0;
        lift = opmode.hardwareMap.get(DcMotor.class, "basketLift"); // config name
        lift.setTargetPosition((int) (pos * encoderTicks));
        lift.setMode(encoderMode); // reset encoder
        lift.setMode(motorMode); // enable motor mode
        BasketLift.opmode = opmode;
    }

//    public static void initLiftAfterAuto(OpMode opmode) { // init motors
//        lift = opmode.hardwareMap.get(DcMotor.class, "specimenLift"); // config name
//        lift.setMode(motorMode); // enable motor mode
//        BasketLift.opmode = opmode;
//    }

    public static void updateLift() {
        // can change controls
        downButtonCurrentlyPressed = opmode.gamepad2.a;
        midButtonCurrentlyPressed = opmode.gamepad2.b;
        upButtonCurrentlyPressed = opmode.gamepad2.y;

        if (!((downButtonCurrentlyPressed && upButtonCurrentlyPressed) || (downButtonCurrentlyPressed && midButtonCurrentlyPressed) || (upButtonCurrentlyPressed && midButtonCurrentlyPressed) || (upButtonCurrentlyPressed && midButtonCurrentlyPressed && downButtonCurrentlyPressed))) {
            if (downButtonCurrentlyPressed && !downButtonPreviouslyPressed) {
                pos = minPos;
            }
            if (midButtonCurrentlyPressed && !midButtonPreviouslyPressed) {
                pos = midPos;
            }
            if (upButtonCurrentlyPressed && !upButtonPreviouslyPressed/* && SpecimenClaw.state.equals("Closed")*/) { //might need to change for new mechs
                pos = maxPos;
            }
        }

        //checkDrop();

        downButtonPreviouslyPressed = downButtonCurrentlyPressed;
        midButtonPreviouslyPressed = midButtonCurrentlyPressed;
        upButtonPreviouslyPressed = upButtonCurrentlyPressed;

        // pos += currentSpeed
        lift.setPower(1.0); // turn motor on //might need to change based on stuff
        lift.setTargetPosition((int) (pos * encoderTicks));
        opmode.telemetry.addData("Basket Lift target position", pos); // Set telemetry
    }

//    private static void checkDrop() {
//        if ((maxDrop > lift.getCurrentPosition() / encoderTicks) && (lift.getCurrentPosition() / encoderTicks > minDrop) && (lift.getTargetPosition() / encoderTicks == minPos)) {
//            SpecimenClaw.open();
//        }
//    }
}