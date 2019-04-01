package com.sm9.projectilebalancer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = ProjectileBalancer.MODID, name = ProjectileBalancer.NAME,
        version = ProjectileBalancer.VERSION,
        dependencies = ProjectileBalancer.DEPENDENCIES,
        acceptedMinecraftVersions = "1.12.2",
        acceptableRemoteVersions = "*")

public class ProjectileBalancer {
    public static final String MODID = "projectilebalancer";
    public static final String NAME = "Projectile Balancer";
    public static final String VERSION = "0.2";
    public static final String MIN_FORGE_VER = "14.23.5.2815";
    public static final String DEPENDENCIES = "after:forge@[" + ProjectileBalancer.MIN_FORGE_VER + ",)";

    public static Configuration g_cConfig;
    public static Logger g_lLogger;
    public static String[] g_sScaleWhitelist;
    public static boolean g_bDebugMode = false;

    public static void loadConfig() {
        g_cConfig.load();
        g_sScaleWhitelist = g_cConfig.getStringList("ScaleList", Configuration.CATEGORY_GENERAL, new String[]{"minecraft:zombie 5.0 0.05"}, "List of mobs which should be scaled \n<mobid mindistance scaleamount>");
        g_bDebugMode = g_cConfig.getBoolean("DebugMode", Configuration.CATEGORY_GENERAL, true, "Prints debug information");
        g_cConfig.save();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evEvent) {
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());

        File fConfigFolder = new File(evEvent.getModConfigurationDirectory(), "sm9/ProjectileBalancer");
        File fConfig = new File(fConfigFolder, "main.cfg");

        g_lLogger = LogManager.getLogger("ProjectileBalancer");
        g_cConfig = new Configuration(fConfig);

        loadConfig();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent evEvent) {
        evEvent.registerServerCommand(new CmdReload());
    }
}