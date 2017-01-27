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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME;
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final float HIGH_PASS_RATE = 0.2f;

    private TextView mainLabel;
    private SensorManager sensorManager;
    private Sensor motionSensor;
    private long lastTimestamp = 0;

    private float gravityX = 0, gravityY = 0, gravityZ = 0;

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
        motionSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (motionSensor == null) {
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
    }

    protected void startTracking() {
        path = new ArrayList<>();
        lastTimestamp = -1;

        mainLabel.setText(R.string.tap_to_stop);
        sensorManager.registerListener(this, motionSensor, SENSOR_DELAY);
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
                sendData(data);
            }
        }).start();
        path = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (path == null) {
            return;
        }

        gravityX = gravityX + HIGH_PASS_RATE*(event.values[0] - gravityX);
        gravityY = gravityY + HIGH_PASS_RATE*(event.values[1] - gravityY);
        gravityZ = gravityZ + HIGH_PASS_RATE*(event.values[2] - gravityZ);

        float duration = 0;
        if (lastTimestamp > 0) {
            duration = (float)(event.timestamp - lastTimestamp) / 1e9f;
        }
        lastTimestamp = event.timestamp;
        path.add(new Movement(duration, event.values[0]-gravityX, event.values[1]-gravityY,
                event.values[2]-gravityZ));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void sendData(final byte[] data) {
        GoogleApiClient client = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Wearable.API)
                .build();
        client.blockingConnect(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        if (!client.isConnected()) {
            displayError("Could not connect");
            return;
        }
        try {
            NodeApi.GetConnectedNodesResult result;
            result = Wearable.NodeApi.getConnectedNodes(client).await();
            if (!result.getStatus().isSuccess()) {
                displayError("Could not get nodes");
                return;
            }
            List<Node> nodes = result.getNodes();
            if (nodes.size() == 0) {
                displayError("No connected devices");
                return;
            }
            MessageApi.SendMessageResult r = Wearable.MessageApi.sendMessage(client,
                    nodes.get(0).getId(), "/movements", data).await();
            if (!r.getStatus().isSuccess()) {
                displayError("Could not send data");
            }
        } finally {
            client.disconnect();
        }
    }

    private void displayError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainLabel.setText(message);
            }
        });
    }
}
