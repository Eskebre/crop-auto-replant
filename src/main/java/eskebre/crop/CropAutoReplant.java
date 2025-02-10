package eskebre.crop;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemStack;

import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

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
			
			//Client Code
			if (world.isClient) {
				// if (state.getBlock() instanceof CropBlock cropBlock) {
				// 	int MAX_AGE = cropBlock.getMaxAge();
				// 	int CURRENT_AGE = cropBlock.getAge(state);
				// 	if (CURRENT_AGE == MAX_AGE) player.swingHand(hand.MAIN_HAND);
				// }
				return ActionResult.PASS;
			}
		
			//Server Code
			if (state.getBlock() instanceof CropBlock cropBlock) {
				int MAX_AGE = cropBlock.getMaxAge();
				int CURRENT_AGE = cropBlock.getAge(state);
				ItemStack handItem = player.getStackInHand(hand);
				//3x3 Hoe Break
				if (handItem.isIn(ItemTags.HOES)) {
					player.swingHand(hand);
					for (int x = -1; x <=1; x++) {
						for (int z = -1; z <= 1; z++) {
							BlockPos iPos = pos.add(x, 0, z);
							BlockState iState = world.getBlockState(iPos);
							if (iState.getBlock() instanceof CropBlock iCropBlock) {
								int iMAX_AGE = iCropBlock.getMaxAge();
								int iCURRENT_AGE = iCropBlock.getAge(iState);
								if (iMAX_AGE == iCURRENT_AGE) {
									world.setBlockState(iPos, iState.getBlock().getDefaultState(), 3);
									Block.dropStacks(iState, world, iPos);
									player.swingHand(hand.MAIN_HAND);
									world.playSound(null, iPos, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS);
								}
							}
						}
					}
					
					return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
				} 
				//Single block
				else if (CURRENT_AGE == MAX_AGE) {
					
					world.setBlockState(pos, state.getBlock().getDefaultState());
					Block.dropStacks(state, world, pos);
					world.playSound(null, pos, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS);
					player.swingHand(hand.MAIN_HAND);
					
					
					return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
				}
			}	
			
			return ActionResult.PASS;
		});
	}


}