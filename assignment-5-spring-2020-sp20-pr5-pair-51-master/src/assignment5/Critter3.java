/*
 * CRITTERS Critter3.java
 * EE422C Project 5 submission by
 * <Alina Nguyen> 
 * <amn2763>
 * <16300>
 * <Raed Asdi>
 * <raa3426>
 * <16300>
 * Slip days used: <0>
 * Spring 2020
 */
package assignment5;

import java.util.List;

import assignment5.Critter.TestCritter;
import javafx.scene.paint.Color;

/**Made by Alina Nguyen
 * Eco-friendly Critter, tries to conserve energy as much as possible. Only walks when traveling and only fights if very strong.
 * If the critter is about to die, decides to reproduce with its last bits of energy and then continues walking.
 * stats tell the player the percentage of the Critter3's that are low on energy and will start to reproduce and those that are still 
 * only walking since they still have a decent amount of energy.
 */
public class Critter3 extends TestCritter {
	
	private static final int GENE_TOTAL = 24;
	private int[] genes = new int[8];
	private int dir;

	
	public Critter3() {
		for (int k = 0; k < 8; k += 1) {
			genes[k] = GENE_TOTAL / 8;
		}
		dir = Critter.getRandomInt(8);
	}
	
	@Override
	public void doTimeStep() {
		if (getEnergy() < 2*Params.MIN_REPRODUCE_ENERGY) {
			Critter3 child = new Critter3();
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
		walk(dir);
		
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
		if (getEnergy() > (Params.START_ENERGY/2)) {
			return true;
		}
		return false;
	}

	public String toString() {
		return "3";
	}

	@Override
	public CritterShape viewShape() {
		return CritterShape.DIAMOND;
	}

	@Override
	public javafx.scene.paint.Color viewOutlineColor() {
		return Color.BLACK;
	}

	@Override
	public javafx.scene.paint.Color viewFillColor() {
		return Color.DEEPPINK;
	}


	public static String runStats(List<Critter> critter3s) {
		int startReproduce = 0;
		int onlyWalk = 0;
		for (Object obj : critter3s) {
			Critter3 c = (Critter3) obj;
			if (c.getEnergy() < 2*Params.MIN_REPRODUCE_ENERGY) {
				startReproduce++;
			}
			else {
				onlyWalk ++;
			}
		}

		String output3 = (critter3s.size() + " total Critter3s    " + startReproduce / ( 0.01 * critter3s.size()) + "%  of Critter3's are going to start reproducing   " + onlyWalk / (0.01 * critter3s.size()) + "% still only walk   ");
		/*System.out.print(critter3s.size() + " total Critter3s    ");
		System.out.print(startReproduce / ( 0.01 * critter3s.size()) + "%  of Critter3's are going to start reproducing   ");
		System.out.print(onlyWalk / (0.01 * critter3s.size()) + "% still only walk   ");
		System.out.println();

		 */
		return output3;
	}

}
