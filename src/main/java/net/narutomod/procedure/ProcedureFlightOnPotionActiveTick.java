package net.narutomod.procedure;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.lang.reflect.Field;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureFlightOnPotionActiveTick extends ElementsNarutomodMod.ModElement {
	public ProcedureFlightOnPotionActiveTick(ElementsNarutomodMod instance) {
		super(instance, 416);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure FlightOnPotionActiveTick!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		int speed = (int) dependencies.get("speed");
		if (entity instanceof EntityPlayer) {
			((EntityPlayer) entity).capabilities.allowFlying = (true);
			if (((EntityPlayer) entity).capabilities.getFlySpeed() == 0.05f) {
				try {
					Field field = ReflectionHelper.findField(PlayerCapabilities.class, "flySpeed", "field_75096_f");
					field.setAccessible(true);
					field.setFloat(((EntityPlayer) entity).capabilities, 0.05f * speed);
				} catch (Exception e) {
				}
			}
			((EntityPlayer) entity).sendPlayerAbilities();
		}
	}
}
