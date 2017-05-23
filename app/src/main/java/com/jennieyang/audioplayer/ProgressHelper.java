package com.jennieyang.audioplayer;

public class ProgressHelper {
    /**
     * Get progress percentage given the current and total duration of the audio file
     * @param currentPosition: current duration in milliseconds
     * @param totalDuration: total duration in milliseconds
     * @return percentage of audio file duration
     */
    public int getProgressPercentage(int currentPosition, int totalDuration) {
        int percentage = (int) ((((double) currentPosition) / totalDuration) * 100);
        return percentage;
    }

    /**
     * Convert the audio progress value to a time
     * @param progress: progress bar's current level of progress
     * @param totalDuration: total duration in milliseconds
     * @return current position in milliseconds
     * */
    public int progressToTime(int progress, int totalDuration) {
        double percentage = ((double) progress) / 100;
        int currentPosition = (int) (percentage * totalDuration);
        return currentPosition;
    }
}
