package com.sm9.projectilebalancer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.util.Formatter;

import static com.sm9.projectilebalancer.ProjectileBalancer.g_bDebugMode;
import static com.sm9.projectilebalancer.ProjectileBalancer.g_sScaleWhitelist;

public class Util {
    public static float getScaledDamage(EntityPlayer localPlayer, EntityLivingBase damageVictim, float fInitialDamage, int iEventId) {
        float fMinDistance = -1.0f;
        float fScaleAmount = -1.0f;
        String[] sMobInfo;

        for (String sMob : g_sScaleWhitelist) {
            sMobInfo = sMob.split(" ");

            if (!sMobInfo[0].equals(Util.findEntityIdByClass(damageVictim.getClass()))) {
                continue;
            }

            fMinDistance = Float.parseFloat(sMobInfo[1]);
            fScaleAmount = Float.parseFloat(sMobInfo[2]);

            break;
        }

        if (fMinDistance == -1.0f || fScaleAmount == -1.0f) {
            return -1.0f;
        }

        float fDistance = localPlayer.getDistance(damageVictim);
        float fScale = ((fDistance - fMinDistance) * fScaleAmount) * 100.0f;
        float fNewDamage = fInitialDamage - ((fInitialDamage * fScale) / 100.0f);

        if (fNewDamage <= 0.0f) {
            fNewDamage = 0.0f;
        }

        if (g_bDebugMode) {
            if (fDistance <= fMinDistance && iEventId == 1) {
                Util.debugToPlayer(localPlayer, "Distance less than %.3f, damage unaffected.", fMinDistance);
            } else if (fNewDamage == 0.0f && iEventId == 0) {
                Util.debugToPlayer(localPlayer, "Damage range ineffective.");
            } else if (iEventId == 1) {
                Util.debugToPlayer(localPlayer, "Distance: %.3f, Old Damage: %.3f, New Damage: %.3f, Scale: %.3f", fDistance, fInitialDamage, fNewDamage, fScale);
            }
        }

        return fNewDamage;
    }

    public static void debugToPlayer(EntityPlayer entityPlayer, String sFormat, Object... oArgs) {
        String sMessage = new Formatter().format(sFormat, oArgs).toString();
        entityPlayer.sendMessage(new TextComponentString("[PB] " + sMessage));
    }

    public static String findEntityIdByClass(Class<? extends Entity> clazz) {
        ResourceLocation key = EntityList.getKey(clazz);
        return key == null ? null : key.toString();
    }
}