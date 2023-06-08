package gov.kallos.ramiel.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class OutlineUtil extends LivingEntityRenderer<PlayerEntity, PlayerEntityModel<PlayerEntity>> {

    private static final Identifier GLOW_SHADER = new Identifier("shaders/post/glow.json");

    public OutlineUtil(EntityRendererFactory.Context ctx, boolean slim) {
        super(ctx, new PlayerEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim), 0.5f);
    }

    @Override
    public void render(PlayerEntity player, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        MinecraftClient client = MinecraftClient.getInstance();
        Entity cameraEntity = client.getCameraEntity();

        if (cameraEntity != player) {
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionShader);

            float glowingColorRed = 1.0f;
            float glowingColorGreen = 1.0f;
            float glowingColorBlue = 0.0f;
            float glowingColorAlpha = 0.5f;

            RenderSystem.setShaderColor(glowingColorRed, glowingColorGreen, glowingColorBlue, glowingColorAlpha);
            super.render(player, yaw, tickDelta, matrices, vertexConsumers, light);

            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
        }
    }

    @Override
    public Identifier getTexture(PlayerEntity entity) {
        return GLOW_SHADER;
    }
}
