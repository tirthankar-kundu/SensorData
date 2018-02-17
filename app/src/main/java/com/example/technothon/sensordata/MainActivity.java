package com.example.technothon.sensordata;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static android.hardware.Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR;
import static android.hardware.Sensor.TYPE_GRAVITY;
import static android.hardware.Sensor.TYPE_GYROSCOPE;
import static android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;
import static android.hardware.Sensor.TYPE_STEP_COUNTER;

public class MainActivity extends AppCompatActivity{

    private SensorManager manager;
    private SensorEventListener listener;
    private EditText editText;
    String value="";
    private Button b1,b2,b3,b4;
    private Socket mSocket;
    private Map<String ,String > sensordata=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextIP);
        manager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        listener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                float dataset[];
                value = "";
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    dataset = event.values;
                    for (float data : dataset) {
                        value += data + " ";
                    }
                    sensordata.put("accelerometer", value);
                    Log.d("Sensor", "Accelerometer" + value);

                }
                if (sensor.getType() == TYPE_GYROSCOPE) {
                    dataset = event.values;
                    for (float data : dataset) {
                        value += data + " ";
                    }
                    Log.d("Sensor", "Gyroscope" + value);
                    sensordata.put("gyroscope", value);
                }
                if (sensor.getType() == TYPE_LINEAR_ACCELERATION) {
                    dataset = event.values;
                    for (float data : dataset) {
                        value += data + " ";
                    }
                    sensordata.put("Linear", value);
                    Log.d("Sensor", "LInear" + value);

                }
                if (sensor.getType() == TYPE_STEP_COUNTER) {
                    dataset = event.values;
                    for (float data : dataset) {
                        value += data + " ";
                    }
                    sensordata.put("Step Counter", value);
                    Log.d("Sensor", "Step" + value);

                }
            }
        };

        b1 = findViewById(R.id.btn_send);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Sending", Toast.LENGTH_SHORT).show();
                Log.d("Socket", String.valueOf(mSocket.connected()));
                String data = "{\"acceleration\":\"abc\"}";
                attemptSend(data);
            }
        });

        b2 = findViewById(R.id.btn_start);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.registerListener(listener, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
                manager.registerListener(listener, manager.getDefaultSensor(TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
                manager.registerListener(listener, manager.getDefaultSensor(TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
                }
        });

        b3=findViewById(R.id.btn_stop);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSocket.disconnect();
                manager.unregisterListener(listener);
            }
        });

        b4=findViewById(R.id.btn_ok);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mSocket = IO.socket("http://"+editText.getText().toString()+"/");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                mSocket.connect();
            }
        });
    }

    private void attemptSend(String message) {
        Log.d("Test",message);
        mSocket.emit("test", message);
    }
}
