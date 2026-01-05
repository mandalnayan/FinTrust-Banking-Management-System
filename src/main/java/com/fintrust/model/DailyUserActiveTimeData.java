package com.fintrust.model;

public class DailyUserActiveTimeData {

    // [timestamp, activeUsers]
    static final Number[][] data = {
        {0, 5},    // 00:00
        {1, 3},
        {2, 2},
        {3, 1},
        {4, 1},
        {5, 4},
        {6, 10},
        {7, 25},
        {8, 45},
        {9, 70},
        {10, 85},
        {11, 95},
        {12, 110},
        {13, 100},
        {14, 90},
        {15, 85},
        {16, 95},
        {17, 120},
        {18, 140},
        {19, 130},
        {20, 100},
        {21, 70},
        {22, 40},
        {23, 20}
    };

    public static Number[][] getData() {
        return data;
    }
}
