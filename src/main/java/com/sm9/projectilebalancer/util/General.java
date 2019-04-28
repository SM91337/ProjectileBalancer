package com.sm9.projectilebalancer.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
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
        String[] sMobInfo;
        HashMap<String, Float> scaleTemp;
        float minDistance;
        float scaleAmount;

        for (String scaledMob : scaleConfig) {
            if (scaledMob == null || scaledMob.length() < 1 || scaledMob.isEmpty()) {
                continue;
            }

            sMobInfo = scaledMob.split(" ");

            if (sMobInfo.length != 3) {
                debugToConsole(Level.ERROR, "Bad scale specifier: '%s' Use <mob>-<mindistance>-<scaleamount>", scaledMob);
                continue;
            }

            if (scaledMobs.get(sMobInfo[0]) != null) {
                debugToConsole(Level.WARN, "Duplicate scale specifier: '%s'", scaledMob);
                continue;
            }

            scaleTemp = new HashMap<>();

            try {
                minDistance = Float.parseFloat(sMobInfo[1]);
                scaleAmount = Float.parseFloat(sMobInfo[2]);

                scaleTemp.put("MinDistance", minDistance);
                scaleTemp.put("ScaleAmount", scaleAmount);
            } catch (ArrayIndexOutOfBoundsException ex) {
                debugToConsole(Level.ERROR, "Failed to parse specifier %s (%s)", scaledMob, ex.toString());
                continue;
            } catch (NullPointerException ex) {
                debugToConsole(Level.ERROR, "Failed to parse specifier %s (%s)", scaledMob, ex.toString());
                continue;
            }

            debugToConsole(Level.INFO, "Successfully added scaled mob %s with MinDistance: %.3f, ScaleAmount: %.3f", sMobInfo[0], minDistance, scaleAmount);
            scaledMobs.put(sMobInfo[0], scaleTemp);
        }
    }

    public static float getScaledDamage(EntityPlayer localPlayer, EntityLivingBase damageVictim, float fInitialDamage, int iEventId) {
        HashMap<String, Float> scaleInfo = scaledMobs.get(findEntityIdByClass(damageVictim.getClass()));

        if (scaleInfo == null) {
            return -1.0f;
        }

        Float minDistance = scaleInfo.get("MinDistance");
        Float scaleAmount = scaleInfo.get("ScaleAmount");

        if (minDistance == -1.0f || scaleAmount == -1.0f) {
            return -1.0f;
        }

        float fDistance = localPlayer.getDistance(damageVictim);
        float fScale = ((fDistance - minDistance) * scaleAmount) * 100.0f;
        float fNewDamage = fInitialDamage - ((fInitialDamage * fScale) / 100.0f);

        if (fNewDamage <= 0.0f) {
            fNewDamage = 0.0f;
        }

        if (debugMode) {
            if (fDistance <= minDistance && iEventId == 1) {
                printToPlayer(localPlayer, "Distance less than %.3f, damage unaffected.", minDistance);
            } else if (fNewDamage == 0.0f && iEventId == 0) {
                printToPlayer(localPlayer, "Damage range ineffective.");
            } else if (iEventId == 1) {
                printToPlayer(localPlayer, "Distance: %.3f, Old Damage: %.3f, New Damage: %.3f, Scale: %.3f", fDistance, fInitialDamage, fNewDamage, fScale);
            }
        }

        return fNewDamage;
    }

    private static String findEntityIdByClass(Class<? extends Entity> clazz) {
        ResourceLocation key = EntityList.getKey(clazz);
        return key == null ? null : key.toString();
    }

    public static void printToPlayer(EntityPlayer entityPlayer, String sFormat, Object... oArgs) {
        String sMessage = new Formatter().format(sFormat, oArgs).toString();
        entityPlayer.sendMessage(new TextComponentString("[PB] " + sMessage));
    }

    public static void debugToConsole(Level logLevel, String sFormat, Object... oArgs) {
        pbLogger.log(logLevel, new Formatter().format(sFormat, oArgs).toString());
    }
}