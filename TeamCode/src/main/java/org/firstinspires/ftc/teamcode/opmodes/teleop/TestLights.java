package org.firstinspires.ftc.teamcode.opmodes.teleop;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;


@TeleOp (name="TestLights")
public class TestLights extends OpMode {
    private DigitalChannel left;
    private DigitalChannel right;

    @Override
    public void init() {
        right = hardwareMap.get(DigitalChannel.class, "right_light");
        left = hardwareMap.get(DigitalChannel.class, "left_light");
        right.setMode(DigitalChannel.Mode.OUTPUT);
        left.setMode(DigitalChannel.Mode.OUTPUT);
    }

    @Override
    public void loop() {
        onRight();
        sleep(2000);
        off();
        sleep(2000);
        onLeft();
        sleep(2000);
        off();
        sleep(2000);
    }

    public void onRight() {
        Log.d("Testing", "state -- right on");
        telemetry.addData("Testing", "state -- right on");
        right.setState(true);
        left.setState(false);
    }

    public void onLeft() {
        Log.d("Testing", "state -- left on");
        telemetry.addData("Testing", "state -- left on");
        left.setState(true);
        right.setState(false);
    }

    public void off() {
        Log.d("Testing", "state -- off");
        telemetry.addData("Testing", "state -- off");
        right.setState(false);
        left.setState(false);
    }

    public final void sleep(long milliseconds) {
        Log.d("Testing", "state -- sleep");
        telemetry.addData("Testing", "state -- sleep");
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
