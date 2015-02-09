package org.jboss.qa.phaser.phase.second;

import org.jboss.qa.phaser.PhaseDefinitionProcessor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SecondPhaseProcessor extends PhaseDefinitionProcessor {

	private Second second;

	public void execute() {
		log.info("SecondPhase: ID={}, mainRef={}", second.id(), second.main());
	}
}
