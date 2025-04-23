package Mechanisms.Pickup;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;

public class Swivel {
    private static Servo swivel;
    private static double orientation = 0.5;
    public static double restingState = 0.5;

    private static OpMode opmode;

    public static void initSwivel(OpMode opmode) {
        swivel = opmode.hardwareMap.get(Servo.class, "Swivel");
        orientation = 0.5;
        restingState = 0.5;
        Swivel.opmode = opmode;
    }

    private static void moveTo(double orientation) {
        swivel.setPosition(orientation);
    }

    public static void updateSwivel() {
        if ((opmode.gamepad2.right_stick_y == 0.0 && opmode.gamepad2.right_stick_x == 0.0) || Wrist.currentPos == 2) {
            orientation = restingState;
        } else {
            orientation = atan2(abs(opmode.gamepad2.right_stick_y), -opmode.gamepad2.right_stick_x);
            orientation = abs(orientation / PI * (0.85 - 0.15) + 0.15); // boundaries are 0.85 and 0.15

            if (Wrist.currentPos == 0 /*&& Raiser.targPos != 0*/  /*might need to change later for new mechs*/) { // might need to move to make sure it spins when the wrist goes up
                restingState = 0.85; // for while it's down
            } else {
                restingState = 0.5;
            }
        }
        moveTo(orientation);

        opmode.telemetry.addData("Claw Swivel Position", orientation);
    }
}