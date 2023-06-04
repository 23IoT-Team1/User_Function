//step detector를 이용한 걸음 수 틀
//츨처 : https://stackoverflow.com/questions/67377284/onsensorchanged-is-not-triggering-for-the-step-detect-sensor
//step detector 코드 수정과 걸음 수 방법 참고 방법
//출처 : https://ppizil.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-Service-Binding%EA%B3%BC-StepCount-%EB%A7%8C%EB%B3%B4%EA%B8%B0

//image와 container의 반환값 해결방법
//출처 : https://stackoverflow.com/questions/3591784/views-getwidth-and-getheight-returns-0
//view의 좌표 개념 참고 사이트
//https://velog.io/@hwi_chance/Android-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-View%EC%9D%98-%EC%9C%84%EC%B9%98%EC%99%80-%ED%81%AC%EA%B8%B0
package com.gachon.userapp;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements SensorEventListener{

    //to use step function
    Button btnReset;
    private SensorManager sensorManager;
    private Sensor stepSensor, magnetormeter,accelermeter;
    private TextView txtSteps,txtdegree;
    private ImageView pointer;
    private int stepCounter=0;

    //to use compass function
    private float[] mR = new float[9];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetormeter = new float[3];

    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    private float azimuthunDegress= 0f;
    private float pivotX, pivotY;
    private float containerWidth, containerHeight;
    private RelativeLayout pointer_container;
    private RotateAnimationHelper rotateAnimationHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        magnetormeter = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelermeter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //layout
        txtSteps = findViewById(R.id.txtSteps);
        btnReset = findViewById(R.id.btnReset);
        txtdegree=findViewById(R.id.degree);
        pointer=findViewById(R.id.pointer);
        pointer_container =findViewById(R.id.pointer_container);
        
        //RotateAnimationHelper class
        rotateAnimationHelper = new RotateAnimationHelper(pointer, pointer_container);

        //step check 
        // txtSteps.setText(String.valueOf(stepCounter));


        pointer_container.post(new Runnable() {
            @Override
            public void run() {
                //pointer가 LinearLayout 내부의 RelativeLayout에 있어 
                //pointer의 상대적 위치를 측정할 때 RelativeLayout을 이용함
                pointer.setImageResource(R.drawable.pointer);
                containerWidth = pointer_container.getWidth();
                containerHeight = pointer_container.getHeight();
                pivotX =pointer.getX();
                pivotY =pointer.getY();
            }
        });


        //reset button click
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepCounter = 0;
                txtSteps.setText(String.valueOf(stepCounter));
            }
        });

    }


    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this, stepSensor, sensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, accelermeter, sensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, magnetormeter, sensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

            sensorManager.unregisterListener(this, stepSensor);
            sensorManager.unregisterListener(this, accelermeter);
            sensorManager.unregisterListener(this, magnetormeter);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //stepCounter
        /*if (event.sensor == stepSensor) {
                stepCounter++;
                txtSteps.setText(String.valueOf(stepCounter) + "\n" + String.valueOf((float) (stepCounter * 0.6)) + " m");
        }
        else */
        if (event.sensor == magnetormeter) {

            //System.arraycopy(event.values, 0, mLastMagnetormeter, 0, 2);
            mLastMagnetormeter[0] = event.values[0];
            mLastMagnetormeter[1] = event.values[1];
            mLastMagnetormeter[2] = event.values[2];

            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetormeter);

            mCurrentDegree = (int) (Math.toDegrees(SensorManager.getOrientation(mR, mOrientation)[0]))%360;
            azimuthunDegress = (int) (Math.toDegrees(SensorManager.getOrientation(mR, mOrientation)[0]) + 360) % 360;
            txtdegree.setText("Heading : " + Float.toString(azimuthunDegress) + " degrees");

            //rotate
            rotateAnimationHelper.rotate(mCurrentDegree, -azimuthunDegress, 1000);
            mCurrentDegree =- azimuthunDegress;
        } else if (event.sensor == accelermeter) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}