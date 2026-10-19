package com.thelastplayer.client;

import com.thelastplayer.TheLastPlayer;
import com.thelastplayer.entity.EchoEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

public class EchoRenderer extends MobRenderer<EchoEntity, EchoModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(TheLastPlayer.MODID, "textures/entity/echo.png");

    public EchoRenderer(EntityRendererManager manager) {
        super(manager, new EchoModel(), 0.25F);
        this.addLayer(new EchoGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(EchoEntity entity) {
        return TEXTURE;
    }

    /** ECHO grows into a towering predator during the failed-ending emergence. */
    @Override
    protected void scale(EchoEntity entity, MatrixStack stack, float partialTickTime) {
        if (!entity.isHunting()) {
            super.scale(entity, stack, partialTickTime);
            return;
        }
        float progress = 1.0F - (entity.getEmergenceTicks() / 45.0F);
        progress = MathHelper.clamp(progress, 0.08F, 1.0F);
        float size = 0.2F + progress * 2.45F;
        stack.scale(size, size, size);
    }

    @Nullable
    @Override
    protected RenderType getRenderType(EchoEntity entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        return RenderType.entityTranslucent(getTextureLocation(entity));
    }
}
