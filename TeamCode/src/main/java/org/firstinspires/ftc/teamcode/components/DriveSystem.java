package org.firstinspires.ftc.teamcode.components;

import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;
import java.util.EnumMap;

public class DriveSystem {

    public enum MotorNames {
        FRONTLEFT, FRONTRIGHT, BACKRIGHT, BACKLEFT
    }

    public enum Direction {
        FORWARD, BACKWARD, LEFT, RIGHT;

        private static boolean isStrafe(Direction direction) {
            return direction == LEFT || direction == RIGHT;
        }
    }

    public static final double SLOW_DRIVE_COEFF = 0.4;

    public static final String TAG = "DriveSystem";
    public static final double P_TURN_COEFF = 0.018;     // Larger is more responsive, but also less stable
    public static final double HEADING_THRESHOLD = 1 ;      // As tight as we can make it with an integer gyro

    public EnumMap<MotorNames, DcMotor> motors;

    public IMUSystem imuSystem;

    private int mTargetTicks;
    private double mTargetHeading;
    private double mInitHeading;
    private double mStrafeHeading;
    private boolean mStrafeSet;
    private int mTurnCounter;
    public boolean mSlowDrive;

    // 12.6 inches circumference of a wheel
    // 319 mm circumference of a wheel
    // 1120 ticks in a revolution
    // 1120 / 319 = 3.51
    private final double TICKS_IN_MM = 3.51;

    /**
     * Handles the data for the abstract creation of a drive system with four wheels
     */
    public DriveSystem(EnumMap<MotorNames, DcMotor> motors, BNO055IMU imu) {
        this.motors = motors;
        mTargetTicks = 0;
        mTurnCounter = 0;
        initMotors();
        imuSystem = new IMUSystem(imu);
    }

    /**
     * Set the power of the drive system
     * @param power power of the system
     */
    public void setMotorPower(double power) {
        for (DcMotor motor : motors.values()) {
            motor.setPower(power);
        }
    }

    public void initMotors() {
        motors.forEach((name, motor) -> {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            switch(name) {
                case FRONTLEFT:
                case BACKLEFT:
                    motor.setDirection(DcMotorSimple.Direction.REVERSE);
                    break;
                case FRONTRIGHT:
                case BACKRIGHT:
                    motor.setDirection(DcMotorSimple.Direction.FORWARD);
                    break;
            }
        });
        setMotorPower(0);
    }

    public void slowDrive(boolean on) {
        mSlowDrive = on;
    }

    private void setDriveSpeed(DcMotor motor, double motorPower) {
        motor.setPower(Range.clip(mSlowDrive ?
                SLOW_DRIVE_COEFF * motorPower : motorPower, -1, 1));
    }

    /**
     * Clips joystick values and drives the motors.
     * @param rightX Right X joystick value
     * @param leftX Left X joystick value
     * @param leftY Left Y joystick value in case you couldn't tell from the others
     */
    public void drive(float rightX, float leftX, float leftY) {
        // Prevent small values from causing the robot to drift
        if (Math.abs(rightX) < 0.01) {
            rightX = 0.0f;
        }
        if (Math.abs(leftX) < 0.01) {
            leftX = 0.0f;
        }
        if (Math.abs(leftY) < 0.01) {
            leftY = 0.0f;
        }

        double frontLeftPower = -leftY + rightX + leftX;
        double frontRightPower = -leftY - rightX - leftX;
        double backLeftPower = -leftY + rightX - leftX;
        double backRightPower = -leftY - rightX + leftX;



        motors.forEach((name, motor) -> {
            switch(name) {
                case FRONTRIGHT:
                    setDriveSpeed(motor, frontRightPower);
                    break;
                case BACKLEFT:
                    setDriveSpeed(motor, backLeftPower);
                    break;
                case FRONTLEFT:
                    setDriveSpeed(motor, frontLeftPower);
                    break;
                case BACKRIGHT:
                    setDriveSpeed(motor, backRightPower);
                    break;
            }
        });
        mSlowDrive = false;
    }


