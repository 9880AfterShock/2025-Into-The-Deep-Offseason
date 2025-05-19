package Mechanisms.Pickup;

import static Mechanisms.Pickup.Claw.lastDropTimestamp;
import static Mechanisms.Scoring.BasketLift.dropDelay;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.Currency;

public class Wrist {
    public static DcMotor wrist;
    public static final double encoderTicks = 752.8; //calculate your own ratio
    public static int[] positions = {0, 90, 180}; //positions, most forward to most backward
    public static int initPos = 200; //innit pos prob 200-220 or so
    public static int transferPos = 210; //position for transfer, be careful with this one
    public static int currentPos = -1; //-1 is transfer pos, -2 is transfer pos
    public static String transferReset = "Complete";
    private static String state = "Init";
    private static boolean backwardWristButtonCurrentlyPressed = false;
    private static boolean backwardWristButtonPreviouslyPressed = false;
    private static boolean forwardWristButtonCurrentlyPressed = false;
    private static boolean forwardWristButtonPreviouslyPressed = false;

    private static OpMode opmode;
    public static DcMotor.RunMode encoderMode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
    public static DcMotor.RunMode motorMode = DcMotor.RunMode.RUN_TO_POSITION;

    public static void initWrist(OpMode opmode) {
        transferReset = "Complete";
        currentPos = -1; //reset pos to innit, change for teleop after auto
        wrist = opmode.hardwareMap.get(DcMotor.class, "wrist"); //config name
        wrist.setTargetPosition((int) ((-encoderTicks * (-initPos + initPos)) / 360)); //might be wrong, old one had mainlift pos by mistake so it probably doesnt matter anyway
        wrist.setMode(encoderMode); //reset encoder
        wrist.setMode(motorMode); //enable motor mode
        Wrist.opmode = opmode;
    }

    public static void updateWrist() {
        forwardWristButtonCurrentlyPressed = opmode.gamepad1.right_bumper; //change these to change the button
        backwardWristButtonCurrentlyPressed = opmode.gamepad1.left_bumper;

        if (!(forwardWristButtonCurrentlyPressed && backwardWristButtonCurrentlyPressed)) { //safety mechanism
            if (forwardWristButtonCurrentlyPressed && !forwardWristButtonPreviouslyPressed) {
                changePosition("forward");
            }
            if (backwardWristButtonCurrentlyPressed && !backwardWristButtonPreviouslyPressed) {
                changePosition("backward");
            }
        }

        if (transferReset != "Complete") {
            if (lastDropTimestamp + dropDelay < opmode.getRuntime() && lastDropTimestamp != 0.0) {
                currentPos = 2;
                transferReset = "Complete";
            }
        }

        updatePosition();

        if (state.equals(Integer.toString(positions[0])) && Math.abs(wrist.getTargetPosition() - wrist.getCurrentPosition()) < 50) {  //make sure it powers off at lowest, 50 is margin of error
            wrist.setPower(0.0);
        } else if (currentPos == -2) {
            wrist.setPower(0.5); //fast for transfer
        } else {
            wrist.setPower(0.25);
        }

        forwardWristButtonPreviouslyPressed = forwardWristButtonCurrentlyPressed;
        backwardWristButtonPreviouslyPressed = backwardWristButtonCurrentlyPressed;

        opmode.telemetry.addData("Wrist State", state);
        opmode.telemetry.addData("Wrist Transfer Sequence State", transferReset);
    }

    public static void changePosition(String direction) {
        if (currentPos == 0 && Extension.lift.getCurrentPosition()/Extension.encoderTicks > Extension.wristUpMaxPos) {
            return;
        } //stop from moving if you would violate the size limit

        if ((currentPos == -1 || currentPos == -2)&& direction.equals("forward")) {
            currentPos = positions.length - 1; //if inited, go to last in array
        } else {
            if ((currentPos != -1 && currentPos != -2)) {
                if (direction.equals("forward") && positions[currentPos] != positions[0]) {
                    currentPos -= 1;
                }
                if (direction.equals("backward") && currentPos != positions.length - 1) {
                    currentPos += 1;
                }
            }
        }
    }

    public static void updatePosition() {

        if (currentPos == -1) {
            wrist.setTargetPosition((int) ((-encoderTicks * (-initPos + initPos)) / 360));
            state = Integer.toString(currentPos);
        } else if (currentPos == -2) {
            if (Extension.lift.getCurrentPosition()/Extension.encoderTicks < Extension.wristUpMaxPos) {
                wrist.setTargetPosition((int) ((-encoderTicks * (-transferPos + initPos)) / 360));
                state = Integer.toString(currentPos);
            }
        } else {
            wrist.setTargetPosition((int) ((-encoderTicks * (-positions[currentPos] + initPos)) / 360));
            state = Integer.toString(positions[currentPos]);
        }
    }
}
