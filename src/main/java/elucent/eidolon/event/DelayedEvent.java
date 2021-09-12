package elucent.eidolon.event;

import elucent.eidolon.spell.IDelayedEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class DelayedEvent extends PlayerEvent {
	private IDelayedEvent event;
	public World world;
	public PlayerEntity caster;
	public Vector3d targetPosition;
	private int lifeInTicks = 0;

	public DelayedEvent(World world, PlayerEntity caster, Vector3d targetPosition, IDelayedEvent event) {
		super(caster);
	
	    this.event = event;
	    this.world = world;
	    this.caster = caster;
	    this.targetPosition = targetPosition;
	}

	public IDelayedEvent getEvent() {
		return this.event;
	}
	
	public boolean alive() {
		return this.lifeInTicks++ < this.event.getDelay();
	}
	
	public void commitEvent() {
		this.event.commit(this);
	}
}