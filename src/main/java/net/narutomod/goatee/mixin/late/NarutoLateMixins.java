package net.narutomod.goatee.mixin.late;

import net.minecraftforge.fml.common.Optional;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;


@Optional.Interface(iface = "zone.rong.mixinbooter.ILateMixinLoader", modid = "mixinbooter")
public class NarutoLateMixins implements  ILateMixinLoader {

    @Optional.Method(modid = "mixinbooter")
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.narutomod.late.json");

    }
}