    public static final double STRAFE_COEFF = 0.09;
    public boolean driveToPositionTicks(int ticks, Direction direction, double maxPower) {
        if (!mStrafeSet) {
            mStrafeHeading = imuSystem.getHeading();
            mStrafeSet = true;
        }
        if(mTargetTicks == 0){
            mTargetTicks = direction == Direction.BACKWARD ? -ticks : ticks;
            motors.forEach((name, motor) -> {
                motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                if(Direction.isStrafe(direction)) {
                    int sign = direction == Direction.LEFT ? -1 : 1;
                    switch(name){
                        case FRONTLEFT:
                        case BACKRIGHT:
                            motor.setTargetPosition(sign * mTargetTicks);
                            break;
                        case FRONTRIGHT:
                        case BACKLEFT:
                            motor.setTargetPosition(sign * -mTargetTicks);
                            break;
                    }
                } else {
                    motor.setTargetPosition(mTargetTicks);
                }
                motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor.setPower(maxPower);
            });
        }

        for (DcMotor motor : motors.values()) {
            int offset = Math.abs(motor.getCurrentPosition() - mTargetTicks);
            if(offset <= 15){
                // Shut down motors
                setMotorPower(0);
                // Reset motors to default run mode
                setRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                // Reset target
                mTargetTicks = 0;
                mStrafeSet = false;
                mStrafeHeading = 0;
                // Motor has reached target
                return true;
            }
        }

        double currHeading = imuSystem.getHeading();
        if (mStrafeSet && Direction.isStrafe(direction)) {
            double diff = computeDegreesDiff(mStrafeHeading, currHeading);
            double correction = Range.clip(STRAFE_COEFF * diff, -1, 1);
            int sign = direction == Direction.LEFT ? -1 : 1;
            motors.forEach((name, motor) -> {
                switch(name) {
                    case FRONTLEFT:
                    case BACKLEFT:
                        motor.setPower(correction > 0 ? 1 - sign * correction: 1);
                        break;
                    case FRONTRIGHT:
                    case BACKRIGHT:
                        motor.setPower(correction < 0 ? 1 + sign * correction : 1);
                        break;
                }
            });


        }
        // Motor has not reached target
        return false;
    }

    public void stopAndReset() {
        setMotorPower(0.0);
        mTargetTicks = 0;
        mTargetHeading = 0;
        mStrafeHeading = 0;
        mStrafeSet = false;
    }

    public void setRunMode(DcMotor.RunMode runMode) {
        for (DcMotor motor : motors.values()) {
            motor.setMode(runMode);
        }
    }

    /**
     * Gets the minimum distance from the target
     * @return
     */
    public int  getMinDistanceFromTarget() {
        int distance = Integer.MAX_VALUE;
        for (DcMotor motor : motors.values()) {
            distance = Math.min(distance, motor.getTargetPosition() - motor.getCurrentPosition());
        }
        return distance;
    }

    public boolean driveToPosition(int millimeters, Direction direction, double maxPower) {
        return driveToPositionTicks(millimetersToTicks(millimeters), direction, maxPower);
    }

    /**
     * Converts millimeters to ticks
     * @param millimeters Millimeters to convert to ticks
     * @return
     */
    public int millimetersToTicks(int millimeters) {
        return (int) Math.round(millimeters * TICKS_IN_MM);
    }

    /**
     * Turns relative the heading upon construction
     * @param degrees The degrees to turn the robot by
     * @param maxPower The maximum power of the motors
     */
    // TODO
    public boolean turnAbsolute(double degrees, double maxPower) {
        // Since it is vertical, use pitch instead of heading
        return turn(diffFromAbs(degrees), maxPower);
    }

