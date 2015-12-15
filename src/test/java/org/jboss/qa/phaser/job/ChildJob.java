package org.jboss.qa.phaser.job;

import org.jboss.qa.phaser.AfterJob;
import org.jboss.qa.phaser.BeforeJob;
import org.jboss.qa.phaser.Create;
import org.jboss.qa.phaser.ExceptionHandling;
import org.jboss.qa.phaser.Inject;
import org.jboss.qa.phaser.OnException;
import org.jboss.qa.phaser.phase.second.Second;
import org.jboss.qa.phaser.point.AbstractInjectionPoint;
import org.jboss.qa.phaser.point.InjectionPoint;
import org.jboss.qa.phaser.point.MyPoint;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChildJob {

	@Inject(id = "IP1")
	public InjectionPoint ip1;

	@Inject
	public AbstractInjectionPoint ip2;

	@BeforeJob
	public void beforeJob() {
		log.info("BEFORE JOB CHILD");
	}

	@Second(id = "Second#1", main = "Main#1")
	@OnException(execution = ExceptionHandling.Execution.FINALIZE, report = ExceptionHandling.Report.LOG)
	public void scpA(InjectionPoint ipLocal, @Create MyPoint mpLocal) throws Exception {
		log.info("Second[ID=Second#1, mainRef=Main#1, order=0]");

		log.info("InjectionPoint: {}", ip1.getContent());
		log.info("AbstractInjectionPoint: {}", ip2.getContent());
		log.info("InjectionPoint: {}", ipLocal.getContent());
		log.info("MyPoint: {}", mpLocal.getContent());
		mpLocal.setContent("CHANGED");
	}

	@Second(id = "Second#2", order = 2)
	public void scpB(MyPoint mpLocal) {
		log.info("Second[ID=Second#2, mainRef=null, order=2]");
		log.info("MyPoint: {}", mpLocal.getContent());
	}

	@AfterJob
	public void afterJob() {
		log.info("AFTER JOB CHILD");
	}
}
