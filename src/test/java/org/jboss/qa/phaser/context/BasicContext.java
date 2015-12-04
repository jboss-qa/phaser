package org.jboss.qa.phaser.context;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicContext implements Context {

	private Map<String, Object> context = new HashMap<>();

	public void set(String key, Object o) {
		context.put(key, o);
	}

	public void set(String key, Object value, Class<?> type) {
		set(key, value);
	}

	public <T> T get(String key, Class<T> type) {
		return get(key, null, type);
	}

	public <T> T get(String key, T value, Class<T> type) {
		return (T) context.get(key);
	}
}
