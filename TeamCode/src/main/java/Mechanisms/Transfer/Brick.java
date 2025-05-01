package Mechanisms.Transfer;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

public class Brick {
    private static Servo transfer;
    private static Servo transferLeft; //left intake servo
    private static Servo transferRight; //right intake servo
    public static double inPos = 0.0; //for pickuop
    public static double backPos = 0.5; //for deposit
    private static String state = "In";
    private static String rollerState = "Static";
    private static boolean transferButtonCurrentlyPressed = false;
    private static boolean transferButtonPreviouslyPressed = false;
    private static boolean transferIntakeButtonCurrentlyPressed = false;
    private static boolean transferOuttakeButtonCurrentlyPressed = false;

    private static OpMode opmode;

    public static void initTransfer(OpMode opmode) {
        transfer = opmode.hardwareMap.get(Servo.class, "transfer"); // config name
        transferLeft = opmode.hardwareMap.get(Servo.class, "transferLeft"); // config name
        transferRight = opmode.hardwareMap.get(Servo.class, "transferRight"); // config name
        Brick.opmode = opmode;
        state = "In";
        rollerState = "Static";
    }

    public static void in() {
        transfer.setPosition(inPos);
        state = "In";
    }

    public static void back() {
        transfer.setPosition(backPos);
        state = "Back";
    }

    public static void transferSwap() {
        if (state == "In") {
            back();
        } else {
            in();
        }
    }

    public static void updateRollers() {
        if (rollerState == "Intake") {
            transferLeft.setPosition(0.0);
            transferRight.setPosition(1.0);
        }
        if (rollerState == "Outtake") {
            transferLeft.setPosition(1.0);
            transferRight.setPosition(0.0);
        }
        if (rollerState == "Static") {
            transferLeft.setPosition(0.5);
            transferRight.setPosition(0.5);
        }
    }

    public static void updateTransfer() {
        transferButtonCurrentlyPressed = opmode.gamepad1.y; // change this to change the button
        transferIntakeButtonCurrentlyPressed = opmode.gamepad1.b; // change this to change the button
        transferOuttakeButtonCurrentlyPressed = opmode.gamepad1.x; // change this to change the button

        if (transferButtonCurrentlyPressed && !transferButtonPreviouslyPressed) {
            transferSwap();
        }

        if (transferIntakeButtonCurrentlyPressed) {
            rollerState = "Intake";
        } else if (transferOuttakeButtonCurrentlyPressed) {
            rollerState = "Outtake";
        } else {
            rollerState = "Static";
        }
        updateRollers();

        transferButtonPreviouslyPressed = transferButtonCurrentlyPressed;


        opmode.telemetry.addData("Transfer State", state);
    }
}