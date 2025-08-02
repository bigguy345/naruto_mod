package net.narutomod.goatee.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NbtUtils {

    public static Map<Integer, Integer> deserializeIntIntMap(NBTTagCompound tag, String key) {
        Map<Integer, Integer> map = new HashMap<>();
        if (!tag.hasKey(key, 10)) return map;

        NBTTagCompound mapTag = tag.getCompoundTag(key);
        for (String mapKey : mapTag.getKeySet()) {
            try {
                int intKey = Integer.parseInt(mapKey);
                int value = mapTag.getInteger(mapKey);
                map.put(intKey, value);
            } catch (NumberFormatException ignored) {
                // skip bad keys
            }
        }
        return map;
    }
    public static Map<Integer, String> deserializeIntStringMap(NBTTagCompound tag, String key) {
        Map<Integer, String> map = new HashMap<>();
        if (!tag.hasKey(key, 10)) return map;

        NBTTagCompound mapTag = tag.getCompoundTag(key);
        for (String mapKey : mapTag.getKeySet()) {
            try {
                int intKey = Integer.parseInt(mapKey);
                String value = mapTag.getString(mapKey);
                map.put(intKey, value);
            } catch (NumberFormatException ignored) {
                // skip bad keys
            }
        }
        return map;
    }

    public static void writeValue(NBTTagCompound tag, String key, Object value) {

        if (value instanceof String) {
            tag.setString(key, (String) value);
        } else if (value instanceof Integer) {
            tag.setInteger(key, (Integer) value);
        } else if (value instanceof Boolean) {
            tag.setBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            tag.setFloat(key, (Float) value);
        } else if (value instanceof Double) {
            tag.setDouble(key, (Double) value);
        } else if (value instanceof Long) {
            tag.setLong(key, (Long) value);
        } else if (value instanceof Short) {
            tag.setShort(key, (Short) value);
        } else if (value instanceof Byte) {
            tag.setByte(key, (Byte) value);
        } else if (value instanceof byte[]) {
            tag.setByteArray(key, (byte[]) value);
        } else if (value instanceof int[]) {
            tag.setIntArray(key, (int[]) value);
        } else if (value instanceof UUID) {
            UUID uuid = (UUID) value;
            tag.setLong(key + "Most", uuid.getMostSignificantBits());
            tag.setLong(key + "Least", uuid.getLeastSignificantBits());
        } else if (value instanceof NBTTagCompound) {
            tag.setTag(key, (NBTTagCompound) value);
        } else if (value instanceof NBTTagList) {
            tag.setTag(key, (NBTTagList) value);
        } else {
            throw new IllegalArgumentException("Unsupported value type for NBT: " + value.getClass());
        }
    }
}
