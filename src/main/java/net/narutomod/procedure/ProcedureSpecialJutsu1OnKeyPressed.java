package net.narutomod.procedure;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodModVariables;
import net.narutomod.goatee.data.JutsuKey;
import net.narutomod.item.ItemDojutsu;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureSpecialJutsu1OnKeyPressed extends ElementsNarutomodMod.ModElement {
	public ProcedureSpecialJutsu1OnKeyPressed(ElementsNarutomodMod instance) {
		super(instance, 64);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure SpecialJutsu1OnKeyPressed!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure SpecialJutsu1OnKeyPressed!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure SpecialJutsu1OnKeyPressed!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		EntityPlayer entity = (EntityPlayer) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		ItemStack helmet = ItemStack.EMPTY;
		entity.getEntityData().setBoolean((NarutomodModVariables.JutsuKey1Pressed), (is_pressed));
		if (((world.isRemote) || entity.isSpectator())) {
			return;
		}
		helmet = ((entity instanceof EntityPlayer) ? ItemDojutsu.getWorn(entity) : ItemStack.EMPTY);
		if (helmet.getItem() instanceof ItemDojutsu.Base) {
			ItemDojutsu.Base eye = (ItemDojutsu.Base) helmet.getItem();
			JutsuKey key = eye.data.getKey(1);
			if (key.hasTask() && key.fire(is_pressed, helmet, entity))
				return;
			
			eye.onJutsuKey1(is_pressed, helmet, entity);
		}
	}
}
