package net.narutomod.goatee.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.narutomod.goatee.data.capability.NarutoCapabilities;
import net.narutomod.goatee.network.PacketHandler;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        NarutoCapabilities.registerCapability();
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
        PacketHandler.Instance = new PacketHandler();
    }

    public EntityPlayer getClientPlayer() {
        return null;
    }

    public static Side side() {
        return FMLCommonHandler.instance().getEffectiveSide();
    }

    public static boolean isServer() {
        return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
    }

}