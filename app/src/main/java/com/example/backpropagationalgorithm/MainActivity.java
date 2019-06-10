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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlotCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AlgorithmService mService;
    private List<Double[]> mPatterns = new ArrayList<>();
    private float mLearningRate;
    private int[] mExpectedOutputs;
    private Layer mHiddenLayer;
    private Layer mOutputLayer;
    private Intent mIntent;
    private TextView mEpochs;
    private TextView mError;
    private LinearLayout mContainer;
    private Button mCalculateButton;

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            AlgorithmService.MyBinder myBinder = (AlgorithmService.MyBinder) binder;
            mService = myBinder.getService();
            mService.setCallback(MainActivity.this);
            mService.setParams(mPatterns, mExpectedOutputs, mLearningRate, mHiddenLayer, mOutputLayer);
            startService(mIntent);
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

        mContainer = findViewById(R.id.values_container);
        mEpochs = findViewById(R.id.epochs);
        mError = findViewById(R.id.error);

        mCalculateButton = findViewById(R.id.calculate_button);
        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalculateButton.setEnabled(false);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
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

        mIntent = new Intent(this, AlgorithmService.class);
        bindService(mIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void draw(List<Entry> data) {
        LineChart chart = findViewById(R.id.chart);
        LineDataSet dataSet = new LineDataSet(data, "Label");
        dataSet.setValueTextColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCalculateButton.setEnabled(true);
            }
        });
    }

    @Override
    public void update(final int epoch, final double error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContainer.setVisibility(View.VISIBLE);
                mEpochs.setText(getString(R.string.epoch, String.valueOf(epoch)));
                mError.setText(getString(R.string.error, String.valueOf(error * 100)));
            }
        });
    }
}
