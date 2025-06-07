package net.narutomod.command;

import net.minecraft.command.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.entity.EntityHiraishin;
import net.narutomod.procedure.ProcedureUtils;

import java.util.ArrayList;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class CommandHiraishinName extends ElementsNarutomodMod.ModElement {
    public CommandHiraishinName(ElementsNarutomodMod instance) {
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
            return new ArrayList();
        }

        @Override
        public boolean isUsernameIndex(String[] string, int index) {
            return true;
        }

        @Override
        public String getName() {
            return "hiraishinname";
        }

        @Override
        public String getUsage(ICommandSender var1) {
            return "/hiraishinname <name> | set's the name of the Flying Raijin marker player is looking at";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length == 0)
                throw new WrongUsageException(getUsage(sender));

            EntityPlayerMP player = getCommandSenderAsPlayer(sender);
            String name = String.join(" ", args);

            RayTraceResult res = ProcedureUtils.objectEntityLookingAt(player, 4d, true);
            if (res.entityHit instanceof EntityHiraishin.EC) {
                setMarkerName((EntityHiraishin.EC) res.entityHit, player, name);
            } else if (res.entityHit instanceof EntityLivingBase) {
                EntityHiraishin.EC marker = (EntityHiraishin.EC) res.entityHit.world.findNearestEntityWithinAABB(EntityHiraishin.EC.class, res.entityHit.getEntityBoundingBox(), res.entityHit);
                if (marker != null)
                    setMarkerName(marker, player, name);
            }
        }

        public void setMarkerName(EntityHiraishin.EC marker, EntityPlayer player, String name) throws CommandException {
            if (marker.getOwner().equals(player)) {
                marker.setMarkerName(name);
                EntityHiraishin.updateServerMarkerMap(marker.getOwnerUuid(), marker.getUniqueID(), marker.getMarkerData());
            } else
                throw new CommandException("Not your mark!");
        }
    }
}
