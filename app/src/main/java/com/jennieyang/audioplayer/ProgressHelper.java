package com.jennieyang.audioplayer;

public class ProgressHelper {
    /**
     * Get progress percentage given the current and total duration of the audio file
     * @param currentDuration: current duration in milliseconds
     * @param totalDuration: total duration in milliseconds
     * @return percentage of audio file duration
     */
    public int getProgressPercentage(int currentDuration, int totalDuration) {
        double percentage;
        percentage = (((double) currentDuration) / totalDuration) * 100;
        return (int) percentage;
    }
}
