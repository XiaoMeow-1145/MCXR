package net.sorenon.mcxr.core.accessor;

import net.sorenon.mcxr.core.Pose;

public interface PlayerEntityAcc {

    Pose getHeadPose();
    Pose getRightArmPose();
    Pose getLeftArmPose();

    void markVR();
    void markRightArm();
    void markLeftArm();

    boolean isXR();
}
