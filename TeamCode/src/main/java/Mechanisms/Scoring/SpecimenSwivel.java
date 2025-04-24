package Mechanisms.Scoring;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

public class SpecimenSwivel {
    private static Servo swivel;
    public static double outPos = 0.93; // the positions
    public static double inPos = 0.7; // the positions
    public static double partialPos = 0.85;
    private static String state = "In";
    private static boolean swivelButtonCurrentlyPressed = false;
    private static boolean swivelButtonPreviouslyPressed = false;
    private static boolean swivelPartButtonCurrentlyPressed = false;
    private static boolean swivelPartButtonPreviouslyPressed = false;

    private static OpMode opmode;

    public static void initSwivel(OpMode opmode) {
        swivel = opmode.hardwareMap.get(Servo.class, "Specimen Swivel"); // config name
        SpecimenSwivel.opmode = opmode;
        state = "In";
    }

    public static void moveOut() {
        swivel.setPosition(outPos);
        state = "Out";
    }

    public static void moveIn() {
        swivel.setPosition(inPos); // swivel doesn't move
        state = "In"; // this runs
    }

    public static void movePart() {
        swivel.setPosition(partialPos); // swivel doesn't move
        state = "partial"; // this runs
    }

    private static void swap() {
        if (state.equals("Out")) {
            moveIn();
        } else {
            moveOut();
        }
    }

    public static void updateSwivel() {
        opmode.telemetry.addData("Swivel Position", state);
        swivelPartButtonCurrentlyPressed = opmode.gamepad1.x;
        // Check the status of the claw button on the gamepad
        swivelButtonCurrentlyPressed = opmode.gamepad1.y; // change this to change the button // disabled for safety

        // If the button state is different than what it was, then act
        if (swivelButtonCurrentlyPressed != swivelButtonPreviouslyPressed) {
            // If the button is (now) down
            if (swivelButtonCurrentlyPressed) {
                swap();
            }
        }
        swivelButtonPreviouslyPressed = swivelButtonCurrentlyPressed;
        if (swivelPartButtonCurrentlyPressed != swivelPartButtonPreviouslyPressed) {
            // If the button is (now) down
            if (swivelPartButtonCurrentlyPressed) {
                movePart();
            }
        } else {
            swap();
        }
        swivelPartButtonPreviouslyPressed = swivelPartButtonCurrentlyPressed;
    }
}