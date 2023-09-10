package com.dmba.config;

public enum SampleRate {
    RATE_16000(16000),
    RATE_22050(22050),
    RATE_44100(44100),
    RATE_48000(48000);
    private final int value;

    SampleRate(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public float floatValue() {
        return (float) value;
    }

    public static SampleRate fromValue(int value) {
        for (SampleRate rate : SampleRate.values()) {
            if (rate.getValue() == value) {
                return rate;
            }
        }
        throw new IllegalArgumentException("Invalid sample rate: " + value);
    }
}