package net.narutomod.command;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.goatee.jutsu.KekkeiGenkai;
import net.narutomod.goatee.util.AdvancementUtil;
import net.narutomod.procedure.ProcedureKGDistribution;

import java.util.*;

@ElementsNarutomodMod.ModElement.Tag
public class CommandKekkeiGenkai extends ElementsNarutomodMod.ModElement {
    public CommandKekkeiGenkai(ElementsNarutomodMod instance) {
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
            return var1.canUseCommand(4, this.getName());
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 4;
        }

        @Override
        public List getAliases() {
            return new ArrayList();
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
            if (args.length == 1)
                return CommandBase.getListOfStringsMatchingLastWord(args, Arrays.asList("grant", "revoke", "random"));
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("random"))
                    return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
                else
                    return CommandBase.getListOfStringsMatchingLastWord(args, Arrays.asList(KekkeiGenkai.values()));
            } else if (args.length == 3)
                return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());

            return new ArrayList();
        }

        @Override
        public boolean isUsernameIndex(String[] string, int index) {
            return true;
        }

        @Override
        public String getName() {
            return "kekkeigenkai";
        }

        @Override
        public String getUsage(ICommandSender var1) {
            return "/kekkeigenkai <grant|revoke> <type> <player>";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length == 0)
                throw new WrongUsageException(getUsage(sender));

            String action = args[0];
            if (!Arrays.asList("grant", "revoke", "random").contains(action.toLowerCase()))
                throw new WrongUsageException(getUsage(sender));

            EntityPlayerMP player = getCommandSenderAsPlayer(sender);
            if (action.equalsIgnoreCase("random")) {
                player = args.length > 1 ? getPlayer(server, sender, args[1]) : player;

                KekkeiGenkai random = KekkeiGenkai.getRandom();
                if (AdvancementUtil.has(player, random.achievement))
                    throw new CommandException("command.kekkei_genkai.already_has", player.getDisplayName().getFormattedText(), random.toString());
                else
                    random.give(player);
            } else {
                KekkeiGenkai kg = null;
                try {
                    kg = KekkeiGenkai.valueOf(args[1]);
                } catch (IllegalArgumentException e) {
                    throw new WrongUsageException("Invalid type!");
                }

                player = args.length > 2 ? getPlayer(server, sender, args[2]) : player;
                if (action.equalsIgnoreCase("grant")) {
                    if (AdvancementUtil.has(player, kg.achievement))
                        throw new CommandException("command.kekkei_genkai.already_has", player.getDisplayName().getFormattedText(), kg.toString());
                    else
                        kg.give(player);
                } else if (action.equalsIgnoreCase("revoke")) {
                    if (!AdvancementUtil.has(player, kg.achievement))
                        throw new CommandException("command.kekkei_genkai.doesn't_have", player.getDisplayName().getFormattedText(), kg.toString());
                    else {
                        kg.remove(player);

                        TextComponentTranslation message = new TextComponentTranslation("command.kekkei_genkai.removed", kg.toString(), player.getDisplayName().getFormattedText());
                        message.getStyle().setColor(TextFormatting.RED);
                        sender.sendMessage(message);
                    }
                }
            }
        }
    }
}
