package org.jboss.qa.phaser.point;

public class Counter {

	private int counter = 0;

	public void add() {
		counter += 1;
	}

	public int getValue() {
		return counter;
	}
}
