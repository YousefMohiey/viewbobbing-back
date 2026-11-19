package vbmod;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/** Applies the config only on the deobfuscated 26.x runtime. */
public class ModernOnlyPlugin implements IMixinConfigPlugin {
    static boolean modernGame() {
        // Metadata only: loading a game class here (even Options) forces KeyMapping to load
        // before mixin preparation and breaks other mods with "loaded too early".
        return net.fabricmc.loader.api.FabricLoader.getInstance()
            .getModContainer("minecraft")
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .map(ModernOnlyPlugin::majorVersion)
            .map(major -> major >= 26)
            .orElse(true);
    }

    static int majorVersion(String version) {
        int dot = version.indexOf('.');
        String head = dot > 0 ? version.substring(0, dot) : version;
        StringBuilder digits = new StringBuilder();
        for (char c : head.toCharArray()) {
            if (Character.isDigit(c)) {
                digits.append(c);
            } else {
                break;
            }
        }
        try {
            return digits.length() == 0 ? 0 : Integer.parseInt(digits.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return modernGame();
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
