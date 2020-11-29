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

package com.github.chainmailstudios.astromine.common.block.entity.base;

import com.github.chainmailstudios.astromine.common.component.general.provider.EnergyComponentProvider;
import com.github.chainmailstudios.astromine.common.component.general.base.EnergyComponent;
import com.github.chainmailstudios.astromine.common.utilities.capability.energy.ComponentEnergyProvider;
import com.github.chainmailstudios.astromine.registry.AstromineComponents;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * A {@link ComponentBlockEntity} with an attached
 * {@link EnergyComponent}.
 */
public abstract class ComponentEnergyBlockEntity extends ComponentBlockEntity implements ComponentEnergyProvider, EnergyComponentProvider {
	private final EnergyComponent energyComponent = createEnergyComponent();

	/** Instantiates a {@link ComponentEnergyBlockEntity}. */
	public ComponentEnergyBlockEntity(BlockEntityType<?> type) {
		super(type);

		addComponent(AstromineComponents.ENERGY_INVENTORY_COMPONENT, getEnergyComponent());
		getEnergyComponent().updateListeners();
	}

	/** Returns the {@link EnergyComponent} to be attached. */
	public abstract EnergyComponent createEnergyComponent();

	/** Returns the attached {@link EnergyComponent}. */
	@Override
	public EnergyComponent getEnergyComponent() {
		return energyComponent;
	}
}
