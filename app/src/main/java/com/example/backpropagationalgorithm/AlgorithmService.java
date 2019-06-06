package com.example.backpropagationalgorithm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlgorithmService extends IntentService {
    private static final String TAG = AlgorithmService.class.getSimpleName();

    MyBinder mBinder = new MyBinder();

    public AlgorithmService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    public void train(List<Double[]> patterns, int[] expectedOutputs, float learningRate,
                       Layer hiddenLayer, Layer outputLayer) {

        List<Double> hiddenLayerOutputs;
        double networkError = 1;
        int epochs = 0;
        List<Double> networkOutputs = new ArrayList<>();

        while (networkError > 0.01 && epochs < 10000) {
            networkOutputs.clear();
            for (int i = 0; i < patterns.size(); i++) {
                hiddenLayer.feedForward(Arrays.asList(patterns.get(i)));
                hiddenLayerOutputs = hiddenLayer.getNeuronsOutput();

                double networkOutput = outputLayer.feedForward(hiddenLayerOutputs);
                Log.i(TAG, "network output " + networkOutput);
                networkOutputs.add(networkOutput);

                double error = expectedOutputs[i] - networkOutput;

                outputLayer.calculateNeuronsDelta(error, null);
                hiddenLayer.calculateNeuronsDelta(error, outputLayer.getNeurons());

                outputLayer.calculateNeuronsWeightGradient();
                hiddenLayer.calculateNeuronsWeightGradient();

                outputLayer.recalculateWeights(learningRate);
                hiddenLayer.recalculateWeights(learningRate);
            }
            networkError = meanSquaredError(networkOutputs, patterns.size(), expectedOutputs);
            Log.i(TAG, "network error " + networkError);
            epochs++;
        }
        Log.i(TAG, "epochs " + epochs);
        stopSelf();
    }

    private Double meanSquaredError(List<Double> networkOutputs, int size, int[] expectedOutputs) {
        double squaredErrors = 0.0;
        for (int i = 0; i < size; i++) {
            double currentResult = (expectedOutputs[i] - networkOutputs.get(i));
            squaredErrors += Math.pow(currentResult, 2);
        }
        return (1f / size) * squaredErrors;
    }

    class MyBinder extends Binder {
        AlgorithmService getService() {
            return AlgorithmService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
