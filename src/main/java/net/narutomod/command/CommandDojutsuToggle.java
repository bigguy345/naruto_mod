package net.narutomod.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.goatee.util.AdvancementUtil;
import net.narutomod.item.*;

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
        public int getRequiredPermissionLevel() {
            return 0; // Allow all players
        }

        @Override
        public List getAliases() {
            return new ArrayList();
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
            if (args.length == 1) {
                return CommandBase.getListOfStringsMatchingLastWord(args, Level1.getAllCommands());
            } else if (args[0].equalsIgnoreCase(Level1.RINNEGANTOMOE.toString())) {
                if (args.length == 2)
                    return CommandBase.getListOfStringsMatchingLastWord(args, "OFF", "ON", "ETERNAL");
                else if (args.length == 3)
                    return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
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

            if (!Level1.isValidType(args[0]))
                throw new WrongUsageException(getUsage(sender));

            Level1 type = Level1.getTypeFromString(args[0]);
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);
            
            boolean isOp = server.getPlayerList().canSendCommands(player.getGameProfile());

            if (type == Level1.DODGE) {
                player = args.length > 1 ? getPlayer(server, sender, args[1]) : player;
                ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                ItemStack sharingan = player.getHeldItemMainhand().getItem() instanceof ItemSharingan.Base ? player.getHeldItemMainhand() : helmet.getItem() instanceof ItemSharingan.Base ? helmet : ItemStack.EMPTY;
                if (sharingan.isEmpty())
                    return;
                
                ItemSharingan.setDodgeEnabled(sharingan, !ItemSharingan.isDodgeEnabled(sharingan));
            } else if (type == Level1.RINNESHARINGAN) {
                player = args.length > 1 ? getPlayer(server, sender, args[1]) : player;
                ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                ItemStack rinnegan = ItemRinnegan.isRinnegan(player.getHeldItemMainhand()) ? player.getHeldItemMainhand() : ItemRinnegan.isRinnegan(helmet) ? helmet : ItemStack.EMPTY;
                if (rinnegan.isEmpty())
                    return;

                if (ItemRinnegan.isRinnesharinganActivated(rinnegan))
                    ItemRinnegan.setRinneSharinganActivated(rinnegan, false);
                else if (ItemRinnegan.isRinnegan(rinnegan) && (isOp || AdvancementUtil.has(player, "narutomod:rinnesharinganactivated")) || ItemTenseigan.isTenseigan(rinnegan) && (isOp || AdvancementUtil.has(player, "narutomod:tensei_byakugan_activated")))
                    ItemRinnegan.setRinneSharinganActivated(rinnegan, true);
            } else if (type == Level1.RINNEGANTOMOE) {
                String error = "/dojutsutoggle rinnegantomoe <on | off | eternal> player";
                if (args.length == 1)
                    throw new WrongUsageException(error);

                player = args.length > 2 ? getPlayer(server, sender, args[2]) : player;
                ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                ItemStack tomoe = ItemRinneganTomoe.isTomoe(player.getHeldItemMainhand()) ? player.getHeldItemMainhand() : ItemRinneganTomoe.isTomoe(helmet) ? helmet : ItemStack.EMPTY;
                if (tomoe.isEmpty())
                    return;

                String s = args[1];
                int status = s.equalsIgnoreCase("off") ? 0 : s.equalsIgnoreCase("on") ? 1 : s.equalsIgnoreCase("eternal") ? 2 : -1;
                if (status == -1)
                    throw new CommandException("Invalid argument: " + s);

                ItemRinneganTomoe.setTomoeStatus(tomoe, status, player);
            }
        }

        public enum Level1 {
            DODGE("dodge"), RINNESHARINGAN("rinnesharingan"), RINNEGANTOMOE("rinnegantomoe");

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

            public static Level1 getTypeFromString(String str) throws WrongUsageException {
                return COMMANDS.get(str.toLowerCase());
            }

            public static boolean isValidType(String str) {
                return COMMANDS.containsKey(str);
            }
            
            public static List<String> getAllCommands() {
                List<String> list = Lists.newArrayList();
                list.addAll(COMMANDS.keySet());
                return list;
            }

            public static String getAllCommandsFormatted() {
                return String.join(" | ", COMMANDS.keySet());
            }
        }
    }
}
