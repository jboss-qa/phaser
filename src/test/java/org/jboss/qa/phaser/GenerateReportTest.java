package org.jboss.qa.phaser;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

import static junit.framework.Assert.assertTrue;

import org.apache.commons.io.FileUtils;

import org.jboss.qa.phaser.job.ReportJob;
import org.jboss.qa.phaser.phase.second.SecondPhase;
import org.jboss.qa.phaser.tools.SpyProxyFactory;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FilenameFilter;

import junit.framework.AssertionFailedError;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenerateReportTest {

	@BeforeTest
	public void removePreviousReports() {
		File phaserReports = new File("target/phaser-reports");
		if (phaserReports.exists()) {
			for(File file : phaserReports.listFiles()) {
				file.delete();
			}
		}
		File userDir = new File(System.getProperty("user.dir"));
		for(File file : userDir.listFiles(new ReportFilenameFilter())) {
			file.delete();
		}
	}

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
		assertEquals(1, reportsDir.listFiles().length);
		final String failedContent = FileUtils.readFileToString(reportsDir.listFiles()[0], "UTF-8");
		testFailed(failedContent);

		final File userDir = new File(System.getProperty("user.dir"));
		final File reportFiles[] = userDir.listFiles(new ReportFilenameFilter());
		assertEquals(1, reportFiles.length);
		final String successfulContent = FileUtils.readFileToString(reportFiles[0], "UTF-8");
		testSuccesful(successfulContent);
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
		Assert.assertTrue(content.contains(AssertionFailedError.class.getName()));
	}

	private class ReportFilenameFilter implements FilenameFilter {
		@Override
		public boolean accept(File file, String s) {
			return s.startsWith("TEST-ReportJob");
		}
	}
}
