package org.jboss.qa.phaser.job;

import org.jboss.qa.phaser.AfterJob;
import org.jboss.qa.phaser.BeforeJob;
import org.jboss.qa.phaser.InstanceRegistry;
import org.jboss.qa.phaser.RunAlways;
import org.jboss.qa.phaser.phase.main.Main;
import org.jboss.qa.phaser.phase.main.MainWrapper;
import org.jboss.qa.phaser.phase.third.Third;
import org.jboss.qa.phaser.point.InjectionPoint;
import org.jboss.qa.phaser.point.MyPoint;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@MainWrapper({
		@Main(id = "Main#1"),
		@Main(id = "Main#2", order = -1)
})
public class ParentJob {

	@BeforeJob
	public void beforeJob() {
		log.info("BEFORE JOB PARENT");

		InstanceRegistry.insert("IP1", new InjectionPoint("IP3"));
		InstanceRegistry.insert(new MyPoint("MyPoint Content"));
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
	@Third(id = "Third#3", order = 1.1, runAlways = "false") // override method @RunAlways
	public void thpC() {
		log.info("Third[ID=Third#3, secondRef=null, order=3]");
	}

	@AfterJob
	public void afterJob() {
		log.info("AFTER JOB PARENT");
	}
}
