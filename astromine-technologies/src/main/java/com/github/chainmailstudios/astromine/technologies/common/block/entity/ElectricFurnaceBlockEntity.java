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

package com.github.chainmailstudios.astromine.technologies.common.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;

import com.github.chainmailstudios.astromine.common.block.entity.base.ComponentEnergyItemBlockEntity;
import com.github.chainmailstudios.astromine.common.component.inventory.EnergyComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.ItemComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleEnergyComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleItemComponent;
import com.github.chainmailstudios.astromine.common.inventory.BaseInventory;
import com.github.chainmailstudios.astromine.common.utilities.tier.MachineTier;
import com.github.chainmailstudios.astromine.common.volume.energy.EnergyVolume;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.EnergySizeProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.SpeedProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.TierProvider;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlockEntityTypes;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class ElectricFurnaceBlockEntity extends ComponentEnergyItemBlockEntity implements EnergySizeProvider, TierProvider, SpeedProvider {
	public double progress = 0;
	public int limit = 100;
	public boolean shouldTry = true;

	private Optional<SmeltingRecipe> optionalRecipe = Optional.empty();

	public ElectricFurnaceBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public ItemComponent createItemComponent() {
		return SimpleItemComponent.of(2).withInsertPredicate((direction, stack, slot) -> {
			if (slot != 1) {
				return false;
			}

			BaseInventory inputInventory = BaseInventory.of(stack);

			if (world != null) {
				Optional<SmeltingRecipe> recipe = (Optional<SmeltingRecipe>) world.getRecipeManager().getFirstMatch((RecipeType) RecipeType.SMELTING, inputInventory, world);
				return recipe.isPresent();
			}

			return false;
		}).withExtractPredicate(((direction, stack, slot) -> {
			return slot == 0;
		})).withListener((inventory) -> {
			shouldTry = true;
			optionalRecipe = Optional.empty();
		});
	}

	@Override
	public EnergyComponent createEnergyComponent() {
		return SimpleEnergyComponent.of(getEnergySize());
	}

	@Override
	public IntSet getItemInputSlots() {
		return IntSets.singleton(1);
	}

	@Override
	public IntSet getItemOutputSlots() {
		return IntSets.singleton(0);
	}

	@Override
	public void tick() {
		super.tick();

		if (world == null || world.isClient || !tickRedstone())
			return;

		ItemComponent itemComponent = getItemComponent();

		EnergyComponent energyComponent = getEnergyComponent();

		if (itemComponent != null && energyComponent != null) {
			EnergyVolume energyVolume = energyComponent.getVolume();

			BaseInventory inputInventory = BaseInventory.of(itemComponent.getSecond());

			if (!optionalRecipe.isPresent() && shouldTry) {
				optionalRecipe = (Optional<SmeltingRecipe>) world.getRecipeManager().getFirstMatch((RecipeType) RecipeType.SMELTING, inputInventory, world);
				shouldTry = false;

				if (!optionalRecipe.isPresent()) {
					progress = 0;
					limit = 100;
				}
			}

			if (optionalRecipe.isPresent()) {
				SmeltingRecipe recipe = optionalRecipe.get();

				if (recipe.matches(inputInventory, world)) {
					limit = recipe.getCookTime();

					double speed = Math.min(getMachineSpeed() * 2, limit - progress);

					ItemStack output = recipe.getOutput().copy();

					boolean isEmpty = itemComponent.getFirst().isEmpty();
					boolean isEqual = ItemStack.areItemsEqual(itemComponent.getFirst(), output) && ItemStack.areTagsEqual(itemComponent.getFirst(), output);

					if ((isEmpty || isEqual) && itemComponent.getFirst().getCount() + output.getCount() <= itemComponent.getFirst().getMaxCount() && energyVolume.hasStored(500.0D / limit * speed)) {
						energyVolume.take(500.0D / limit * speed);

						if (progress + speed >= limit) {
							optionalRecipe = Optional.empty();

							itemComponent.getSecond().decrement(1);

							if (isEmpty) {
								itemComponent.setFirst(output);
							} else {
								itemComponent.getFirst().increment(output.getCount());

								shouldTry = true; // Vanilla is garbage; if we don't do it here, it only triggers the listener on #setStack.
							}

							progress = 0;
						} else {
							progress += speed;
						}

						tickActive();
					} else {
						tickInactive();
					}
				} else {
					tickInactive();
				}
			} else {
				tickInactive();
			}
		}
	}

	@Override
	public void fromTag(BlockState state, @NotNull CompoundTag tag) {
		super.fromTag(state, tag);
		progress = tag.getDouble("progress");
		limit = tag.getInt("limit");
		shouldTry = true;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putDouble("progress", progress);
		tag.putInt("limit", limit);
		return super.toTag(tag);
	}

	public static class Primitive extends ElectricFurnaceBlockEntity {
		public Primitive() {
			super(AstromineTechnologiesBlockEntityTypes.PRIMITIVE_ELECTRIC_FURNACE);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().primitiveElectricFurnaceSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().primitiveElectricFurnaceEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.PRIMITIVE;
		}
	}

	public static class Basic extends ElectricFurnaceBlockEntity {
		public Basic() {
			super(AstromineTechnologiesBlockEntityTypes.BASIC_ELECTRIC_FURNACE);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().basicElectricFurnaceSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().basicElectricFurnaceEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.BASIC;
		}
	}

	public static class Advanced extends ElectricFurnaceBlockEntity {
		public Advanced() {
			super(AstromineTechnologiesBlockEntityTypes.ADVANCED_ELECTRIC_FURNACE);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().advancedElectricFurnaceSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().advancedElectricFurnaceEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.ADVANCED;
		}
	}

	public static class Elite extends ElectricFurnaceBlockEntity {
		public Elite() {
			super(AstromineTechnologiesBlockEntityTypes.ELITE_ELECTRIC_FURNACE);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().eliteElectricFurnaceSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().eliteElectricFurnaceEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.ELITE;
		}
	}
}
