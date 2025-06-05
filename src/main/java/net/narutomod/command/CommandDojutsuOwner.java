package net.narutomod.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemJutsu;

import java.util.ArrayList;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class CommandDojutsuOwner extends ElementsNarutomodMod.ModElement {
    public CommandDojutsuOwner(ElementsNarutomodMod instance) {
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
        public List getAliases() {
            return new ArrayList();
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
            if (args.length == 1) 
                return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());

            return new ArrayList();
        }

        @Override
        public boolean isUsernameIndex(String[] string, int index) {
            return true;
        }

        @Override
        public String getName() {
            return "narutosetowner";
        }

        @Override
        public String getUsage(ICommandSender var1) {
            return "/narutosetowner player | sets held jutsu/dojutsu's owner to player";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);

            player = args.length > 0 ? getPlayer(server, sender, args[0]) : player;
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.isEmpty())
                return;

            if (stack.getItem() instanceof ItemDojutsu.Base)
                ((ItemDojutsu.Base) stack.getItem()).setOwner(stack, player);

            if (stack.getItem() instanceof ItemJutsu.Base)
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
        }
    }
}
