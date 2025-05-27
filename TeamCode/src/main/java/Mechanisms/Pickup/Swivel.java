package Mechanisms.Pickup;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;

public class Swivel {
    private static Servo swivel;
    private static double orientation = 0.15;
    public static double restingState = 0.15;

    private static OpMode opmode;

    public static void initSwivel(OpMode opmode) {
        swivel = opmode.hardwareMap.get(Servo.class, "Swivel");
        orientation = 0.15;
        restingState = 0.15;
        Swivel.opmode = opmode;
    }

    private static void moveTo(double orientation) {
        swivel.setPosition(orientation);
    }

    public static void updateSwivel() {

        if (Wrist.currentPos <= 0) { //make sure that the wrist is not upright or at init/transfer poses
            restingState = 0.15; // for while it's down
        } else {
            restingState = 0.5;
        }

        if ((opmode.gamepad2.right_stick_y == 0.0 && opmode.gamepad2.right_stick_x == 0.0) || Wrist.currentPos != 0) {
            orientation = restingState;
        } else {
            orientation = atan2(abs(opmode.gamepad2.right_stick_y), -opmode.gamepad2.right_stick_x);
            orientation = abs(orientation / PI * (0.85 - 0.15) + 0.15); // boundaries are 0.85 and 0.15
        }
        moveTo(orientation);

        opmode.telemetry.addData("Claw Swivel Position", orientation);
        opmode.telemetry.addData("Claw Swivel 'Resting State' Position", restingState);
    }
}