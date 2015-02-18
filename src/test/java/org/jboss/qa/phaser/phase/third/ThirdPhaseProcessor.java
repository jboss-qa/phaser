package org.jboss.qa.phaser.phase.third;

import org.jboss.qa.phaser.PhaseDefinitionProcessor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ThirdPhaseProcessor extends PhaseDefinitionProcessor {

	private Third third;

	public void execute() {
		log.info("ThirdPhase: ID={}, secondRef={}", third.id(), third.second());
	}
}
