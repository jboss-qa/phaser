package org.jboss.qa.phaser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jboss.qa.phaser.context.Context;
import org.jboss.qa.phaser.context.SimpleContext;
import org.jboss.qa.phaser.job.PropertiesJob;
import org.jboss.qa.phaser.phase.main.MainPhase;
import org.jboss.qa.phaser.phase.second.SecondPhase;
import org.jboss.qa.phaser.phase.third.ThirdPhase;
import org.jboss.qa.phaser.registry.SimpleInstanceRegistry;
import org.jboss.qa.phaser.tools.SpyProxyFactory;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class ContextTest {

	private Context ctx;

	@BeforeTest
	public void createCtx() throws MalformedURLException {
		ctx = new SimpleContext();
		ctx.set("property1", "String");
		ctx.set("property2", 10.0);
		final ArrayList<String> prop3 = new ArrayList<>(
				Arrays.asList("aa", "bb", "cc"));
		ctx.set("property3", prop3);
		final ArrayList<Integer> prop4 = new ArrayList<>(
				Arrays.asList(1, 2, 3));
		ctx.set("property4", prop4);
		ctx.set("url", new URL("https://github.com/jboss-soa-qa/phaser"));
	}

	@Test
	public void testContext() throws Exception {
		final PropertiesJob mock = mock(PropertiesJob.class);
		final PropertiesJob proxy = SpyProxyFactory.createProxy(PropertiesJob.class, mock);

		final SimpleInstanceRegistry registry = new SimpleInstanceRegistry();
		registry.insert(ctx);

		final MainPhase dp = new MainPhase();
		final SecondPhase scp = new SecondPhase();
		final ThirdPhase thp = new ThirdPhase();

		final PhaseTreeBuilder builder = new PhaseTreeBuilder();
		builder.addPhase(dp).next().addPhase(scp).next().addPhase(thp);
		new Phaser(builder.build(), proxy).run(registry);

		verify(mock, times(1)).beforeJobA();
		verify(mock, times(1)).scpB(new URL("https://github.com/jboss-soa-qa/phaser"), ctx);
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testWithoutContext() throws Exception {
		final PropertiesJob mock = mock(PropertiesJob.class);
		final PropertiesJob proxy = SpyProxyFactory.createProxy(PropertiesJob.class, mock);

		final MainPhase dp = new MainPhase();
		final SecondPhase scp = new SecondPhase();
		final ThirdPhase thp = new ThirdPhase();

		final PhaseTreeBuilder builder = new PhaseTreeBuilder();
		builder.addPhase(dp).next().addPhase(scp).next().addPhase(thp);
		new Phaser(builder.build(), proxy).run();
	}
}
