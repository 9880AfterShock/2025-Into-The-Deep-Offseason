package Mechanisms;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

public class VisionSwivel {
    private static Servo swivel;
    public static double initPos = 0.5; //when folded in
    public static double visionPos = 0.0; //when at pickup
    public static double sweepPos = 1.0; //during auto
    public static String state = "Init";
    private static boolean swivelButtonCurrentlyPressed = false;
    private static boolean swivelButtonPreviouslyPressed = false;

    private static OpMode opmode;

    public static void initSwivel(OpMode opmode) {
        swivel = opmode.hardwareMap.get(Servo.class, "visionSwivel"); // config name
        VisionSwivel.opmode = opmode;
        state = "Init";
    }

    public static void scan() { //unused for now
        swivel.setPosition(initPos);
        state = "Init";
    }

    private static void vision() {
        swivel.setPosition(visionPos);
        state = "Vision";
    }

    private static void sweep() {
        swivel.setPosition(sweepPos);
        state = "Sweep";
    }

    private static void swap() {
        if (state.equals("Vision")) {
            sweep();
        } else {
            vision();
        }
    }

    public static void updateSwivel() {
        // Check the status of the button on the gamepad
        //swivelButtonCurrentlyPressed = opmode.gamepad1.{button goes here}; // change this to change the button //should be autonomous

        // If the button has now been pressed
        if (swivelButtonCurrentlyPressed && !swivelButtonPreviouslyPressed) {
            swap();
        }

        swivelButtonPreviouslyPressed = swivelButtonCurrentlyPressed;
        opmode.telemetry.addData("Vision Swivel State", state);
    }
}
