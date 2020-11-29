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

package com.github.chainmailstudios.astromine.discoveries.common.entity;

import com.github.chainmailstudios.astromine.common.component.general.*;
import com.github.chainmailstudios.astromine.common.component.general.base.FluidComponent;
import com.github.chainmailstudios.astromine.common.component.general.base.ItemComponent;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.common.recipe.ingredient.FluidIngredient;
import com.github.chainmailstudios.astromine.common.utilities.VolumeUtilities;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.github.chainmailstudios.astromine.discoveries.common.entity.base.RocketEntity;
import com.github.chainmailstudios.astromine.discoveries.common.screenhandler.PrimitiveRocketScreenHandler;
import com.github.chainmailstudios.astromine.discoveries.registry.AstromineDiscoveriesDimensions;
import com.github.chainmailstudios.astromine.discoveries.registry.AstromineDiscoveriesItems;
import com.github.chainmailstudios.astromine.foundations.registry.AstromineFoundationsFluids;
import io.netty.buffer.Unpooled;
import org.joml.Vector3d;
import org.joml.Vector3f;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.util.Collection;

public class PrimitiveRocketEntity extends RocketEntity implements ExtendedScreenHandlerFactory {
	public static final ResourceLocation PRIMITIVE_ROCKET_SPAWN = AstromineCommon.identifier("primitive_rocket_spawn");

	private static final FluidIngredient KEROSENE_INGREDIENT = FluidIngredient.ofFluidVolumes(FluidVolume.of(Fraction.ofDecimal(0.288D), AstromineFoundationsFluids.KEROSENE));

	private static final FluidIngredient OXYGEN_INGREDIENT = FluidIngredient.ofFluidVolumes(FluidVolume.of(Fraction.ofDecimal(0.112D), AstromineFoundationsFluids.OXYGEN));

	public PrimitiveRocketEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Override
	public FluidComponent createFluidComponent() {
		FluidComponent fluidComponent = SimpleAutoSyncedFluidComponent.of(2);
		fluidComponent.getFirst().setSize(Fraction.of(1024L));
		fluidComponent.getSecond().setSize(Fraction.of(1024L));
		return fluidComponent;
	}

	@Override
	public ItemComponent createItemComponent() {
		return SimpleAutoSyncedItemComponent.of(4);
	}

	@Override
	protected boolean isFuelMatching() {
		return KEROSENE_INGREDIENT.test(getFluidComponent().getFirst()) && OXYGEN_INGREDIENT.test(getFluidComponent().getSecond());
	}

	@Override
	protected void consumeFuel() {
		getFluidComponent().getFirst().take(KEROSENE_INGREDIENT.testMatching(getFluidComponent().getFirst()).getAmount());
		getFluidComponent().getSecond().take(OXYGEN_INGREDIENT.testMatching(getFluidComponent().getSecond()).getAmount());
	}

	@Override
	protected Vector3d getAcceleration() {
		return new Vector3d(0D, 0.000025 / (getY() / 1024D), 0D);
	}

	@Override
	protected Vector3f getPassengerPosition() {
		return new Vector3f(0.0F, 7.75F, 0.0F);
	}

	@Override
	protected Collection<ItemStack> getDroppedStacks() {
		return Lists.newArrayList(new ItemStack(AstromineDiscoveriesItems.PRIMITIVE_ROCKET_BOOSTER), new ItemStack(AstromineDiscoveriesItems.PRIMITIVE_ROCKET_HULL), new ItemStack(AstromineDiscoveriesItems.PRIMITIVE_ROCKET_PLATING, 2));
	}

	@Override
	public void openInventory(Player player) {
		player.openMenu(this);
	}

	@Override
	public boolean isPickable() {
		return !this.removed;
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
		if (player.level.isClientSide) {
			return InteractionResult.CONSUME;
		}

		if (player.isShiftKeyDown()) {
			this.openInventory(player);
		} else {
			player.startRiding(this);
		}

		return super.interactAt(player, hitPos, hand);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());

		packet.writeDouble(this.getX());
		packet.writeDouble(this.getY());
		packet.writeDouble(this.getZ());
		packet.writeUUID(this.getUUID());
		packet.writeInt(this.getId());

		return ServerSidePacketRegistry.INSTANCE.toPacket(PRIMITIVE_ROCKET_SPAWN, packet);
	}

	@Override
	public void writeScreenOpeningData(ServerPlayer serverPlayerEntity, FriendlyByteBuf buffer) {
		buffer.writeInt(this.getId());
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
		return new PrimitiveRocketScreenHandler(syncId, player, getId());
	}

	@Override
	public void tick() {
		if (level.dimension().equals(AstromineDiscoveriesDimensions.EARTH_SPACE_WORLD)) {
			setDeltaMovement(0, 0, 0);

			getEntityData().set(IS_RUNNING, false);
		}

		VolumeUtilities.transferBetween(getItemComponent(), getFluidComponent(), 0, 1, 0);
		VolumeUtilities.transferBetween(getItemComponent(), getFluidComponent(), 2, 3, 1);

		super.tick();
	}
}
