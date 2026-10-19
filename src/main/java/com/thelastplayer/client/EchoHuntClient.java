package com.thelastplayer.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.thelastplayer.TheLastPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Client-side emergency red flicker used during ECHO's failed-ending hunt. */
@Mod.EventBusSubscriber(modid = TheLastPlayer.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class EchoHuntClient {
    private static int redTicks;
    private static int elapsedTicks;

    private EchoHuntClient() { }

    public static void start(int durationTicks) {
        redTicks = Math.max(redTicks, Math.max(1, durationTicks));
        elapsedTicks = 0;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (redTicks > 0) {
            redTicks--;
            elapsedTicks++;
        }
    }

    @SubscribeEvent
    public static void onOverlay(RenderGameOverlayEvent.Post event) {
        if (redTicks <= 0 || event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Pulse between near-black and emergency red instead of covering the entire screen solidly.
        float pulse = (MathHelper.sin(elapsedTicks * 0.22F) + 1.0F) * 0.5F;
        int alpha = 24 + (int) (pulse * 35.0F);
        int color = (alpha << 24) | (190 << 16) | (3 << 8) | 12;
        MatrixStack matrix = event.getMatrixStack();
        AbstractGui.fill(matrix, 0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), color);
    }
}
