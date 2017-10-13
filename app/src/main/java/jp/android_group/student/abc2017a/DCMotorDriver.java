package jp.android_group.student.abc2017a;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.Pwm;

import java.io.IOException;

/**
 * Created by hayatokimura on 2017/10/13.
 */

public class DCMotorDriver {

    private static final double PWM_FREQUENCY_HZ = 50.0;
    private Pwm mPwm;
    private Gpio mVecGPIO;
    private PeripheralManagerService mManager;

    DCMotorDriver(PeripheralManagerService manager,String pwmName,String vecPin){
        mManager = manager;
        try{
            this.mPwm = mManager.openPwm(pwmName);
            this.mPwm.setPwmFrequencyHz(PWM_FREQUENCY_HZ);
            this.mPwm.setPwmDutyCycle(0.0);
            this.mPwm.setEnabled(true);

            this.mVecGPIO = mManager.openGpio(vecPin);
            this.mVecGPIO.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            this.mVecGPIO.setActiveType(Gpio.ACTIVE_LOW);
        }catch (IOException e){
            Log.w("DCMotorDriver", "Unable to access PWM", e);
        }
    }

    /*-100~100*/
    public void speed(double speed){

        if(100<speed || speed<-100){
            return;
        }
        if(0.0<=speed){
            try{
                this.mVecGPIO.setValue(false); //set HIGH
                this.mPwm.setPwmDutyCycle(speed);
            }catch (IOException e){
                Log.w("DCMotorDriver", "Unable to access PWM", e);
            }
        }else{
            try{
                this.mVecGPIO.setValue(true); //set LOW
                this.mPwm.setPwmDutyCycle(-speed);
            }catch (IOException e){
                Log.w("DCMotorDriver", "Unable to access PWM", e);
            }
        }
    }

    public void close(){

        if (mPwm != null) {
            try {
                mPwm.close();
                mPwm = null;
            } catch (IOException e) {
                Log.w("DCMotorDriver", "Unable to close mPwm", e);
            }
        }
        if (mVecGPIO != null) {
            try {
                mVecGPIO.close();
                mVecGPIO = null;
            } catch (IOException e) {
                Log.w("DCMotorDriver", "Unable to close mVecGPIO", e);
            }
        }
    }
}
