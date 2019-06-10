package com.example.backpropagationalgorithm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlgorithmService extends IntentService {
    private static final String TAG = AlgorithmService.class.getSimpleName();
    MyBinder mBinder = new MyBinder();
    private List<Double[]> mPatterns;
    private int[] mExpectedOutputs;
    private float mLearningRate;
    private Layer mHiddenLayer;
    private Layer mOutputLayer;
    private PlotCallback mCallback;

    public AlgorithmService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<Double> hiddenLayerOutputs;
        double networkError = 1;
        int epochs = 0;
        List<Double> networkOutputs = new ArrayList<>();

        List<Entry> data = new ArrayList<>();

        while (networkError > 0.01 && epochs < 10000) {
            networkOutputs.clear();
            for (int i = 0; i < mPatterns.size(); i++) {
                mHiddenLayer.feedForward(Arrays.asList(mPatterns.get(i)));
                hiddenLayerOutputs = mHiddenLayer.getNeuronsOutput();

                double networkOutput = mOutputLayer.feedForward(hiddenLayerOutputs);
                Log.i(TAG, "network output " + networkOutput);
                networkOutputs.add(networkOutput);

                double error = mExpectedOutputs[i] - networkOutput;

                mOutputLayer.calculateNeuronsDelta(error, null);
                mHiddenLayer.calculateNeuronsDelta(error, mOutputLayer.getNeurons());

                mOutputLayer.calculateNeuronsWeightGradient();
                mHiddenLayer.calculateNeuronsWeightGradient();

                mOutputLayer.recalculateWeights(mLearningRate);
                mHiddenLayer.recalculateWeights(mLearningRate);
            }
            networkError = meanSquaredError(networkOutputs, mPatterns.size(), mExpectedOutputs);
            Log.i(TAG, "network error " + networkError);
            data.add(new Entry(epochs, (float) networkError));

            mCallback.update(epochs, networkError);
            epochs++;
        }
        mCallback.draw(data);
        Log.i(TAG, "epochs " + epochs);
        stopSelf();
    }

    public void setParams(List<Double[]> patterns, int[] expectedOutputs, float learningRate,
                          Layer hiddenLayer, Layer outputLayer) {
        mPatterns = patterns;
        mExpectedOutputs = expectedOutputs;
        mLearningRate = learningRate;
        mHiddenLayer = hiddenLayer;
        mOutputLayer = outputLayer;
    }

    private Double meanSquaredError(List<Double> networkOutputs, int size, int[] expectedOutputs) {
        double squaredErrors = 0.0;
        for (int i = 0; i < size; i++) {
            double currentResult = (expectedOutputs[i] - networkOutputs.get(i));
            squaredErrors += Math.pow(currentResult, 2);
        }
        return (1f / size) * squaredErrors;
    }

    public void setCallback(PlotCallback callback) {
        mCallback = callback;
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
