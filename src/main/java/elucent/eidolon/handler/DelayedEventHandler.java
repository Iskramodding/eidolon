package elucent.eidolon.handler;

import java.util.ArrayList;
import java.util.List;

import elucent.eidolon.event.DelayedEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class DelayedEventHandler {
	static List<DelayedEvent> PENDING_SPELLS = new ArrayList<DelayedEvent>();

	@SubscribeEvent
	public static void onServerTickEvent(ServerTickEvent event) {
		if(PENDING_SPELLS.size() < 1) return;

		int remove = -1;
		for (int i = 0; i < PENDING_SPELLS.size(); i++) {
			DelayedEvent delayedEvent = PENDING_SPELLS.get(i);
			if (!delayedEvent.alive()) {
				remove = i;
				continue;
			}
		}

		if(remove > -1) {
			DelayedEvent delayedEvent = PENDING_SPELLS.get(remove);
			delayedEvent.commitEvent();
			PENDING_SPELLS.remove(remove);
		}
	}

	@SubscribeEvent
	public static void onDelayedEvent(DelayedEvent event) {
		PENDING_SPELLS.add(event);
	}

}