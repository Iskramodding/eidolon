package elucent.eidolon.spell;

import elucent.eidolon.event.DelayedEvent;

public interface IDelayedEvent {
	void commit(DelayedEvent event);
	void delay(DelayedEvent event);
	int getDelay();
}
