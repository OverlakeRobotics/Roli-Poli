package org.firstinspires.ftc.teamcode.tests;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.components.DriveSystem;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

@Autonomous (name="TestTicks")
public class TestTicks extends BaseOpMode {
    private final String TAG = "TestTicks";

    public void init() {
        super.init();
    }

    public void loop() {
        Log.d(TAG, "driving");
        driveSystem.driveToPositionTicks(1000, DriveSystem.Direction.FORWARD, 0.5);
    }
}

