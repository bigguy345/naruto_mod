package net.narutomod.command;

import akka.io.SelectionHandlerSettings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.entity.EntitySusanooWinged;
import net.narutomod.goatee.data.NarutoData;
import net.narutomod.goatee.data.SusanooData;
import net.narutomod.goatee.network.packets.NarutoSyncData;
import net.narutomod.goatee.util.AdvancementUtil;
import net.narutomod.item.*;
import net.narutomod.procedure.ProcedureUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class CommandSusanooToggle extends ElementsNarutomodMod.ModElement {
    public CommandSusanooToggle(ElementsNarutomodMod instance) {
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
            if (args.length == 1) {
                return CommandBase.getListOfStringsMatchingLastWord(args, Level1.getAllCommands());
            } else if (args[0].equalsIgnoreCase(Level1.SIZE.toString())) {
                if (args.length == 2)
                    return CommandBase.getListOfStringsMatchingLastWord(args, "5");
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
            return "susanootoggle";
        }

        @Override
        public String getUsage(ICommandSender var1) {
            return "/susanootoggle <" + Level1.getAllCommandsFormatted() + "> <player>";
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
            boolean sync = false;
            if (type == Level1.SIZE) {
                String error = "/susanootoggle size <value> player";
                if (args.length == 1)
                    throw new WrongUsageException(error);

                float size = (float) parseDouble(args[1]);

                player = args.length > 2 ? getPlayer(server, sender, args[2]) : player;
                SusanooData.Entry riddenSusanoo = getSusanooData(player);

                if (riddenSusanoo != null) {
                    riddenSusanoo.size = size;
                    sync = true;

                    EntitySusanooBase mounted = getMountedSusanoo(player);
                    if (mounted != null) {
                        mounted.setSize(size >= 0 ? size : mounted.getDefaultSize());
                    }
                } else
                    throw new WrongUsageException("Player must be in Full Winged Susanoo to use this command!");
            } else if (type == Level1.OFFSET) {
                String error = "/susanootoggle offset <value> player";
                if (args.length == 1)
                    throw new WrongUsageException(error);

                float offset = (float) parseDouble(args[1]);

                player = args.length > 2 ? getPlayer(server, sender, args[2]) : player;
                SusanooData.Entry riddenSusanoo = getSusanooData(player);

                if (riddenSusanoo != null) {
                    riddenSusanoo.offset = offset;
                    sync = true;
                } else
                    throw new WrongUsageException("Player must be in Full Winged Susanoo to use this command!");
            } else if (type == Level1.PLAYER) {
                player = args.length > 1 ? getPlayer(server, sender, args[1]) : player;
                SusanooData.Entry riddenSusanoo = getSusanooData(player);

                if (riddenSusanoo != null) {
                    boolean bo = !riddenSusanoo.renderPlayer;
                    riddenSusanoo.renderPlayer = bo;
                    player.sendMessage(new TextComponentString("\u00a76Toggled " + (bo ? "\u00a7aOn" : "\u00a7cOff")));
                    sync = true;
                } else
                    throw new WrongUsageException("Player must be in Full Winged Susanoo to use this command!");
            } else if (type == Level1.MINI) {
                player = args.length > 1 ? getPlayer(server, sender, args[1]) : player;
                SusanooData.Entry riddenSusanoo = getSusanooData(player);

                if (riddenSusanoo != null) {
                    boolean bo = !riddenSusanoo.mini;
                    riddenSusanoo.mini = bo;
                    player.sendMessage(new TextComponentString("\u00a76Toggled " + (bo ? "\u00a7aOn" : "\u00a7cOff")));
                    sync = true;

                    riddenSusanoo.size = bo ? 1 : -1;
                    riddenSusanoo.offset = bo ? 3 : -1;
                    riddenSusanoo.renderPlayer = bo ? false : true;


                    EntitySusanooBase mounted = getMountedSusanoo(player);
                    if (mounted != null)
                        mounted.setSize(bo ? riddenSusanoo.size : mounted.getDefaultSize());
                } else
                    throw new WrongUsageException("Player must be in Full Winged Susanoo to use this command!");
            }

            if (sync)
                NarutoSyncData.syncTrackingClients(player);
        }

        private SusanooData.Entry getSusanooData(EntityPlayerMP p) {
            return NarutoData.getSusanooData(p);
        }

        private EntitySusanooBase getMountedSusanoo(EntityPlayerMP p) {
            if (p.getRidingEntity() instanceof EntitySusanooBase)
                return (EntitySusanooBase) p.getRidingEntity();

            return null;
        }

        public enum Level1 {
            SIZE("SIZE"),
            PLAYER("RENDER_PLAYER"),
            OFFSET("OFFSET"),
            MINI("MINI");

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
                return COMMANDS.get(str.toUpperCase());
            }

            public static boolean isValidType(String str) {
                return COMMANDS.containsKey(str.toUpperCase());
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
