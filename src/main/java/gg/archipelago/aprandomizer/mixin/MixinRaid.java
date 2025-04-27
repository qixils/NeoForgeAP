package gg.archipelago.aprandomizer.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Raid.class)
public abstract class MixinRaid {

    @Shadow
    public abstract BlockPos getCenter();
    @Inject(method = "findRandomSpawnPos(Lnet/minecraft/server/level/ServerLevel;I)Lnet/minecraft/core/BlockPos;", at = @At(value = "HEAD"), cancellable = true)
    protected void onFindRandomSpawnPos(ServerLevel level, int attempts, CallbackInfoReturnable<BlockPos> cir) {
        if (level.dimension() == Level.NETHER) {
            cir.setReturnValue(getCenter());
        }
    }
}
