package org.jboss.qa.phaser;

import org.jboss.qa.phaser.job.TestJob;
import org.jboss.qa.phaser.phase.main.MainPhase;
import org.jboss.qa.phaser.phase.second.SecondPhase;
import org.jboss.qa.phaser.phase.third.ThirdPhase;
import org.jboss.qa.phaser.point.Counter;
import org.jboss.qa.phaser.point.InjectionPoint;
import org.jboss.qa.phaser.point.MyPoint;
import org.jboss.qa.phaser.registry.InstanceRegistry;
import org.jboss.qa.phaser.registry.SimpleInstanceRegistry;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistryTest {

	private PhaseTree phaseTree;

	@BeforeClass
	public void init() throws Exception {
		MainPhase dp = new MainPhase();
		SecondPhase scp = new SecondPhase();
		ThirdPhase thp = new ThirdPhase();

		PhaseTreeBuilder builder = new PhaseTreeBuilder();
		builder.addPhase(dp).next().addPhase(scp).next().addPhase(thp);
		phaseTree = builder.build();
	}

	/**
	 * Run two times TestJob with shared register.
	 *
	 * Expected result for numberOfInstances is 8 because every run create 4 new instances of MyPoint.
	 * Expected result for numberOfExecution is 5 because first run execute only once method scpB (register has one
	 * MyPoint bean). Second run of job execute 4 times method scpB.
	 *
	 * @throws Exception
	 */
	@Test
	public void sharedRegistryTest() throws Exception {
		InstanceRegistry registry = new SimpleInstanceRegistry();
		run(registry);
		run(registry);
		verifyRegistry(registry, 8, 5);
	}

	@Test
	public void separateRegistryTest() throws Exception {
		InstanceRegistry regA = new SimpleInstanceRegistry();
		InstanceRegistry regB = new SimpleInstanceRegistry();
		run(regA);
		run(regB);
		verifyRegistry(regA, 4, 1);
		verifyRegistry(regB, 4, 1);
	}

	private void run(InstanceRegistry registry) throws Exception {
		new Phaser(phaseTree, TestJob.class.newInstance()).run(registry);
	}

	private void verifyRegistry(InstanceRegistry registry, int numberOfIstances, int numberOfExecution) {
		Assert.assertEquals(registry.get("counter", Counter.class).getValue(), numberOfExecution);
		Assert.assertEquals("IP3", registry.get("IP1", InjectionPoint.class).getContent());
		Assert.assertEquals("Third#1", registry.get("mypoint", MyPoint.class).getContent());
		Assert.assertEquals(registry.get(MyPoint.class).size(), numberOfIstances);
	}
}
