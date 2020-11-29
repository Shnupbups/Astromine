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
import com.github.chainmailstudios.astromine.common.component.general.SimpleItemComponent;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;

/**
 * An {@link ItemComponent} wrapped over an {@link WorldlyContainer}.
 */
public class ItemComponentFromSidedInventory extends SimpleItemComponent {
	WorldlyContainer inventory;

	List<Runnable> listeners = new ArrayList<>();

	/** Instantiates an {@link ItemComponentFromSidedInventory}. */
	private ItemComponentFromSidedInventory(WorldlyContainer inventory) {
		super(inventory.getContainerSize());
		this.inventory = inventory;
	}

	/** Instantiates an {@link ItemComponentFromSidedInventory}. */
	public static ItemComponentFromSidedInventory of(WorldlyContainer inventory) {
		return new ItemComponentFromSidedInventory(inventory);
	}

	/** Returns this component's size. */
	@Override
	public int getSize() {
		return this.inventory.getContainerSize();
	}

	/** Returns this component's listeners. */
	@Override
	public List<Runnable> getListeners() {
		return this.listeners;
	}

	/** Returns this component's contents. */
	@Override
	public Map<Integer, ItemStack> getContents() {
		Int2ObjectOpenHashMap<ItemStack> contents = new Int2ObjectOpenHashMap<>();

		for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
			contents.put(i, this.inventory.getItem(i));
		}

		return contents;
	}

	/** Asserts whether the given stack can be inserted through the specified
	 * direction into the supplied slot. */
	@Override
	public boolean canInsert(@Nullable Direction direction, ItemStack stack, int slot) {
		return this.inventory.canPlaceItem(slot, stack) && Arrays.stream(this.inventory.getSlotsForFace(direction)).anyMatch(it -> it == slot);
	}

	/** Asserts whether the given stack can be extracted from the specified
	 * direction from the supplied slot. */
	@Override
	public boolean canExtract(@Nullable Direction direction, ItemStack stack, int slot) {
		return super.canExtract(direction, stack, slot) && Arrays.stream(this.inventory.getSlotsForFace(direction)).anyMatch(it -> it == slot);
	}

	/* Returns the {@link ItemStack} at the given slot. */
	@Override
	public ItemStack getStack(int slot) {
		return this.inventory.getItem(slot);
	}

	/** Sets the {@link ItemStack} at the given slot to the specified value. */
	@Override
	public void setStack(int slot, ItemStack stack) {
		this.inventory.setItem(slot, stack);
	}
}
