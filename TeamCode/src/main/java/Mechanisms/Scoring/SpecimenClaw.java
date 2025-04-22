package Mechanisms.Scoring;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

public class SpecimenClaw {
    private static Servo claw;
    public static double openPos = 0.7; // the positions
    public static double closePos = 1.0; // the positions
    public static String state = "Closed";
    private static boolean clawButtonCurrentlyPressed = false;
    private static boolean clawButtonPreviouslyPressed = false;

    private static OpMode opmode;

    public static void initClaw(OpMode opmode) {
        claw = opmode.hardwareMap.get(Servo.class, "Specimen Claw"); // config name
        SpecimenClaw.opmode = opmode;
        state = "Closed";
    }

    public static void open() {
        claw.setPosition(openPos);
        state = "Open";
    }

    private static void close() {
        claw.setPosition(closePos); // claw doesn't move
        state = "Closed"; // this runs
    }

    private static void swap() {
        if (state.equals("Open")) {
            close();
        } else {
            open();
        }
    }

    public static void updateClaw() {
        opmode.telemetry.addData("Specimen Claw State", state);
        // Check the status of the claw button on the gamepad
        clawButtonCurrentlyPressed = opmode.gamepad1.b; // change this to change the button

        // If the button state is different than what it was, then act
        if (clawButtonCurrentlyPressed != clawButtonPreviouslyPressed) {
            // If the button is (now) down
            if (clawButtonCurrentlyPressed) {
                swap();
            }
        }
        clawButtonPreviouslyPressed = clawButtonCurrentlyPressed;
    }
}