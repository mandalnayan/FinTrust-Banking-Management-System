package com.fintrust.model;

public class DailyUserActiveTimeData {

    // [timestamp, activeUsers]
    static final Number[][] data = {
        {0, 5},    // 00:00
        {1, 20},
        {2, 52},
        {3, 69},
        {4, 71},
        {5, 70},
        {6, 60},
        {7, 65},
        {8, 75},
        {9, 70},
        {10, 85},
        {11, 95},
        {12, 110},
        {13, 100},
        {14, 110},
        {15, 135},
        {16, 145},
        {17, 120},
        {18, 140},
        {19, 130},
        {20, 140},
        {21, 100},
        {22, 140},
        {23, 170}
    };

    public static Number[][] getData() {
        return data;
    }
}
