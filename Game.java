import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import info.gridworld.actor.Actor;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public class Game {
	// Creates 8x8 board -- coloring is done in GridPanel paintComponent()
	public static void createBoard(MouseWorld world) {
		world.setGrid(new BoundedGrid<Actor>(8, 8));
	}

	// Creates checkers pieces
	public static void createPieces(MouseWorld world) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (x % 2 == 1 && y % 2 == 0 && x <= 2) {
					world.add(new Location(x, y), new Reg(Color.RED));
				} else if (x % 2 == 0 && y % 2 == 1 && x <= 2) {
					world.add(new Location(x, y), new Reg(Color.RED));
				} else if (x % 2 == 1 && y % 2 == 0 && x >= 5) {
					world.add(new Location(x, y), new Reg(Color.BLACK));
				} else if (x % 2 == 0 && y % 2 == 1 && x >= 5) {
					world.add(new Location(x, y), new Reg(Color.BLACK));
				}
			}
		}
	}

	// Runs main script for general play
	public static void start(MouseWorld world) {
		// Creates board, pieces, shows world
		createBoard(world);
		createPieces(world);
		world.show();
		world.getFrame().setSize(500, 500);

		Player PlayerRed = new Player(Color.RED);
		Player PlayerBlack = new Player(Color.BLACK);

		boolean redLose = false;
		boolean blackLose = false;

		Player currPlayer = PlayerBlack;
		world.setMessage(null);
		world.setMessage("Welcome to Checkers! Black goes first.");

		// Moves and keeps track of which player wins
		while (redLose == false && blackLose == false) {
			// Player moves
			act(world, currPlayer);

			// Promotes pieces to kings if they have reached opposite end
			promote(world);

			// Counts the pieces left by either player
			PlayerRed.count(world);
			PlayerBlack.count(world);

			// Checks if someone has lost. If yes, ends game.
			Color loser = checkWin(world, PlayerRed, PlayerBlack);
			if (loser != null) {
				if (loser == Color.RED) {
					redLose = true;
					end(world, PlayerRed);
				} else {
					blackLose = true;
					end(world, PlayerBlack);
				}
			}

			// Switches black to red or vice versa
			currPlayer = switchPlayer(world, currPlayer);
		}
	}

	// Declares winner from passed param Player p
	public static void end(MouseWorld world, Player loser) {
		String winner;
		if (loser.getColor() == Color.RED)
			winner = "Black Player";
		else
			winner = "Red Player";
		// Shows winner in JOP window. When window is closed, GUI also closes.
		JOptionPane.showMessageDialog(null, "Congratulations " + winner + "!");
		world.getFrame().dispose();
	}

	// Moves pieces and checks rules.
	public static void act(MouseWorld world, Player pl) {
		Grid<Actor> gr = world.getGrid();
		Location[] loc = new Location[] { null, null };
		boolean repeat = false;

		do {
			repeat = false;
			loc = go(world, pl, loc[1]);

			// If go() did not move the piece, it was an invalid move.
			while (gr.get(loc[0]) instanceof Piece) {
				world.setMessage(null);
				world.setMessage("Invalid. Try again.");
				loc = go(world, pl, null);
			}

			Piece movedPiece = (Piece) gr.get(loc[1]);

			// If new location is not within 1 square, it jumped (2 squares)
			ArrayList<Location> formerAdj = new ArrayList<Location>();
			for (int k = 0; k < 8; k++) {
				formerAdj.add(loc[0].getAdjacentLocation(k * 45));
			}

			if (formerAdj.indexOf(loc[1]) < 0)
				movedPiece.setJumped(true);

			// If piece jumped, then piece that was jumped over is removed
			if (movedPiece.getJumped() == true) {
				removeJumped(world, loc);
				movedPiece.checkLegalMoves(world, loc[1], pl);
				// If piece jumped and piece can jump, player goes again
				if (movedPiece.getCanJump()) {
					world.setMessage(null);
					world.setMessage("Jump again.");
					repeat = true;
				}
				movedPiece.setJumped(false);

			}
		} while (repeat);
	}

	// Player moves pieces. If the desired move is invalid, then it doesn't
	// move.
	public static Location[] go(MouseWorld world, Player pl, Location first) {
		Grid<Actor> gr = world.getGrid();
		Piece p;
		Location pieceLoc = first;
		// User clicks piece that will move
		if (pieceLoc == null) {
			pieceLoc = world.getLocationWhenClicked();
			ArrayList<Location> jumpingPieces = new ArrayList<Location>();
			world.resetClickedLocation();

			// Checks if any of the player's pieces can jump
			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {
					Location test = new Location(x, y);
					if (gr.get(test) != null) {
						((Piece) gr.get(test)).checkLegalMoves(world, test, pl);
						if (gr.get(test).getColor() == pl.getColor()) {
							if (((Piece) gr.get(test)).getCanJump()) {
								jumpingPieces.add(test);
							}
						}
					}
				}
			}

			while (gr.get(pieceLoc) == null) {
				// Repeat until user clicks an occupied location
				world.setMessage(null);
				world.setMessage("No piece here. Pick again.");
				pieceLoc = world.getLocationWhenClicked();
				world.resetClickedLocation();
			}

			while (jumpingPieces.size() > 0 && jumpingPieces.indexOf(pieceLoc) < 0) {
				// If any piece can jump, one of them must jump
				world.setMessage(null);
				world.setMessage("You have a piece that can jump. Pick again.");
				pieceLoc = world.getLocationWhenClicked();
				world.resetClickedLocation();
			}
		}

		// User clicks location to move piece to
		world.setMessage(null);
		world.setMessage("Pick location to move to.");
		Location newLoc = world.getLocationWhenClicked();
		world.resetClickedLocation();

		// Checks legality of move. If illegal, doesn't move.
		if (gr.get(pieceLoc) != null) {
			p = (Piece) gr.get(pieceLoc);
			ArrayList<Location> legalMoves = p.checkLegalMoves(world, pieceLoc, pl);
			for (int i = 0; i < legalMoves.size(); i++) {
				// If newLoc is in legalMoves, the piece moves to newLoc
				if (legalMoves.get(i).equals(newLoc))
					p.move(world, newLoc);
			}
		}
		// Returns the original location of piece and new location
		return new Location[] { pieceLoc, newLoc };
	}

	public static void removeJumped(MouseWorld world, Location[] loc) {
		Grid<Actor> gr = world.getGrid();

		// Removes jumped piece (in between old and new location)
		int x1 = loc[0].getRow();
		int y1 = loc[0].getCol();
		int x2 = loc[1].getRow();
		int y2 = loc[1].getCol();

		int reX = (x1 + x2) / 2;
		int reY = (y1 + y2) / 2;
		Location reLoc = new Location(reX, reY);
		gr.get(reLoc).removeSelfFromGrid();
	}

	public static void promote(MouseWorld world) {
		Grid<Actor> gr = world.getGrid();
		// If black piece is in red's final row, promotes to king
		for (int i = 0; i < 8; i++) {
			Location testLoc = new Location(0, i);
			if (gr.get(testLoc) instanceof Piece) {
				Piece pieceAt = (Piece) gr.get(testLoc);
				if (pieceAt.getColor() == Color.BLACK) {
					pieceAt.removeSelfFromGrid();
					world.add(testLoc, new King(Color.BLACK));
				}
			}
		}

		// If red piece is in black's final row, promotes to king
		for (int i = 0; i < 8; i++) {
			Location testLoc = new Location(7, i);
			if (gr.get(testLoc) instanceof Piece) {
				Piece pieceAt = (Piece) gr.get(testLoc);
				if (pieceAt.getColor() == Color.RED) {
					pieceAt.removeSelfFromGrid();
					world.add(testLoc, new King(Color.RED));
				}
			}
		}
	}

	public static Color checkWin(MouseWorld world, Player PlayerRed, Player PlayerBlack) {
		if (!(PlayerRed.getCount() > 0)) {
			// If there are no red pieces left, red has lost.
			return Color.RED;
		} else if (!(PlayerBlack.getCount() > 0)) {
			// If there are no black pieces left, black has lost.
			return Color.BLACK;
		} else {
			// If both have pieces, then no one has lost.
			return null;
		}
	}

	// If currently red, switches to black, and vice versa.
	public static Player switchPlayer(MouseWorld world, Player curr) {
		if (curr.getColor() == Color.RED) {
			world.setMessage(null);
			world.setMessage("Player Black. Click where you want to move.");
			return new Player((Color.BLACK));
		} else {
			world.setMessage(null);
			world.setMessage("Player Red. Click where you want to move.");
			return new Player((Color.RED));
		}
	}

	public static void main(String[] args) {
		MouseWorld world = new MouseWorld();

		start(world);
	}
}