package com.sm9.projectilebalancer.common.Config;

import com.sm9.projectilebalancer.util.General;
import net.minecraftforge.common.config.Configuration;

import static com.sm9.projectilebalancer.handler.ForgeEvents.scaledMobs;

public class MainConfig {
    public static String[] scaleConfig;
    public static boolean debugMode = false;
    public static Configuration mainConfig;

    public static void loadConfig() {
        scaledMobs.clear();
        mainConfig.load();

        scaleConfig = mainConfig.getStringList("ScaleList", Configuration.CATEGORY_GENERAL, new String[]{"minecraft:zombie 5.0 0.05"}, "List of mobs which should be scaled \n<mobid mindistance scaleamount>");
        debugMode = mainConfig.getBoolean("DebugMode", Configuration.CATEGORY_GENERAL, true, "Prints debug information");

        mainConfig.save();

        General.loadScaledMobs();
    }
}
