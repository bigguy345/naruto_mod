package net.narutomod.goatee.client.gui;

import com.google.common.base.Preconditions;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemOnBody;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.client.gui.inventory.GuiInventory.drawEntityOnScreen;

public class GuiInventoryJutsu extends ElementsNarutomodMod.ModElement {
    public static int GUIID = 500;

    public GuiInventoryJutsu(ElementsNarutomodMod elements, int sortid) {
        super(elements, 1000);
    }

    public static class GuiContainerMod extends Container {
        private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

        public GuiContainerMod(World world, int x, int y, int z, EntityPlayer player) {
            setupSlots(player);
        }

        public void setupSlots(EntityPlayer player) {
            playerSlots(player);

            for (int k = 0; k < 4; ++k) {
                this.addSlotToContainer(new Slot(player.inventory, 11, 77, 8 + k * 18) {
                    public boolean isItemValid(ItemStack stack) {
                        return stack.getItem() instanceof ItemDojutsu.Base;
                    }
                });
            }

            for (int y = 0; y < 3; ++y) {
                for (int x = 0; x < 3; ++x) {
                    this.addSlotToContainer(new Slot(player.inventory, 12, 98 + x * 18, 18 + y * 18) {
                        public boolean isItemValid(ItemStack stack) {
                            return stack.getItem() instanceof ItemJutsu.Base;
                        }
                    });
                }
            }

            for (int k = 0; k < 4; ++k) {
                this.addSlotToContainer(new Slot(player.inventory, 13, 155, 8 + k * 18) {
                    public boolean isItemValid(ItemStack stack) {
                        return stack.getItem() instanceof ItemOnBody.Interface;
                    }
                });
            }
        }

        public void playerSlots(EntityPlayer player) {

            //  Add player main inventory (3 rows × 9 columns)
            for (int row = 0; row < 3; ++row)
                for (int col = 0; col < 9; ++col)
                    this.addSlotToContainer(new Slot(player.inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));


            // Add hotbar (1 row × 9 columns)
            for (int i = 0; i < 9; ++i)
                this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));


            for (int k = 0; k < 4; ++k) {
                final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
                this.addSlotToContainer(new Slot(player.inventory, 36 + (3 - k), 8, 8 + k * 18) {
                    public int getSlotStackLimit() {
                        return 1;
                    }

                    public boolean isItemValid(ItemStack stack) {
                        return stack.getItem().isValidArmor(stack, entityequipmentslot, player);
                    }

                    public boolean canTakeStack(EntityPlayer playerIn) {
                        ItemStack itemstack = this.getStack();
                        return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
                    }

                    @Nullable
                    @SideOnly(Side.CLIENT)
                    public String getSlotTexture() {
                        return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                    }
                });
            }
        }

        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
        }
    }

    public static class GuiWindow extends GuiContainer {

        private static final ResourceLocation JUTSU_INVENTORY_BACKGROUND = new ResourceLocation("narutomod:textures/gui/jutsu_inventory.png");

        private float oldMouseX;
        private float oldMouseY;

        public GuiWindow(World world, int x, int y, int z, EntityPlayer entity) {
            super(new GuiInventoryJutsu.GuiContainerMod(world, x, y, z, entity));
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.renderHoveredToolTip(mouseX, mouseY);

            this.oldMouseX = (float) mouseX;
            this.oldMouseY = (float) mouseY;
        }

        @Override
        protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

        }

        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(JUTSU_INVENTORY_BACKGROUND);
            int i = this.guiLeft;
            int j = this.guiTop;
            this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
            drawEntityOnScreen(i + 51, j + 75, 30, (float) (i + 51) - this.oldMouseX, (float) (j + 75 - 50) - this.oldMouseY, this.mc.player);
        }
    }
}
