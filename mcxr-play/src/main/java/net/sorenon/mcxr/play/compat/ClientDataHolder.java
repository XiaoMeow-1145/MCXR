package net.sorenon.mcxr.play.compat;

import net.minecraft.client.resources.model.ModelResourceLocation;
import org.vivecraft.gameplay.VRPlayer;
import org.vivecraft.provider.MCVR;
import org.vivecraft.provider.VRRenderer;
import org.vivecraft.settings.VRSettings;

import java.util.ArrayList;
import java.util.List;

public class ClientDataHolder {

    public static ModelResourceLocation thirdPersonCameraModel = new ModelResourceLocation("vivecraft:camcorder");
    public static ModelResourceLocation thirdPersonCameraDisplayModel = new ModelResourceLocation("vivecraft:camcorder_display");
    public static List<String> hrtfList = new ArrayList<>();
    private static ClientDataHolder INSTANCE;
    //public String minecriftVerString; Common
    public VRPlayer vrPlayer;
    public MCVR vr;
    public VRRenderer vrRenderer;

    public VRSettings vrSettings;
    public long lastIntegratedServerLaunchCheck = 0L;
    public boolean integratedServerLaunchInProgress = false;

    public static ClientDataHolder getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientDataHolder();
        }
        return INSTANCE;
    }

    public void print(String string) {
        string = string.replace("\n", "\n[Minecrift] ");
        System.out.println("[Minecrift] " + string);
    }


}
