package jp.android_group.student.rcdroid;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;

import java.net.DatagramSocket;
import java.util.List;

public class MainActivity extends Activity {

    private static final String PWM_NAME = "";
    private Pwm mPwmDc;
    private Gpio mGPIOForwardBackDc;
    private PeripheralManager mPeripheralManager = PeripheralManager.getInstance();
    private DCMotorDriver mDcMDriver;
    private double mSpeed=0;

    private ServoMotorDriver mServoDriver;
    private Handler mHandler = new Handler();
    DatagramSocket mServerSocket;
    private TextView debugTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        debugTextView = (TextView)findViewById(R.id.debug_text_view);
        Button forwardtBtn = (Button)findViewById(R.id.dcforward);
        Button backBtn = (Button)findViewById(R.id.dcback);
        Button stopBtn = (Button)findViewById(R.id.dcstop);
        SeekBar servoSeekBar = (SeekBar)findViewById(R.id.seekBar);
        List<String> portList = mPeripheralManager.getPwmList();
        if (portList.isEmpty()) {
            debugTextView.setText("No PWM port available on this device.");
            Log.i("MainActivity", "No PWM port available on this device.");
        } else {
            debugTextView.setText("List of available ports: " + portList);
            Log.i("MainActivity", "List of available ports: " + portList);
        }

        mDcMDriver = new DCMotorDriver(mPeripheralManager,portList.get(0),"BCM21");
        mServoDriver = new ServoMotorDriver(mPeripheralManager,portList.get(1));

        try{
            mServerSocket = new DatagramSocket(12345);
            mHandler.post(recvControl);
        }catch (Exception e){}


        forwardtBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(mSpeed<0){
                    mSpeed=0;
                }
                mSpeed+=10;
                mDcMDriver.speed(mSpeed);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(0<=mSpeed){
                    mSpeed=0;
                }
                mSpeed-=10;
                mDcMDriver.speed(mSpeed);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mSpeed=0;
                mDcMDriver.speed(mSpeed);
            }
        });

        servoSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // ツマミをドラッグしたときに呼ばれる
                        debugTextView.setText("seekbar:"+seekBar.getProgress());
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // ツマミに触れたときに呼ばれる
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // ツマミを離したときに呼ばれる
                        mServoDriver.rotate(seekBar.getProgress());
                        debugTextView.setText("seekbar:"+seekBar.getProgress());
                    }
                }
        );
    }


    private Thread recvControl = new Thread() {
        @Override
        public void run() {
            try {
                RecvControlClass recvctl = new RecvControlClass(mServerSocket,mHandler,this,mDcMDriver,mServoDriver);
                recvctl.execute();
            }catch (Exception e){}
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDcMDriver.close();
        mServoDriver.close();
    }
}
