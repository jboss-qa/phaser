package org.jboss.qa.phaser;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import org.jboss.qa.phaser.job.ChildJob;
import org.jboss.qa.phaser.job.ParentJob;
import org.jboss.qa.phaser.job.PropertiesJob;
import org.jboss.qa.phaser.phase.main.MainPhase;
import org.jboss.qa.phaser.phase.second.SecondPhase;
import org.jboss.qa.phaser.phase.third.ThirdPhase;
import org.jboss.qa.phaser.point.InjectionPoint;
import org.jboss.qa.phaser.point.MyPoint;
import org.jboss.qa.phaser.tools.SpyProxyFactory;

import org.mockito.InOrder;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class JobCompositionTest {

	@Test
	public void testJobComposition() throws Exception {
		final MainPhase dp = new MainPhase();
		final SecondPhase scp = new SecondPhase();
		final ThirdPhase thp = new ThirdPhase();

		final PhaseTreeBuilder builder = new PhaseTreeBuilder();
		builder.addPhase(dp).next().addPhase(scp).next().addPhase(thp);

		final ChildJob mockChildJob = mock(ChildJob.class);
		final ChildJob proxyChildJob = SpyProxyFactory.createProxy(ChildJob.class, mockChildJob);

		final ParentJob mockParentJob = mock(ParentJob.class);
		final ParentJob proxyParentJob = SpyProxyFactory.createProxy(ParentJob.class, mockParentJob);

		final List<Object> jobs = new ArrayList<>(2);
		jobs.add(proxyChildJob);
		jobs.add(proxyParentJob);

		new Phaser(builder.build(), jobs).run();

		final InOrder order = inOrder(mockChildJob, mockParentJob);
		order.verify(mockChildJob).beforeJob();
		order.verify(mockParentJob).beforeJob();

		order.verify(mockChildJob).scpB(any(MyPoint.class));
		order.verify(mockChildJob).scpA(any(InjectionPoint.class), any(MyPoint.class));

		order.verify(mockChildJob, times(2)).scpB(any(MyPoint.class));

		order.verify(mockParentJob).thpA();
		order.verify(mockParentJob).thpC();
		order.verify(mockParentJob).thpB();
		order.verify(mockParentJob).thpC();
		order.verify(mockParentJob).thpA();
		order.verify(mockParentJob).thpC();
		order.verify(mockParentJob).thpB();

		order.verify(mockChildJob).afterJob();
		order.verify(mockParentJob).afterJob();
		order.verifyNoMoreInteractions();
	}
}
