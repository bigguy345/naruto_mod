package net.narutomod.procedure;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodModVariables;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.goatee.data.JutsuKey;
import net.narutomod.item.ItemDojutsu;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureSpecialJutsu2OnKeyPressed extends ElementsNarutomodMod.ModElement {
	public ProcedureSpecialJutsu2OnKeyPressed(ElementsNarutomodMod instance) {
		super(instance, 66);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure SpecialJutsu2OnKeyPressed!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure SpecialJutsu2OnKeyPressed!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure SpecialJutsu2OnKeyPressed!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		EntityPlayer entity = (EntityPlayer) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double which_path = 0;
		boolean f1 = false;
		String CTRL_pressed = "";
		ItemStack stack = ItemStack.EMPTY;
		ItemStack helmet = ItemStack.EMPTY;
		CTRL_pressed = "CTRL_pressed";
		if (entity.isSpectator()) {
			return;
		}
		entity.getEntityData().setBoolean((NarutomodModVariables.JutsuKey2Pressed), (is_pressed));
		if ((world.isRemote)) {
			return;
		}
		stack = ((entity instanceof EntityLivingBase) ? entity.getHeldItemMainhand() : ItemStack.EMPTY);
		helmet = ((entity instanceof EntityPlayer) ? ItemDojutsu.getWorn(entity) : ItemStack.EMPTY);
		if (helmet.getItem() instanceof ItemDojutsu.Base) {
			ItemDojutsu.Base eye = (ItemDojutsu.Base) helmet.getItem();
			JutsuKey key = eye.data.getKey(2);
			if (key.hasTask() && key.fire(is_pressed, helmet, entity))
				return;

			eye.onJutsuKey2(is_pressed, helmet, entity);
		} else if (EntityBijuManager.isJinchuriki(entity)) {
			if ((!(is_pressed))) {
				EntityBijuManager.toggleBijuCloak(entity);
			}
		}
	}
}
