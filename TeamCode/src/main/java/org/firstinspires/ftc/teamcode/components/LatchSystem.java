package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.Servo;

import java.util.EnumMap;


public class LatchSystem {

    public enum Latch {
        LEFT (0.715, 0.446, false),
        RIGHT (0.189, 0.456, false);

        private final double upPosition;
        private final double downPosition;
        private boolean latched;

        Latch(double downPosition, double upPosition, boolean latched) {
            this.downPosition = downPosition;
            this.upPosition = upPosition;
            this.latched = latched;
        }

        private double upPosition() {
            return upPosition;
        }

        private double downPosition() {
            return downPosition;
        }

        private boolean getLatched() {
            return latched;
        }

        private void setLatched(boolean isLatched) {
            latched = isLatched;
        }
    }

    public EnumMap<Latch, Servo> latches;


    private void initServo() {
        latches.forEach((name, servo) -> {
            servo.setPosition(name.upPosition());
        });
    }

    public LatchSystem(EnumMap<Latch, Servo> map) {
        latches = map;
        initServo();
    }

    public void toggle(Latch servoName) {
        if(servoName.getLatched()) {
            latches.get(servoName).setPosition(servoName.upPosition());
            servoName.setLatched(false);
        } else {
            latches.get(servoName).setPosition(servoName.downPosition());
            servoName.setLatched(true);
        }
    }

    public void bothUp() {
        latches.forEach((name, servo) -> {
            servo.setPosition(name.upPosition());
            name.latched = false;
        });
    }

    public void bothDown() {
        latches.forEach((name, servo) -> {
            servo.setPosition(name.downPosition());
            name.latched = true;
        });
    }
}