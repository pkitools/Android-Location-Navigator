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

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationApplications {
    public static final String GEO_URI = "GEO:";

   private NavigationApplications() {
        supportedAppNames.put(GOOGLE_MAPS, "Google Maps");
        supportedAppNames.put(CITYMAPPER, "Citymapper");
        supportedAppNames.put(UBER, "Uber");
        supportedAppNames.put(WAZE, "Waze");
        supportedAppNames.put(YANDEX, "Yandex Navigator");
        supportedAppNames.put(SYGIC, "Sygic");
        supportedAppNames.put(HERE_MAPS, "HERE Maps");
        supportedAppNames.put(MOOVIT, "Moovit");
        supportedAppNames.put(LYFT, "Lyft");
        supportedAppNames.put(MAPS_ME, "MAPS.ME");
        supportedAppNames.put(CABIFY, "Cabify");
        supportedAppNames.put(BAIDU, "Baidu Maps");
        supportedAppNames.put(TAXIS_99, "99 Taxi");
        supportedAppNames.put(GAODE, "Gaode Maps (Amap)");

    }

    NavigationApplications(Context context) {
        this();
        setContext(context);
        discoverAvailableApps();
    }

    public static NavigationApplications with(Context context) {
        return new NavigationApplications(context);
    }

    void setContext(Context context) {
        this.context = context;
        packageManager = context.getApplicationContext().getPackageManager();
    }


    public static final String GOOGLE_MAPS = "google_maps";
    public static final String CITYMAPPER = "citymapper";
    public static final String UBER = "uber";
    public static final String WAZE = "waze";
    public static final String YANDEX = "yandex";
    public static final String SYGIC = "sygic";
    public static final String HERE_MAPS = "here_maps";
    public static final String MOOVIT = "moovit";
    public static final String LYFT = "lyft";
    public static final String MAPS_ME = "maps_me";
    public static final String CABIFY = "cabify";
    public static final String BAIDU = "baidu";
    public static final String TAXIS_99 = "taxis_99";
    public static final String GAODE = "gaode";
    // Explicitly supported apps
    protected static final String GEO = "GEO"; // Use native app choose for GEO: intent
    private static final Map<String, String> supportedAppPackages = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            put(GOOGLE_MAPS, "com.google.android.apps.maps");
            put(CITYMAPPER, "com.citymapper.app.release");
            put(UBER, "com.ubercab");
            put(WAZE, "com.waze");
            put(YANDEX, "ru.yandex.yandexnavi");
            put(SYGIC, "com.sygic.aura");
            put(HERE_MAPS, "com.here.app.maps");
            put(MOOVIT, "com.tranzmate");
            put(LYFT, "me.lyft.android");
            put(MAPS_ME, "com.mapswithme.maps.pro");
            put(CABIFY, "com.cabify.rider");
            put(BAIDU, "com.baidu.BaiduMap");
            put(TAXIS_99, "com.taxis99");
            put(GAODE, "com.autonavi.minimap");
        }
    });

    public static String getPackage(String applicationName) {
        return supportedAppPackages.get(applicationName);

    }

    private final Map<String, String> supportedAppNames = new HashMap<>();

    Context context;
    PackageManager packageManager;

    // Map of app name to package name
    protected Map<String, String> availableApps;
    List<String> supportedApps;


    public Map<String, Boolean> getAvailableApps() {
        Map<String, Boolean> apps = new HashMap<>();

        // Add explicitly supported apps first
        for (Map.Entry<String, String> entry : supportedAppPackages.entrySet()) {
            String appName = entry.getKey();
            String packageName = entry.getValue();
            apps.put(appName, availableApps.containsValue(packageName));
        }

        // Iterate over available apps and add any dynamically discovered ones
        for (Map.Entry<String, String> entry : availableApps.entrySet()) {
            String packageName = entry.getValue();
            // If it's not already present
            if (!apps.containsKey(packageName) && !supportedAppPackages.containsValue(packageName)) {
                apps.put(packageName, true);
            }
        }
        return apps;
    }

    protected void discoverAvailableApps() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GEO_URI));
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
        availableApps = new HashMap<>();
        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            String appName = getAppName(packageName);
            if (!supportedAppPackages.containsValue(packageName)) { // if it's not already an explicitly supported app
                availableApps.put(appName, packageName);
            }
        }

        // Check if explicitly supported apps are installed
        for (Map.Entry<String, String> entry : supportedAppPackages.entrySet()) {
            String selectedAppName = entry.getKey();
            String selectedPackageName = entry.getValue();
            if (isPackageInstalled(selectedPackageName, packageManager)) {
                availableApps.put(supportedAppNames.get(selectedAppName), selectedPackageName);
            }
        }
    }

    private String getAppName(String packageName) {
        ApplicationInfo ai;
        try {
            ai = packageManager.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? packageManager.getApplicationLabel(ai) : null);
    }

    public String getThisAppName() {
        return context.getApplicationInfo().loadLabel(packageManager).toString();
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            ApplicationInfo ai = packageManager.getApplicationInfo(packagename, 0);
            return ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public ArrayList getSupportedApps() {
        Map<String, Boolean> apps = new HashMap<>();
        ArrayList list = new ArrayList();
        // Add explicitly supported apps first
        for (Map.Entry<String, String> entry : supportedAppPackages.entrySet()) {
            String appName = entry.getKey();
            String packageName = entry.getValue();
            apps.put(appName, availableApps.containsValue(packageName));
        }

        // Iterate over available apps and add any dynamically discovered ones
        for (Map.Entry<String, String> entry : availableApps.entrySet()) {
            String packageName = entry.getValue();
            // If it's not already present
            if (!apps.containsKey(packageName) && !supportedAppPackages.containsValue(packageName)) {
                list.add(packageName);
            }
        }
        return list;
    }

    public boolean isAppAvailable(String appName) {
        if (supportedAppPackages.containsKey(appName)) {
            appName = supportedAppPackages.get(appName);
        }
        return availableApps.containsValue(appName);
    }

    public Map<String, String> getGeoApps() {
        HashMap<String, String> apps = new HashMap<>();
        // Dynamically populate from discovered available apps that support GEO: protocol
        for (Map.Entry<String, String> entry : availableApps.entrySet()) {
            String appName = entry.getKey();
            String packageName = entry.getValue();
            // If it's not already an explicitly supported app
            if (!supportedAppPackages.containsValue(packageName)) {
                apps.put(appName, packageName);
            }
        }
        return apps;
    }


    public String getAppDisplayName(String packageName) {
        String name = "[Not found]";
        if (packageName.equals(GEO)) {
            return "[Native chooser]";
        }
        for (Map.Entry<String, String> entry : availableApps.entrySet()) {
            String appName = entry.getKey();
            String availablePackageName = entry.getValue();
            if (packageName.equals(availablePackageName)) {
                name = appName;
                break;
            }
        }
        return name;
    }
}
