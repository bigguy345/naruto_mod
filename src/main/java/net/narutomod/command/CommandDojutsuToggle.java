package net.narutomod.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.goatee.util.AdvancementUtil;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemTenseigan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class CommandDojutsuToggle extends ElementsNarutomodMod.ModElement {
    public CommandDojutsuToggle(ElementsNarutomodMod instance) {
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
            return var1.canUseCommand(0, this.getName());
        }

        @Override
        public List getAliases() {
            return new ArrayList();
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
            if (args.length == 1) {
                return Level1.getAllCommands();
            } else if (args.length == 2) {
                return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            }
            return new ArrayList();
        }

        @Override
        public boolean isUsernameIndex(String[] string, int index) {
            return true;
        }

        @Override
        public String getName() {
            return "dojutsutoggle";
        }

        @Override
        public String getUsage(ICommandSender var1) {
            return "/dojutsutoggle <" + Level1.getAllCommandsFormatted() + ">";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

            if (args.length == 0)
                throw new WrongUsageException(getUsage(sender));
            EntityPlayer player = args.length > 1 ? getPlayer(server, sender, args[1]) : getCommandSenderAsPlayer(sender);

            if (args[0].equals(Level1.DODGE.toString())) {
                ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                if (helmet.getItem() instanceof ItemSharingan.Base)
                    ItemSharingan.setDodgeEnabled(helmet, !ItemSharingan.isDodgeEnabled(helmet));
            } else if (args[0].equals(Level1.RINNESHARINGAN.toString())) {
                ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                if (ItemRinnegan.isRinnesharinganActivated(helmet))
                    ItemRinnegan.setRinneSharinganActivated(helmet, false);
                else if (ItemRinnegan.isRinnegan(helmet) && AdvancementUtil.has((EntityPlayerMP) player, "narutomod:rinnesharinganactivated") || ItemTenseigan.isTenseigan(helmet) && AdvancementUtil.has((EntityPlayerMP) player, "narutomod:tensei_byakugan_activated"))
                    ItemRinnegan.setRinneSharinganActivated(helmet, true);
            }
        }

        public enum Level1 {
            DODGE("dodge"), RINNESHARINGAN("rinnesharingan");

            private final String argString;
            private static final Map<String, Level1> COMMANDS = Maps.newHashMap();

            static {
                for (Level1 cmd : values()) {
                    if (cmd.argString != null) {
                        COMMANDS.put(cmd.argString, cmd);
                    }
                }
            }

            Level1() {
                this.argString = null;
            }

            Level1(String str) {
                this.argString = str;
            }

            public String toString() {
                return this.argString;
            }

            public static Level1 getTypeFromString(String str) {
                return COMMANDS.get(str);
            }

            public static List<String> getAllCommands() {
                List<String> list = Lists.<String>newArrayList();
                list.addAll(COMMANDS.keySet());
                return list;
            }

            public static String getAllCommandsFormatted() {
                return String.join(" | ", COMMANDS.keySet());
            }
        }
    }
}
