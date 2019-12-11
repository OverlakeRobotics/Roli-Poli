package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.teamcode.components.DriveSystem;
import org.firstinspires.ftc.teamcode.components.Tensorflow;
import org.firstinspires.ftc.teamcode.components.Vuforia;
import org.firstinspires.ftc.teamcode.opmodes.base.BaseOpMode;

public abstract class BaseAutonomous extends BaseOpMode {
    DistanceSensor distanceCenter;
    DistanceSensor distanceOutside;
    DriveSystem.Direction centerDirection;
    DriveSystem.Direction outsideDirection;
    Tensorflow tensorflow;
    ColorSensor colorSensor;
    Team currentTeam;

    public enum Team {
        RED, BLUE
    }

    public void init(BaseStateMachine.Team team) {
        if (team == BaseStateMachine.Team.RED) {
            distanceCenter = hardwareMap.get(DistanceSensor.class, "FRONTLEFTLIDAR");
            distanceOutside = hardwareMap.get(DistanceSensor.class, "FRONTRIGHTLIDAR");
            centerDirection = DriveSystem.Direction.LEFT;
            outsideDirection = DriveSystem.Direction.RIGHT;
        } else {
            distanceCenter = hardwareMap.get(DistanceSensor.class, "FRONTRIGHTLIDAR");
            distanceOutside = hardwareMap.get(DistanceSensor.class, "FRONTLEFTLIDAR");
            centerDirection = DriveSystem.Direction.RIGHT;
            outsideDirection = DriveSystem.Direction.LEFT;
        }
        tensorflow = new Tensorflow(hardwareMap, Vuforia.CameraChoice.WEBCAM1);
        colorSensor = hardwareMap.get(ColorSensor.class, "COLORSENSOR");
        currentTeam = team;
    }
}