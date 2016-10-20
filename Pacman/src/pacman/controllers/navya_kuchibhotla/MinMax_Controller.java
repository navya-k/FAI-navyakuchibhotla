package pacman.controllers.navya_kuchibhotla;



import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

/**
*
* @author Navya
*/
public class MinMax_Controller extends Controller<MOVE>
{

	/*public enum MOVE 
	{
		UP      { public MOVE opposite(){return MOVE.DOWN;		};},	
		RIGHT 	{ public MOVE opposite(){return MOVE.LEFT;		};}, 	
		DOWN 	{ public MOVE opposite(){return MOVE.UP;		};},		
		LEFT 	{ public MOVE opposite(){return MOVE.RIGHT;		};}, 	
		NEUTRAL	{ public MOVE opposite(){return MOVE.NEUTRAL;	};};	
	
		public abstract MOVE opposite();
	};*/

	/**
	 * Returns the best move given the state of the game
	 */
	public int MAX_DEPTH = 8;
	public static StarterGhosts ghosts = new StarterGhosts();
	
	public MOVE getMove(Game gameState,long timeDue)
	{
		int highestScore = Integer.MIN_VALUE;
		MOVE bestMove = null;
		int depth = 0;
		MOVE[] availableMoves = gameState.getPossibleMoves(gameState.getPacmanCurrentNodeIndex());
		int score = 0;
		for (MOVE move : availableMoves)
		{
			Game gameCopy = gameState.copy();
			gameCopy.advanceGame(move, ghosts.getMove(gameCopy, timeDue));
			score = min(gameCopy, timeDue, depth);
			if (score > highestScore) {
				highestScore = score;
				bestMove = move;
			}
		}
		depth++;
	
		return bestMove;
	}
	
	 // min player move and score
	 
	private int min(Game state,long timeDue,int  depth) {
		
		if (depth == MAX_DEPTH)
			return eval(state);
		
		depth++;
		int lowestScore = Integer.MAX_VALUE;
		int score = 0;
		
		MOVE[] availableMoves  = state.getPossibleMoves(state.getPacmanCurrentNodeIndex());
		
		for (MOVE move : availableMoves) {
			
			Game stateCopy = state.copy();
			stateCopy.advanceGame(move, ghosts.getMove(stateCopy, timeDue));
			score = max(stateCopy, timeDue, depth);
			
			if (score < lowestScore)
				lowestScore = score;
		}
		return lowestScore;
	}
	
	// min player move and score
	
	private int max(Game state,long timeDue,int depth) {
		if (depth == MAX_DEPTH)
			return eval(state);
	
		int highestScore = Integer.MIN_VALUE;
		int score = 0;
		
		depth++;
		MOVE[] moves = state.getPossibleMoves(state.getPacmanCurrentNodeIndex());
		
		for (MOVE move : moves) {
			
			Game stateCopy = state.copy();
			stateCopy.advanceGame(move, ghosts.getMove(stateCopy, timeDue));
			score = min(stateCopy,timeDue, depth);
			
			if (score > highestScore)
				highestScore = score;
		}
	
		return highestScore;
	}
	
	/**
	 * Returns -1 if game is over or else 1
	 */
	private int eval(Game state) {
		if (state.gameOver())
			return -1;
		else return 1;
	} 
}
