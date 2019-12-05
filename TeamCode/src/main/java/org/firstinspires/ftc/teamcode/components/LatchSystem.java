package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.Servo;

import java.util.EnumMap;


public class LatchSystem {

    public enum Latch {
        LEFT (0.715, 0.446),
        RIGHT (0.189, 0.456);

        private final double upPosition;
        private final double downPosition;

        Latch(double downPosition, double upPosition) {
            this.downPosition = downPosition;
            this.upPosition = upPosition;
        }

        private double upPosition() {
            return upPosition;
        }

        private double downPosition() {
            return downPosition;
        }
    }

    public EnumMap<Latch, Servo> latches;

    private void initServo() {
        latches.forEach((name, servo) -> {
            servo.setPosition(name.upPosition());
        });
    }
    private EnumMap<ServoNames, Servo> latchMap;
    public boolean rightLatched;
    public boolean leftLatched;
    private final double LEFT_DOWN_POSITION = 0.714623491755917;
    private final double RIGHT_DOWN_POSITION = 0.18877972287358707;
    private final double LEFT_UP_POSITION = 0.4461410963209186;
    private final double RIGHT_UP_POSITION = 0.45632352852029817;

    public LatchSystem(EnumMap<ServoNames, Servo> map) {
        latchMap = map;
        initServo();
    }

    private void initServo() {
        unlatchLeft();
        unlatchRight();
    }




    public void toggleLeft() {
        if (leftLatched) {
            unlatchLeft();
        } else {
            latchLeft();
        }
    }

    public void latch(ServoNames name) {
        leftLatched = true;
        latchMap.get(name).setPosition(LEFT_DOWN_POSITION);
        latchMap.get(name).close();
    }

    public void unlatchLeft() {
        leftLatched = false;
        leftServo.setPosition(LEFT_UP_POSITION);
        leftServo.close();
    }



    public void toggleRight() {
        if (rightLatched) {
            unlatchRight();
        } else {
            latchRight();
        }
    }

    public void latchRight() {
        rightLatched = true;
        rightServo.setPosition(RIGHT_DOWN_POSITION);
        rightServo.close();
    }

    public void unlatchRight() {
        rightLatched = false;
        rightServo.setPosition(RIGHT_UP_POSITION);
        rightServo.close();
    }
}