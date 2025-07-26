package net.narutomod.procedure;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.goatee.data.JutsuKey;
import net.narutomod.item.ItemBijuCloak;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemJutsu;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedurePowerIncreaseOnKeyPressed extends ElementsNarutomodMod.ModElement {
	public ProcedurePowerIncreaseOnKeyPressed(ElementsNarutomodMod instance) {
		super(instance, 104);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure PowerIncreaseOnKeyPressed!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure PowerIncreaseOnKeyPressed!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure PowerIncreaseOnKeyPressed!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double i = 0;
		ItemStack helmet = ItemStack.EMPTY;
		ItemStack itemmainhand = ItemStack.EMPTY;
		ItemStack itemoffhand = ItemStack.EMPTY;
		if ((!(world.isRemote))) {
			helmet = ((entity instanceof EntityPlayer) ? ItemDojutsu.getWorn(((EntityPlayer) entity)) : ItemStack.EMPTY);
			itemmainhand = ((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY);
			itemoffhand = ((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemOffhand() : ItemStack.EMPTY);
			boolean wearingDojutsu = ItemDojutsu.is(helmet);
			boolean isBijuuCloak = EntityBijuManager.cloakLevel((EntityPlayer) entity) > 0;
			System.out.println(EntityBijuManager.cloakLevel((EntityPlayer) entity));

			if (itemmainhand.getItem() instanceof ItemJutsu.Base) {
				if ((!(is_pressed))) {
					ItemJutsu.Base.switchNextJutsu(itemmainhand, (EntityLivingBase) entity);
				}
			} else if (itemoffhand.getItem() instanceof ItemJutsu.Base) {
				if ((!(is_pressed))) {
					ItemJutsu.Base.switchNextJutsu(itemoffhand, (EntityLivingBase) entity);
				}
			}  else if (wearingDojutsu) {
				ItemDojutsu.Base eye = (ItemDojutsu.Base) helmet.getItem();
				JutsuKey key = eye.data.getSwitchJutsuKey();
				if (key.hasTask() && key.fire(is_pressed, helmet, (EntityPlayer) entity))
					return;

				eye.onSwitchJutsuKey(is_pressed, helmet, (EntityPlayer) entity);
			} else if ((((helmet).getItem() == new ItemStack(ItemBijuCloak.helmet, (int) (1)).getItem())
					&& ((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(2) : ItemStack.EMPTY)
							.getItem() == new ItemStack(ItemBijuCloak.body, (int) (1)).getItem())
							&& (((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(1) : ItemStack.EMPTY)
									.getItem() == new ItemStack(ItemBijuCloak.legs, (int) (1)).getItem())))) {
				if ((!(is_pressed))) {
					EntityBijuManager.increaseCloakLevel((EntityPlayer) entity);
				}
			}
		}
	}
}
