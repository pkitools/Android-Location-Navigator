package tools.pki.aln;

public interface ILogger {
   public ILogger setEnabled(boolean enabled);
    public boolean getEnabled();
    public void error(String msg);
    public void warn(String msg);
    public void info(String msg);
    public void debug(String msg);
    public void verbose(String msg);
}