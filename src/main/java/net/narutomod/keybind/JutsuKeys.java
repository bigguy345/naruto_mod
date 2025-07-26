package net.narutomod.keybind;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.entity.EntityHiraishin;
import net.narutomod.goatee.client.hud.wheel.HUDItemStackWheel;
import net.narutomod.goatee.data.JutsuKey;
import net.narutomod.goatee.data.NarutoData;
import net.narutomod.goatee.network.AbstractPacket;
import net.narutomod.goatee.network.PacketHandler;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemSharingan;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class JutsuKeys {
	public static List<Key> jutsuKeys = new ArrayList<>();
	
	private static Key keyBijuu = new Key(3, "key.bijuu_cloak", Keyboard.KEY_NONE, "key.mcreator.category");
	private static Key key4 = new Key(4, "key.jutsu.4", Keyboard.KEY_NUMPAD4, "key.mcreator.category");
	private static Key key5 = new Key(5, "key.jutsu.5", Keyboard.KEY_NUMPAD5, "key.mcreator.category");
	private static Key key6 = new Key(6, "key.jutsu.6", Keyboard.KEY_NUMPAD6, "key.mcreator.category");
	private static Key key7 = new Key(7, "key.jutsu.7", Keyboard.KEY_NUMPAD7, "key.mcreator.category");

	private static Key lockOn = new Key(10, "key.lock_on", Keyboard.KEY_TAB, "key.mcreator.category");

	////////////////////////
	///////////////////////
	// Client handled keys

	public static Key dojutsuWheel = new Key(0, "key.dojutsu_wheel", Keyboard.KEY_V, "key.mcreator.category") {
		public boolean onPress(byte pressType) {
			if (pressType == 0)
				Minecraft.getMinecraft().displayGuiScreen(new HUDItemStackWheel(NarutoData.getClient().dojutsuWheel));

			return false; //client sided only
		}
	};

	public static Key teleportBehind = new Key(0, "key.teleport_behind", Keyboard.KEY_LMENU, "key.mcreator.category") {
		public boolean onPress(byte pressType) {
			if (pressType == 2)
				EntityHiraishin.teleportBehind();
			return false; //client sided only
		}
	};

	@SideOnly(Side.CLIENT)
	public static class Key extends KeyBinding {
		public byte keyId;
		public boolean wasDown;

		public Key(int keyId, String description, int keyCode, String category) {
			super(description, keyCode, category);
			this.keyId = (byte) keyId;
			jutsuKeys.add(this);
		}

		/**
		 * @return Pressed = 0, Held = 1, Released = 2
		 */
		public byte getPressType() {
			boolean isDown = isKeyDown();
			return (byte) (!wasDown && isDown ? 0 : wasDown && !isDown ? 2 : 1);
		}

		public boolean onPress(byte pressType) {
			return true; //send packet
		}
	}

	public static void register() {
		jutsuKeys.forEach(key -> ClientRegistry.registerKeyBinding(key));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void tick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START || Minecraft.getMinecraft().currentScreen != null || Minecraft.getMinecraft().player == null)
			return;

		for (Key key : jutsuKeys) {
			boolean isDown = key.isKeyDown();
			if (isDown || key.wasDown) {
				byte pressType = key.getPressType();
				if (key.onPress(pressType))
					PacketHandler.Instance.sendToServer(new Packet(key.keyId, pressType));
				key.wasDown = isDown;
			}
		}
	}

	public static class Packet extends AbstractPacket {
		public static final String packetName = "jutsuKey";

		public String getChannel() {
			return packetName;
		}

		byte keyId;
		byte pressType;

		public Packet() {
		}

		public Packet(byte keyId, byte is_pressed) {
			this.pressType = is_pressed;
			this.keyId = keyId;
		}

		public void sendData(ByteBuf out) throws IOException {
			out.writeByte(this.keyId);
			out.writeByte(this.pressType);
		}

		public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
			this.keyId = in.readByte();
			this.pressType = in.readByte();

			ItemStack helmet = ItemDojutsu.getWorn(player);


			if (ItemSharingan.wearingAny(player) && keyId == 10 && pressType == 0)
				((ItemSharingan.Base) helmet.getItem()).lockOnLookingAt(player, helmet);
			else if ((helmet.getItem() instanceof ItemDojutsu.Base)) {
				ItemDojutsu.Base eye = (ItemDojutsu.Base) helmet.getItem();

				JutsuKey key = eye.data.getKey(keyId);
				if (key.hasTask() && key.fire(pressType == 0, helmet, player))
					return;

				if (keyId == 4)
					eye.onJutsuKey4(pressType, helmet, player);
				else if (keyId == 5)
					eye.onJutsuKey5(pressType, helmet, player);
				else if (keyId == 6)
					eye.onJutsuKey6(pressType, helmet, player);
				else if (keyId == 7)
					eye.onJutsuKey7(pressType, helmet, player);
			}


			if (keyId == 3 && EntityBijuManager.isJinchuriki(player) && pressType == 2)
				EntityBijuManager.toggleBijuCloak(player);
		
		}
	}
}
