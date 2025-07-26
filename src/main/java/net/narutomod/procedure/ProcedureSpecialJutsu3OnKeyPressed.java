package net.narutomod.procedure;

import net.narutomod.goatee.data.JutsuKey;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.entity.EntityTailedBeast;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureSpecialJutsu3OnKeyPressed extends ElementsNarutomodMod.ModElement {
	public ProcedureSpecialJutsu3OnKeyPressed(ElementsNarutomodMod instance) {
		super(instance, 101);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure SpecialJutsu3OnKeyPressed!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure SpecialJutsu3OnKeyPressed!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure SpecialJutsu3OnKeyPressed!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		EntityPlayer entity = (EntityPlayer) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		ItemStack helmet = ItemStack.EMPTY;
		if (((world.isRemote) || entity.isSpectator())) {
			return;
		}
		helmet = ((entity instanceof EntityPlayer) ? ItemDojutsu.getWorn(entity) : ItemStack.EMPTY);
		if (helmet.getItem() instanceof ItemDojutsu.Base) {
			ItemDojutsu.Base eye = (ItemDojutsu.Base) helmet.getItem();
			JutsuKey key = eye.data.getKey(3);
			if (key.hasTask() && key.fire(is_pressed, helmet, entity))
				return;

			eye.onJutsuKey3(is_pressed, helmet, entity);
		} else if (EntityBijuManager.cloakLevel(entity) == 3) {
			if ((!(is_pressed))) {
				EntityBijuManager.getBijuOfPlayerInWorld(entity).attackEntityWithRangedAttack(entity, 0f);
			}
		} else if (EntityBijuManager.cloakLevel(entity) == 2) {
			EntityTailedBeast.EntityTailBeastBall.create(entity, is_pressed);
		}
	}
}
