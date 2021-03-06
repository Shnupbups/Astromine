package com.github.chainmailstudios.astromine.common.utilities;

import net.minecraft.network.PacketByteBuf;

import com.github.chainmailstudios.astromine.AstromineCommon;
import io.netty.buffer.ByteBuf;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class IntegerUtilities {
    /** Serializes the given integer to a {@link ByteBuf}. */
    public static void toPacket(PacketByteBuf buffer, int number) {
        buffer.writeInt(number);
    }

    /** Deserializes an integer from a {@link ByteBuf}. */
    public static int fromPacket(PacketByteBuf buffer) {
        return buffer.readInt();
    }

    /** Serializes the given integer to a {@link JsonElement}. */
    public static JsonElement toJson(int number) {
        return new JsonPrimitive(number);
    }

    /** Deserializes an integer from a {@link JsonElement}. */
    public static int fromJson(JsonElement json) {
        return AstromineCommon.GSON.fromJson(json, Integer.class);
    }
}