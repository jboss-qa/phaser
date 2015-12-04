package org.jboss.qa.phaser;

import org.jboss.qa.phaser.job.TestJob;
import org.jboss.qa.phaser.phase.main.MainPhase;
import org.jboss.qa.phaser.phase.second.SecondPhase;
import org.jboss.qa.phaser.phase.third.ThirdPhase;

import org.testng.annotations.Test;

public class PhaserDummyTest {

	@Test
	public void testRun() throws Exception {
		final MainPhase dp = new MainPhase();
		final SecondPhase scp = new SecondPhase();
		final ThirdPhase thp = new ThirdPhase();

		final PhaseTreeBuilder builder = new PhaseTreeBuilder();
		builder.addPhase(dp).next().addPhase(scp).next().addPhase(thp);
		new Phaser(builder.build(), TestJob.class.newInstance()).run();
	}
}
