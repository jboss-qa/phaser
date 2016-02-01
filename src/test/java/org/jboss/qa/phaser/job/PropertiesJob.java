package org.jboss.qa.phaser.job;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jboss.qa.phaser.BeforeJob;
import org.jboss.qa.phaser.context.Context;
import org.jboss.qa.phaser.context.Property;
import org.jboss.qa.phaser.phase.main.Main;
import org.jboss.qa.phaser.phase.main.MainWrapper;
import org.jboss.qa.phaser.phase.second.Second;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@MainWrapper({
		@Main(id = "Main#1")
})
public class PropertiesJob {

	@Property("property1")
	private String property1;

	@Property("url")
	private URL url;

	@Property("property2")
	private Double double2;

	@Property("property3")
	private List<String> list;

	@Property("property4")
	private List<Integer> listInteger;

	@Property("new_prop")
	private String newProperty = "default_value";

	@BeforeJob
	public void beforeJobA() throws MalformedURLException {
		log.info("BEFORE JOB #1");
		assertEquals(property1, "String");
		assertEquals(double2, 10.0);
		assertEquals(url, new URL("https://github.com/jboss-soa-qa/phaser"));
		assertEquals(newProperty, "default_value");

		assertThat(list, hasItems(is("aa"), is("bb"), is("cc")));
		assertThat(listInteger, hasItems(is(1), is(2), is(3)));
	}

	@Second(id = "Second#2", order = 2)
	public void scpB(@Property("url") URL url, Context ctx) throws MalformedURLException {
		log.info("Second[ID=Second#2, mainRef=null, order=2]");
		log.info("URL property: {}", url);
		log.info("Context object: {}", ctx);

		assertEquals(url, new URL("https://github.com/jboss-soa-qa/phaser"));
		assertNotNull(ctx);
	}
}
