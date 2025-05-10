package Mechanisms.Pickup;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Claw {
    private static Servo claw;
    public static double openPos = 0.98; // the positions
    public static double closePos = 0.73; // the positions // timo changed from 0.6 to 0.82 for new claw
    public static volatile double lastDropTimestamp = 0.0; //the last time that the claw dropped into the transfer
    private static String state = "Closed";
    private static boolean clawButtonCurrentlyPressed = false;
    private static boolean clawButtonPreviouslyPressed = false;

    private static OpMode opmode;

    public static void initClaw(OpMode opmode) {
        claw = opmode.hardwareMap.get(Servo.class, "Claw"); // config name
        Claw.opmode = opmode;
        state = "Closed";
    }

    private static void open() {
        claw.setPosition(openPos);
        state = "Open";
        if (Wrist.currentPos == -2) {
            transfer();
        }
    }

    public static void close() {
        claw.setPosition(closePos); // claw doesn't move
        state = "Close"; // this runs
    }

    public static void clawSwap() { // renamed for auto purposes -Oscar
        if (state.equals("Open")) {
            close();
        } else {
            open();
        }
    }

    private static void transfer() {
        lastDropTimestamp = opmode.getRuntime();
        Wrist.currentPos = 2;
        Wrist.updatePosition(Wrist.positions[Wrist.currentPos]);
    }

    public static void updateClaw() {
        opmode.telemetry.addData("Claw State", state);
        // Check the status of the claw button on the gamepad
        clawButtonCurrentlyPressed = opmode.gamepad1.a; // change this to change the button

        // If the button state is different than what it was, then act
        if (clawButtonCurrentlyPressed != clawButtonPreviouslyPressed) {
            // If the button is (now) down
            if (clawButtonCurrentlyPressed) {
                clawSwap();
            }
        }
        clawButtonPreviouslyPressed = clawButtonCurrentlyPressed;
    }
}