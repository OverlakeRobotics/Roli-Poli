package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.components.ArmSystem;
import org.firstinspires.ftc.teamcode.components.LatchSystem;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

import java.util.EnumMap;

@TeleOp(name = "Real Teleop", group="TeleOp")
public class DriveTeleop extends BaseOpMode {

    private boolean leftLatchHit = false;
    private boolean rightLatchHit = false;
    
    public void loop(){
        float rx = (float) Math.pow(gamepad1.right_stick_x, 3);
        float lx = (float) Math.pow(gamepad1.left_stick_x, 3);
        float ly = (float) Math.pow(gamepad1.left_stick_y, 3);
        driveSystem.slowDrive(gamepad1.left_trigger > 0.3f);
        driveSystem.drive(rx, lx, ly);


        if (gamepad1.left_bumper) {
            intakeSystem.unsuck();
        } else if (gamepad1.right_bumper) {
            intakeSystem.suck();
        } else {
            intakeSystem.stop();
        }

        if (gamepad1.b && !leftLatchHit) {
            leftLatchHit = true;
            latchSystem.toggle(LatchSystem.Latch.LEFT);
        } else if (!gamepad1.b) {
            leftLatchHit = false;
        }

        if (gamepad1.x && !rightLatchHit) {
            rightLatchHit = true;
            latchSystem.toggle(LatchSystem.Latch.RIGHT);
        } else if (!gamepad1.x) {
            rightLatchHit = false;
        }

        if (gamepad1.y) {
            latchSystem.bothUp();
        }

        if (gamepad1.a) {
            latchSystem.bothDown();
        }

    }
}