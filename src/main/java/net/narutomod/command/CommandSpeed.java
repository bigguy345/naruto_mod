package net.narutomod.command;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class CommandSpeed extends ElementsNarutomodMod.ModElement {
    public CommandSpeed(ElementsNarutomodMod instance) {
        super(instance, 908);
    }

    @Override
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandHandler());
    }

    public static class CommandHandler extends CommandBase implements ICommand {
        @Override
        public int compareTo(ICommand c) {
            return getName().compareTo(c.getName());
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender var1) {
            return true;
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 0; // Allow all players
        }

        @Override
        public List getAliases() {
            return new ArrayList();
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
            return new ArrayList();
        }

        @Override
        public boolean isUsernameIndex(String[] string, int index) {
            return true;
        }

        @Override
        public String getName() {
            return "narutospeed";
        }

        @Override
        public String getUsage(ICommandSender var1) {
            return "/narutospeed <value> ";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length == 0)
                throw new WrongUsageException(getUsage(sender));

            EntityPlayerMP player = getCommandSenderAsPlayer(sender);

            if (player.capabilities.isFlying) {
                float speed = (float) parseDouble(args[0]);

                ItemStack helmet = ItemDojutsu.getWorn(player);

                float maxSpeed = 1;
                if (player.isCreative() || ItemRinnegan.isRinnesharinganActivated(helmet))
                    maxSpeed = 10;
                else if (!ItemTenseigan.getHeldChakraCloak(player).isEmpty())
                    maxSpeed = 5;

                if (speed > maxSpeed)
                    throw new CommandException("Max allowed speed is " + maxSpeed);

                if (speed == 1)
                    speed = 0.99f;

                speed = MathHelper.clamp(speed, 0, maxSpeed);
                try {
                    Field field = ReflectionHelper.findField(PlayerCapabilities.class, "flySpeed", "field_75096_f");
                    field.setAccessible(true);
                    field.setFloat(player.capabilities, 0.05f * speed);
                } catch (Exception e) {
                }
            } else
                throw new CommandException("Not flying!" + "");
        }
    }
}
