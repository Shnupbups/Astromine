/*
 * MIT License
 *
 * Copyright (c) 2020 Chainmail Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.chainmailstudios.astromine.common.utilities;

import net.minecraft.util.Tuple;;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import io.netty.buffer.ByteBuf;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class StackUtilities {
	/** Attempts to merge two {@link ItemStack}s, returning a {@link Tuple}
	 * with the results.
	 *
	 * The amount transferred is the {@link Math#min(int, int)} between
	 * their available space, our amount, and the specified amount.
	 * */
	public static Tuple<ItemStack, ItemStack> merge(ItemStack source, ItemStack target) {
		int targetMax = target.getMaxStackSize();

		if (ItemStack.isSameIgnoreDurability(source, target) && ItemStack.tagMatches(source, target)) {
			int sourceCount = source.getCount();
			int targetCount = target.getCount();

			int targetAvailable = Math.max(0, targetMax - targetCount);

			target.grow(Math.min(sourceCount, targetAvailable));
			source.setCount(Math.max(sourceCount - targetAvailable, 0));
		} else {
			if (target.isEmpty() && !source.isEmpty()) {
				int targetCount = target.getCount();
				int targetAvailable = targetMax - targetCount;

				int sourceCount = source.getCount();

				target = source.copy();
				target.setCount(Math.min(sourceCount, targetAvailable));
				source.shrink(Math.min(sourceCount, targetAvailable));
			}
		}

		return new Tuple<>(source, target);
	}

	/** Asserts whether the source {@link ItemStack} matches the second,
	 * and whether the target can fit the source, or not.
	 */
	public static boolean test(ItemStack source, ItemStack target) {
		return target.isEmpty() || ItemStack.isSameIgnoreDurability(source, target) && ItemStack.tagMatches(source, target) && target.getMaxStackSize() - target.getCount() >= source.getCount();
	}

	/** Weakly merges an {@link ItemStack} into another, returning the resulting target. */
	public static ItemStack into(ItemStack source, ItemStack target) {
		if (target.isEmpty()) {
			return source.copy();
		} else {
			target = target.copy();
			target.grow(source.getCount());
			return target;
		}
	}

	/** Asserts equality of {@link ItemStack} {@link CompoundTag} and {@link Item}. */
	public static boolean areItemsAndTagsEqual(ItemStack left, ItemStack right) {
		return ItemStack.isSameIgnoreDurability(left, right) && ItemStack.tagMatches(left, right);
	}

	/** Deserializes an {@link ItemStack} from a {@link JsonElement}. */
	public static ItemStack fromJson(JsonElement jsonElement) {
		if (!jsonElement.isJsonObject()) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();

				if (primitive.isString()) {
					return new ItemStack(Registry.ITEM.get(new ResourceLocation(primitive.getAsString())));
				} else {
					return ItemStack.EMPTY;
				}
			} else {
				return ItemStack.EMPTY;
			}
		} else {
			return ShapedRecipe.itemFromJson(jsonElement.getAsJsonObject());
		}
	}

	/** Serializes the given {@link ItemStack} to a {@link JsonElement}. */
	public static JsonElement toJson(ItemStack stack) {
		JsonObject object = new JsonObject();
		object.addProperty("item", Registry.ITEM.getKey(stack.getItem()).toString());
		object.addProperty("count", stack.getCount());
		return object;
	}

	/** Deserializes an {@link ItemStack} from a {@link ByteBuf}. */
	public static ItemStack fromPacket(FriendlyByteBuf buffer) {
		return buffer.readItem();
	}

	/** Serializes the given {@link ItemStack} to a {@link ByteBuf}. */
	public static void toPacket(FriendlyByteBuf buffer, ItemStack stack) {
		buffer.writeItem(stack);
	}
}
