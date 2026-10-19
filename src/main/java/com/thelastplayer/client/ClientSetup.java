package com.thelastplayer.client;

import com.thelastplayer.TheLastPlayer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.ConfigGuiHandler;
import com.thelastplayer.registry.ModBlocks;
import com.thelastplayer.registry.ModEntities;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TheLastPlayer.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    /** Called from the mod constructor via DistExecutor so client GUI classes never load on a dedicated server. */
    public static void registerConfigGui() {
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((minecraft, parent) -> new EchoConfigScreen(parent)));
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ECHO.get(), EchoRenderer::new);
        event.enqueueWork(() -> RenderTypeLookup.setRenderLayer(ModBlocks.LAB_LAMP.get(), RenderType.cutout()));
    }
}
