package gg.archipelago.aprandomizer.mixin;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragonFight.class)
public abstract class MixinSpawnGateways {

    @Shadow
    protected abstract void spawnNewGateway();

    @Inject(method = "createNewDragon()Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;", at = @At("RETURN"))
     private void spawnNewDragon(CallbackInfoReturnable<EnderDragon> cir) {
        for (int i = 0; i < 20; i++) {
            spawnNewGateway();
        }
     }
}
