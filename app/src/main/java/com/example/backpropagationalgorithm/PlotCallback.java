package com.example.backpropagationalgorithm;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

public interface PlotCallback {
    void draw(List<Entry> data);
    void update(int epoch, double error);
}
