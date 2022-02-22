package net.sorenon.mcxr.core;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sorenon.mcxr.core.accessor.PlayerEntityAcc;
import net.sorenon.mcxr.core.config.MCXRCoreConfig;
import net.sorenon.mcxr.core.config.MCXRCoreConfigImpl;
import net.sorenon.mcxr.core.mixin.ServerLoginNetworkHandlerAcc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MCXRCore implements ModInitializer {

    public static final ResourceLocation S2C_CONFIG = new ResourceLocation("mcxr", "config");

    public static final ResourceLocation POSEHEAD = new ResourceLocation("mcxr", "posehead");
    public static final ResourceLocation POSELARM = new ResourceLocation("mcxr", "poselarm");
    public static final ResourceLocation POSERARM = new ResourceLocation("mcxr", "poserarm");
    public static MCXRCore INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger("MCXR Core");

    public final MCXRCoreConfigImpl config = new MCXRCoreConfigImpl();

    @Override
    public void onInitialize() {
        INSTANCE = this;
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            config.xrEnabled = true;
        }

        ServerLoginNetworking.registerGlobalReceiver(S2C_CONFIG, (server, handler, understood, buf, synchronizer, responseSender) -> {
            if (understood) {
                boolean xr = buf.readBoolean();
                var profile = ((ServerLoginNetworkHandlerAcc) handler).getGameProfile();
                if (xr) {
                    LOGGER.info("Received XR login packet from " + profile.getId());
                } else {
                    LOGGER.info("Received login packet from " + profile.getId());
                }
            }
        });

        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            LOGGER.debug("Sending login packet to " + handler.getUserName());
            var buf = PacketByteBufs.create();
            sender.sendPacket(S2C_CONFIG, buf);
        });

        ServerPlayNetworking.registerGlobalReceiver(POSEHEAD,
                (server, player, handler, buf, responseSender) -> {
                    Vector3f vec = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                    Quaternionf quat = new Quaternionf(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
                    server.execute(() -> {
                        Pose pose = new Pose();
                        pose.pos.set(vec);
                        pose.orientation.set(quat);
                        setPlayerHeadPose(player, pose);
                        for(ServerPlayer sPlayer: server.getPlayerList().getPlayers()) {
                            ServerPlayNetworking.send(sPlayer, POSEHEAD, buf);
                        }
                    });
                });

        ServerPlayNetworking.registerGlobalReceiver(POSERARM,
                (server, player, handler, buf, responseSender) -> {
                    Vector3f vec = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                    Quaternionf quat = new Quaternionf(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
                    server.execute(() -> {
                        Pose pose = new Pose();
                        pose.pos.set(vec);
                        pose.orientation.set(quat);
                        setPlayerRightArmPose(player, pose);
                        for(ServerPlayer sPlayer: server.getPlayerList().getPlayers()) {
                            ServerPlayNetworking.send(sPlayer, POSERARM, buf);
                        }
                    });
                });

        ServerPlayNetworking.registerGlobalReceiver(POSELARM,
                (server, player, handler, buf, responseSender) -> {
                    Vector3f vec = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                    Quaternionf quat = new Quaternionf(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
                    server.execute(() -> {
                        Pose pose = new Pose();
                        pose.pos.set(vec);
                        pose.orientation.set(quat);
                        setPlayerLeftArmPose(player, pose);
                        for(ServerPlayer sPlayer: server.getPlayerList().getPlayers()) {
                            ServerPlayNetworking.send(sPlayer, POSELARM, buf);
                        }
                    });
                });
    }

    public void setPlayerHeadPose(Player player, Pose pose) {
        PlayerEntityAcc acc = (PlayerEntityAcc) player;
        if (acc.getHeadPose() == null) {
            acc.markVR();
        }
        acc.getHeadPose().set(pose);

        if (player.level.isClientSide && player instanceof LocalPlayer) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            Vector3f pos = pose.pos;
            Quaternionf quat = pose.orientation;
            buf.writeFloat(pos.x).writeFloat(pos.y).writeFloat(pos.z);
            buf.writeFloat(quat.x).writeFloat(quat.y).writeFloat(quat.z).writeFloat(quat.w);

            ClientPlayNetworking.send(POSEHEAD, buf);
        }
    }

    public void setPlayerRightArmPose(Player player, Pose pose) {
        PlayerEntityAcc acc = (PlayerEntityAcc) player;
        if (acc.getRightArmPose() == null) {
            acc.markRightArm();
        }
        acc.getRightArmPose().set(pose);

        if (player.level.isClientSide && player instanceof LocalPlayer) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            Vector3f pos = pose.pos;
            Quaternionf quat = pose.orientation;
            buf.writeFloat(pos.x).writeFloat(pos.y).writeFloat(pos.z);
            buf.writeFloat(quat.x).writeFloat(quat.y).writeFloat(quat.z).writeFloat(quat.w);

            ClientPlayNetworking.send(POSERARM, buf);
        }
    }

    public void setPlayerLeftArmPose(Player player, Pose pose) {
        PlayerEntityAcc acc = (PlayerEntityAcc) player;
        if (acc.getLeftArmPose() == null) {
            acc.markLeftArm();
        }
        acc.getLeftArmPose().set(pose);

        if (player.level.isClientSide && player instanceof LocalPlayer) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            Vector3f pos = pose.pos;
            Quaternionf quat = pose.orientation;
            buf.writeFloat(pos.x).writeFloat(pos.y).writeFloat(pos.z);
            buf.writeFloat(quat.x).writeFloat(quat.y).writeFloat(quat.z).writeFloat(quat.w);

            ClientPlayNetworking.send(POSELARM, buf);
        }
    }

    public static MCXRCoreConfig getCoreConfig() {
        return INSTANCE.config;
    }

}
