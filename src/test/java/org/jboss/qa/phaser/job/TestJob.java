package org.jboss.qa.phaser.job;

import org.jboss.qa.phaser.AfterJob;
import org.jboss.qa.phaser.BeforeJob;
import org.jboss.qa.phaser.Create;
import org.jboss.qa.phaser.ExceptionHandling;
import org.jboss.qa.phaser.Inject;
import org.jboss.qa.phaser.OnException;
import org.jboss.qa.phaser.RunAlways;
import org.jboss.qa.phaser.phase.main.Main;
import org.jboss.qa.phaser.phase.main.MainWrapper;
import org.jboss.qa.phaser.phase.second.Second;
import org.jboss.qa.phaser.phase.third.Third;
import org.jboss.qa.phaser.point.AbstractInjectionPoint;
import org.jboss.qa.phaser.point.Counter;
import org.jboss.qa.phaser.point.InjectionPoint;
import org.jboss.qa.phaser.point.MyPoint;
import org.jboss.qa.phaser.registry.InstanceRegistry;

import junit.framework.Assert;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@MainWrapper({
		@Main(id = "Main#1"),
		@Main(id = "Main#2", order = -1),
})
public class TestJob {

	@Inject(id = "IP1")
	public InjectionPoint ip1;

	@Inject
	public AbstractInjectionPoint ip2;

	@Inject
	public InstanceRegistry registry;

	@Inject(id = "counter")
	public Counter counter;

	@BeforeJob
	public void beforeJobA() {
		log.info("BEFORE JOB #1");

		registry.insert("counter", new Counter());
		registry.insert("IP1", new InjectionPoint("IP3"));
		registry.insert(new MyPoint("MyPoint Content"));
	}

	@BeforeJob
	public void beforeJobB() {
		log.info("BEFORE JOB #2");
	}

	@Second(id = "Second#1", main = "Main#1")
	@OnException(execution = ExceptionHandling.Execution.FINALIZE, report = ExceptionHandling.Report.LOG)
	public void scpA(InjectionPoint ipLocal, @Create MyPoint mpLocal) throws Exception {
		log.info("Second[ID=Second#1, mainRef=Main#1, order=0]");
		log.info("EXC WILL BE THROWN");

		throw new Exception("Test Exception!");
	}

	@Second(id = "Second#2", order = 2)
	public void scpB(MyPoint mpLocal) {
		log.info("Second[ID=Second#2, mainRef=null, order=2]");
		log.info("MyPoint: {}", mpLocal.getContent());
		counter.add();
	}

	@RunAlways
	@Third(id = "Third#1", second = "Second#2", order = 1)
	public void thpA(@Create(id = "mypoint") MyPoint myPoint) {
		log.info("Third[ID=Third#1, secondRef=null, order=1]");
		myPoint.setContent("Third#1");
	}

	@Third(id = "Third#2", second = "Second#2", order = 2, runAlways = "true")
	public void thpB(InstanceRegistry ir) {
		log.info("Third[ID=Third#2, secondRef=null, order=2]");
		ir.get(MyPoint.class).get(0).setContent("Third#2");
	}

	@RunAlways
	@Third(id = "Third#3", second = "Second#2", order = 1.1, runAlways = "false") // override method @RunAlways
	public void thpC() {
		log.info("Third[ID=Third#3, secondRef=null, order=3]");
		Assert.assertTrue("This phase should be skipped!", false);
	}

	@RunAlways
	@Second(id="Second#3", order = 2, main = "Main#2")
	public void scpC(@Inject(id = "IP1") InjectionPoint myPoint){
		log.info("Second[ID=Second#3, mainRef=null, order=2]");
		log.info("InjectionPoint: {}", myPoint.getContent());
	}

	@AfterJob
	public void afterJobA() {
		log.info("AFTER JOB #1");
	}

	@AfterJob
	public void afterJobB() {
		log.info("AFTER JOB #2");
	}
}
