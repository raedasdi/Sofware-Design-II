/*
 * CRITTERS Critter2.java
 * EE422C Project 4 submission by
 * <Alina Nguyen> 
 * <amn2763>
 * <16300>
 * <Raed Asdi>
 * <raa3426>
 * <16300>
 * Slip days used: <0>
 * Spring 2020
 */

/*
 * Critter2 - done by Raed Asdi
 * Runner Critter, always running, will even
 * run from fights, only eats clover and
 * will run from all fights if it is not
 * a clover. It uses the default runStats
 * from Critter.java
 */
package assignment4;

import java.util.List;

import assignment4.Critter.TestCritter;

public class Critter2 extends TestCritter {

	private static final int GENE_TOTAL = 24;
	private int[] genes = new int[8];
	private int dir;
	
	public Critter2() {
		for (int k = 0; k < 8; k += 1) {
			genes[k] = GENE_TOTAL / 8;
		}
		dir = Critter.getRandomInt(8);
	}
	
	@Override
	public void doTimeStep() {
		run(dir);
		
		int reproduce = Critter.getRandomInt(1);
		
		//try to reproduce only if reproduce flag is set off
		if (reproduce == 1) {
			Critter2 child = new Critter2();
			for (int k = 0; k < 8; k += 1) {
				child.genes[k] = this.genes[k];
			}
			int g = Critter.getRandomInt(8);
			while (child.genes[g] == 0) {
				g = Critter.getRandomInt(8);
			}
			child.genes[g] -= 1;
			g = Critter.getRandomInt(8);
			child.genes[g] += 1;
			reproduce(child, Critter.getRandomInt(8));
		}

		/* pick a new direction based on our genes */
		int roll = Critter.getRandomInt(GENE_TOTAL);
		int turn = 0;
		while (genes[turn] <= roll) {
			roll = roll - genes[turn];
			turn++;
		}
		assert (turn < 8);

		dir = (dir + turn) % 8;
	}

	@Override
	public boolean fight(String opponent) {
		if(opponent.equals("@")) return true;
		else {
			run(dir);
			return false;
		}
	}
	
	public String toString() {
		return "2";
	}
	
}
