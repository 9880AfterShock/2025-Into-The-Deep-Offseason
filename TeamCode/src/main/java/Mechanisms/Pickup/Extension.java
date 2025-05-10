package Mechanisms.Pickup;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class Extension { //Prefix for commands
    public static DcMotorEx lift;
    public static double pos = 0.0; //Starting Position
    public static double currentSpeed = 0.0; //Starting speed
    public static double speed = 0.1; //Update speed
    public static final double encoderTicks = 384.5; //might need to change (old old was 537.7)
    public static double minPos = 0.0;
    public static double transferPos = 0.35; //posiiton for transfering sample to bucket thingy
    public static double maxPos = 5.0; //needs to be changed
    private static boolean transferPrepButtonCurrentlyPressed = false;
    private static boolean transferPrepButtonPreviouslyPressed = false;
    public static OpMode opmode;
    public static DcMotor.RunMode encoderMode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
    public static DcMotor.RunMode motorMode = DcMotor.RunMode.RUN_TO_POSITION;

    public static void initLift(OpMode opmode) {
        pos = 0.0;
        lift = opmode.hardwareMap.get(DcMotorEx.class, "extension"); //config name
        lift.setTargetPosition((int) (pos * encoderTicks));
        lift.setMode(encoderMode); //reset encoder
        lift.setMode(motorMode); //enable motor mode
        Extension.opmode = opmode;
    }

    public static void updateLift() {
        transferPrepButtonCurrentlyPressed = opmode.gamepad2.left_trigger > 0.5; //can change controls

        if (opmode.gamepad2.dpad_up && !opmode.gamepad2.dpad_down) {// can change controls
            currentSpeed = speed;
        } else if (opmode.gamepad2.dpad_down && !opmode.gamepad2.dpad_up) {
            currentSpeed = -speed;
        } else {
            currentSpeed = 0.0;
        }

        if (transferPrepButtonCurrentlyPressed && !transferPrepButtonPreviouslyPressed) {
            transferSequence();
        }

        pos += currentSpeed;

        if (pos > maxPos) {
            pos = maxPos;
        }
        if (pos < minPos) {
            pos = minPos;
        }

        transferPrepButtonPreviouslyPressed = transferPrepButtonCurrentlyPressed;

        lift.setPower(1.0);
        lift.setTargetPosition((int) (pos * encoderTicks));
        opmode.telemetry.addData("Extension target position", pos);
    }

    private static void transferSequence() {
        Wrist.currentPos = -2; //placeholder value for transfer pos
        Wrist.updatePosition(positions[currentPos])
        Extension.pos = Extension.transferPos;
    }
}
