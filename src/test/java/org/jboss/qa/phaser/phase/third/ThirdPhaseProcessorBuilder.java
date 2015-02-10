package org.jboss.qa.phaser.phase.third;

import org.jboss.qa.phaser.PhaseDefinitionProcessorBuilder;

import java.lang.reflect.Method;

public class ThirdPhaseProcessorBuilder extends PhaseDefinitionProcessorBuilder<Third> {

	public ThirdPhaseProcessor buildProcessor(Third annotation, Method method) {
		return new ThirdPhaseProcessor(annotation);
	}
}
