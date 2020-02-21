package tools.pki.aln;

public class NavigationAppBuilder {
    private String name;
    private String friendlyName;
    private String packageName;
    private boolean installed;
    private boolean supported;

    public NavigationAppBuilder name(String name) {
        this.name = name;
        return this;
    }

    public NavigationAppBuilder friendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
        return this;
    }

    public NavigationAppBuilder packageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public NavigationAppBuilder installed(boolean installed) {
        this.installed = installed;
        return this;
    }

    public NavigationAppBuilder supported(boolean supported) {
        this.supported = supported;
        return this;
    }

    public NavigationAppInfo build() {
        return new NavigationAppInfo(name, friendlyName, packageName, installed, supported);
    }
}