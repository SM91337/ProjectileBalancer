package com.sm9.projectilebalancer;

import com.sm9.projectilebalancer.handler.ForgeEvents;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod
        (
                modid = Constants.MOD_ID,
                name = Constants.MOD_NAME,
                version = Constants.MOD_VERSION,
                dependencies = Constants.DEPENDENCIES,
                acceptedMinecraftVersions = Constants.MINECRAFT_VERSION,
                acceptableRemoteVersions = "*"
        )
public
class ProjectileBalancer {
    public static Logger pbLogger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ForgeEvents.preInit(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ForgeEvents.postInit();
    }

    @Mod.EventHandler
    public void onWorldLoad(FMLServerStartingEvent event) {
        ForgeEvents.onWorldLoad(event);
    }
}