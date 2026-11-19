package vbmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.Arrays;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Puts "View Bobbing" in the first row of Video Settings, right column. */
@Mixin(VideoSettingsScreen.class)
public class VideoSettingsScreenMixin {
    @ModifyReturnValue(method = "displayOptions", at = @At("RETURN"))
    private static OptionInstance<?>[] vbmod$addBobbing(OptionInstance<?>[] original, Options options) {
        OptionInstance<?>[] out = Arrays.copyOf(original, original.length + 1);
        out[0] = original[0];
        out[1] = options.bobView();
        System.arraycopy(original, 1, out, 2, original.length - 1);
        return out;
    }
}
