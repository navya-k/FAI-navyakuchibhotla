package pacman.controllers.navya_kuchibhotla;

import pacman.controllers.Controller;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import pacman.game.Game;

import java.util.ArrayList;

import static pacman.game.Constants.*;

public class K_Nearest_Controller extends Controller<MOVE>{
	 
	private int c_pill; 
	private int c_pill_dist;
	private int[] c_pill_path;
	private int ghostInPath;
	private static int K = 5;
	private static int[][] Training_Data = tokenizeTrainingData(); 

	// Read from the training data file and tokenize the info in a 2D array
	// Stores closest pill and whether a ghost exists in the path to the pill or not.
	// also stores the choice made for each tuple.
	private static int[][] tokenizeTrainingData()
	{
		String s="";
		int c; 
		// Take input from the file
		try{
		FileInputStream fs= new FileInputStream("Training_Data.txt");
		while((c=fs.read())!=-1)
			s+=Character.toString((char)c);
		}
		catch (IOException ex){
			System.out.println("Exception while reading file: " + ex);
		}

		// Tokenize Input
		StringTokenizer strTokens=new StringTokenizer(s,"|\n\r",false);

		// count number of requests
		int numberOfTuples=(strTokens.countTokens())/3;
		System.out.println("no of req " + numberOfTuples);
		// define array sizes
		int[][] tempArray=new int[numberOfTuples][3]; 

		//store input in 2D request array
		for(int i=0;i<numberOfTuples;i++)
		{
			for(int j=0;j<3;j++)
			{
				if(strTokens.hasMoreTokens())
					tempArray[i][j]=Integer.parseInt(strTokens.nextToken());
			}
		} 
		return tempArray;

	}
	
	public MOVE getMove(Game game, long timeDue){


		int node = game.getPacmanCurrentNodeIndex();   
		System.out.println("instances  is " + Training_Data);
		int choice = finalChoice(game);

		if (choice == 1)
			return game.getNextMoveTowardsTarget(node, c_pill, DM.PATH);
		else return game.getNextMoveAwayFromTarget(node, c_pill , DM.PATH);
		 
			
	}

	// evaluate choice from K = 5 nearest neighbors
	private int finalChoice(Game game) {
		analyzeState(game);
		System.out.println("closest pill dist is " + c_pill_dist);
		int[] closestTuples = new int[K];
		int[] indexes = new int[K];
		int i;
 
		for (i = 0; i < K; i++)
			closestTuples[i] = Integer.MAX_VALUE;

		
		// find K closest tuples from Training Data
		int difference;
		for (i = 0; i < Training_Data.length; i++) {
			difference = (int)  (Training_Data[i][0] - c_pill_dist); 

			System.out.println("dist at " + i + " is " + difference);
			if (difference <= closestTuples[0]) {
				System.out.println("replace distances 0 " + i + " with " + difference);
				closestTuples[4] = closestTuples[3];
				closestTuples[3] = closestTuples[2];
				closestTuples[2] = closestTuples[1];
				closestTuples[1] = closestTuples[0];
				closestTuples[0] = difference; 
				indexes[2] = indexes[1];
				indexes[1] = indexes[0];
				indexes[0] = i;
			} else if (difference <= closestTuples[1]) {
				System.out.println("replace distances 1 " + i + " with " + difference);
				closestTuples[4] = closestTuples[3];
				closestTuples[3] = closestTuples[2];
				closestTuples[2] = closestTuples[1];
				closestTuples[1] = difference; 
				indexes[2] = indexes[1];
				indexes[1] = i;
			} else if (difference <= closestTuples[2]) {
				System.out.println("replace distances 2 " + i + " with " + difference);
				closestTuples[4] = closestTuples[3];
				closestTuples[3] = closestTuples[2];
				closestTuples[2] = difference; 
				indexes[2] = i;
			}
			else if (difference <= closestTuples[3]) {
				System.out.println("replace distances 3 " + i + " with " + difference);
				closestTuples[4] = closestTuples[3];
				closestTuples[3] =  difference; 
				indexes[3] = i;
			}
			else if (difference <= closestTuples[4]) {
				System.out.println("replace distances 4 " + i + " with " + difference);
				closestTuples[4] =  difference;
				indexes[4] = i;
			}
		}

		int sum = 0;
		for (i = 0; i < K; i++) {
			sum += Training_Data[indexes[i]][2];
		}
		System.out.println("sum is " + sum + "and return value is " + sum/K);
		return Math.round(sum / K);
	}
 
	private void analyzeState(Game game) { 

		int current = game.getPacmanCurrentNodeIndex(); 
		int[] pills = game.getActivePillsIndices();
		int[] powerPills = game.getActivePowerPillsIndices(); 
		
		int[] targetsArray = new int[pills.length + powerPills.length];
		System.arraycopy(pills, 0, targetsArray, 0, pills.length);
		System.arraycopy(powerPills, 0, targetsArray, pills.length, powerPills.length); 
		
		c_pill = game.getClosestNodeIndexFromNodeIndex(current, targetsArray, DM.PATH);
		c_pill_path = game.getShortestPath (current, c_pill); 
		c_pill_dist = game.getShortestPathDistance(current, c_pill);

		ghostInPath = ghostFoundInPath(game,game.getShortestPath(current, c_pill));
	}

	private int ghostFoundInPath(Game game, int[] path) {
		int ghostFound = 0; 
		for (GHOST ghost : GHOST.values()) {
			int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
			for(int i= 0; i < path.length; i++) {
				if(path[i] == ghostIndex){
					ghostFound = 1;
					break;
				}
			}
		}
		return ghostFound;	
	}
}
