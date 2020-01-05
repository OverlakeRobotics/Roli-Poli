package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.EnumMap;

public class IntakeSystem {

    public enum MotorNames {
        RIGHT_INTAKE, LEFT_INTAKE
    }

    private EnumMap<IntakeSystem.MotorNames, DcMotor> motors;

    private Servo bottomServo;

    public IntakeSystem(EnumMap<IntakeSystem.MotorNames, DcMotor> motors, Servo servo) {
        this.motors = motors;
        initMotors();
        this.bottomServo = servo;
        this.bottomServo.setPosition(0.5);
    }

    private void initMotors() {
        motors.forEach((name, motor) -> {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            if (name == MotorNames.RIGHT_INTAKE) {
                motor.setDirection(DcMotorSimple.Direction.REVERSE);
            } else {
                motor.setDirection(DcMotorSimple.Direction.FORWARD);
            }
            motor.setPower(0.0);
        });
    }

    public void stop() {
        setMotorPowers(0.0);
        bottomServo.setPosition(0.5);
    }

    public void suck() {
        setMotorPowers(-0.7);
        bottomServo.setPosition(1.0);
    }

    public void unsuck() {
        setMotorPowers(0.7);
        bottomServo.setPosition(0.0);
    }

    private void setMotorPowers(double power) {
        for (DcMotor motor : motors.values()) {
            motor.setPower(power);
        }
    }
}
