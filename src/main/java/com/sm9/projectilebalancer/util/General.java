package com.sm9.projectilebalancer.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.Level;

import java.util.Formatter;
import java.util.HashMap;

import static com.sm9.projectilebalancer.ProjectileBalancer.pbLogger;
import static com.sm9.projectilebalancer.common.Config.MainConfig.debugMode;
import static com.sm9.projectilebalancer.common.Config.MainConfig.scaleConfig;
import static com.sm9.projectilebalancer.handler.ForgeEvents.scaledMobs;

public class General {
    public static void loadScaledMobs() {
        String[] mobInfoSplit;
        HashMap<String, Float> scaleInfo;
        float minDistance;
        float scaleAmount;

        Class<? extends Entity> cLazz;

        for (String scaledMob : scaleConfig) {
            if (scaledMob == null || scaledMob.isEmpty()) {
                continue;
            }

            mobInfoSplit = scaledMob.split(" ");

            if (mobInfoSplit.length != 3) {
                debugToConsole(Level.ERROR, "Bad scale specifier: '%s' Use <mob>-<mindistance>-<scaleamount>", scaledMob);
                continue;
            }

            cLazz = EntityList.getClass(new ResourceLocation(mobInfoSplit[0]));

            if (cLazz == null || !EntityLiving.class.isAssignableFrom(cLazz) || !findEntityIdByClass(cLazz).equals(mobInfoSplit[0])) {
                debugToConsole(Level.ERROR, "Invalid mob specified: %s", mobInfoSplit[0]);
                continue;
            }

            if (scaledMobs.get(mobInfoSplit[0]) != null) {
                debugToConsole(Level.WARN, "Duplicate scale specifier: '%s'", scaledMob);
                continue;
            }

            scaleInfo = new HashMap<>();

            try {
                minDistance = Float.parseFloat(mobInfoSplit[1]);
                scaleAmount = Float.parseFloat(mobInfoSplit[2]);

                scaleInfo.put("MinDistance", minDistance);
                scaleInfo.put("ScaleAmount", scaleAmount);
            } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {
                debugToConsole(Level.ERROR, "Failed to parse specifier %s (%s)", scaledMob, ex.toString());
                continue;
            }

            debugToConsole(Level.INFO, "Successfully added scaled mob %s with MinDistance: %.3f, ScaleAmount: %.3f", mobInfoSplit[0], minDistance, scaleAmount);
            scaledMobs.put(mobInfoSplit[0], scaleInfo);
        }
    }

    public static float getScaledDamage(EntityPlayer localPlayer, EntityLivingBase damageVictim, float initialDamage, int eventId) {
        HashMap<String, Float> scaleInfo = scaledMobs.get(findEntityIdByClass(damageVictim.getClass()));

        if (scaleInfo == null) {
            return -1.0f;
        }

        Float minDistance = scaleInfo.get("MinDistance");
        Float scaleAmount = scaleInfo.get("ScaleAmount");

        if (minDistance == -1.0f || scaleAmount == -1.0f) {
            return -1.0f;
        }

        float distance = localPlayer.getDistance(damageVictim);
        float scale = ((distance - minDistance) * scaleAmount) * 100.0f;
        float newDamage = initialDamage - ((initialDamage * scale) / 100.0f);

        if (newDamage <= 0.0f) {
            newDamage = 0.0f;
        }

        if (debugMode) {
            if (distance <= minDistance && eventId == 1) {
                printToPlayer(localPlayer, "Distance less than %.3f, damage unaffected.", minDistance);
            } else if (newDamage == 0.0f && eventId == 0) {
                printToPlayer(localPlayer, "Damage range ineffective.");
            } else if (eventId == 1) {
                printToPlayer(localPlayer, "Distance: %.3f, Old Damage: %.3f, New Damage: %.3f, Scale: %.3f", distance, initialDamage, newDamage, scale);
            }
        }

        return newDamage;
    }

    private static String findEntityIdByClass(Class<? extends Entity> clazz) {
        ResourceLocation key = EntityList.getKey(clazz);
        return key == null ? null : key.toString();
    }

    private static void printToPlayer(EntityPlayer entityPlayer, String format, Object... args) {
        String message = new Formatter().format(format, args).toString();
        entityPlayer.sendMessage(new TextComponentString("[PB] " + message));
    }

    private static void debugToConsole(Level logLevel, String format, Object... args) {
        pbLogger.log(logLevel, new Formatter().format(format, args).toString());
    }
}