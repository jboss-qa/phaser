package org.jboss.qa.phaser.phase.main;

import org.jboss.qa.phaser.Id;
import org.jboss.qa.phaser.Order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Main {

	@Id
	public String id();

	@Order
	public int order() default 0;
}
