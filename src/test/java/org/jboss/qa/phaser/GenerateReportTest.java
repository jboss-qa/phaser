package org.jboss.qa.phaser;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

import static junit.framework.Assert.assertTrue;

import org.apache.commons.io.FileUtils;

import org.jboss.qa.phaser.job.ReportJob;
import org.jboss.qa.phaser.phase.second.SecondPhase;
import org.jboss.qa.phaser.tools.SpyProxyFactory;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenerateReportTest {

	@Test
	public void generateReportTest() throws Exception {
		log.info("starting report test");
		final SecondPhase scp = new SecondPhase();
		final PhaseTreeBuilder builder = new PhaseTreeBuilder();

		final ReportJob reportJob = mock(ReportJob.class);
		final ReportJob reportproxy = SpyProxyFactory.createProxy(ReportJob.class, reportJob);
		builder.addPhase(scp);
		try {
			new Phaser(builder.build(), reportproxy).run();
		} catch (Exception ex) {
			// OK
		}
		final File reportsDir = new File("target/phaser-reports");
		assertTrue(reportsDir.exists());
		assertEquals(2, reportsDir.listFiles().length);
		final String successfulContent = FileUtils.readFileToString(reportsDir.listFiles()[0], "UTF-8");
		final String failedContent = FileUtils.readFileToString(reportsDir.listFiles()[1], "UTF-8");
		try {
			testSuccesful(successfulContent);
			testFailed(failedContent);
		} catch (AssertionError ex) {
			// in case of error swap files
			testSuccesful(failedContent);
			testFailed(successfulContent);
		}
	}

	private void testSuccesful(String content) {
		Assert.assertTrue(content.contains("errors=\"0\""));
		Assert.assertTrue(content.contains("org.jboss.qa.phaser.job.ReportJob"));
		Assert.assertTrue(content.contains("successPhase"));
	}

	private void testFailed(String content) {
		Assert.assertTrue(content.contains("errors=\"1\""));
		Assert.assertTrue(content.contains("org.jboss.qa.phaser.job.ReportJob"));
		Assert.assertTrue(content.contains("failingPhase"));
		Assert.assertTrue(content.contains("test failure"));
	}
}
