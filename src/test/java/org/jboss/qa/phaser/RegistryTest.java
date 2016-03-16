package org.jboss.qa.phaser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jboss.qa.phaser.job.TestJob;
import org.jboss.qa.phaser.phase.main.MainPhase;
import org.jboss.qa.phaser.phase.second.SecondPhase;
import org.jboss.qa.phaser.phase.third.ThirdPhase;
import org.jboss.qa.phaser.point.Counter;
import org.jboss.qa.phaser.point.InjectionPoint;
import org.jboss.qa.phaser.point.MyPoint;
import org.jboss.qa.phaser.registry.InstanceRegistry;
import org.jboss.qa.phaser.registry.SimpleInstanceRegistry;
import org.jboss.qa.phaser.tools.SpyProxyFactory;

import org.hamcrest.generator.HamcrestFactoryWriter;
import org.mockito.Matchers;
import org.mockito.verification.VerificationMode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistryTest {

	private PhaseTree phaseTree;

	@BeforeClass
	public void init() throws Exception {
		final MainPhase dp = new MainPhase();
		final SecondPhase scp = new SecondPhase();
		final ThirdPhase thp = new ThirdPhase();

		final PhaseTreeBuilder builder = new PhaseTreeBuilder();
		builder.addPhase(dp).next().addPhase(scp).next().addPhase(thp);
		phaseTree = builder.build();
	}

	/**
	 * Run two times TestJob with shared register.
	 * <p/>
	 * Expected result for numberOfInstances is 8 because every run create 4 new instances of MyPoint.
	 * Expected result for numberOfExecution is 5 because first run execute only once method scpB (register has one
	 * MyPoint bean). Second run of job execute 4 times method scpB.
	 *
	 * @throws Exception
	 */
	@Test
	public void sharedRegistryTest() throws Exception {
		final TestJob mock1 = mock(TestJob.class);
		final TestJob mock2 = mock(TestJob.class);
		final TestJob proxy1 = SpyProxyFactory.createProxy(TestJob.class, mock1);
		final TestJob proxy2 = SpyProxyFactory.createProxy(TestJob.class, mock2);

		final InstanceRegistry registry = new SimpleInstanceRegistry();
		run(registry, proxy1);
		run(registry, proxy2);
		verifyRegistry(registry, 8, 5);

		verifyCalledTimes(mock1, times(1), times(1), times(2), times(2), never());
		verifyCalledTimes(mock2, times(1), times(5), times(2), times(2), never());
	}

	@Test
	public void separateRegistryTest() throws Exception {
		final TestJob mock1 = mock(TestJob.class);
		final TestJob mock2 = mock(TestJob.class);
		final TestJob proxy1 = SpyProxyFactory.createProxy(TestJob.class, mock1);
		final TestJob proxy2 = SpyProxyFactory.createProxy(TestJob.class, mock2);

		final InstanceRegistry regA = new SimpleInstanceRegistry();
		final InstanceRegistry regB = new SimpleInstanceRegistry();
		run(regA, proxy1);
		run(regB, proxy2);
		verifyRegistry(regA, 4, 1);
		verifyRegistry(regB, 4, 1);

		verifyCalledTimes(mock1, times(1), times(1), times(2), times(2), never());
		verifyCalledTimes(mock2, times(1), times(1), times(2), times(2), never());
	}

	private void run(InstanceRegistry registry, Object job) throws Exception {
		new Phaser(phaseTree, job).run(registry);
	}

	private void verifyRegistry(InstanceRegistry registry, int numberOfIstances, int numberOfExecution) {
		Assert.assertEquals(registry.get("counter", Counter.class).getValue(), numberOfExecution);
		Assert.assertEquals("IP3", registry.get("IP1", InjectionPoint.class).getContent());
		Assert.assertEquals("Third#1", registry.get("mypoint", MyPoint.class).getContent());
		Assert.assertEquals(registry.get(MyPoint.class).size(), numberOfIstances);
	}

	private void verifyCalledTimes(TestJob mock, VerificationMode scpA, VerificationMode scpB, VerificationMode thpA,
			VerificationMode thbB, VerificationMode thpC) throws Exception {
		verify(mock, scpB).scpB(any(MyPoint.class));
		verify(mock, scpA).scpA(any(InjectionPoint.class), any(MyPoint.class));
		verify(mock, times(1)).scpC(any(InjectionPoint.class));

		verify(mock, thpA).thpA(any(MyPoint.class));
		verify(mock, thpC).thpC();
		verify(mock, thbB).thpB(any(org.jboss.qa.phaser.registry.InstanceRegistry.class));
	}
}
