package pacman.controllers.navya_kuchibhotla;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.DM;
import java.util.ArrayList;

public class Genetic_Algorithm_Controller extends Controller<MOVE> {

	/*public enum MOVE 
	{
		UP      { public MOVE opposite(){return MOVE.DOWN;		};},	
		RIGHT 	{ public MOVE opposite(){return MOVE.LEFT;		};}, 	
		DOWN 	{ public MOVE opposite(){return MOVE.UP;		};},		
		LEFT 	{ public MOVE opposite(){return MOVE.RIGHT;		};}, 	
		NEUTRAL	{ public MOVE opposite(){return MOVE.NEUTRAL;	};};	

		public abstract MOVE opposite();
	};*/

	public static StarterGhosts ghosts = new StarterGhosts();
	// initializing the first set of population, starting from all the current possible moves
	MOVE[] population;
	public MOVE getMove(Game gameState,long timeDue)
	{
		population = gameState.getPossibleMoves(gameState.getPacmanCurrentNodeIndex());
		MOVE bestMove = null;
		int highestScore = -1;
		int score = 0;
		for (MOVE move : population)
		{
			Game gameCopy = gameState.copy();
			gameCopy.advanceGame(move, ghosts.getMove(gameCopy, timeDue));
			population = gAFunction(gameCopy, timeDue);
			for (MOVE m : population)
			{
				score = fitnessFunction(gameState,timeDue,m);
				if (score > highestScore) {
					highestScore = score;
					bestMove = move;
				}
			}	
		}

		return bestMove;
	}

	private MOVE[] gAFunction(Game state,long timeDue) {

		MOVE[] newPopulation = population;
		MOVE parent1 = selectParent(state,timeDue,population);
		MOVE parent2 = selectParent(state,timeDue,removeIndividual(population,parent1));

		newPopulation = crossover(state,timeDue,parent1, parent2,population.length);
		return newPopulation;
	}

	private MOVE[] crossover(Game state,long timeDue, MOVE parent1, MOVE parent2, int populationSize) {

		MOVE[] newPopulationSet1, newPopulationSet2;

		Game gameCopy1 = state.copy();
		gameCopy1.advanceGame(parent1, ghosts.getMove(gameCopy1, timeDue));
		newPopulationSet1 = gameCopy1.getPossibleMoves(gameCopy1.getPacmanCurrentNodeIndex());

		Game gameCopy2 = state.copy();
		gameCopy2.advanceGame(parent2, ghosts.getMove(gameCopy2, timeDue));
		newPopulationSet2 = gameCopy2.getPossibleMoves(gameCopy2.getPacmanCurrentNodeIndex());

		MOVE[] newPopulation = merge(newPopulationSet1, newPopulationSet2);
		return elite(state,timeDue,newPopulation, populationSize );
	}

	private MOVE selectParent(Game state,long timeDue,MOVE[] population) {
		MOVE individual = null;
		int highestScore = -1;
		int score = 0;
		for (MOVE m : population)
		{
			score = fitnessFunction(state,timeDue,m);
			if (score > highestScore) {
				highestScore = score;
				individual = m;
			}
		}
		return individual;
	}

	private MOVE[] removeIndividual(MOVE[] population,MOVE move) {

		MOVE[] truncatedPopulation = new MOVE[0];
		int j = 0;
		for (int i = 0; i < population.length; i++)
		{
			MOVE m = population[i];
			if (m != move) {
				truncatedPopulation[j++] = m;
			}
		}
		return truncatedPopulation;
	}

	private MOVE[] merge(MOVE[] set1,MOVE[] set2) {

		MOVE[] increasedPopulation = set1;
		int j = set1.length;
		for (int i = 0 ; i <  set2.length; i++)
		{
			increasedPopulation[j++] = set2[i];
		}
		return increasedPopulation;
	}

	private MOVE[] elite(Game state,long timeDue,MOVE[] population,int size) {

		MOVE[] elitePopulation = null;
		MOVE[] sortedPopulation = sortOnfitness(state,timeDue,population);
		for (int i = 0 ; i < size; i++)
		{
			elitePopulation[i] = sortedPopulation[i];
		}
		return population;
	}

	private MOVE[] sortOnfitness(Game state,long timeDue,MOVE[] population) {

		if (population == null || population.length == 0) {
			return population;
		}

		return  quickSort(state, timeDue, population, 0, population.length - 1);
	}

	private MOVE[] quickSort(Game state,long timeDue,MOVE[] array, int lowerIndex, int higherIndex) {

		int i = lowerIndex;
		int j = higherIndex;
		// calculate pivot number, I am taking pivot as middle index number
		MOVE pivot = array[lowerIndex+(higherIndex-lowerIndex)/2];
		// Divide into two arrays
		while (i <= j) {

			while (fitnessFunction(state,timeDue,array[i]) < fitnessFunction(state,timeDue,pivot)) {
				i++;
			}
			while (fitnessFunction(state,timeDue,array[i]) > fitnessFunction(state,timeDue,pivot)) {
				j--;
			}
			if (i <= j) {
				exchangeNumbers(array, i, j);
				//move index to next position on both sides
				i++;
				j--;
			}
		}
		// call quickSort() method recursively
		if (lowerIndex < j)
			quickSort(state, timeDue, array,lowerIndex, j);
		if (i < higherIndex)
			quickSort(state, timeDue, array,i, higherIndex);

		return array;
	}

	private void exchangeNumbers(MOVE[] array, int i, int j) {
		MOVE temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	private int fitnessFunction(Game state,long timeDue,MOVE m) {

		Game gameCopy = state.copy();
		gameCopy.advanceGame(m, ghosts.getMove(gameCopy, timeDue));
		int fitness = 0;
		int currentIndex = gameCopy.getPacmanCurrentNodeIndex();
		fitness = gameCopy.getClosestNodeIndexFromNodeIndex(currentIndex, gameCopy.getPowerPillIndices(),DM.PATH);
		return fitness;
	}
}
