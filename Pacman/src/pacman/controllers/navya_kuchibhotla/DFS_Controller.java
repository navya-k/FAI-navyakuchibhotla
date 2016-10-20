/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.controllers.navya_kuchibhotla; 
import java.util.Stack; 
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants; 
import pacman.game.Constants.MOVE;
import pacman.game.Game; 

/**
 *
 * @author Navya
 * Reference taken from example code provided by Amy Hoover
 */
public class DFS_Controller extends Controller<MOVE>{

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

		Stack<PacManNode> stackOfNodes = new Stack<PacManNode>();
		stackOfNodes.push(gameState);

		// while stack is not empty, find new nodes resulting from operations on the node at the top of the stack
		while(!stackOfNodes.isEmpty())
		{
			// Pop the stack and replace with list of child nodes
			PacManNode currentNode = stackOfNodes.pop(); 

			if(currentNode.depth >= maxdepth) 
			{
				int score = currentNode.gameState.getScore();
				if (highScore < score)
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
					stackOfNodes.push(node);
				}
			}
		}
		return highScore;
	}
}