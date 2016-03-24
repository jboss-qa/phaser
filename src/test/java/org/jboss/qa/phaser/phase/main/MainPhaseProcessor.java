package org.jboss.qa.phaser.phase.main;

import org.jboss.qa.phaser.Inject;
import org.jboss.qa.phaser.PhaseDefinitionProcessor;
import org.jboss.qa.phaser.registry.InstanceRegistry;

import org.testng.Assert;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MainPhaseProcessor extends PhaseDefinitionProcessor {

	@Inject
	private InstanceRegistry registry;

	@NonNull
	private Main main;

	public void execute() {
		log.info("MainPhase: ID={}", main.id());
		Assert.assertNotNull(registry);
	}
}
