package org.jboss.qa.phaser.phase.second;

import org.jboss.qa.phaser.Id;
import org.jboss.qa.phaser.Order;
import org.jboss.qa.phaser.ParentId;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Second {

	@Id
	String id() default "";

	@ParentId
	String main() default "";

	@Order
	int order() default 0;
}
