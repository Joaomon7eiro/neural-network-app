package com.example.backpropagationalgorithm;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class Layer {
    private static final String TAG = Layer.class.getSimpleName();
    private final List<Neuron> mNeurons;
    private List<Double> mNeuronsOutput = new ArrayList<>();

    Layer(List<Neuron> neurons) {
        mNeurons = neurons;
    }

    Double feedForward(List<Double> inputs) {
        Double layerOutput = 0.0;
        mNeuronsOutput.clear();
        for (int i = 0; i < mNeurons.size(); i++) {
            Double output = mNeurons.get(i).sigmoid(inputs);
            layerOutput += output;
            mNeuronsOutput.add(output);
        }
        layerOutput /= mNeurons.size();
        return layerOutput;
    }

    List<Double> getNeuronsOutput() {
        return mNeuronsOutput;
    }

    List<Neuron> getNeurons() {
        return mNeurons;
    }

    void calculateNeuronsDelta(double error, List<Neuron> outputLayerNeurons) {
        for (int i = 0; i < mNeurons.size(); i++) {
            double neuronOutput = mNeurons.get(i).getOutput();
            double derivative = neuronOutput * (1 - neuronOutput);

            double delta;
            // if layer is output
            if (outputLayerNeurons == null) {
                delta = derivative * error;
            } else {
                double sum = 0;
                for (int j = 0; j < outputLayerNeurons.size(); j++) {
                    Neuron outputNeuron = outputLayerNeurons.get(j);
                    sum += outputNeuron.getWeights().get(i + 1) * outputNeuron.getDelta();
                }
                delta = derivative * sum;
            }
            Log.i(TAG, "calculateNeuronsDelta: " + delta);
            mNeurons.get(i).setDelta(delta);
        }
    }

    void calculateNeuronsWeightGradient() {
        for (int i = 0; i < mNeurons.size(); i++) {
            Neuron neuron = mNeurons.get(i);
            List<Double> weights = neuron.getWeights();
            List<Double> inputs = neuron.getInputs();

            List<Double> gradients = new ArrayList<>();
            gradients.add(neuron.getDelta());
            for (int j = 0; j < weights.size() - 1; j++) {
                double gradient = inputs.get(j) * neuron.getDelta();
                gradients.add(gradient);
            }
            neuron.setWeightsGradient(gradients);
        }
    }

    void recalculateWeights(float learningRate) {
        for (int i = 0; i < mNeurons.size(); i++) {
            Neuron neuron = mNeurons.get(i);
            List<Double> newWeights = new ArrayList<>();
            List<Double> oldWeights = neuron.getWeights();
            List<Double> oldWeightsGradient = neuron.getWeightsGradient();

            for (int j = 0; j < oldWeights.size(); j++) {
                newWeights.add(oldWeights.get(j) + (learningRate * oldWeightsGradient.get(j)));
            }
            neuron.setWeights(newWeights);
        }
    }
}
