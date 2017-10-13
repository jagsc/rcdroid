package jp.android_group.student.abc2017a;

import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.Pwm;

import java.io.IOException;

/**
 * Created by hayatokimura on 2017/10/13.
 */

public class ServoMotorDriver {
    private static final double PWM_FREQUENCY_HZ = 50.0;
    private Pwm mPwm;
    private PeripheralManagerService mManager;
    private Handler mHandler = new Handler();

    ServoMotorDriver(PeripheralManagerService manager, String pwmName){
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
      *duty: 5-10
      *parcent 0-100
    */
    public void rotate(double parcent){
        if(parcent<0.0 || 100.0<parcent){
            return;
        }
        double duty = 0.05*parcent+5; //parcent to duty (range 5-10)
        try{
            this.mPwm.setPwmDutyCycle(duty);
            this.mPwm.setEnabled(true);
            //mHandler.postDelayed(stopPwm,500);
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
