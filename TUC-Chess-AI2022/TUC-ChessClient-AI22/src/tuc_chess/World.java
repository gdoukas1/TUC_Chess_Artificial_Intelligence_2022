package tuc_chess;

import minimax_montecarlo.Minimax;
import minimax_montecarlo.MonteCarloTreeSearch;

import java.util.ArrayList;
import java.util.Random;


public class World
{
	private String[][] board = null;
	private int rows = 7;
	private int columns = 5;
	private int myColor = 0;
	private ArrayList<String> availableMoves = null;
	private int rookBlocks = 3;		// rook can move towards <rookBlocks> blocks in any vertical or horizontal direction
	private int nTurns = 0;
	private int nBranches = 0;
	private int noPrize = 9;

	private int scoreWhite = 0;
	private int scoreBlack = 0;
	private boolean whiteKingIsUp = false;
	private boolean blackKingIsUp = false;

	private int maxDepth = 5;    //Max Depth of minimax tree
	private boolean pruning = true;  // Alpha Beta pruning on/off
	private int endTime = 3;  // How long to calculate in secs
	private int iterations = 1000000; // change if you want to run monte carlo for exact number of iterations
	private int option = 0;   // option=0 -> Minimax, option=1 -> MonteCarlo, option=? -> Random move


	public World()
	{
		board = new String[rows][columns];
		
		/* represent the board
		
		BP|BR|BK|BR|BP
		BP|BP|BP|BP|BP
		--|--|--|--|--
		P |P |P |P |P 
		--|--|--|--|--
		WP|WP|WP|WP|WP
		WP|WR|WK|WR|WP
		*/
		
		// initialization of the board
		for(int i=0; i<rows; i++)
			for(int j=0; j<columns; j++)
				board[i][j] = " ";
		
		// setting the black player's chess parts
		
		// black pawns
		for(int j=0; j<columns; j++)
			board[1][j] = "BP";
		
		board[0][0] = "BP";
		board[0][columns-1] = "BP";
		
		// black rooks
		board[0][1] = "BR";
		board[0][columns-2] = "BR";
		
		// black king
		board[0][columns/2] = "BK";
		
		// setting the white player's chess parts
		
		// white pawns
		for(int j=0; j<columns; j++)
			board[rows-2][j] = "WP";
		
		board[rows-1][0] = "WP";
		board[rows-1][columns-1] = "WP";
		
		// white rooks
		board[rows-1][1] = "WR";
		board[rows-1][columns-2] = "WR";
		
		// white king
		board[rows-1][columns/2] = "WK";
		
		// setting the prizes
		for(int j=0; j<columns; j++)
			board[rows/2][j] = "P";
		
		availableMoves = new ArrayList<String>();
	}
	
	public void setMyColor(int myColor)
	{
		this.myColor = myColor;
	}


	public String[][] getBoard() {
		return board;
	}

	public void setBoard(String[][] board) {
		this.board = board;
	}

	public int getScoreWhite() {
		return scoreWhite;
	}

	public void setScoreWhite(int scoreWhite) {
		this.scoreWhite = scoreWhite;
	}

	public int getScoreBlack() {
		return scoreBlack;
	}

	public void setScoreBlack(int scoreBlack) {
		this.scoreBlack = scoreBlack;
	}

	public int getMyColor() {
		return myColor;
	}

	public ArrayList<String> getAvailableMoves(){
		availableMoves = new ArrayList<String>();

		if(myColor == 0)		// I am the white player
			this.whiteMoves();
		else					// I am the black player
			this.blackMoves();

		// keeping track of the branch factor
		nTurns++;
		nBranches += availableMoves.size();
		return availableMoves;
	}

	public String selectAction()
	{
		if(option == 0) {       // Minimax
			this.getAvailableMoves(); // keeping track of the branch factor
			Minimax minimax = new Minimax(myColor, maxDepth, pruning);
			return minimax.alphaBeta(this);
		}
		else if (option == 1) { // Monte Carlo Tree Search
			this.getAvailableMoves(); // keeping track of the branch factor
			MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(myColor, endTime, iterations);
			return mcts.findNextMove(this);
		}
		else            // Random move
			return this.selectRandomAction();
	}
	
	public void whiteMoves()
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";
				
		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(board[i][j].charAt(0));
				
