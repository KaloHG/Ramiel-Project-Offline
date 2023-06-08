package gov.kallos.ramiel.client.mixin;

import gov.kallos.ramiel.client.RamielClient;
import gov.kallos.ramiel.client.config.RGBValue;
import gov.kallos.ramiel.client.manager.PlayerRegistry;
import gov.kallos.ramiel.client.model.Location;
import gov.kallos.ramiel.client.model.RamielPlayer;
import gov.kallos.ramiel.client.model.Standing;
import gov.kallos.ramiel.client.util.BoxUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static gov.kallos.ramiel.client.util.RenderUtil.drawHoop;
import static gov.kallos.ramiel.client.util.RenderUtil.drawWaypoint;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    /**
     * This class currently handles literally EVERYTHING for rendering player decorations.
     * It will eventually be massively shortened.
     */
    @Inject(method={"render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V"}, at={@At(value="RETURN")})
    private void injectRender(MatrixStack matrixStack, float partialTicks, long timeSlice, boolean lookingAtBlock, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        final MinecraftClient game = MinecraftClient.getInstance();
        final ClientPlayerEntity gamePlayer = MinecraftClient.getInstance().player;

        if (gamePlayer == null)
            return;

        final Vec3d pos = MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getPos();

        if (pos == null)
            return;

        final double posX = pos.getX();
        final double posY = pos.getY();
        final double posZ = pos.getZ();

        if(!RamielClient.getInstance().visualsEnabled()) {
            //Visuals are not enabled, halt.
            return;
        }

        List<String> playersOnRadar = new ArrayList<>();

        Iterator entityIterator = MinecraftClient.getInstance().world.getPlayers().iterator();
        //Iterates (Smoothly) Through all players on render distance.
        while (entityIterator.hasNext()){
                AbstractClientPlayerEntity playerEntity = (AbstractClientPlayerEntity) entityIterator.next();
                if(!playerEntity.equals(MinecraftClient.getInstance().player)) {
                    playersOnRadar.add(playerEntity.getDisplayName().getString());
                    //Batshit insane xyz differentiation for graphical shit.
                    double x = MathHelper.lerp((double) partialTicks, playerEntity.prevX,
                            playerEntity.getPos().getX()) - posX;
                    double y = MathHelper.lerp((double) partialTicks, playerEntity.prevY,
                            playerEntity.getPos().getY()) + 2.5 - posY;
                    double z = MathHelper.lerp((double) partialTicks, playerEntity.prevZ,
                            playerEntity.getPos().getZ()) - posZ;
                    double dist = Math.sqrt(x * x + y * y + z * z);

                    Vec3d subtickMove = playerEntity.getLerpedPos(partialTicks).subtract(playerEntity.getPos());

                    int maxWaypointDist = RamielClient.getInstance().getMaxWaypointDist();

                    if (maxWaypointDist > -1 && dist > (double) maxWaypointDist)
                        continue;

                    RamielPlayer rplayer = PlayerRegistry.getInstance().getOrCreatePlayer(playerEntity.getDisplayName().getString());
                    RGBValue standingRgb = RamielClient.getInstance().getConfig().getRgbByStanding(rplayer.getStanding());

                    if(RamielClient.getInstance().getConfig().renderHoops()) {
                        drawHoop(matrixStack, (float) x,
                                (float) y - 1.5F, (float) z, ColorHelper.Argb.getArgb(1, standingRgb.getR(), standingRgb.getG(), standingRgb.getB()),
                                100);
                    }

                    if(RamielClient.getInstance().getConfig().renderHitboxes()) {
                        Tessellator tes = Tessellator.getInstance();
                        BufferBuilder buf = tes.getBuffer();
                        BoxUtil.drawBox(matrixStack, tes, buf, playerEntity,
                                (float) x,(float)  y - 2.5F, (float) z
                                , standingRgb.getRBox(), standingRgb.getGBox()
                                , standingRgb.getBBox(), standingRgb.getRBox(), standingRgb.getGBox(), standingRgb.getBBox(), 1, 3.0F);
                    }

                    //Once you're close enough waypoints shouldn't appear.
                    if (dist < RamielClient.getInstance().getConfig().getPersonalSpace()) {
                        continue;
                    }

                    double viewDist = dist;
                    double maxDist = game.options.viewDistance * 16;
                    if (dist > maxDist) {
                        x = x / dist * maxDist;
                        y = y / dist * maxDist;
                        z = z / dist * maxDist;
                        viewDist = maxDist;
                    }
                    float scale = (float) (0.0025D * (viewDist + 4.0D));
                    String altText = rplayer.isAlt() ? "[" + rplayer.getMain() + "]" : "";
                    String waypointText = rplayer.getUsername() + " " + altText + " (" + (int) dist + "m) ";
                    if(rplayer.getStanding().equals(Standing.FOCUSED)) {
                        //VoxelShape voxelShape = playerEntity.getBlockStateAtPos().getOutlineShape(MinecraftClient.getInstance().world,
                        //        MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getBlockPos(), ShapeContext.of(playerEntity));
                        //drawShapeOutline(matrixStack, voxelShape, x, y, z, 1, standingRgb.r, standingRgb.g, standingRgb.b);
                    } else {
                    }
                    drawWaypoint(matrixStack,
                            game.textRenderer,
                            waypointText,
                            ColorHelper.Argb.getArgb(1, standingRgb.getR(), standingRgb.getG(), standingRgb.getB()),
                            (float) x, (float) y, (float) z,
                            scale,
                            game.getEntityRenderDispatcher().getRotation());
                    //TODO figure out how to get the actual world name if possible, otherwise base it entirely off of server. Not an issue for the moment, but later on
                    // it will be.
                    PlayerRegistry.getInstance().updateLocation(playerEntity, new Location(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
                            "world"));
                }
		}
        Iterator cacheIt = PlayerRegistry.getInstance().fetchPlayersInCache().iterator();
        while (cacheIt.hasNext()){
            RamielPlayer cachedPlayer = (RamielPlayer) cacheIt.next();
            //If the player is on radar or it has been longer than 15 minutes nothing should happen.
            if(playersOnRadar.contains(cachedPlayer.getUsername()) ||
                    ((System.currentTimeMillis() - cachedPlayer.getLastUpdate()) > (RamielClient.getInstance().getConfig().getTimeToDisappear() * 60L) * 1000) ||
                    cachedPlayer.getLocation() == null) {
                //Do nothing
            } else {
                Location loc = cachedPlayer.getLocation();
                double x = loc.getX() - posX;
                double y = loc.getY() + 0.8D - posY;
                double z = loc.getZ() - posZ;
                double dist = Math.sqrt(x * x + y * y + z * z);

                int maxWaypointDist = RamielClient.getInstance().getMaxWaypointDist();

                if (maxWaypointDist > -1 && dist > (double) maxWaypointDist)
                    continue;

                double viewDist = dist;
                double maxDist = game.options.viewDistance * 16;
                if (dist > maxDist) {
                    x = x / dist * maxDist;
                    y = y / dist * maxDist;
                    z = z / dist * maxDist;
                    viewDist = maxDist;
                }
                float scale = (float) (0.0025D * (viewDist + 4.0D));
                String altText = cachedPlayer.isAlt() ? " [" + cachedPlayer.getMain() + "] " : "";
                String waypointText = cachedPlayer.getUsername() + altText + "(" + (int) dist + "m) " + formatAge(cachedPlayer.getLastUpdate());
                RGBValue standingRgb = RamielClient.getInstance().getConfig().getRgbByStanding(cachedPlayer.getStanding());
                drawWaypoint(matrixStack,
                        game.textRenderer,
                        waypointText,
                        ColorHelper.Argb.getArgb(1, standingRgb.getR(), standingRgb.getG(), standingRgb.getB()),
                        (float) x, (float) y, (float) z,
                        scale,
                        game.getEntityRenderDispatcher().getRotation());
            }
        }
    }

    //TODO fix (maybe use for focus)
    private static void drawShapeOutline(MatrixStack matrices, VoxelShape voxelShape, double x, double y, double z, float alpha, float red, float g, float b) {
        MatrixStack.Entry entry = matrices.peek();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bb = tes.getBuffer();
        bb.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
        voxelShape.forEachEdge((k, l, m, n, o, p) -> {
            float q = (float)(n - k);
            float r = (float)(o - l);
            float s = (float)(p - m);
            float t = MathHelper.sqrt(q * q + r * r + s * s);
            q /= t;
            r /= t;
            s /= t;
            bb.vertex(entry.getPositionMatrix(), (float)(k + x), (float)(l + y), (float)(m + z)).color(alpha, red, g, b).normal(entry.getNormalMatrix(), q, r, s).next();
            bb.vertex(entry.getPositionMatrix(), (float)(n + x), (float)(o + y), (float)(p + z)).color(alpha, red, g, b).normal(entry.getNormalMatrix(), q, r, s).next();
        });
        tes.draw();
        matrices.pop();
    }

    /**
     * Quickly formats the age according to timestamps.
     * @param timestamp the timestamp to grab the age for
     * @return A nice looking string for the timestamp.
     */
    private String formatAge(long timestamp) {
        final long age = System.currentTimeMillis() - timestamp;
        if (age < 0) {
            return "future";
        } else if (age < 10 * 1000) {
            return "now";
        } else if (age < 60 * 1000) {
            return "" + (age / 1000 / 10) * 10 + "s";
        } else if (age < 3600 * 1000) {
            return "" + age / 1000 / 60 + "min";
        } else if (age < 24 * 3600 * 1000) {
            return "" + age / 3600 / 1000 + "h" + (age / 1000 / 60) % 60 + "min";
        } else {
            return new SimpleDateFormat("MM/dd HH:mm").format(new Date(timestamp));
        }
    }
}
