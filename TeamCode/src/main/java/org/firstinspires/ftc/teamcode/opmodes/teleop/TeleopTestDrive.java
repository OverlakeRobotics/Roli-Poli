package org.firstinspires.ftc.teamcode.opmodes.teleop;

import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.components.DriveSystem;

import java.util.EnumMap;

@TeleOp (name="test")
public class TeleopTestDrive extends OpMode {
    protected DriveSystem driveSystem;

    @Override
    public void init() {

        EnumMap<DriveSystem.MotorNames, DcMotor> driveMap = new EnumMap<>(DriveSystem.MotorNames.class);
        for(DriveSystem.MotorNames name : DriveSystem.MotorNames.values()){
            driveMap.put(name,hardwareMap.get(DcMotor.class, name.toString()));
        }
        driveSystem = new DriveSystem(driveMap, hardwareMap.get(BNO055IMU.class, "imu"));
    }

    @Override
    public void loop() {
        float rx = (float) Math.pow(gamepad1.right_stick_x, 3);
        float lx = (float) Math.pow(gamepad1.left_stick_x, 3);
        float ly = (float) Math.pow(gamepad1.left_stick_y, 3);
        driveSystem.drive(rx, lx, -ly, gamepad1.x);
        Log.d("DriveSystem", "x pressed -- " + gamepad1.x);
        Log.d("DriveSystem", "left trig -- " + gamepad1.left_trigger);
        telemetry.addData("DriveSystem", "controller -- " + gamepad1.toString());
        telemetry.addData("DriveSystem", "left trig -- " + gamepad1.left_trigger);
    }
}
