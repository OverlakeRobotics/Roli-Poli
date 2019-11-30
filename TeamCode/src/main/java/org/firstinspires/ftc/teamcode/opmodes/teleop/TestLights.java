package org.firstinspires.ftc.teamcode.opmodes.teleop;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.teamcode.components.LightSystem;


@TeleOp (name="TestLights")
public class TestLights extends OpMode {
    private LightSystem lightSystem;

    @Override
    public void init() {
        lightSystem = lightSystem = new LightSystem(hardwareMap.get(DigitalChannel.class, "right_light"), hardwareMap.get(DigitalChannel.class, "left_light"));

    }

    @Override
    public void loop() {
        if(gamepad1.x) {
            lightSystem.leftLightOn();
        } else if(gamepad1.y) {
            lightSystem.rightLightOn();
        } else {
            lightSystem.off();
        }
    }
}
