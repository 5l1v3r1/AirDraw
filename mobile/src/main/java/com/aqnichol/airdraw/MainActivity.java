package com.aqnichol.airdraw;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.aqnichol.movements.Absolute;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends AppCompatActivity implements MessageApi.MessageListener {

    private GoogleApiClient client;
    private DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        drawingView = (DrawingView)findViewById(R.id.drawing_view);

        client = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Wearable.API)
                .build();

        final MessageApi.MessageListener listener = this;
        final GoogleApiClient finalClient = client;
        client.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                Wearable.MessageApi.addListener(finalClient, listener);
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onStart();
        client.connect();
    }

    @Override
    protected void onPause() {
        super.onStop();
        Wearable.MessageApi.removeListener(client, this);
        client.disconnect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        byte[] data = messageEvent.getData();
        try {
            Absolute[] positions = Absolute.unmarshal(data);
            drawingView.setPath(positions);
            drawingView.invalidate();
        } catch (Absolute.UnmarshalException e) {
            Log.e("AirDraw", "Unmarshal exception: " + e.getMessage());
        }
    }
}
