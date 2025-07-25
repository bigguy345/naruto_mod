package net.narutomod.goatee.data;

import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemDojutsu;

import static net.narutomod.item.ItemDojutsu.is;

public class DojutsuData {
    public final ItemDojutsu.Base eye;
 //   public final List<JutsuKey> jutsus = new ArrayList<>();
  //  public final JutsuKey switchJutsu = new JutsuKey(-1);

    public DojutsuData(ItemDojutsu.Base eye) {
        this.eye = eye;

       // for (int i = 0; i < 6; i++)
         //   jutsus.add(new JutsuKey(i));
    }

//    public JutsuKey getKey(int key) {
//        return jutsus.get(clamp(key - 1, 0, jutsus.size()));
//    }
//
//    public JutsuKey getSwitchJutsuKey() {
//        return switchJutsu;
//    }

    public SideData left(ItemStack stack) {
        return getLeft(stack);
    }

    public SideData right(ItemStack stack) {
        return getRight(stack);
    }

    public static SideData getLeft(ItemStack stack) {
        if (!is(stack))
            return null;

        ItemDojutsu.Base eye = (ItemDojutsu.Base) stack.getItem();
        return new SideData(SideData.Side.LEFT, stack, eye);
    }

    public static SideData getRight(ItemStack stack) {
        if (!is(stack))
            return null;

        ItemDojutsu.Base eye = (ItemDojutsu.Base) stack.getItem();
        return new SideData(SideData.Side.RIGHT, stack, eye);
    }
}