				// if it there is not a white chess part in this position then keep on searching
				if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;
				
				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));
				
				if(secondLetter.equals("P"))	// it is a pawn
				{
					
					// check if it can move one vertical position ahead
					firstLetter = Character.toString(board[i-1][j].charAt(0));
					
					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j);
						
						availableMoves.add(move);
					}
					
					// check if it can move crosswise to the left
					if(j!=0 && i!=0)
					{
						firstLetter = Character.toString(board[i-1][j-1].charAt(0));						
						if(!(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) + 
									   Integer.toString(i-1) + Integer.toString(j-1);
								
							availableMoves.add(move);
						}											
					}
					
					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=0)
					{
						firstLetter = Character.toString(board[i-1][j+1].charAt(0));
						if(!(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							
							move = Integer.toString(i) + Integer.toString(j) + 
									   Integer.toString(i-1) + Integer.toString(j+1);							
							availableMoves.add(move);
						}
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;
						
						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j-(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;
						
						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j+(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(board[i-1][j].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i-1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(board[i+1][j].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i+1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(board[i][j-1].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j-1);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(board[i][j+1].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j+1);
								
							availableMoves.add(move);	
						}
					}
				}			
			}	
		}
	}
	
	public void blackMoves()
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";
				
		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(board[i][j].charAt(0));
				
				// if it there is not a black chess part in this position then keep on searching
				if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;
				
				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));
				
				if(secondLetter.equals("P"))	// it is a pawn
				{
					
					// check if it can move one vertical position ahead
					firstLetter = Character.toString(board[i+1][j].charAt(0));
					
					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j);
						
						availableMoves.add(move);
					}
					
					// check if it can move crosswise to the left
					if(j!=0 && i!=rows-1)
					{
						firstLetter = Character.toString(board[i+1][j-1].charAt(0));
						
						if(!(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) + 
									   Integer.toString(i+1) + Integer.toString(j-1);
								
							availableMoves.add(move);
						}																	
					}
					
					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=rows-1)
					{
						firstLetter = Character.toString(board[i+1][j+1].charAt(0));
						
						if(!(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) + 
									   Integer.toString(i+1) + Integer.toString(j+1);
								
							availableMoves.add(move);
						}
							
						
						
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;
						
						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j-(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;
						
						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j+(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(board[i-1][j].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i-1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(board[i+1][j].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i+1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(board[i][j-1].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j-1);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(board[i][j+1].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j+1);
								
							availableMoves.add(move);	
						}
					}
				}			
			}	
		}
	}
	
	private String selectRandomAction()
	{
		this.getAvailableMoves();

		Random ran = new Random();
		int x = ran.nextInt(availableMoves.size());
		
		return availableMoves.get(x);
	}
	
	public double getAvgBFactor()
	{
		return nBranches / (double) nTurns;
	}
	
	public void makeMove(int x1, int y1, int x2, int y2, int prizeX, int prizeY, int scWhite, int scBlack)
	{
		scoreWhite = scWhite;
		scoreBlack = scBlack;

		String chesspart = Character.toString(board[x1][y1].charAt(1));
		
		boolean pawnLastRow = false;
		
		// check if it is a move that has made a move to the last line
		if(chesspart.equals("P"))
			if( (x1==rows-2 && x2==rows-1) || (x1==1 && x2==0) )
			{
				board[x2][y2] = " ";	// in a case an opponent's chess part has just been captured
				board[x1][y1] = " ";
				pawnLastRow = true;
			}
		
		// otherwise
		if(!pawnLastRow)
		{
			board[x2][y2] = board[x1][y1];
			board[x1][y1] = " ";
		}
		
		// check if a prize has been added in the game
		if(prizeX != noPrize)
			board[prizeX][prizeY] = "P";
	}


	public void performMove(String move)
	{
		int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
		int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
		int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
		int y2 = Integer.parseInt(Character.toString(move.charAt(3)));
		
		String player = Character.toString(board[x1][y1].charAt(0));
		String chesspart = Character.toString(board[x1][y1].charAt(1));
		String target;

		if (Character.toString(board[x2][y2].charAt(0)).equals(" "))
			target = " ";
		else if (Character.toString(board[x2][y2].charAt(0)).equals("P"))
			target = "P";
		else
			target = Character.toString(board[x2][y2].charAt(1));

		if (player.equals("W")){
			switch (target) {
				case "P": // Pawn or Present
					scoreWhite += 1;
					break;
				case "R": // Rook
					scoreWhite += 3;
					break;
				case "K": // King
					scoreWhite += 8;
					break;
			}
		}
		else {
			switch (target) {
				case "P": // Pawn or Present
					scoreBlack += 1;
					break;
				case "R": // Rook
					scoreBlack += 3;
					break;
				case "K": // King
					scoreBlack += 8;
					break;
			}
		}

		boolean pawnLastRow = false;

		// check if it is a move that has made a move to the last line
		if(chesspart.equals("P"))
			if( (x1==rows-2 && x2==rows-1) || (x1==1 && x2==0) )
			{
				board[x2][y2] = " ";	// in a case an opponent's chess part has just been captured
				board[x1][y1] = " ";
				pawnLastRow = true;
			}

		// otherwise
		if(!pawnLastRow)
		{
			board[x2][y2] = board[x1][y1];
			board[x1][y1] = " ";
		}

	}


	public boolean terminalTest(){

		boolean termination = false;
		boolean whitePieceLeft = false;
		boolean blackPieceLeft = false;
		whiteKingIsUp = false;
		blackKingIsUp = false;

		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				String firstLetter = Character.toString(board[i][j].charAt(0));

				if(firstLetter.equals("W")){
					String secondLetter = Character.toString(board[i][j].charAt(1));
					if(secondLetter.equals("K"))
						whiteKingIsUp = true;
					else
						whitePieceLeft = true;
				}
				if (firstLetter.equals("B")){
					String secondLetter = Character.toString(board[i][j].charAt(1));
					if(secondLetter.equals("K"))
						blackKingIsUp = true;
					else
						blackPieceLeft = true;
				}
			}
		}

		if (!whiteKingIsUp || !blackKingIsUp){
			termination = true;
		}

		if ((whiteKingIsUp && blackKingIsUp) && !(whitePieceLeft || blackPieceLeft)){   // Draw
			termination = true;
		}

		return termination;
	}

	public int evaluate(int player) {
		int value = 0;
		int whitePieces = 0;
		int blackPieces = 0;
		String whiteKingAt = "";
		String blackKingAt = "";

		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				String first = Character.toString(board[i][j].charAt(0));
				if(first.equals("W")){
					if(Character.toString(board[i][j].charAt(1)).equals("R"))
						whitePieces += 10;
					else if ((Character.toString(board[i][j].charAt(1)).equals("K")))
						whiteKingAt = Integer.toString(i) + Integer.toString(j);
					else
						whitePieces += 1;
				}
				else if(first.equals("B")){
					if(Character.toString(board[i][j].charAt(1)).equals("R"))
						blackPieces += 10;
					else if ((Character.toString(board[i][j].charAt(1)).equals("K")))
						blackKingAt = Integer.toString(i) + Integer.toString(j);
					else
						blackPieces += 1;
				}
			}
		}

		if(player == 0)
		{
			value = scoreWhite + whitePieces - scoreBlack - blackPieces;
			if(!blackKingIsUp && scoreWhite<scoreBlack)
				value -= 20;
			if(whiteKingIsUp) {
				if (isKingChecked(0, whiteKingAt))
					value -= 20;
			}
		}
		else
		{
			value = scoreBlack + blackPieces - scoreWhite - whitePieces;
			if(!whiteKingIsUp && scoreBlack<scoreWhite)
				value -= 20;
			if(blackKingIsUp){
				if(isKingChecked(1, blackKingAt))
					value -= 20;
			}
		}
		return value;
	}

	public boolean isKingChecked(int player, String position){
		int targetX = Integer.parseInt(Character.toString(position.charAt(0)));
		int targetY = Integer.parseInt(Character.toString(position.charAt(1)));
		boolean check = false;

		if (player == 0){
			setMyColor(1); //opponent
			getAvailableMoves();
			for (String move : availableMoves){
				int x1 = Integer.parseInt(Character.toString(move.charAt(2)));
				int y1 = Integer.parseInt(Character.toString(move.charAt(3)));
				if (x1 == targetX && y1 == targetY){
					check = true;
					break;
				}
			}
			setMyColor(0);
		}
		else {
			setMyColor(0);
			getAvailableMoves();
			for (String move : availableMoves){
				int x1 = Integer.parseInt(Character.toString(move.charAt(2)));
				int y1 = Integer.parseInt(Character.toString(move.charAt(3)));
				if (x1 == targetX && y1 == targetY){
					check = true;
					break;
				}
			}
			setMyColor(1);
		}
		return check;
	}

	public static String[][] cloneArray2D(String[][] src) {
		int length = src.length;
		String[][] target = new String[length][src[0].length];
		for (int i = 0; i < length; i++) {
			target[i] = src[i].clone();
		}
		return target;
	}

}

