package net.sorenon.mcxr.core.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.sorenon.mcxr.core.Pose;
import net.sorenon.mcxr.core.accessor.PlayerEntityAcc;

public class QCXRLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public QCXRLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractClientPlayer entity, float f, float g, float h, float j, float k, float l) {
        if(((PlayerEntityAcc) entity).isXR()) {
            PlayerEntityAcc acc = (PlayerEntityAcc) entity;
            Pose rightArm = acc.getRightArmPose();
            Pose leftArm = acc.getLeftArmPose();
            getParentModel().rightArm.setPos(
                    rightArm.pos.x,
                    rightArm.pos.y,
                    rightArm.pos.z
            );
            getParentModel().rightArm.setRotation(
                    rightArm.orientation.x,
                    rightArm.orientation.y,
                    rightArm.orientation.z
            );
            getParentModel().leftArm.setPos(
                    leftArm.pos.x,
                    leftArm.pos.y,
                    leftArm.pos.z
            );
            getParentModel().leftArm.setRotation(
                    leftArm.orientation.x,
                    leftArm.orientation.y,
                    leftArm.orientation.z
            );
        }
    }
}
