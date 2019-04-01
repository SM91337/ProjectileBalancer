package com.sm9.projectilebalancer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ForgeEventHandlers {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onMobAttacked(LivingAttackEvent evEvent) {
        DamageSource damageSource = evEvent.getSource();
        float fInitialDamage = evEvent.getAmount();

        if (damageSource == null || !damageSource.isProjectile()) {
            return;
        }

        Entity damageEntity = damageSource.getTrueSource();

        if (damageEntity == null || !(damageEntity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer localPlayer = (EntityPlayer) damageEntity;

        if (localPlayer == null) {
            return;
        }

        EntityLivingBase damageVictim = evEvent.getEntityLiving();

        if (damageVictim == null) {
            return;
        }

        float fNewDamage = Util.getScaledDamage(localPlayer, damageVictim, fInitialDamage, 0);

        if (fNewDamage < 0.0f) {
            return;
        }

        evEvent.setCanceled(fNewDamage <= 0.0f);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onMobTakeDamage(LivingDamageEvent evEvent) {
        DamageSource damageSource = evEvent.getSource();
        float fInitialDamage = evEvent.getAmount();

        if (damageSource == null || !damageSource.isProjectile()) {
            return;
        }

        Entity damageEntity = damageSource.getTrueSource();

        if (damageEntity == null || !(damageEntity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer localPlayer = (EntityPlayer) damageEntity;

        if (localPlayer == null) {
            return;
        }

        EntityLivingBase damageVictim = evEvent.getEntityLiving();

        if (damageVictim == null) {
            return;
        }

        float fNewDamage = Util.getScaledDamage(localPlayer, damageVictim, fInitialDamage, 1);

        if (fNewDamage < 0.0f) {
            return;
        }

        evEvent.setAmount(fNewDamage);
        evEvent.setCanceled(fNewDamage <= 0.0f);
    }
}