package org.jboss.qa.phaser.phase.main;

import org.jboss.qa.phaser.Id;
import org.jboss.qa.phaser.Order;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Main {

	@Id
	String id();

	@Order
	int order() default 0;
}
