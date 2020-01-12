package org.firstinspires.ftc.teamcode.components;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import  com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.EnumMap;
import java.util.concurrent.TimeUnit;

/*
    This class controls everything related to the arm, including driver assist features.
    IMPORTANT: When working on this class (and arm stuff in general),
    keep the servo names consistent: (from closest to the block to farthest)
        - Gripper
        - Wrist
        - Elbow
        - Pivot
 */
public class ArmSystem {
    public enum Position {
        // Double values ordered Pivot, elbow, wrist.
        POSITION_HOME(new double[] {0.96, 0.15, 0.79}, 0),
        POSITION_WEST(new double[] {0.16, 0.22, 0.72}),
        POSITION_SOUTH(new double[] {0.16, 0.22, 0.37}),
        POSITION_EAST(new double[] {0.16, 0.58, 0.37}),
        POSITION_NORTH(new double[] {0.16, 0.58, 0.05}),
        POSITION_CAPSTONE(new double[] {0.53, 0.21, 0.80}, 0.5);

        private double[] posArr;
        private double height;

        Position(double[] positions, double height) {
            posArr = positions;
            this.height = height;
        }

        Position(double[] positions) {
            posArr = positions;
        }

        private double[] getPos() {
            return this.posArr;
        }
        private double getHeight() {return this.height; }
    }

    public enum ServoNames {
        GRIPPER, WRIST, ELBOW, PIVOT
    }
    public enum ArmState {
        STATE_CHECK_CLEARANCE,
        STATE_CLEAR_CHASSIS,
        STATE_ADJUST_ORIENTATION,
        STATE_SETTLE,
        STATE_RAISE,
        STATE_LOWER_HEIGHT,
        STATE_DROP,
        STATE_WAITING,
        STATE_OPEN,
        STATE_CLEAR_TOWER,
        STATE_HOME,
        STATE_INITIAL,
    }
    public enum ArmDirection {
        UP, DOWN, IDLE
    }

    private ArmState mCurrentState;
    private ArmDirection mDirection;

    // Don't change this unless in calibrate() or init(), is read in the calculateHeight method
    private int mCalibrationDistance;

    private EnumMap<ServoNames, Servo> servoEnumMap;
    private DcMotor slider;

    // This is in block positions, not ticks
    public double mTargetHeight;
    // The queued position
    private double mQueuePos;
    // This variable is used for all the auto methods.
    private Deadline mWaiting;

    private final int MAX_HEIGHT = 6;
    private final int INCREMENT_HEIGHT = 525; // how much the ticks increase when a block is added
    private final double GRIPPER_OPEN = 0.9;
    private final double GRIPPER_CLOSE = 0.3;
    private final int WAIT_TIME = 400;

    public static final String TAG = "ArmSystem"; // for debugging

    /*
     If the robot is at the bottom of the screen, and X is the block:
     XO
     XO  <--- Position west
     OO
     XX  <--- Position south
     OX
     OX  <--- Position east
     XX
     OO  <--- Position north
     */
    public ArmSystem(EnumMap<ServoNames, Servo> servos, DcMotor slider) {
        servoEnumMap = servos;
        this.slider = slider;
        this.mCalibrationDistance = slider.getCurrentPosition();
        this.slider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.slider.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        mWaiting = new Deadline(WAIT_TIME, TimeUnit.MILLISECONDS);
        mTargetHeight = 0;
        setSliderHeight(mTargetHeight);
        movePresetPosition(Position.POSITION_HOME);
        mCurrentState = ArmState.STATE_CHECK_CLEARANCE;
        openGripper();
    }

    // Go to "west" position
    public void moveWest() {
        movePresetPosition(Position.POSITION_WEST);
    }

    // Go to "north" position
    public void moveNorth() {
        movePresetPosition(Position.POSITION_NORTH);
    }

    public void moveEast() {
        movePresetPosition(Position.POSITION_EAST);
    }

    public void moveSouth() {
        movePresetPosition(Position.POSITION_SOUTH);
    }

    // Go to capstone position
    public boolean moveToCapstone() {
        return moveInToPosition(Position.POSITION_CAPSTONE);
    }

    // Helper method for going to capstone or home
    private boolean moveInToPosition(Position position) {
        switch(mCurrentState) {
            case STATE_CHECK_CLEARANCE:
                ensureIsAboveChassis();
                break;
            case STATE_CLEAR_CHASSIS:
                if (runSliderToTarget()) {
                    movePresetPosition(position);
                    mWaiting.reset();
                    mCurrentState = ArmState.STATE_ADJUST_ORIENTATION;
                }
                break;
            case STATE_ADJUST_ORIENTATION:
                if(mWaiting.hasExpired()) {
                    openGripper();
                    setSliderHeight(position.getHeight());
                    mCurrentState = ArmState.STATE_SETTLE;
                }
                break;
            case STATE_SETTLE:
                if (runSliderToTarget()) {
                    mCurrentState = ArmState.STATE_CHECK_CLEARANCE;
                    return true;
                }
                break;
        }
        return false;
    }

