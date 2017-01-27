package com.aqnichol.airdraw;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.TextView;

import com.aqnichol.movements.Movement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private static final int sensorDelay = SensorManager.SENSOR_DELAY_GAME;

    private TextView mainLabel;
    private SensorManager sensorManager;
    private Sensor sensor;
    private long lastTimestamp = 0;

    private ArrayList<Movement> path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLabel = (TextView)findViewById(R.id.main_label);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) {
            mainLabel.setText(R.string.sensor_error);
            return;
        }
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (sensor == null) {
            mainLabel.setText(R.string.sensor_error);
            return;
        }

        mainLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTracking();
            }
        });
    }

    protected void toggleTracking() {
        if (path == null) {
            startTracking();
        } else {
            stopTracking();
        }

        sensorManager.registerListener(this, sensor, sensorDelay);
    }

    protected void startTracking() {
        path = new ArrayList<>();
        lastTimestamp = -1;

        mainLabel.setText(R.string.tap_to_stop);
        sensorManager.registerListener(this, sensor, sensorDelay);
    }

    protected void stopTracking() {
        sensorManager.unregisterListener(this);
        mainLabel.setText(R.string.tap_to_start);

        Movement[] array = new Movement[path.size()];
        path.toArray(array);
        final byte[] data = Movement.marshal(array);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO: send data to the phone.
            }
        }).start();
        path = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (path == null) {
            return;
        }
        float duration = 0;
        if (lastTimestamp > 0) {
            duration = (float)(event.timestamp - lastTimestamp) / 1e9f;
        }
        lastTimestamp = event.timestamp;
        path.add(new Movement(duration, event.values[0], event.values[1], event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
