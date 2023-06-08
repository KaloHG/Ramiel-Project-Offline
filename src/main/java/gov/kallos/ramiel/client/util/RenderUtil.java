package gov.kallos.ramiel.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

public class RenderUtil {

    public static void drawWaypoint(MatrixStack m, TextRenderer text, String name, int colorhex, float x, float y, float z, float size, Quaternion rotate) {
        int i = text.getWidth(name) / 2 + 2;
        m.push();
        m.translate((double)x, (double)y, (double)z);
        m.multiply(rotate);
        m.scale(-size, -size, size);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        Matrix4f mat = m.peek().getPositionMatrix();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bb = tes.getBuffer();

        bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bb.vertex(mat, (float)(-i), -3.0F, 0.0F).color(0.0F, 0.0F, 0.0F, 0.6F).next();
        bb.vertex(mat, (float)(-i), 10.0F, 0.0F).color(0.0F, 0.0F, 0.0F, 0.6F).next();
        bb.vertex(mat, (float)i, 10.0F, 0.0F).color(0.0F, 0.0F, 0.0F, 0.6F).next();
        bb.vertex(mat, (float)i, -3.0F, 0.0F).color(0.0F, 0.0F, 0.0F, 0.6F).next();
        tes.draw();
        bb.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        bb.vertex(mat, 0.0F, 16.0F, 0.0F).color(colorhex >> 16 & 255, colorhex >> 8 & 255, colorhex & 255, 255).next();
        bb.vertex(mat, -4.0F, 16.0F, 0.0F).color(colorhex >> 16 & 255, colorhex >> 8 & 255, colorhex & 255, 255).next();
        bb.vertex(mat, 0.0F, 20.0F, 0.0F).color(colorhex >> 16 & 255, colorhex >> 8 & 255, colorhex & 255, 255).next();
        bb.vertex(mat, 4.0F, 16.0F, 0.0F).color(colorhex >> 16 & 255, colorhex >> 8 & 255, colorhex & 255, 255).next();
        bb.vertex(mat, 0.0F, 12.0F, 0.0F).color(colorhex >> 16 & 255, colorhex >> 8 & 255, colorhex & 255, 255).next();
        bb.vertex(mat, -4.0F, 16.0F, 0.0F).color(colorhex >> 16 & 255, colorhex >> 8 & 255, colorhex & 255, 255).next();
        tes.draw();
        RenderSystem.enableTexture();
        VertexConsumerProvider.Immediate wvc = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        text.draw(new LiteralText(name), (float)(-i), 0.0F, colorhex, false, mat, wvc, true, 0, 15728880);
        wvc.draw();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        m.pop();
        RenderSystem.enableDepthTest();
    }

    public static void drawHoop(MatrixStack m, float xPos, float yPos, float zPos, int color, float alpha) {
        m.push();
        m.translate(xPos, yPos, zPos);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.lineWidth(3.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        Matrix4f mat = m.peek().getPositionMatrix();
        double radius = 1;
        double theta = 0.19634954084936207;
        double c = Math.cos((double)theta);
        double s = Math.sin((double)theta);
        double x = radius;
        double z = 0.0;
        Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        for (int circleSegment = 0; circleSegment <= 32; ++circleSegment) {
            bufferBuilder.vertex(mat, ((float)(xPos + x)), ((float)yPos), ((float)(zPos + z))).color(color >> 16 & 255, color >> 8 & 255, color & 255, 255).next();
            double t = x;
            x = c * x - s * z;
            z = s * t + c * z;
        }
        tesselator.draw();
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        m.pop();
        RenderSystem.enableDepthTest();
    }
}
