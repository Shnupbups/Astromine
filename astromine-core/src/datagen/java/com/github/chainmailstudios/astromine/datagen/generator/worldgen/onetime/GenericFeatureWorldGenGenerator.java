package com.github.chainmailstudios.astromine.datagen.generator.worldgen.onetime;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import me.shedaniel.cloth.api.datagen.v1.WorldGenData;

public class GenericFeatureWorldGenGenerator implements OneTimeWorldGenGenerator {
	private final ResourceLocation identifier;
	private final ConfiguredFeature<?, ?> feature;

	public GenericFeatureWorldGenGenerator(ResourceLocation identifier, ConfiguredFeature<?, ?> feature) {
		this.identifier = identifier;
		this.feature = feature;
	}

	@Override
	public String getGeneratorName() {
		return "generic_feature";
	}

	@Override
	public void generate(WorldGenData data) {
		data.addFeature(identifier, feature);
	}
}
