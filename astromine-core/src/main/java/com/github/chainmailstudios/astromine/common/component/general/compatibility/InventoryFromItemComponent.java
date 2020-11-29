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

package com.github.chainmailstudios.astromine.common.component.general.compatibility;

import com.github.chainmailstudios.astromine.common.component.general.base.ItemComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static java.lang.Integer.min;

/**
 * An {@link Container} wrapped over an {@link ItemComponent}.
 */
public interface InventoryFromItemComponent extends Container {
	ItemComponent getItemComponent();
	
	/** Instantiates an {@link InventoryFromItemComponent}. */
	static InventoryFromItemComponent of(ItemComponent itemComponent) {
		return () -> itemComponent;
	}

	/** Returns this inventory's size. */
	@Override
	default int getContainerSize() {
		return getItemComponent().getSize();
	}

	/** Returns the {@link ItemStack} at the given slot. */
	@Override
	default ItemStack getItem(int slot) {
		return getItemComponent().getStack(slot);
	}

	/** Sets the {@link ItemStack} at the given slot to the specified value. */
	@Override
	default void setItem(int slot, ItemStack stack) {
		getItemComponent().setStack(slot, stack);
	}

	/** Removes the {@link ItemStack} at the given slot,
	 * or a part of it as per the specified count, and returns it. */
	@Override
	default ItemStack removeItem(int slot, int count) {
		ItemStack removed = getItemComponent().removeStack(slot);

		ItemStack returned = removed.copy();

		returned.setCount(min(count, removed.getCount()));

		removed.shrink(count);

		getItemComponent().setStack(slot, removed);

		return returned;
	}

	/** Removes the {@link ItemStack} at the given slot, and returns it. */
	@Override
	default ItemStack removeItemNoUpdate(int slot) {
		return getItemComponent().removeStack(slot);
	}

	/** Asserts whether this inventory's contents are all empty or not. */
	@Override
	default boolean isEmpty() {
		return getItemComponent().isEmpty();
	}

	/** Clears this inventory's contents. */
	@Override
	default void clearContent() {
		getItemComponent().clear();
	}

	/** Marks this inventory as dirt, or, pending update. */
	@Override
	default void setChanged() {
		getItemComponent().updateListeners();
	}

	/** Allow the player to use this inventory by default. */
	@Override
	default boolean stillValid(Player player) {
		return true;
	}
}
