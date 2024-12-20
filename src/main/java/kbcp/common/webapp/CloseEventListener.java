package kbcp.common.webapp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class CloseEventListener implements ApplicationListener<ContextClosedEvent>{
	@Override
	public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
		log.info("CloseEventListener!!");
	}

}
