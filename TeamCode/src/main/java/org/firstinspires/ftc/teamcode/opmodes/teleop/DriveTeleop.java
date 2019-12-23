package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.components.LatchSystem;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@TeleOp(name = "Real Teleop", group="TeleOp")
public class DriveTeleop extends BaseOpMode {

    private boolean leftLatchHit = false;
    private boolean rightLatchHit = false;

    private final double SLIDER_SPEED = 1;
    private boolean gripped, down, up;
    
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

        if (armSystem.isHoming()) {
            armSystem.moveHome();
        } else if (armSystem.isGettingCapstone()) {
            armSystem.moveToCapstone();
        } else if (gamepad2.x) {
            armSystem.moveHome();
            return;
        } else if (gamepad2.y) {
            armSystem.moveToCapstone();
        } else if (gamepad2.dpad_left) {
            armSystem.moveWest();
        } else if (gamepad2.dpad_right) {
            armSystem.moveEast();
        } else if (gamepad2.dpad_up) {
            armSystem.moveNorth();
        } else if (gamepad2.dpad_down) {
            armSystem.moveSouth();
        }

        if (gamepad2.a && !gripped) {
            armSystem.toggleGripper();
            gripped = true;
        } else if (!gamepad2.a) {
            gripped = false;
        }

        if (gamepad2.right_bumper && !up) {
            armSystem.setSliderHeight(armSystem.mTargetHeight + 1);
            up = true;
        } else if (!gamepad2.right_bumper) {
            up = false;
        }

        if (gamepad2.left_bumper && !down) {
            armSystem.setSliderHeight(armSystem.mTargetHeight - 1);
            down = true;
        } else if (!gamepad2.left_bumper) {
            down = false;
        }
        //telemetry.addData("Target height: ", armSystem);

        armSystem.runSliderToTarget(SLIDER_SPEED);

    }
}