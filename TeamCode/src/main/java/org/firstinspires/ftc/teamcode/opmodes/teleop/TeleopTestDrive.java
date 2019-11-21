package org.firstinspires.ftc.teamcode.opmodes.teleop;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;


@TeleOp (name="test")
public class TeleopTestDrive extends OpMode {
    private DigitalChannel digitalChannel1;
    private DigitalChannel digitalChannel2;

    @Override
    public void init() {
        digitalChannel1 = hardwareMap.get(DigitalChannel.class, "light1");
        digitalChannel2 = hardwareMap.get(DigitalChannel.class, "light2");
        digitalChannel1.setMode(DigitalChannel.Mode.OUTPUT);
        digitalChannel2.setMode(DigitalChannel.Mode.OUTPUT);

    }

    @Override
    public void loop() {
        on();
        sleep(2000);
        off();
        sleep(2000);
    }

    public void on() {
        Log.d("Testing", "state -- on");
        telemetry.addData("Testing", "state -- on");
        digitalChannel1.setState(true);
        digitalChannel2.setState(false);
    }

    public void off() {
        Log.d("Testing", "state -- off");
        telemetry.addData("Testing", "state -- off");
        digitalChannel1.setState(false);
        digitalChannel2.setState(false);
    }

    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
