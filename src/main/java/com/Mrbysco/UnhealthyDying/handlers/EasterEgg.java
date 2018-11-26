package com.Mrbysco.UnhealthyDying.handlers;

import com.Mrbysco.UnhealthyDying.Reference;
import com.Mrbysco.UnhealthyDying.config.DyingConfigGen;
import com.Mrbysco.UnhealthyDying.util.UnhealthyHelper;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EasterEgg {
	@SubscribeEvent
	public void killedEntityEvent(LivingDeathEvent event) {
		if(DyingConfigGen.regen.regenHealth)
		{
			String[] targets = DyingConfigGen.regen.regenTargets;
			if(targets.length > 0)
			{
				for(int i = 0; i < targets.length; i++)
				{
					if (EntityList.isMatchingName(event.getEntityLiving(), UnhealthyHelper.getEntityLocation(targets[i])))
					{
						if (event.getSource().getTrueSource() instanceof EntityPlayer) {
							EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
							NBTTagCompound playerData = player.getEntityData();
							NBTTagCompound data = UnhealthyHelper.getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);
							
						    int playerHealth = (int)player.getMaxHealth();
						    int maxRegained = DyingConfigGen.regen.maxRegenned;
						    int healthPerKill = DyingConfigGen.regen.healthPerKill;
						    
						    if(playerHealth < maxRegained)
						    {
							    if(!data.hasKey(Reference.REDUCED_HEALTH_TAG))
								{
									if(!player.world.isRemote)
									{
										if(!data.hasKey(Reference.REDUCED_HEALTH_TAG)) 
										{
											data.setInteger(Reference.REDUCED_HEALTH_TAG, (int)playerHealth);
											playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
											UnhealthyHelper.setHealth(player, playerHealth, false);
										}
									}
								}
							    else
							    {
							    	int oldMaxHealth = data.getInteger(Reference.REDUCED_HEALTH_TAG);
									int healthPlusKill = oldMaxHealth + healthPerKill;
									int newMaxHealth = healthPlusKill >= maxRegained ? maxRegained : healthPlusKill;
									
									data.setInteger(Reference.REDUCED_HEALTH_TAG, (int)newMaxHealth);
									playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
									UnhealthyHelper.setHealth(player, newMaxHealth, true);

									if(DyingConfigGen.regen.regennedHealthMessage)
									{
										player.sendStatusMessage(new TextComponentTranslation("unhealthydying:regennedHealth.message", new Object[newMaxHealth]), true);
									}
							    }
						    }
						}
						break;
					}
				}
			}
		}
	}
}
