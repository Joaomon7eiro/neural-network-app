package com.example.backpropagationalgorithm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AlgorithmService mService;
    private List<Double[]> mPatterns = new ArrayList<>();
    private float mLearningRate;
    private int[] mExpectedOutputs;
    private Layer mHiddenLayer;
    private Layer mOutputLayer;

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            AlgorithmService.MyBinder myBinder = (AlgorithmService.MyBinder) binder;
            mService = myBinder.getService();
            mService.train(mPatterns, mExpectedOutputs, mLearningRate, mHiddenLayer, mOutputLayer);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button calculateButton = findViewById(R.id.calculate_button);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    unbindService(mServiceConnection);
                } catch (Exception e) {
                    Log.e(TAG, "Service not started yet");
                    e.printStackTrace();
                }
                startAlgorithm();
            }
        });
    }

    private void startAlgorithm() {
//        test
//        Double[] h1 = new Double[]{-0.46, -0.07, 0.22};
//        Double[] h2 = new Double[]{0.10, 0.94, 0.46};
//        Double[] o1 = new Double[]{0.78, -0.22, 0.58};
//        Neuron neuronH1 = new Neuron(3, Arrays.asList(h1));
//        Neuron neuronH2 = new Neuron(3, Arrays.asList(h2));
//        Neuron neuronO1 = new Neuron(3, Arrays.asList(o1));

        Neuron neuronH1 = new Neuron(3);
        Neuron neuronH2 = new Neuron(3);
        Neuron neuronO1 = new Neuron(3);
        Neuron neuronO2 = new Neuron(3);

        mExpectedOutputs = new int[]{0, 1, 1, 0};

        mPatterns = new ArrayList<>();
        mPatterns.add(new Double[]{0.0, 0.0});
        mPatterns.add(new Double[]{0.0, 1.0});
        mPatterns.add(new Double[]{1.0, 0.0});
        mPatterns.add(new Double[]{1.0, 1.0});

        mLearningRate = 0.2f;

        List<Neuron> hiddenLayerNeurons = new ArrayList<>();
        hiddenLayerNeurons.add(neuronH1);
        hiddenLayerNeurons.add(neuronH2);
        mHiddenLayer = new Layer(hiddenLayerNeurons);

        List<Neuron> outputLayerNeurons = new ArrayList<>();
        outputLayerNeurons.add(neuronO1);
        outputLayerNeurons.add(neuronO2);
        mOutputLayer = new Layer(outputLayerNeurons);

        Intent intent = new Intent(this, AlgorithmService.class);
        startService(intent);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }
}
