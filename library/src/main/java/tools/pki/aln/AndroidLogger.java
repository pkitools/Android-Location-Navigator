/*
 * Copyright (c) 2020. PKI.Tools
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
