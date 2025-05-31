package Autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

//@Disabled
@Autonomous(name="Time Auto 1 Net")
public class TimeAuto0 extends LinearOpMode {
    private DcMotor frontLeftDrive   = null;
    private DcMotor frontRightDrive  = null;
    private DcMotor backLeftDrive   = null;
    private DcMotor backRightDrive  = null;
    Servo swivel;
    private DcMotor specimenLift = null;
    Servo specimenClaw;
    static final double FORWARD_SPEED = 0.6;
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

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        specimenLift.setTargetPosition(0);
        specimenLift.setPower(0.0);
        specimenLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        waitForStart();
        runtime.reset();

        //move to bar
        frontLeftDrive.setPower(FORWARD_SPEED);
        frontRightDrive.setPower(-FORWARD_SPEED);
        backLeftDrive.setPower(-FORWARD_SPEED);
        backRightDrive.setPower(FORWARD_SPEED);
        while (opModeIsActive() && (runtime.seconds() < 1)) {
            sleep(10);
        }

        frontLeftDrive.setPower(STOP_SPEED);
        frontRightDrive.setPower(STOP_SPEED);
        backLeftDrive.setPower(STOP_SPEED);
        backRightDrive.setPower(STOP_SPEED);

        while (opModeIsActive()) {
            sleep(1);
        }
    }
}