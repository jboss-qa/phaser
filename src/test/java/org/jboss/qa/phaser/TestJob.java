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
	public void scpA(InjectionPoint ipLocal, @Create MyPoint mpLocal) {
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

	@Third(id = "Third#1", second = "Second#2")
	public void thpA() {
		log.info("Third[ID=Third#1, secondRef=null, order=0]");
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
