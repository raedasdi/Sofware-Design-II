package assignment4;

import java.util.List;

import assignment4.Critter.TestCritter;

/*
 * CRITTERS Critter4.java
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

/**
 * Made by Alina Nguyen
 * Family Protector Critter. Will try to reproduce whenever it can (each doTimeStep) and will fight to protect it's family.
 * Likes to keep the family on the move when no encounters, thus always runs every doTimeStep.
 * Will also always try to reproduce every time step in order to keep increasing it's family, thus reproducing if it has enough energy
 * and always placing its child in front of him to watch over and make sure it is safe.
 * This critter is also very protective, so it will always fight to protect its family.
 * Stats tell player what directions the Critter4s are going to go next step, so can tell where the families are travelling and expanding.
 * Gives percent of the Critter4s that are going forward (up dir# 1-3), backwards (down dir# 5-7), and straight left (dir# 4) and straight right (dir# 0)
 */
public class Critter4 extends TestCritter {

	private static final int GENE_TOTAL = 24;
	private int[] genes = new int[8];
	private int dir;
	
	public Critter4() {
		for (int k = 0; k < 8; k += 1) {
			genes[k] = GENE_TOTAL / 8;
		}
		dir = Critter.getRandomInt(8);
	}
	
	
	@Override
	public void doTimeStep() {
		
		run(dir);
		
		if (getEnergy() > Params.MIN_REPRODUCE_ENERGY) {//only produces if enough energy, likes to keep children in front of him 
			Critter4 child = new Critter4();
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
			reproduce(child, 2);
		}
		
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
		return true;
	}

	public String toString() {
		return "4";
	}

	public static void runStats(List<Critter> critter4s) {
		int total_forward = 0;
		int total_backwards = 0;
		int total_left = 0;
		int total_right = 0;
		for (Object obj : critter4s) {
			Critter4 c = (Critter4) obj;
			if (c.dir > 0 && c.dir < 4) {
				total_forward ++;
			}
			else if (c.dir == 4) {
				total_left++;
			}
			else if (c.dir > 4 && c.dir < 8) {
				total_backwards++;
			}
			else {//meaning the dir is 0 -> directly right
				total_right++;
			}
		}
		System.out.print(critter4s.size() + " total Critter4s    ");
		System.out.print(total_forward / (0.01 * critter4s.size()) + "% going forward   ");
		System.out.print(total_backwards / (0.01 * critter4s.size()) + "% going backwards   ");
		System.out.print(total_left / (0.01 * critter4s.size()) + "% going straight left   ");
		System.out.print(total_right / (0.01 * critter4s.size()) + "% going straight right   ");
		System.out.println();
	}
	
}
