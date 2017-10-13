package jp.android_group.student.abc2017a;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hayatokimura on 2017/10/14.
 */

public class RecvControlClass extends AsyncTask<Void, Void, List> {

    private Handler mHandler = null;
    private Runnable mR;
    private DatagramSocket mServerSocket;
    private DCMotorDriver mDcMDriver;
    private ServoMotorDriver mServoDriver;

    RecvControlClass(DatagramSocket serverSocket, Handler handler, Runnable r,DCMotorDriver dcMotorDriver,ServoMotorDriver servoMotorDriver){
        this.mServerSocket = serverSocket;
        this.mHandler = handler;
        this.mR = r;
        this.mDcMDriver = dcMotorDriver;
        this.mServoDriver = servoMotorDriver;
    }

    @Override
    protected List doInBackground(Void... params) {

        List obj=null;
        try {
            obj  = (List) UDPObjectTransfer.receive(mServerSocket, 1024);
        }catch (Exception e){
            String erroval = e.toString();
            Log.d("",e.toString());
        }
        return obj;
    }

    @Override
    protected void onPostExecute(List result) {
        double steering = ((double)result.get(0)+100.0)/2.0; //convert range 0-100
        double speed = (double)result.get(1);
        mServoDriver.rotate(steering);
        mDcMDriver.speed(speed);
        mHandler.post(mR);
    }

}