    // Auto method for moving out to the queued height and given position
    public boolean moveOutToPosition(Position position) {
        switch(mCurrentState) {
            case STATE_CHECK_CLEARANCE:
                if (mQueuePos < 2) {
                    setSliderHeight(2);
                } else {
                    setSliderHeight(mQueuePos);
                }
                mCurrentState = ArmState.STATE_CLEAR_CHASSIS;
            case STATE_CLEAR_CHASSIS:
                if (runSliderToTarget()) {
                    movePresetPosition(position);
                    mWaiting.reset();
                    mCurrentState = ArmState.STATE_ADJUST_ORIENTATION;
                }
                break;
            case STATE_ADJUST_ORIENTATION:
                if(mWaiting.hasExpired()) {
                    setSliderHeight(mQueuePos);
                    mCurrentState = ArmState.STATE_RAISE;
                }
                break;
            case STATE_RAISE:
                if (runSliderToTarget()) {
                    Log.d(TAG, "Run");
                    incrementQueue();
                    mCurrentState = ArmState.STATE_CHECK_CLEARANCE;
                    return true;
                }
                break;
        }
        return false;
    }

    // Makes sure that the arm is above height 2 in order to clear the chassis
    private void ensureIsAboveChassis() {
        if (getSliderPos() < calculateHeight(2)) {
            setSliderHeight(2);
        } else {
            slider.setTargetPosition(slider.getCurrentPosition());
        }
        mCurrentState = ArmState.STATE_CLEAR_CHASSIS;
    }

    // Go to the home position
    // Moves the slider up to one block high, moves the gripper to the home position, and then moves
    // back down so we can fit under the bridge.
    public boolean moveToHome() {
        return moveInToPosition(Position.POSITION_HOME);
    }

    public void openGripper() {
        servoEnumMap.get(ServoNames.GRIPPER).setPosition(GRIPPER_OPEN);
    }

    public void closeGripper() {
        servoEnumMap.get(ServoNames.GRIPPER).setPosition(GRIPPER_CLOSE);
    }

    public void toggleGripper() {
        if (servoEnumMap.get(ServoNames.GRIPPER).getPosition() == GRIPPER_CLOSE) {
            openGripper();
        } else {
            closeGripper();
        }
    }

    private void movePresetPosition(Position pos){
        double[] posArray = pos.getPos();
        servoEnumMap.get(ServoNames.PIVOT).setPosition(posArray[0]);
        servoEnumMap.get(ServoNames.ELBOW).setPosition(posArray[1]);
        servoEnumMap.get(ServoNames.WRIST).setPosition(posArray[2]);
    }

    // Pos should be the # of blocks high it should be
    // MUST BE CALLED before runSliderToTarget
    public void setSliderHeight(double pos) {
        mTargetHeight = Range.clip(pos, 0, MAX_HEIGHT);
        setPosTarget();
        if (slider.getCurrentPosition() == calculateHeight(mTargetHeight)) {
            mDirection = ArmDirection.IDLE;
            return;
        } else if (slider.getCurrentPosition() > calculateHeight(mTargetHeight)) {
            mDirection = ArmDirection.DOWN;
        } else {
            mDirection = ArmDirection.UP;
        }
        slider.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setSliderHeight(int pos) {
        // * 1.0 converts to double
        setSliderHeight(pos * 1.0);
    }

    // Little helper method for setSliderHeight
    private int calculateHeight(double pos){
        return (int) (pos == 0 ? mCalibrationDistance : mCalibrationDistance + (pos * INCREMENT_HEIGHT));
    }

    private double reverseCalcHeight(double pos) {
        return  Math.round(pos/INCREMENT_HEIGHT);
    }

    // Must be called every loop
    public boolean runSliderToTarget() {
        Log.d(TAG, "Direction:" + mDirection);
        Log.d(TAG, "Curr Pos" + slider.getCurrentPosition());
        Log.d(TAG, "Target Pos" + slider.getTargetPosition());
        if (mDirection == ArmDirection.IDLE) {
            return true;
        } else {
            slider.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        if (mDirection == ArmDirection.UP && slider.getCurrentPosition() <  slider.getTargetPosition()){
            slider.setPower(1.0);
        } else if (mDirection == ArmDirection.DOWN && slider.getCurrentPosition() > slider.getTargetPosition()) {
            slider.setPower(-1.0);
        } else {
            mDirection = ArmDirection.IDLE;
            slider.setTargetPosition(slider.getCurrentPosition());
            slider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            return true;
        }
        return false;
    }

    public int getSliderPos() {
        return slider.getCurrentPosition();
    }

    private void setPosTarget() {
        slider.setTargetPosition(calculateHeight(mTargetHeight));
    }

    public void resetQueue() {
        mQueuePos = 0;
    }

    public void incrementQueue() {
        mQueuePos++;
        if (mQueuePos > MAX_HEIGHT) {
            resetQueue();
        }
    }

    public void decrementQueue() {
        mQueuePos = Math.max(0, mQueuePos - 1);
    }

    public double getQueue() {
        return mQueuePos;
    }


    public boolean place() {
        switch(mCurrentState) {
            // Drops the block
            case STATE_DROP:
                Log.d(TAG, "started drop");
                openGripper();
                setSliderHeight(mTargetHeight + 0.5);
                mCurrentState = ArmState.STATE_CLEAR_TOWER;
                Log.d(TAG, "start waiting");
                break;
            // Raises up a half-block
            case STATE_CLEAR_TOWER:
                if (runSliderToTarget()) {
                    Log.d(TAG, "tower cleared");
                    mCurrentState = ArmState.STATE_INITIAL;
                    return true;
                }
                break;
        }
        return false;
    }

    public void changePlaceState(ArmState state) {
        mCurrentState = state;
    }

    public void startPlacing() {
        mCurrentState = ArmState.STATE_DROP;
    }

    private boolean areRoughlyEqual(int a, int b) {
        return Math.abs(Math.abs(a) - Math.abs(b)) < 10;
    }

}