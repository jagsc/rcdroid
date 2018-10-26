package jp.android_group.student.abc2017a;

import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;

import java.io.IOException;

/**
 * Created by hayatokimura on 2017/10/13.
 */

public class ServoMotorDriver {
    private static final double PWM_FREQUENCY_HZ = 50.0;
    private Pwm mPwm;
    private PeripheralManager mManager;
    private Handler mHandler = new Handler();

    ServoMotorDriver(PeripheralManager manager, String pwmName){
        this.mManager = manager;
        try{
            this.mPwm = mManager.openPwm(pwmName);
            this.mPwm.setPwmFrequencyHz(PWM_FREQUENCY_HZ);
            this.mPwm.setPwmDutyCycle(0.0);
            this.mPwm.setEnabled(true);
        }catch (IOException e){
            Log.w("ServoMotorDriver", "Unable to access PWM", e);
        }
    }

    /*
      *duty: 6-9
      *parcent 0-100
    */
    public void rotate(double percent){
        if(percent<0.0 || 100.0<percent){
            return;
        }
        double duty = 0.03*percent+6; //parcent to duty (range 6-9)
        try{
            this.mPwm.setPwmDutyCycle(duty);
            this.mPwm.setEnabled(true);
            //mHandler.postDelayed(stopPwm,1000);
        }catch (IOException e){
            Log.w("ServoMotorDriver", "Unable to access PWM", e);
        }
    }

    private Runnable stopPwm = new Runnable() {
        @Override
        public void run() {
            try{
                mPwm.setEnabled(false);
            }catch (IOException e){
                Log.w("ServoMotorDriver", "Unable to access PWM", e);
            }
        }
    };

    public void close(){

        if (mPwm != null) {
            try {
                mPwm.close();
                mPwm = null;
            } catch (IOException e) {
                Log.w("ServoMotorDriver", "Unable to close mPwm", e);
            }
        }
        mHandler.removeCallbacks(stopPwm);
    }
}
