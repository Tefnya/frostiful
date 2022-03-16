package com.github.thedeathlycow.lostinthecold.mixins;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {


    @Inject(method = "renderHealthBar",
            at = @At("TAIL"))
    private void renderFrozenHearts(MatrixStack matrices, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        double freezingProgress = ((double) player.getFrozenTicks()) / player.getMinFreezeDamageTicks();
        int numFrozenHearts = MathHelper.ceil(freezingProgress * player.getHealth() / 2.0D);

        // Many of the following variables are essentially copies of local variables in the renderHealthBar
        // method. The names used here are my best-guess as to what they actually are.

        int hardcoreTextureOffset = 9 * (player.world.getLevelProperties().isHardcore() ? 5 : 0);
        for (int i = 0; i < numFrozenHearts; i++) {
            int row = i / 10;
            int col = i % 10;
            int screenXPos = x + col * 8;
            int screenYPos = y - row * lines;

            int r = i * 2;

            boolean isHalfHeart = r + 1 == numFrozenHearts;
            ((InGameHudInvoker) this).invokeDrawHeart(matrices, InGameHud.HeartType.FROZEN, screenXPos, screenYPos, hardcoreTextureOffset, false, isHalfHeart);
        }
    }

}
