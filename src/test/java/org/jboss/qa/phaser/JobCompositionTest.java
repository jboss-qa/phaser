package org.jboss.qa.phaser;

import org.jboss.qa.phaser.job.ChildJob;
import org.jboss.qa.phaser.job.ParentJob;
import org.jboss.qa.phaser.phase.main.MainPhase;
import org.jboss.qa.phaser.phase.second.SecondPhase;
import org.jboss.qa.phaser.phase.third.ThirdPhase;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class JobCompositionTest {

	@Test
	public void testJobComposition() throws Exception {
		MainPhase dp = new MainPhase();
		SecondPhase scp = new SecondPhase();
		ThirdPhase thp = new ThirdPhase();

		PhaseTreeBuilder builder = new PhaseTreeBuilder();
		builder.addPhase(dp).next().addPhase(scp).next().addPhase(thp);

		List<Object> jobs = new ArrayList<>(2);
		jobs.add(ChildJob.class.newInstance());
		jobs.add(ParentJob.class.newInstance());

		new Phaser(builder.build(), jobs).run();
	}
}
