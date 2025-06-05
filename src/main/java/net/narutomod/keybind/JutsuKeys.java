package net.narutomod.keybind;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.NarutomodMod;
import net.narutomod.goatee.network.AbstractPacket;
import net.narutomod.goatee.network.PacketHandler;
import net.narutomod.item.ItemDojutsu;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class JutsuKeys {
	public static List<Key> jutsuKeys = new ArrayList<>();
	private static Key key4 = new Key(4, "key.mcreator.specialjutsu4", Keyboard.KEY_NONE, "key.mcreator.category");
	private static Key key5 = new Key(5, "key.mcreator.specialjutsu5", Keyboard.KEY_NONE, "key.mcreator.category");
	private static Key key6 = new Key(6, "key.mcreator.specialjutsu6", Keyboard.KEY_NONE, "key.mcreator.category");

	@SideOnly(Side.CLIENT)
	public static class Key extends KeyBinding {
		public byte keyId;
		public boolean wasDown;

		public Key(int keyId, String description, int keyCode, String category) {
			super(description, keyCode, category);
			this.keyId = (byte) keyId;
			jutsuKeys.add(this);
			ClientRegistry.registerKeyBinding(this);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void tick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START || Minecraft.getMinecraft().currentScreen != null)
			return;

		for (Key key : jutsuKeys) {
			boolean isDown = key.isKeyDown();

			//Pressed = 0, Held = 1, Released = 2
			byte pressType = (byte) (!key.wasDown && isDown ? 0 : key.wasDown && !isDown ? 2 : 1);
			if (isDown || key.wasDown) {
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

			ItemStack helmet = player.inventory.armorInventory.get(3);
			if ((helmet.getItem() instanceof ItemDojutsu.Base)) {
				ItemDojutsu.Base eye = (ItemDojutsu.Base) helmet.getItem();
				
				if (keyId == 4)
					eye.onJutsuKey4(pressType, helmet, player);
				else if (keyId == 5)
					eye.onJutsuKey5(pressType, helmet, player);
				else if (keyId == 6)
					eye.onJutsuKey6(pressType, helmet, player);
			}
		}
	}
}
