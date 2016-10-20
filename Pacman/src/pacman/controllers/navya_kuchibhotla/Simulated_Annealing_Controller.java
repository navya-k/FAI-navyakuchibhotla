package pacman.controllers.navya_kuchibhotla;

import java.util.Queue;
import java.util.LinkedList;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

/**
*
* @author Navya
*/
public class Simulated_Annealing_Controller extends Controller<MOVE>{
	
	/*public enum MOVE 
	{
		UP      { public MOVE opposite(){return MOVE.DOWN;		};},	
		RIGHT 	{ public MOVE opposite(){return MOVE.LEFT;		};}, 	
		DOWN 	{ public MOVE opposite(){return MOVE.UP;		};},		
		LEFT 	{ public MOVE opposite(){return MOVE.RIGHT;		};}, 	
		NEUTRAL	{ public MOVE opposite(){return MOVE.NEUTRAL;	};};	

		public abstract MOVE opposite();
	};*/

	// Set a high initial temperature
    double temp = 49000;

    // Cooling rate
    double coolingRate = 0.0004;

	public static StarterGhosts ghosts = new StarterGhosts();
	public MOVE getMove(Game gameState,long timeDue)
	{
		MOVE[] availableMoves=MOVE.values();

		int highestScore = -1;
		MOVE highestMove = null;

		for(MOVE m: availableMoves)
		{
			Game copyOfgameState = gameState.copy();

			copyOfgameState.advanceGame(m, ghosts.getMove(copyOfgameState, timeDue));
			int tempScore = getScoreFromNode(new PacManNode(copyOfgameState, 0), 7);
						
			
			if(highestScore < tempScore)
			{
            	highestScore = tempScore;
            	highestMove = m;
           }
            System.out.println("Score after move : " + m + " is : " + tempScore);

		}

		System.out.println("Chosen score: " + highestScore + " and Chosen Move:" + highestMove);
		
		return highestMove;

	}

	public int getScoreFromNode(PacManNode gameState, int maxdepth)
	{
		MOVE[] allMoves=Constants.MOVE.values();

		int highScore = -1;

		Queue<PacManNode> queue = new LinkedList<PacManNode>();
		queue.add(gameState);
        
		// while stack is not empty, find new nodes resulting from operations on the node at the top of the stack
		while(!queue.isEmpty())
		{
			// Pop the stack and replace with list of child nodes
			PacManNode currentNode = queue.remove(); 

			if(currentNode.depth >= maxdepth) 
			{				
				int score = currentNode.gameState.getScore();
				double prob = getProbability(highScore, score, temp);
				// Decide if we should accept the neighbor
	            if (prob > Math.random())
	            	highScore = score;
			}
			else
			{
				// For each of the available moves, generate the resulting nodes from current node
				// push the new node to the top of the stack
				for(MOVE m: allMoves)
				{
					Game copyNode = currentNode.gameState.copy();
					copyNode.advanceGame(m, ghosts.getMove(copyNode, 0));
					PacManNode node = new PacManNode(copyNode, currentNode.depth+1);
					queue.add(node);
				}
			}
		}
		// reduce temperature
		temp *= 1-coolingRate;
		return highScore;
	}

	private double getProbability(int highScore, int score, double temperature) {
		
		// accept better solution
		if (score < highScore) {
			return 1.0;
		}
		// get probability of acceptance
		return Math.exp((highScore - score) / temperature);
	}
}