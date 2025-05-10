package Mechanisms.Scoring;

import static Mechanisms.Pickup.Claw.lastDropTimestamp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import Mechanisms.Transfer.PipeWrench;

public class BasketLift { // Prefix for commands
    private static DcMotor lift; // Init Motor Var
    private static double pos = 0.0; // starting Position
    private static double safePos = 0.0; // alternate position for holding while transfer moves
    private static final double encoderTicks = 537.7; // calculate your own ratio // negative to invert values
    public static double minPos = 0.0; // all the way down
    public static double inPos = 1.5; //height that it can clear the transfer basket
    public static double backPos = 3.0; //height that it can clear the specimen lift
    public static double pickupPos = 0.2; //height that it can go back in for pickup
    public static double midPos = 3.5; //for low basket
    public static double maxPos = 6.5; // need to change
    public static double dropDelay = 0.5*1000000000; //time to wait to go in after the claw drops the sample (the multiplier for for converting to nanoseconds)
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
            if (upButtonCurrentlyPressed && !upButtonPreviouslyPressed) {
                pos = maxPos;
            }
        }

        safePos = pos;

        checkTransfer();

        downButtonPreviouslyPressed = downButtonCurrentlyPressed;
        midButtonPreviouslyPressed = midButtonCurrentlyPressed;
        upButtonPreviouslyPressed = upButtonCurrentlyPressed;

        // pos += currentSpeed
        lift.setPower(1.0); // turn motor on //might need to change based on stuff
        lift.setTargetPosition((int) (safePos * encoderTicks));
        opmode.telemetry.addData("Basket Lift target position", pos); // Set telemetry
        opmode.telemetry.addData("Basket Lift safety position", safePos); // Set telemetry
    }

//    private static void checkDrop() {
//        if ((maxDrop > lift.getCurrentPosition() / encoderTicks) && (lift.getCurrentPosition() / encoderTicks > minDrop) && (lift.getTargetPosition() / encoderTicks == minPos)) {
//            SpecimenClaw.open();
//        }
//    }
    private static void checkTransfer() {

        //Setting PipeWrench position
        if (pos > backPos + SpecimenLift.pos && lift.getCurrentPosition()/encoderTicks > backPos + SpecimenLift.pos) { //make sure that if the specimen lift is up, it is still safe to go (assuming specilift and baskilift use same rotation to vertical extension ratio)
            PipeWrench.back();
        } else if (pos > inPos && lift.getCurrentPosition()/encoderTicks > inPos) {
            PipeWrench.in();
        } else if (pos > minPos) {
            PipeWrench.out();
        } else if (lastDropTimestamp + dropDelay > opmode.getRuntime() && lastDropTimestamp != 0.0) { //make sure it was not the init/resting setup of 0.0
            PipeWrench.in();
            if (PipeWrench.transfer.getPosition() == PipeWrench.inPos) {
                PipeWrench.close();
            }
        } else {
            PipeWrench.out(); //idk if this is right //seems to be
        }

        //Making sure the lift is safe for the PipeWrench
        if (pos < backPos + SpecimenLift.pos && PipeWrench.transfer.getPosition() > PipeWrench.inPos) { //is in inPos or outPos //to improve, should add a position for which it is safe to come down from the specilift (and if we are really going, seperatte for the specifilift and specilift mount)
            safePos = backPos;
        }
        if (pos < inPos && lift.getCurrentPosition()/encoderTicks > inPos && PipeWrench.transfer.getPosition() == PipeWrench.outPos) {
            safePos = inPos;
        }
        if (pos > pickupPos && lift.getCurrentPosition()/encoderTicks < pickupPos && PipeWrench.transfer.getPosition() == PipeWrench.outPos) {
            safePos = pickupPos;
        }


    }
}