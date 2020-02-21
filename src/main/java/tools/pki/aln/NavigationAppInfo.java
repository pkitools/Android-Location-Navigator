package tools.pki.aln;



public class NavigationAppInfo {

    public static NavigationAppBuilder builder(){
        return  new NavigationAppBuilder();
    }

    public NavigationAppInfo(String name, String friendlyName, String packageName, boolean installed, boolean supported) {
        this.name = name;
        this.friendlyName = friendlyName;
        this.packageName = packageName;
        this.installed = installed;
        this.supported = supported;
    }

    public String getName() {
        return name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getPackageName() {
        return packageName;
    }

    String name;
    String friendlyName;
    String packageName;
    boolean installed;
    boolean supported;
}
