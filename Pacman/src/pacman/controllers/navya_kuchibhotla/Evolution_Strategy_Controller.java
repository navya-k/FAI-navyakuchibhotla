package pacman.controllers.navya_kuchibhotla;

import java.util.Random;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.DM;

public class Evolution_Strategy_Controller extends Controller<MOVE> {

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
				population = eSFunction(gameCopy, timeDue);
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

		private MOVE[] eSFunction(Game state,long timeDue) {
			
			MOVE[] newPopulation = null;
			int i = 0;
			for (MOVE m : population)
			{
				newPopulation[i] = mutate(state,timeDue,m);
				
			}
			return newPopulation;
		}

		private MOVE mutate(Game state,long timeDue, MOVE move) {

			MOVE[] newPopulationSet;

			Game gameCopy1 = state.copy();
			gameCopy1.advanceGame(move, ghosts.getMove(gameCopy1, timeDue));
			newPopulationSet = gameCopy1.getPossibleMoves(gameCopy1.getPacmanCurrentNodeIndex());
			MOVE individual = getRandom(newPopulationSet);
			return individual;

		} 
		
		public static MOVE getRandom(MOVE[] array) {
		    int rnd = new Random().nextInt(array.length);
		    return array[rnd];
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
