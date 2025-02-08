package eskebre.crop;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CropAutoReplant implements ModInitializer {
	public static final String MOD_ID = "crop-auto-replant";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Crop Auto Replant Loaded!");
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			BlockPos pos = hitResult.getBlockPos();
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof CropBlock cropBlock) {
				int MAX_AGE = cropBlock.getMaxAge();
				int CURRENT_AGE = cropBlock.getAge(state);
				LOGGER.info(String.valueOf(MAX_AGE));
				if (CURRENT_AGE == MAX_AGE) {
					world.setBlockState(pos, state.getBlock().getDefaultState());
					Block.dropStacks(state, world, pos);
					player.swingHand(hand);
					world.playSound(null, pos, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS);
					return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
				}
			}	
			
			return ActionResult.PASS;
		});
	}


}