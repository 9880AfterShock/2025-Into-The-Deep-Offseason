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
    public static double transferPos = 0.35; //posiiton for transfering sample to bucket thingy, as well as autoretract
    public static double extendPos = 3.5; //position for when you autoextend
    public static double wristUpMaxPos = 4.0; //max extension when wrist is up
    public static double maxPos = 5.0; //needs to be changed
    private static boolean transferPrepButtonCurrentlyPressed = false;
    private static boolean transferPrepButtonPreviouslyPressed = false;
    private static boolean extendButtonCurrentlyPressed = false;
    private static boolean extendButtonPreviouslyPressed = false;
    private static boolean extended = false;
    public static OpMode opmode;
    public static DcMotor.RunMode encoderMode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
    public static DcMotor.RunMode motorMode = DcMotor.RunMode.RUN_TO_POSITION;

    public static void initLift(OpMode opmode) {
        pos = 0.0;
        extended = false;
        lift = opmode.hardwareMap.get(DcMotorEx.class, "extension"); //config name
        lift.setTargetPosition((int) (pos * encoderTicks));
        lift.setMode(encoderMode); //reset encoder
        lift.setMode(motorMode); //enable motor mode
        Extension.opmode = opmode;
    }

    public static void updateLift() {
        transferPrepButtonCurrentlyPressed = opmode.gamepad2.left_trigger > 0.5; //can change controls
        extendButtonCurrentlyPressed = opmode.gamepad1.left_trigger > 0.5; //can change controls

        if (opmode.gamepad2.dpad_up && !opmode.gamepad2.dpad_down) {// can change controls
            currentSpeed = speed;
            extended = true;
        } else if (opmode.gamepad2.dpad_down && !opmode.gamepad2.dpad_up) {
            currentSpeed = -speed;
            extended = true;
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
        if (pos > wristUpMaxPos && !(Wrist.currentPos == 0 && Math.abs(Wrist.wrist.getTargetPosition() - Wrist.wrist.getCurrentPosition()) < 50)) {
            pos = wristUpMaxPos;
        }
        if (pos < minPos) {
            pos = minPos;
        }

        transferPrepButtonPreviouslyPressed = transferPrepButtonCurrentlyPressed;
        extendButtonPreviouslyPressed = extendButtonCurrentlyPressed;

        lift.setPower(1.0);
        lift.setTargetPosition((int) (pos * encoderTicks));
        opmode.telemetry.addData("Extension target position", pos);
    }

    private static void transferSequence() {
        extended = false;
        Wrist.currentPos = -2; //placeholder value for transfer pos
        Extension.pos = Extension.transferPos;
    }

    private static void toggleExtend() {
        if (extended) {
            pos = transferPos;
            Wrist.changePosition("backward");
        } else {
            pos = extendPos;
            Wrist.changePosition("forward");
            Claw.open();
        }
        extended = !extended;
    }
}
