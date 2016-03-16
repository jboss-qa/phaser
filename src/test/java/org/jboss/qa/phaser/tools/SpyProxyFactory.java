package org.jboss.qa.phaser.tools;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;

public class SpyProxyFactory {

	public static <T> T createProxy(Class<T> tClass, T mockInstance) throws IllegalAccessException, InstantiationException {
		return new ByteBuddy()
				.subclass(tClass)
				.method(isDeclaredBy(tClass))
				.intercept(MethodDelegation.to(mockInstance).andThen(SuperMethodCall.INSTANCE))
				.attribute(MethodAttributeAppender.ForInstrumentedMethod.INCLUDING_RECEIVER)
				.make()
				.load(tClass.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
				.getLoaded()
				.newInstance();
	}
}
