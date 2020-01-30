package tools.pki.aln;

import android.util.Log;

class AndroidLogger implements ILogger {
    protected AndroidLogger() {
    }

    protected AndroidLogger(String tag, boolean enabled) {
        if (tag != null)
            this.tag = tag;
        this.enabled = enabled;
    }

    /**
     * Logging for a specific tag
     *
     * @param tag Tag for logger, if it is null we use current class name
     * @return A logger
     */
    public AndroidLogger forTag(String tag) {
        return new AndroidLogger(tag, true);
    }



    private String tag = this.getClass().getSimpleName();

    /**
     * Get a logger
     *
     * @param isEnabled if false it only logs error
     * @return An android logger using logcat
     */
    public static AndroidLogger enabled(boolean isEnabled) {
        return new AndroidLogger(null, isEnabled);
    }

    private boolean enabled;

    @Override
    public AndroidLogger setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean getEnabled() {
        return enabled;
    }

    @Override
    public void error(String msg) {
        Log.e(tag, msg);
    }

    @Override
    public void warn(String msg) {
        if (enabled)
            Log.w(tag, msg);
    }

    @Override
    public void info(String msg) {
        if (enabled)
            Log.i(tag, msg);
    }

    @Override
    public void debug(String msg) {
        if (enabled)
            Log.d(tag, msg);
    }

    @Override
    public void verbose(String msg) {
        if (enabled)
            Log.v(tag, msg);
    }
}
