package net.sorenon.mcxr.play;

public class MCXRNativeLoad {
    static {
        System.loadLibrary("mcxr_loader");
    }

    public static native long getJVMPtr();
    public static native long getApplicationActivityPtr();
    public static native void renderImage(int width, int height, int index, int eye, byte[] data);
    public static native void setImageHandle(int color, int index);
    public static native void setImageCount(int count);
}