    /**
     * Turns the robot by a given number of degrees
     * @param degrees The degrees to turn the robot by
     * @param maxPower The maximum power of the motors
     */
    public boolean turn(double degrees, double maxPower) {
        // Since controller hub is vertical, use pitch instead of heading
        double heading = imuSystem.getHeading();
        // if controller hub is flat: double heading = imuSystem.getHeading();
        if(mTargetHeading == 0) {
            mTargetHeading = (heading + degrees) % 360;
            Log.d(TAG, "Setting Heading -- Target: " + mTargetHeading);

            Log.d(TAG, "Degrees: " + degrees);
        }
        double difference = mTargetHeading - heading;
        if (mTurnCounter == 0) {
            mInitHeading = difference;
        }
        Log.d(TAG,"Difference: " + difference);
        mTurnCounter++;
        return onHeading(maxPower, heading);

    }

    /**
     * Perform one cycle of closed loop heading control.
     * @param speed     Desired speed of turn
     */
    public boolean onHeading(double speed, double heading) {
        double leftSpeed;

        // determine turn power based on +/- error
        double error = getError(heading);

        // If it gets there: stop
        if (Math.abs(error) <= HEADING_THRESHOLD) {
            mTargetHeading = 0;
            setMotorPower(0);
            mTurnCounter = 0;
            return true;
        }

        // TODO
        // Go full speed until 60% there
        leftSpeed = error > Math.abs(0.85 * (mInitHeading)) ? speed : (speed * getSteer(error));
        // leftSpeed = speed * getSteer(error);


        if (leftSpeed < 0) {
            leftSpeed = Range.clip(leftSpeed, -1.0, -0.22);
        } else {
            leftSpeed = Range.clip(leftSpeed, 0.22, 1.0);
        }
        // Send desired speeds to motors.
        tankDrive(leftSpeed, -leftSpeed);

        return false;
    }

    /**
     * getError determines the error between the target angle and the robot's current heading
     * @param   heading  Desired angle (relative to global reference established at last Gyro Reset).
     * @return  error angle: Degrees in the range +/- 180. Centered on the robot's frame of reference
     *          +ve error means the robot should turn LEFT (CCW) to reduce error.
     */
    public double getError(double heading) {
        // calculate error in -179 to +180 range  (
        double robotError = mTargetHeading - heading;
        Log.d(TAG,"Robot Error: " + robotError);
        while (robotError > 180) {
            robotError -= 360;
        }
        while (robotError <= -180) {
            robotError += 360;
        }
        return robotError;
    }

    public double diffFromAbs(double heading) {
        // calculate error in -179 to +180 range
        // When vertical use pitch instead of heading
        double robotDiff = heading - imuSystem.getHeading();
        Log.d(TAG,"Difference from initial: " + robotDiff);
        while (robotDiff > 180) {
            robotDiff -= 360;
        }
        while (robotDiff <= -180) {
            robotDiff += 360;
        }
        return robotDiff;
    }

    /**
     * returns desired steering force.  +/- 1 range.  +ve = steer left
     * @param error   Error angle in robot relative degrees
     * @return
     */
    // TODO
    public double getSteer(double error) {
        return Range.clip(error *  P_TURN_COEFF, -1, 1);
    }

    /**
     * Causes the system to tank drive
     * @param leftPower sets the left side power of the robot
     * @param rightPower sets the right side power of the robot
     */
    public void tankDrive(double leftPower, double rightPower) {
        motors.forEach((name, motor) -> {
            switch(name) {
                case FRONTLEFT:
                case BACKLEFT:
                    motor.setPower(leftPower);
                    break;
                case FRONTRIGHT:
                case BACKRIGHT:
                    motor.setPower(rightPower);
                    break;
            }
        });
    }

    /**
     * Gets the turn power needed
     * @param degrees Number of degrees to turn
     * @return motor power from 0 - 0.8
     */
    private double getTurnPower(double degrees, double maxPower) {
        // double power = Math.abs(degrees / 100.0);
        return Range.clip(degrees / 100.0, -maxPower, maxPower);
    }

    private double computeDegreesDiff(double targetHeading, double heading) {
        double diff = targetHeading - heading;
        if (diff > 180) {
            return diff - 360;
        }
        if (diff < -180) {
            return 360 + diff;
        }
        return diff;
    }

}