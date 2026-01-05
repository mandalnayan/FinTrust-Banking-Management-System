package com.fintrust.model;

public class UserRegistrationData {

    static final Number[][] data = {
        {1672531200000L, 12},  // week starting 1 Jan
        {1673136000000L, 18},  // week starting 8 Jan
        {1673740800000L, 10},  // week starting 15 Jan
        {1674345600000L, 22},  // week starting 22 Jan
        {1674950400000L, 15}   // week starting 29 Jan
    };

    public static Number[][] getData() {
        return data;
    }
}

