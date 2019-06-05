package com.sm9.projectilebalancer.handler;

import com.sm9.projectilebalancer.command.Reload;
import com.sm9.projectilebalancer.util.General;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.HashMap;

import static com.sm9.projectilebalancer.ProjectileBalancer.pbLogger;
import static com.sm9.projectilebalancer.common.Config.MainConfig.loadConfig;
import static com.sm9.projectilebalancer.common.Config.MainConfig.mainConfig;

public class ForgeEvents {
    public static HashMap<String, HashMap> scaledMobs;

    public static void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ForgeEvents());

        File configDirectory = new File(event.getModConfigurationDirectory(), "sm9/ProjectileBalancer");
        File configFile = new File(configDirectory, "main.cfg");

        pbLogger = LogManager.getLogger("ProjectileBalancer");
        mainConfig = new Configuration(configFile);
        scaledMobs = new HashMap<>();
    }

    public static void postInit() {
        loadConfig();
    }

    public static void onWorldLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new Reload());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onMobAttacked(LivingAttackEvent event) {
        DamageSource damageSource = event.getSource();
        float initialDamage = event.getAmount();

        if (damageSource == null || !damageSource.isProjectile()) {
            return;
        }

        Entity damageEntity = damageSource.getTrueSource();

        if (!(damageEntity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer localPlayer = (EntityPlayer) damageEntity;
        EntityLivingBase damageVictim = event.getEntityLiving();

        if (damageVictim == null) {
            return;
        }

        float newDamage = General.getScaledDamage(localPlayer, damageVictim, initialDamage, 0);

        if (newDamage < 0.0f) {
            return;
        }

        event.setCanceled(newDamage <= 0.0f);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onMobTakeDamage(LivingDamageEvent event) {
        DamageSource damageSource = event.getSource();
        float initialDamage = event.getAmount();

        if (damageSource == null || !damageSource.isProjectile()) {
            return;
        }

        Entity damageEntity = damageSource.getTrueSource();

        if (!(damageEntity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer localPlayer = (EntityPlayer) damageEntity;
        EntityLivingBase damageVictim = event.getEntityLiving();

        if (damageVictim == null) {
            return;
        }

        float newDamage = General.getScaledDamage(localPlayer, damageVictim, initialDamage, 1);

        if (newDamage < 0.0f) {
            return;
        }

        event.setAmount(newDamage);
        event.setCanceled(newDamage <= 0.0f);
    }
}