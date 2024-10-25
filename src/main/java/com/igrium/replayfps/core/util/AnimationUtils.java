package com.igrium.replayfps.core.util;

public final class AnimationUtils {
    private AnimationUtils() {
    }

    /**
     * Count the number of frames that have elapsed in a given amount of time.
     *
     * @param time          Time in milliseconds.
     * @param framerate     Framerate numerator.
     * @param framerateBase Framerate denominator.
     * @return Number of frames.
     */
    public static int countFrames(int time, int framerate, int framerateBase) {
        // Technically the equation is (time / 1000) * (framerate / framerateBase), but
        // this form is equivalent and it avoids needing to use floats.
        return (time * framerate) / (framerateBase * 1000);
    }

    /**
     * Calculate the amount of time it should take for a given amount of frames to run.
     *
     * @param numFrames     Number of frames.
     * @param framerate     Framerate numerator.
     * @param framerateBase Framerate denominator.
     * @return Time in milliseconds.
     */
    public static long getDuration(int numFrames, int framerate, int framerateBase) {
        return ((long) numFrames * framerateBase) * 1000 / framerate;
    }
}
