import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import Mechanisms.Pickup.Swivel;
import Mechanisms.VisionSwivel;
import Sensors.Vision;

@TeleOp(name = "vison testing")
//Toggle Disabled to make appear in list or not.
//@Disabled
public class VisionTest extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        //Add init msg
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        Vision.initVision(this);
        Swivel.initSwivel(this);
        VisionSwivel.initSwivel(this);
        VisionSwivel.vision();
        // init commands here

        //Wait for start
        //Running Loop
        while (opModeIsActive() || opModeInInit()) {
            //Tick Commands Here
            Vision.updateVision();
            Swivel.updateSwivel();
        }
        Vision.stopVision();
    }
}

