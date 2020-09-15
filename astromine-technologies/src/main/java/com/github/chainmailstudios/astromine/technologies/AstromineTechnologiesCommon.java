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

package com.github.chainmailstudios.astromine.technologies;

import com.github.chainmailstudios.astromine.AstromineClient;
import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.AstromineDedicated;
import com.github.chainmailstudios.astromine.technologies.registry.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod("astrominetechnologies")
public class AstromineTechnologiesCommon extends AstromineCommon {
	@Override
	public void onInitialize(IEventBus modBus, IEventBus forgeBus) {
		AstromineTechnologiesBlockEntityTypes.initialize();
		AstromineTechnologiesBlocks.initialize();
		AstromineTechnologiesItems.initialize();
		AstromineTechnologiesItemGroups.initialize();
		AstromineTechnologiesRecipeSerializers.initialize();
		AstromineTechnologiesScreenHandlers.initialize();
		AstromineTechnologiesToolMaterials.initialize();
		AstromineTechnologiesCommonCallbacks.initialize();
		AstromineTechnologiesEntityTypes.initialize();
		AstromineTechnologiesNetworkMembers.initialize();
		AstromineTechnologiesRecipeSerializers.initialize();
		AstromineTechnologiesCommonPackets.initialize();
	}

	@Override
	public Supplier<? extends AstromineClient> getClientInitializer() {
		return AstromineTechnologiesClient::new;
	}

	@Override
	public Supplier<? extends AstromineDedicated> getServerInitializer() {
		return AstromineTechnologiesDedicated::new;
	}
}
