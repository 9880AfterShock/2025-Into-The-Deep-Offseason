package Autos;

import static java.lang.Math.abs;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

//@Disabled
@Autonomous(name="Time Auto 2 Spec + Park")
public class TimeAuto2 extends LinearOpMode {
    private DcMotor frontLeftDrive   = null;
    private DcMotor frontRightDrive  = null;
    private DcMotor backLeftDrive   = null;
    private DcMotor backRightDrive  = null;
    Servo swivel;
    IMU imu;
    private DcMotor specimenLift = null;
    Servo specimenClaw;
    static final double FORWARD_SPEED = 0.6;
    static final double PICKUP_SPEED = 0.2;
    static final double STOP_SPEED = 0.0;
    public static double outPos = 0.93; // for swivel
    public static double inPos = 0.7; // for swivel
    public static double openPos = 0.7; // for specimen claw
    public static double closePos = 1.0; // for specimen claw
    private ElapsedTime runtime = new ElapsedTime();
    @Override
    public void runOpMode() {
        frontLeftDrive  = hardwareMap.get(DcMotor.class, "leftFront");
        frontRightDrive = hardwareMap.get(DcMotor.class, "rightFront");
        backLeftDrive  = hardwareMap.get(DcMotor.class, "leftRear");
        backRightDrive = hardwareMap.get(DcMotor.class, "rightRear");
        swivel = hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, "Specimen Swivel");
        specimenLift = hardwareMap.get(DcMotor.class, "specimenLift");
        specimenClaw = hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, "Specimen Claw");
        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.LEFT;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        specimenLift.setTargetPosition(0);
        specimenLift.setPower(0.0);
        specimenLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        waitForStart();
        runtime.reset();
        imu.resetYaw();

        //extend
        swivel.setPosition(outPos);
        specimenClaw.setPosition(closePos);
        sleep(300);
        specimenLift.setTargetPosition((int)(3.5*-537.7));
        specimenLift.setPower(1.0);

        sleep(500);

        //move to bar
        frontLeftDrive.setPower(FORWARD_SPEED);
        frontRightDrive.setPower(-FORWARD_SPEED);
        backLeftDrive.setPower(-FORWARD_SPEED);
        backRightDrive.setPower(FORWARD_SPEED);

        while (opModeIsActive() && (runtime.seconds() < 2.035)) {
            sleep(10);
        }

        //stop and score
        frontLeftDrive.setPower(STOP_SPEED);
        frontRightDrive.setPower(STOP_SPEED);
        backLeftDrive.setPower(STOP_SPEED);
        backRightDrive.setPower(STOP_SPEED);
        while (opModeIsActive() && specimenLift.getCurrentPosition() > (int)3.4*-537.7) {
            sleep(10);
        }
        specimenLift.setTargetPosition(0);
        while (opModeIsActive() && specimenLift.getCurrentPosition() < (int)(2.9*-537.7)) {
            sleep(10);
        }
        specimenClaw.setPosition(openPos);

        //go back
        frontLeftDrive.setPower(-FORWARD_SPEED);
        frontRightDrive.setPower(FORWARD_SPEED);
        backLeftDrive.setPower(FORWARD_SPEED);
        backRightDrive.setPower(-FORWARD_SPEED);
        while (opModeIsActive() && (runtime.seconds() < 3.3)) {
            sleep(10);
        }

        //spin
        frontLeftDrive.setPower(FORWARD_SPEED);
        frontRightDrive.setPower(-FORWARD_SPEED);
        backLeftDrive.setPower(FORWARD_SPEED);
        backRightDrive.setPower(-FORWARD_SPEED);
        while (opModeIsActive() && (10 < abs(abs(imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES))-180))) {
            sleep(1);
        }


        //go to pickup
        runtime.reset();
        frontLeftDrive.setPower(PICKUP_SPEED);
        frontRightDrive.setPower(-PICKUP_SPEED);
        backLeftDrive.setPower(-PICKUP_SPEED);
        backRightDrive.setPower(PICKUP_SPEED);

        while (opModeIsActive() && runtime.seconds() < 0.4) {
            sleep(10);
        }

        frontLeftDrive.setPower(FORWARD_SPEED);
        frontRightDrive.setPower(FORWARD_SPEED);
        backLeftDrive.setPower(FORWARD_SPEED);
        backRightDrive.setPower(FORWARD_SPEED);

        while (opModeIsActive() && runtime.seconds() < 1.7) {
            sleep(10);
        }
        frontLeftDrive.setPower(STOP_SPEED);
        frontRightDrive.setPower(STOP_SPEED);
        backLeftDrive.setPower(STOP_SPEED);
        backRightDrive.setPower(STOP_SPEED);

        //pickup

        sleep(300);
        specimenClaw.setPosition(closePos);
        sleep(300);
        specimenLift.setTargetPosition((int)(3.5*-537.7));

        sleep(500);

        //move away from pickup
        runtime.reset();
        frontLeftDrive.setPower(-PICKUP_SPEED);
        frontRightDrive.setPower(PICKUP_SPEED);
        backLeftDrive.setPower(PICKUP_SPEED);
        backRightDrive.setPower(-PICKUP_SPEED);

        while (opModeIsActive() && runtime.seconds() < 0.4) {
            sleep(10);
        }

        frontLeftDrive.setPower(-FORWARD_SPEED);
        frontRightDrive.setPower(-FORWARD_SPEED);
        backLeftDrive.setPower(-FORWARD_SPEED);
        backRightDrive.setPower(-FORWARD_SPEED);

        while (opModeIsActive() && runtime.seconds() < 1.7) {
            sleep(10);
        }

        //spin back
        frontLeftDrive.setPower(-FORWARD_SPEED);
        frontRightDrive.setPower(FORWARD_SPEED);
        backLeftDrive.setPower(-FORWARD_SPEED);
        backRightDrive.setPower(FORWARD_SPEED);
        while (opModeIsActive() && (10 < abs(abs(imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES))))) {
            sleep(1);
        }
        runtime.reset();
        double x = 1.1;

        //move to bar
        frontLeftDrive.setPower(FORWARD_SPEED);
        frontRightDrive.setPower(-FORWARD_SPEED);
        backLeftDrive.setPower(-FORWARD_SPEED);
        backRightDrive.setPower(FORWARD_SPEED);

        while (opModeIsActive() && (runtime.seconds() < x)) {
            sleep(10);
        }

        //stop and score
        frontLeftDrive.setPower(STOP_SPEED);
        frontRightDrive.setPower(STOP_SPEED);
        backLeftDrive.setPower(STOP_SPEED);
        backRightDrive.setPower(STOP_SPEED);
        while (opModeIsActive() && specimenLift.getCurrentPosition() > (int)3.4*-537.7) {
            sleep(10);
        }
        specimenLift.setTargetPosition(0);
        while (opModeIsActive() && specimenLift.getCurrentPosition() < (int)(2.9*-537.7)) {
            sleep(10);
        }
        specimenClaw.setPosition(openPos);

        frontLeftDrive.setPower(-FORWARD_SPEED);
        frontRightDrive.setPower(FORWARD_SPEED);
        backLeftDrive.setPower(FORWARD_SPEED);
        backRightDrive.setPower(-FORWARD_SPEED);
        while (opModeIsActive() && (runtime.seconds() < x+1.3)) {
            sleep(10);
        }



        //park
        frontLeftDrive.setPower(-FORWARD_SPEED);
        frontRightDrive.setPower(-FORWARD_SPEED);
        backLeftDrive.setPower(-FORWARD_SPEED);
        backRightDrive.setPower(-FORWARD_SPEED);
        while (opModeIsActive() && (runtime.seconds() < x+1.3+1.3)) {
            sleep(10);
        }

        //stop
        frontLeftDrive.setPower(STOP_SPEED);
        frontRightDrive.setPower(STOP_SPEED);
        backLeftDrive.setPower(STOP_SPEED);
        backRightDrive.setPower(STOP_SPEED);

        //wait
        while (opModeIsActive()) {
            sleep(1);
        }
    }
}