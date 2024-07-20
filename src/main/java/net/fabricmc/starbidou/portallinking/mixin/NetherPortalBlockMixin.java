package net.fabricmc.starbidou.portallinking.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.Portal;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin extends Block implements Portal {

    public NetherPortalBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOrCreateExitPortalTarget", at = @At("HEAD"), cancellable = true)
    private void inject(ServerWorld world, Entity entity, BlockPos pos, CallbackInfoReturnable<TeleportTarget> cir)
    {

    }
}
