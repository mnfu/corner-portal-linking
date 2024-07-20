package net.fabricmc.starbidou.portallinking.mixin;

import net.fabricmc.starbidou.portallinking.PortalHelper;
import net.fabricmc.starbidou.portallinking.PortalLinking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.NetherPortal;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable,
        EntityLike,
        CommandOutput {

    // @Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void inject(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir)
    {
        Entity entity = ((Entity)(Object)this);
        cir.setReturnValue(getTeleportTarget(entity, destination));
    }

    protected TeleportTarget getTeleportTarget(Entity entity, ServerWorld destination)
    {
        // Very similar to vanilla except for the call to entity.getPortalRect
        // It is only called if the portal has no valid linking corners.

        // Quick access variables
        boolean toTheOverworld = destination.getRegistryKey() == World.OVERWORLD;
        boolean toTheNether = destination.getRegistryKey() == World.NETHER;
        boolean toTheEnd = destination.getRegistryKey() == World.END;

        boolean fromTheNether = entity.getWorld().getRegistryKey() == World.NETHER;
        boolean fromTheEnd = entity.getWorld().getRegistryKey() == World.END;

        // Handling fixed teleportations from and to the end
        if ((fromTheEnd && toTheOverworld) || toTheEnd) {
            BlockPos blockPos = toTheEnd ?
                    ServerWorld.END_SPAWN_POS
                    : destination.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, destination.getSpawnPos());

            return null;
            // return new TeleportTarget(new Vec3d((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5), entity.getVelocity(), entity.getYaw(), entity.getPitch());
        }

        // Only supported situations left are nether related
        if (!fromTheNether && !toTheNether) {
            return null;
        }

        // Nether related
        WorldBorder worldBorder = destination.getWorldBorder();
        double d = DimensionType.getCoordinateScaleFactor(entity.getWorld().getDimension(), destination.getDimension());
        BlockPos blockPos2 = worldBorder.clamp(entity.getX() * d, entity.getY(), entity.getZ() * d);

        Optional<BlockLocating.Rectangle> portalRect;

        // Linking
        var corners = PortalHelper.getLastNetherPortalCornersVector(entity);
        if( corners.hasLinkingBlocks())
        {
            // Modded
            portalRect = PortalHelper.modifiedGetPortalRect(entity, destination, blockPos2, toTheNether, worldBorder, corners);
        }
        else
        {
            // Vanilla
            // portalRect = entity.getPortalRect(destination, blockPos2, toTheNether, worldBorder);
        }

        return null;
        /*return portalRect.map(rect -> {
            Vec3d vec3d;
            Direction.Axis axis;
            BlockState blockState = entity.getWorld().getBlockState(entity.lastNetherPortalPosition);
            if (blockState.contains(Properties.HORIZONTAL_AXIS))
            {
                axis = blockState.get(Properties.HORIZONTAL_AXIS); // X or Z only
                var rectangle = BlockLocating.getLargestRectangle(entity.lastNetherPortalPosition, axis, 21, Direction.Axis.Y, 21, pos -> entity.getWorld().getBlockState((BlockPos)pos) == blockState);
                vec3d = entity.positionInPortal(axis, rectangle);
            }
            else
            {
                axis = Direction.Axis.X; // Arbitrary choice between X and Z
                vec3d = new Vec3d(0.5, 0.0, 0.0); // Bottom-Center of the portal
            }
            return NetherPortal.getNetherTeleportTarget(destination, rect, axis, vec3d, entity, entity.getVelocity(), entity.getYaw(), entity.getPitch());
        }).orElse(null);*/
    }
}
