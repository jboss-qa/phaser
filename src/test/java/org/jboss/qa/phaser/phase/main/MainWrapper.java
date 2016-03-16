package org.jboss.qa.phaser.phase.main;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MainWrapper {

	Main[] value();
}
