package org.jboss.qa.phaser;

import org.jboss.qa.phaser.phase.main.Main;
import org.jboss.qa.phaser.phase.main.MainWrapper;
import org.jboss.qa.phaser.phase.second.Second;
import org.jboss.qa.phaser.phase.third.Third;

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

	@BeforeJob
	public void beforeJobA() {
		log.info("BEFORE JOB #1");

		InstanceRegistry.insert("IP1", new InjectionPoint("IP3"));
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

		if (true) {
			throw new Exception("Test Exception!");
		}

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

	@RunAlways
	@Third(id = "Third#1", second = "Second#2", order = 1)
	public void thpA() {
		log.info("Third[ID=Third#1, secondRef=null, order=1]");
	}

	@Third(id = "Third#2", second = "Second#2", order = 2, runAlways = "true")
	public void thpB() {
		log.info("Third[ID=Third#2, secondRef=null, order=2]");
	}

	@RunAlways
	@Third(id = "Third#3", second = "Second#2", order = 1.1, runAlways = "false") // override method @RunAlways
	public void thpC() {
		log.info("Third[ID=Third#3, secondRef=null, order=3]");
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
