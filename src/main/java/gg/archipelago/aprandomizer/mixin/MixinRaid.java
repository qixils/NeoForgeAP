package gg.archipelago.aprandomizer.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Raid.class, remap = false)
public abstract class MixinRaid {

    @Final
    @Shadow
    private ServerLevel level;

    @Shadow
    public abstract BlockPos getCenter();

    @Inject(method = "findRandomSpawnPos", at = @At(value = "HEAD"), cancellable = true)
    protected void onFindRandomSpawnPos(int p_37708_, CallbackInfoReturnable<BlockPos> cir) {
        if (this.level.dimension() == Level.NETHER) {
            cir.setReturnValue(getCenter());
        }
    }
}
