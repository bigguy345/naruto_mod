package net.narutomod.procedure;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodModVariables;
import net.narutomod.PlayerTracker;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.goatee.data.NarutoData;
import net.narutomod.goatee.util.AdvancementUtil;
import net.narutomod.item.*;

import java.util.List;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnPlayerDeath extends ElementsNarutomodMod.ModElement {
	public ProcedureOnPlayerDeath(ElementsNarutomodMod instance) {
		super(instance, 729);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure OnPlayerDeath!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		boolean keepInventory = false;
		ItemStack stack = ItemStack.EMPTY;
		ItemStack stack2 = ItemStack.EMPTY;
		if ((entity instanceof EntityPlayerMP)) {
			keepInventory = (boolean) entity.world.getGameRules().getBoolean("keepInventory");

			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				List<ItemStack> stacks = ProcedureUtils.getAllItemsOfSubType(player, ItemRinnegan.Base.class);
				stacks.addAll(ProcedureUtils.getAllItemsOfSubType(player, ItemRinneganTomoe.Base.class));
				for (ItemStack stck : stacks) {
					if (stck.hasTagCompound() && stck.getTagCompound().hasUniqueId("KoH_id")) {
						if (entity instanceof EntityLivingBase)
							((EntityLivingBase) entity).setHealth((float) 2);
						if (dependencies.get("event") != null) {
							Object _obj = dependencies.get("event");
							if (_obj instanceof net.minecraftforge.fml.common.eventhandler.Event) {
								net.minecraftforge.fml.common.eventhandler.Event _evt = (net.minecraftforge.fml.common.eventhandler.Event) _obj;
								if (_evt.isCancelable())
									_evt.setCanceled(true);
							}
						}
					} else if (!keepInventory) {
						stck.setCount(0);
						if(AdvancementUtil.has((EntityPlayerMP) player,"narutomod:rinneganawakened"))
							AdvancementUtil.revoke((EntityPlayerMP) player,"narutomod:rinneganawakened");
					}
				}
			}

			if ((((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1)))
					: false) && (!(keepInventory)))) {
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1)).getItem(), -1,
							(int) 1, null);
			}
			if (entity instanceof EntityPlayer) {
				stack = ProcedureUtils.getItem((EntityPlayer) entity, stk -> ItemByakugan.is(stk));
				if (!stack.isEmpty()) {
					ItemStack _stack = (stack);
					if (!_stack.hasTagCompound())
						_stack.setTagCompound(new NBTTagCompound());
					_stack.getTagCompound().setBoolean((NarutomodModVariables.RINNESHARINGAN_ACTIVATED), (false));
				}
			}
			if (entity instanceof EntityPlayer)
				((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemAsuraPathArmor.body, (int) (1)).getItem(), -1, (int) (-1),
						null);
			if ((!(keepInventory))) {
				entity.getEntityData().setBoolean((NarutomodModVariables.FirstGotNinjutsu), (false));
				if (EntityBijuManager.isJinchuriki((EntityPlayer) entity)) {
					EntityBijuManager.unsetPlayerAsJinchuriki((EntityPlayer) entity);
				}
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemMokuton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemEightGates.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemRaiton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemFuton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemNinjutsu.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemDoton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemYoton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemInton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemKaton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemJinton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemSuiton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemBakuton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemHyoton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemJiton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemShakuton.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemYooton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemRanton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemFutton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemShikotsumyaku.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemGourd.body, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemIryoJutsu.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemSenjutsu.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
			} else {
				if ((EntityBijuManager.cloakLevel((EntityPlayer) entity) > 0)) {
					EntityBijuManager.toggleBijuCloak((EntityPlayer) entity);
				}
				if (entity.world.getGameRules().getBoolean(PlayerTracker.FORCE_DOJUTSU_DROP_RULE)) {
					stack = ProcedureUtils.getItem((EntityPlayer) entity, stk -> ItemByakugan.is(stk));
					if (stack != null && !stack.isEmpty()) {
						((EntityPlayer) entity).dropItem(stack.copy(), true, true);
						stack.setCount(0);
					}
					stack = ProcedureUtils.getMatchingItemStack((EntityPlayer) entity, ItemSharingan.helmet);
					if (stack != null) {
						((EntityPlayer) entity).dropItem(stack.copy(), true, true);
						stack.setCount(0);
					}
					stack = ProcedureUtils.getMatchingItemStack((EntityPlayer) entity, ItemMangekyoSharingan.helmet);
					if (stack != null) {
						((EntityPlayer) entity).dropItem(stack.copy(), true, true);
						stack2 = new ItemStack(ItemSharingan.helmet, (int) (1));
						((ItemSharingan.Base) stack2.getItem()).copyOwner(stack2, stack);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack2);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						((stack)).shrink((int) 1);
					}
					stack = ProcedureUtils.getMatchingItemStack((EntityPlayer) entity, ItemMangekyoSharinganObito.helmet);
					if (stack != null) {
						((EntityPlayer) entity).dropItem(stack.copy(), true, true);
						stack2 = new ItemStack(ItemSharingan.helmet, (int) (1));
						((ItemSharingan.Base) stack2.getItem()).copyOwner(stack2, stack);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack2);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						((stack)).shrink((int) 1);
					}
					EntityPlayer player = (EntityPlayer) entity;

					NarutoData data = NarutoData.get(player);
					List<ItemStack> toDrops = data.dojutsuWheel.dropAll(player, eye -> ItemDojutsu.isDroppable(eye), false);

					if (ItemDojutsu.isDroppable(data.getDojutsuSlot())) {
						ItemStack slot = data.getDojutsuSlot();
						ItemStack copy = slot.copy();
						player.dropItem(copy, true, false);
						slot.setCount(0);
						toDrops.add(copy);

					}

					for (ItemStack eye : toDrops) {
						if (eye.getItem() == ItemMangekyoSharingan.helmet || eye.getItem() == ItemMangekyoSharinganObito.helmet) {
							ItemStack stk = new ItemStack(ItemSharingan.helmet, 1);
							((ItemSharingan.Base) stk.getItem()).copyOwner(stk, eye);
							ItemHandlerHelper.giveItemToPlayer(player, stk);
						}
					}
				}
			}
			PlayerTracker.PlayerHook.addPersistentData(entity, NarutomodModVariables.FirstGotNinjutsu,
					entity.getEntityData().getBoolean(NarutomodModVariables.FirstGotNinjutsu));
			ProcedureSync.EntityNBTTag.removeAndSync(entity, NarutomodModVariables.forceBowPose);
			entity.getEntityData().setBoolean("susanoo_activated", (false));
			entity.getEntityData().setInteger("ForceExtinguish", 5);
			entity.setNoGravity(false);
		}
	}

	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		if (event != null && event.getEntity() != null) {
			Entity entity = event.getEntity();
			int i = (int) entity.posX;
			int j = (int) entity.posY;
			int k = (int) entity.posZ;
			World world = entity.world;
			java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
			dependencies.put("x", i);
			dependencies.put("y", j);
			dependencies.put("z", k);
			dependencies.put("world", world);
			dependencies.put("entity", entity);
			dependencies.put("event", event);
			this.executeProcedure(dependencies);
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
