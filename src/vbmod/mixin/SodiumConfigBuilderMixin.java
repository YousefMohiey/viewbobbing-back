package vbmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.caffeinemc.mods.sodium.api.config.structure.BooleanOptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.caffeinemc.mods.sodium.client.gui.SodiumConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Sodium replaces the Video Settings screen with its own, so the vanilla mixin never shows up there.
 * This appends View Bobbing to Sodium's General page instead.
 *
 * Soft target: skipped cleanly when Sodium is not installed.
 */
@Pseudo
@Mixin(SodiumConfigBuilder.class)
public class SodiumConfigBuilderMixin {
    @ModifyReturnValue(method = "buildGeneralPage", at = @At("RETURN"))
    private OptionPageBuilder vbmod$addBobbing(OptionPageBuilder page, ConfigBuilder builder) {
        Options options = Minecraft.getInstance().options;
        BooleanOptionBuilder bobbing = builder
            .createBooleanOption(Identifier.fromNamespaceAndPath("viewbobbingback", "view_bobbing"))
            .setName(Component.translatable("options.viewBobbing"))
            .setTooltip(Component.literal("Camera shake while walking. Same vanilla setting."))
            .setStorageHandler(options::save)
            .setBinding(value -> options.bobView().set(value), () -> options.bobView().get())
            .setDefaultValue(true);

        page.addOptionGroup(builder.createOptionGroup().addOption(bobbing));
        return page;
    }
}
