package elucent.eidolon.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import java.util.List;
import elucent.eidolon.event.DelayedEvent;
import elucent.eidolon.spell.IDelayedEvent;

public class PathfinderWandItem extends WandItem implements IDelayedEvent {
	
	public static final double DIRECT_TELEPORT_REACH = 3.0D;
	public static final int INDIRECT_TELEPORT_TICK_DELAY = 90;
	
	public int getDelay() {
		return INDIRECT_TELEPORT_TICK_DELAY;
	}
	
	public PathfinderWandItem(Properties builderIn) {
        super(builderIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (this.loreTag != null) {
            tooltip.add(new StringTextComponent(""));
            tooltip.add(new StringTextComponent("" + TextFormatting.DARK_PURPLE + TextFormatting.ITALIC + I18n.format(this.loreTag)));
        }
    }

    
    @Override
    public void delay(DelayedEvent evt) {
    	MinecraftForge.EVENT_BUS.post(evt);
    }
    
    @Override
	public void commit(DelayedEvent event) {
    	PlayerEntity player = event.caster;
    	World world = event.world;
    	
    	Vector3d toLocation = event.targetPosition;
    	Vector3d fromLocation = player.getPositionVec();
    	
    	
        player.setPosition(toLocation.x, toLocation.y, toLocation.z);
        
        world.playSound(player, toLocation.x, toLocation.y, toLocation.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f);
        world.playSound(player, fromLocation.x, fromLocation.y, fromLocation.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f);
    	// TODO: show particles
	}
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        
    	Vector3d initialLocationVec = player.getPositionVec();
        
        RayTraceResult rresult = player.pick(64.0D, 2.0F, true);
        if(rresult.getType() == RayTraceResult.Type.MISS) {
        	return ActionResult.resultFail(player.getHeldItem(hand));
        }
        
        Vector3d targetLocation = rresult.getHitVec();
    	
        if(targetLocation.distanceTo(initialLocationVec) < DIRECT_TELEPORT_REACH) {
        	this.delayTeleportTo(world, player, targetLocation);
        } else {
        	this.teleportTo(world, player, initialLocationVec, targetLocation);
        }

        ItemStack stack = player.getHeldItem(hand);
        stack.damageItem(1, player, (ply) -> {
            ply.sendBreakAnimation(hand);
        });
        player.swingArm(hand);
        return ActionResult.resultPass(stack);
    }
    
    private void delayTeleportTo(World world, PlayerEntity player, Vector3d fromLocation) {
		this.delay(new DelayedEvent(world, player, fromLocation, this));
    }

    private void teleportTo(World world, PlayerEntity player, Vector3d fromLocation, Vector3d toLocation) {
        AxisAlignedBB areaOfEffect = new AxisAlignedBB(new BlockPos(toLocation)).grow(2, 2, 2);

        for(LivingEntity target: world.getEntitiesWithinAABB(LivingEntity.class, areaOfEffect)) {
            target.lookAt(EntityAnchorArgument.Type.EYES, fromLocation);
        	target.setPosition(fromLocation.x, fromLocation.y, fromLocation.z);
        }

        player.setPosition(toLocation.x, toLocation.y, toLocation.z);
                
        world.playSound(player, toLocation.x, toLocation.y, toLocation.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f);
        world.playSound(player, fromLocation.x, fromLocation.y, fromLocation.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f);
    	// TODO: show particles
    }
}
