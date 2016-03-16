package org.jboss.qa.phaser;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jboss.qa.phaser.job.TestJob;
import org.jboss.qa.phaser.phase.main.MainPhase;
import org.jboss.qa.phaser.phase.second.SecondPhase;
import org.jboss.qa.phaser.phase.third.ThirdPhase;
import org.jboss.qa.phaser.point.InjectionPoint;
import org.jboss.qa.phaser.point.MyPoint;
import org.jboss.qa.phaser.registry.*;
import org.jboss.qa.phaser.tools.SpyProxyFactory;

import org.mockito.InOrder;
import org.testng.annotations.Test;

public class PhaserDummyTest {

	@Test
	public void testRun() throws Exception {
		final TestJob mock = mock(TestJob.class);
		final TestJob proxy = SpyProxyFactory.createProxy(TestJob.class, mock);

		final MainPhase dp = new MainPhase();
		final SecondPhase scp = new SecondPhase();
		final ThirdPhase thp = new ThirdPhase();

		final PhaseTreeBuilder builder = new PhaseTreeBuilder();
		builder.addPhase(dp).next().addPhase(scp).next().addPhase(thp);
		new Phaser(builder.build(), proxy).run();

		verify(mock).beforeJobA();
		verify(mock).beforeJobB();

		verify(mock, times(1)).scpB(any(MyPoint.class));
		verify(mock, times(1)).scpA(any(InjectionPoint.class), any(MyPoint.class));
		verify(mock, times(1)).scpC(any(InjectionPoint.class));

		verify(mock, times(2)).thpA(any(MyPoint.class));
		verify(mock, never()).thpC();
		verify(mock, times(2)).thpB(any(org.jboss.qa.phaser.registry.InstanceRegistry.class));

		verify(mock).afterJobA();
		verify(mock).afterJobB();
	}
}
