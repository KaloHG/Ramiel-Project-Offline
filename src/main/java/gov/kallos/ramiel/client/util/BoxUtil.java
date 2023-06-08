package gov.kallos.ramiel.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import gov.kallos.ramiel.client.RamielClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class BoxUtil {
    public static void drawBox(MatrixStack matrix, Tessellator tes, BufferBuilder vertices, Entity entity, float x, float y, float z, float red, float green, float blue, float red2, float green2, float blue2, float alpha, float lineWidth) {
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());


        drawSizedBox(matrix, tes, vertices, x, y, z, box.maxX, box.maxY, box.maxZ, box.minX, box.minY, box.minZ, red, green, blue, alpha, red2, green2, blue2, lineWidth);
    }
    public static void drawSizedBox(MatrixStack matrices, Tessellator tes, BufferBuilder buffer, float x, float y, float z, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha, float red2, float green2, float blue2, float lineWidth) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Matrix3f matrix3f = matrices.peek().getNormalMatrix();
        float f = (float)x1;
        float g = (float)y1;
        float h = (float)z1;
        float i = (float)x2;
        float j = (float)y2;
        float k = (float)z2;

        matrices.push();
        RenderSystem.lineWidth(lineWidth * 1.1F);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.lineWidth(3.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix4f, f + x, g + y, h + z).color(red, green, blue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        buffer.vertex(matrix4f, i+ x, g + y, h + z).color(red, green, blue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        buffer.vertex(matrix4f, f+ x, g + y, h + z).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix4f, f+ x, j + y, h + z).color(red2, green2, blue2, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();

        buffer.vertex(matrix4f, f+ x, g + y, h + z).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        buffer.vertex(matrix4f, f+ x, g + y, k + z).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        buffer.vertex(matrix4f, i+ x, g + y, h + z).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix4f, i+ x, j + y, h + z).color(red2, green2, blue2, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();

        buffer.vertex(matrix4f, i+ x, j + y, h + z).color(red2, green2, blue2, alpha).normal(matrix3f, -1.0F, 0.0F, 0.0F).next(); /**LOW2*/
        buffer.vertex(matrix4f, f+ x, j + y, h + z).color(red2, green2, blue2, alpha).normal(matrix3f, -1.0F, 0.0F, 0.0F).next(); /**LOW2*/
        buffer.vertex(matrix4f, f+ x, j + y, h + z).color(red2, green2, blue2, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next(); /**LOW1*/
        buffer.vertex(matrix4f, f+ x, j + y, k + z).color(red2, green2, blue2, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next(); /**LOW1*/

        buffer.vertex(matrix4f, f+ x, j + y, k + z).color(red2, green2, blue2, alpha).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
        buffer.vertex(matrix4f, f+ x, g + y, k + z).color(red, green, blue, alpha).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
        buffer.vertex(matrix4f, f+ x, g + y, k + z).color(red, green, blue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next(); /**TOP2*/
        buffer.vertex(matrix4f, i+ x, g + y, k + z).color(red, green, blue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next(); /**TOP2*/

        buffer.vertex(matrix4f, i+ x, g + y, k + z).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, -1.0F).next(); /**TOP1*/
        buffer.vertex(matrix4f, i+ x, g + y, h + z).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, -1.0F).next(); /**TOP1*/
        buffer.vertex(matrix4f, f+ x, j + y, k + z).color(red2, green2, blue2, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next(); /**LOW4*/
        buffer.vertex(matrix4f, i+ x, j + y, k + z).color(red2, green2, blue2, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next(); /**LOW4*/

        buffer.vertex(matrix4f, i+ x, g + y, k + z).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix4f, i+ x, j + y, k + z).color(red2, green2, blue2, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix4f, i+ x, j + y, h + z).color(red2, green2, blue2, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next(); /**LOW3*/
        buffer.vertex(matrix4f, i+ x, j + y, k + z).color(red2, green2, blue2, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next(); /**LOW3*/
        tes.draw();
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        matrices.pop();
        RenderSystem.enableDepthTest();
    }
}