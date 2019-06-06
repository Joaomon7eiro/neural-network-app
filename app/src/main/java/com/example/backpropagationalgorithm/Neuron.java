package com.example.backpropagationalgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Neuron {

    private List<Double> mWeights;
    private List<Double> mWeightsGradient;
    private List<Double> mInputs;
    private Double mOutput;
    private double mDelta;

    //public Neuron(int totalWeights, List<Double> weights) {
    //    mWeights = weights;

    public Neuron(int totalWeights) {
        mWeights = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < totalWeights; i++) {
            double weight = random.nextDouble();
            mWeights.add(weight);
        }
    }

    private Double sum(List<Double> inputs) {
        Double results = mWeights.get(0);

        for (int i = 0; i < inputs.size(); i++) {
            results += inputs.get(i) * mWeights.get(i + 1);
        }

        return results;
    }

    Double sigmoid(List<Double> inputs) {
        mInputs = inputs;
        mOutput = 1 / (1 + Math.exp(-sum(inputs)));
        return mOutput;
    }

    double getDelta() {
        return mDelta;
    }

    void setDelta(double delta) {
        mDelta = delta;
    }

    Double getOutput() {
        return mOutput;
    }

    List<Double> getWeights() {
        return mWeights;
    }

    List<Double> getInputs() {
        return mInputs;
    }

    List<Double> getWeightsGradient() {
        return mWeightsGradient;
    }

    void setWeightsGradient(List<Double> weightsGradient) {
        mWeightsGradient = weightsGradient;
    }

    void setWeights(List<Double> newWeights) {
        mWeights = newWeights;
    }
}
