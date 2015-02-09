package org.jboss.qa.phaser.phase.main;

import org.jboss.qa.phaser.PhaseDefinitionProcessor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class MainPhaseProcessor extends PhaseDefinitionProcessor {

	private Main download;

	public void execute() {
		log.info("MainPhase: ID={}", download.id());
	}
}
