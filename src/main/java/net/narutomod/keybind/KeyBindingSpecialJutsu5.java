package net.narutomod.keybind;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodMod;
import net.narutomod.item.ItemDojutsu;
import org.lwjgl.input.Keyboard;

@ElementsNarutomodMod.ModElement.Tag
public class KeyBindingSpecialJutsu5 extends ElementsNarutomodMod.ModElement {
	private KeyBinding keys;
	private boolean wasKeyDown;

	public KeyBindingSpecialJutsu5(ElementsNarutomodMod instance) {
		super(instance, 100);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		elements.addNetworkMessage(KeyBindingPressedMessageHandler.class, KeyBindingPressedMessage.class, Side.SERVER);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {
		keys = new KeyBinding("key.mcreator.specialjutsu5", Keyboard.KEY_C, "key.mcreator.category");
		ClientRegistry.registerKeyBinding(keys);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientPostTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().currentScreen == null) {
			this.processKeyBind();
		}
	}

	@SideOnly(Side.CLIENT)
	private void processKeyBind() {
		boolean isKeyDown = this.keys.isKeyDown();
		//Pressed = 0, Held = 1, Released = 2
		byte pressType = (byte) (!wasKeyDown && isKeyDown ? 0 : wasKeyDown && !isKeyDown ? 2 : 1);
		
		if (isKeyDown || this.wasKeyDown) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new KeyBindingPressedMessage(pressType));
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player != null) {
				pressAction(player, pressType);
			}
		}
		this.wasKeyDown = isKeyDown;
	}

	public static class KeyBindingPressedMessageHandler implements IMessageHandler<KeyBindingPressedMessage, IMessage> {
		@Override
		public IMessage onMessage(KeyBindingPressedMessage message, MessageContext context) {
			EntityPlayerMP entity = context.getServerHandler().player;
			entity.getServerWorld().addScheduledTask(() -> {
				pressAction(entity, message.pressType);
			});
			return null;
		}
	}

	public static class KeyBindingPressedMessage implements IMessage {
		byte pressType;

		public KeyBindingPressedMessage() {
		}

		public KeyBindingPressedMessage(byte is_pressed) {
			this.pressType = is_pressed;
		}

		public void toBytes(ByteBuf buf) {
			buf.writeByte(this.pressType);
		}

		public void fromBytes(ByteBuf buf) {
			this.pressType = buf.readByte();
		}
	}

	private static void pressAction(EntityPlayer entity, byte pressType) {
		World world = entity.world;

		// security measure to prevent arbitrary chunk generation
		if (!world.isBlockLoaded(new BlockPos(entity.posX, entity.posY, entity.posZ)) || world.isRemote || entity.isSpectator())
			return;

		ItemStack helmet = entity.inventory.armorInventory.get(3);
		if ((helmet.getItem() instanceof ItemDojutsu.Base))
			((ItemDojutsu.Base) helmet.getItem()).onJutsuKey5(pressType, helmet, entity);
	}
}
