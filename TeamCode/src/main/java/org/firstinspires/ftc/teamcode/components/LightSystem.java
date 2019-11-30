package org.firstinspires.ftc.teamcode.components;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DigitalChannel;

public class LightSystem {

    private DigitalChannel left;
    private DigitalChannel right;

    public LightSystem(DigitalChannel rightLight, DigitalChannel leftLight) {
        right = rightLight;
        left = leftLight;
        right.setMode(DigitalChannel.Mode.OUTPUT);
        left.setMode(DigitalChannel.Mode.OUTPUT);
    }

    public void on() {
        right.setMode(DigitalChannel.Mode.INPUT);
        left.setMode(DigitalChannel.Mode.INPUT);
    }

    public void off() {
        right.setMode(DigitalChannel.Mode.OUTPUT);
        left.setMode(DigitalChannel.Mode.OUTPUT);
    }
}
