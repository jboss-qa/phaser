package org.jboss.qa.phaser;

import org.jboss.qa.phaser.context.BasicContext;
import org.jboss.qa.phaser.context.Context;
import org.jboss.qa.phaser.job.PropertiesJob;
import org.jboss.qa.phaser.phase.main.MainPhase;
import org.jboss.qa.phaser.phase.second.SecondPhase;
import org.jboss.qa.phaser.phase.third.ThirdPhase;

import org.testng.annotations.AfterMethod;
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
		ctx = new BasicContext();
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
		InstanceRegistry.insert(ctx);

		final MainPhase dp = new MainPhase();
		final SecondPhase scp = new SecondPhase();
		final ThirdPhase thp = new ThirdPhase();

		final PhaseTreeBuilder builder = new PhaseTreeBuilder();
		builder.addPhase(dp).next().addPhase(scp).next().addPhase(thp);
		new Phaser(builder.build(), PropertiesJob.class.newInstance()).run();
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testWithoutContext() throws Exception {
		final MainPhase dp = new MainPhase();
		final SecondPhase scp = new SecondPhase();
		final ThirdPhase thp = new ThirdPhase();

		final PhaseTreeBuilder builder = new PhaseTreeBuilder();
		builder.addPhase(dp).next().addPhase(scp).next().addPhase(thp);
		new Phaser(builder.build(), PropertiesJob.class.newInstance()).run();
	}

	@AfterMethod
	public void clean() {
		InstanceRegistry.reset();
	}
}
