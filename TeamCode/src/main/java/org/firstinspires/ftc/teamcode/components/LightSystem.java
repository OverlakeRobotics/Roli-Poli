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

    public void rightLightOn() {
        Log.d("Testing", "state -- right on");
        right.setMode(DigitalChannel.Mode.INPUT);
    }

    public void leftLightOn() {
        Log.d("Testing", "state -- left on");
        left.setMode(DigitalChannel.Mode.INPUT);
    }

    public void off() {
        Log.d("Testing", "state -- off");
        right.setMode(DigitalChannel.Mode.OUTPUT);
        left.setMode(DigitalChannel.Mode.OUTPUT);
    }

    public final void sleep(long milliseconds) {
        Log.d("Testing", "state -- sleep");
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
