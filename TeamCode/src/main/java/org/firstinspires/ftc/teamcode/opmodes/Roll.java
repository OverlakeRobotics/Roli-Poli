package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "RollyBot", group="TeleOp")
public class Roll extends OpMode {
    DcMotor mainWheel;
    DcMotor reactionWheel;

    public void init() {
        mainWheel = hardwareMap.get(DcMotor.class, "mainWheel");

        reactionWheel = hardwareMap.get(DcMotor.class, "reactionWheel");

        mainWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void loop() {
        double mainPower = gamepad1.left_stick_y;

        if (Math.abs(mainPower) < 0.1) {
            mainPower = 0.0;
        }

        mainWheel.setPower(mainPower);

        if (gamepad1.right_bumper) {
            reactionWheel.setPower(1);
        } else if (gamepad1.left_bumper) {
            reactionWheel.setPower(-1);
        } else {
            reactionWheel.setPower(0);
        }
    }
}
