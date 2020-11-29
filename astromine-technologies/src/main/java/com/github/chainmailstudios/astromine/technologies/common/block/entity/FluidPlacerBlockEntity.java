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

import com.github.chainmailstudios.astromine.common.component.general.SimpleDirectionalFluidComponent;
import com.github.chainmailstudios.astromine.common.block.entity.base.ComponentEnergyFluidBlockEntity;
import com.github.chainmailstudios.astromine.common.component.general.base.EnergyComponent;
import com.github.chainmailstudios.astromine.common.component.general.base.FluidComponent;
import com.github.chainmailstudios.astromine.common.component.general.SimpleEnergyComponent;
import com.github.chainmailstudios.astromine.common.component.general.SimpleFluidComponent;
import com.github.chainmailstudios.astromine.common.volume.energy.EnergyVolume;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.EnergyConsumedProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.EnergySizeProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.SpeedProvider;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FluidPlacerBlockEntity extends ComponentEnergyFluidBlockEntity implements EnergySizeProvider, SpeedProvider, EnergyConsumedProvider {
	private Fraction cooldown = Fraction.EMPTY;

	public FluidPlacerBlockEntity() {
		super(AstromineTechnologiesBlockEntityTypes.FLUID_INSERTER);
	}

	@Override
	public FluidComponent createFluidComponent() {
		FluidComponent fluidComponent = SimpleDirectionalFluidComponent.of(this, 1);
		fluidComponent.getFirst().setSize(Fraction.of(8));
		return fluidComponent;
	}

	@Override
	public EnergyComponent createEnergyComponent() {
		return SimpleEnergyComponent.of(getEnergySize());
	}

	@Override
	public double getEnergyConsumed() {
		return AstromineConfig.get().fluidPlacerEnergyConsumed;
	}

	@Override
	public double getEnergySize() {
		return AstromineConfig.get().fluidPlacerEnergy;
	}

	@Override
	public double getMachineSpeed() {
		return AstromineConfig.get().fluidPlacerSpeed;
	}

	@Override
	public void tick() {
		super.tick();

		if (level == null || level.isClientSide || !tickRedstone())
			return;

		FluidComponent fluidComponent = getFluidComponent();

		EnergyComponent energyComponent = getEnergyComponent();

		if (fluidComponent != null && energyComponent != null) {
			EnergyVolume energyVolume = energyComponent.getVolume();

			if (energyVolume.getAmount() < getEnergyConsumed()) {
				cooldown = Fraction.EMPTY;

				tickInactive();
			} else {
				tickActive();

				cooldown = cooldown.add(Fraction.ofDecimal(1.0D / getMachineSpeed()));

				if (cooldown.biggerOrEqualThan(Fraction.of(1))) {
					cooldown = Fraction.EMPTY;

					FluidVolume fluidVolume = fluidComponent.getFirst();

					Direction direction = getBlockState().getValue(HorizontalDirectionalBlock.FACING);

					BlockPos targetPos = worldPosition.relative(direction);

					BlockState targetState = level.getBlockState(targetPos);

					if (targetState.isAir()) {
						if (fluidVolume.hasStored(Fraction.BUCKET)) {
							FluidVolume toInsert = FluidVolume.of(Fraction.BUCKET, fluidVolume.getFluid());

							fluidVolume.take(Fraction.BUCKET);

							energyVolume.take(getEnergyConsumed());

							level.setBlockAndUpdate(targetPos, toInsert.getFluid().defaultFluidState().createLegacyBlock());
							level.playSound(null, worldPosition, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1, 1);
						}
					}
				}
			}
		}
	}
}
