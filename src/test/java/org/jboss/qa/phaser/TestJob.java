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

	@Second(id = "Second#1", main = "Main#1")
	public void scpA() {
		log.info("Second[ID=Second#1, mainRef=Main#1, order=0]");
	}

	@Second(id = "Second#2", order = -1)
	public void scpB() {
		log.info("Second[ID=Second#2, mainRef=null, order=-1]");
	}

	@Third(id = "Third#1", second = "Second#2")
	public void thpA() {
		log.info("Third[ID=Third#1, secondRef=null, order=0]");
	}
}
