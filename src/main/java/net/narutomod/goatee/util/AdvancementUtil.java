package net.narutomod.goatee.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public class AdvancementUtil {

    public static boolean grant(EntityPlayerMP player, String advancementId) {
        return grant(player, new ResourceLocation(advancementId));
    }

    public static boolean grant(EntityPlayerMP player, ResourceLocation advancementId) {
        MinecraftServer server = player.getServer();
        if (server == null)
            return false;

        Advancement advancement = server.getAdvancementManager().getAdvancement(advancementId);
        if (advancement == null)
            return false;


        AdvancementProgress playerProgress = player.getAdvancements().getProgress(advancement);
        if (!playerProgress.isDone())
            for (String criterion : playerProgress.getRemaningCriteria())
                player.getAdvancements().grantCriterion(advancement, criterion);


        return playerProgress.isDone();
    }

    public static boolean revoke(EntityPlayerMP player, String advancementId) {
        return revoke(player, new ResourceLocation(advancementId));
    }

    public static boolean revoke(EntityPlayerMP player, ResourceLocation advancementId) {
        MinecraftServer server = player.getServer();
        if (server == null)
            return false;


        Advancement advancement = server.getAdvancementManager().getAdvancement(advancementId);
        if (advancement == null)
            return false;


        AdvancementProgress playerProgress = player.getAdvancements().getProgress(advancement);
        if (!playerProgress.isDone())
            for (String criterion : playerProgress.getCompletedCriteria())
                player.getAdvancements().revokeCriterion(advancement, criterion);


        return playerProgress.isDone();
    }

    public static boolean has(EntityPlayerMP player, String advancementId) {
        return has(player, new ResourceLocation(advancementId));
    }

    public static boolean has(EntityPlayerMP player, ResourceLocation advancementId) {
        MinecraftServer server = player.getServer();
        if (server == null)
            return false;

        Advancement advancement = server.getAdvancementManager().getAdvancement(advancementId);
        if (advancement == null)
            return false;


        return player.getAdvancements().getProgress(advancement).isDone();
    }
}
