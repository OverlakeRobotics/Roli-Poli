package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.Servo;


public class LatchSystem {
    private Servo leftServo;
    private Servo rightServo;
    public boolean rightLatched;
    public boolean leftLatched;
    private final double LEFT_DOWN_POSITION = 0.714623491755917;
    private final double RIGHT_DOWN_POSITION = 0.18877972287358707;
    private final double LEFT_UP_POSITION = 0.4461410963209186;
    private final double RIGHT_UP_POSITION = 0.45632352852029817;

    public LatchSystem(Servo left, Servo right) {
        this.leftServo = left;
        this.rightServo = right;
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

    public void latchLeft() {
        leftLatched = true;
        leftServo.setPosition(LEFT_DOWN_POSITION);
        leftServo.close();
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