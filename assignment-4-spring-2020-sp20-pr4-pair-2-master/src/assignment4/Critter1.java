/*
 * CRITTERS Critter1.java
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
 * Critter1 - done by Raed Asdi
 * Smart critter which will only reproduce if
 * it has enough energy and wants to reproduce
 * also will only move if it has enough energy.
 * Stats will give the number of Critter1s that
 * are able to reproduce at a given time as well
 * as those that are "endangered" or have low energy
 */
package assignment4;

import java.util.List;

import assignment4.Critter.TestCritter;

public class Critter1 extends TestCritter {

	
	private static final int GENE_TOTAL = 24;
	private int[] genes = new int[8];
	private int dir;
	
	public Critter1() {
		for (int k = 0; k < 8; k += 1) {
			genes[k] = GENE_TOTAL / 8;
		}
		dir = Critter.getRandomInt(8);
	}
	
	@Override
	public void doTimeStep() {
		//will only move if it has the energy
		if(getEnergy() > Params.RUN_ENERGY_COST) {
			run(dir);
		} else if(getEnergy() > Params.WALK_ENERGY_COST) {
			walk(dir);
		}
		
		int reproduce = Critter.getRandomInt(1);
		
		//try to reproduce only if has enough energy and reproduce flag is set off
		if (getEnergy() > Params.MIN_REPRODUCE_ENERGY && reproduce == 1) {
			Critter1 child = new Critter1();
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
			turn = turn + 1;
		}
		assert (turn < 8);

		dir = (dir + turn) % 8;
	}

	@Override
	public boolean fight(String opponent) {
		if(this.getEnergy() > 15) return true;
		else {
			run(dir);
			return false;
		}
	}
	
	public String toString() {
		return "1";
	}
	
	public static void runStats(List<Critter> critter1s) {
		int reproduceAble = 0;
		int endangered = 0;
		for (Object obj : critter1s) {
			Critter1 c = (Critter1) obj;
			if(c.getEnergy() >= Params.MIN_REPRODUCE_ENERGY) reproduceAble++;
			if(c.getEnergy() <= Params.RUN_ENERGY_COST) endangered++;
		}
		System.out.print(critter1s.size() + " total Critter1s    ");
		System.out.print(reproduceAble / (0.01 * critter1s.size()) + "% able to repproduce   ");
		System.out.print(endangered/ (0.01 * critter1s.size()) + "% in danger of dying   ");
		System.out.println();
	}
}

