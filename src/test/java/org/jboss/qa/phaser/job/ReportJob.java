package org.jboss.qa.phaser.job;

import static org.jboss.qa.phaser.ExceptionHandling.Execution.CONTINUE;
import static org.jboss.qa.phaser.ExceptionHandling.Report.SUPPRESS;

import static junit.framework.Assert.fail;

import org.jboss.qa.phaser.GenerateReport;
import org.jboss.qa.phaser.OnException;
import org.jboss.qa.phaser.phase.second.Second;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportJob {

	@GenerateReport
	@Second
	public void successPhase() {
		log.info("ReportJob[ID=Second#1]");
	}

	@OnException(execution = CONTINUE, report = SUPPRESS)
	@GenerateReport(reportsDir = "target/phaser-reports")
	@Second
	public void failingPhase() {
		log.info("ReportJob[ID=Second#1]");
		fail("expected fail");
	}
}
