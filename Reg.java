import java.awt.Color;
import java.util.ArrayList;
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public class Reg extends Piece {
	private Color pColor;
	public boolean jump;
	public boolean move;
	public boolean jumped;

	public Reg(Color pieceColor) {
		pColor = pieceColor;
		setColor(pColor);
		jump = false;
		move = false;
		jumped = false;
	}

	public Color getColor() {
		return this.pColor;
	}

	public boolean getCanJump() {
		return this.jump;
	}

	public boolean getCanMove() {
		return this.move;
	}

	public boolean getJumped() {
		return this.jumped;
	}

	public void setJumped(boolean didJump) {
		jumped = didJump;
	}

	public boolean legalJump(MouseWorld world, int r1, int c1, int r2, int c2, int r3, int c3) {
		Location p1 = new Location(r1, c1);
		Location p2 = new Location(r2, c2);
		Location p3 = new Location(r3, c3);
		Grid<Actor> gr = world.getGrid();

		if (gr.isValid(p3) == false) {
			// (r3,c3) is off the board.
			return false;
		}

		if (gr.get(p3) != null) {
			// (r3,c3) already contains a piece.
			return false;
		}

		if (gr.get(p2) == null) {
			// (r2, c2) doesn't contain a piece to jump.
			return false;
		}

		if (gr.get(p1).getColor() == Color.RED) {
			if (r3 < r1) {
				// Regular red piece can only move down.
				return false;
			}
			if (gr.get(p2).getColor() != Color.BLACK) {
				// There is no black piece to jump.
				return false;
			}
			// The jump is legal.
			jump = true;
			return true;
		} else {
			if (r3 > r1)
				// Regular black piece can only move up.
				return false;
			if (gr.get(p2).getColor() != Color.RED) {
				// There is no red piece to jump.
				return false;
			}
			jump = true; // The jump is legal.
			return true;
		}
	}

	public boolean legalMove(MouseWorld world, int r1, int c1, int r2, int c2) {
		Grid<Actor> gr = world.getGrid();
		Location p1 = new Location(r1, c1);
		Location p2 = new Location(r2, c2);

		if (!(gr.isValid(p2)))
			// (r2,c2) is off the board.
			return false;

		if (gr.get(p2) != null) {
			// (r2,c2) already contains a piece.
			return false;
		}

		if (gr.get(p1).getColor() == Color.RED) {
			if (r2 < r1)
				return false; // Regular red piece can only move down.
			move = true; // The move is legal.
			return true;
		} else {
			if (r2 > r1)
				return false; // Regular black piece can only move up.
			move = true; // The move is legal.
			return true;
		}
	}

	public ArrayList<Location> checkLegalMoves(MouseWorld world, Location loc, Player pl) {
		ArrayList<Location> moves = new ArrayList<Location>();
		Grid<Actor> gr = world.getGrid();
		Color playerCol = pl.getColor();
		jump = false;
		move = false;
		jumped = false;

		int x = loc.getRow();
		int y = loc.getCol();
		Reg a = (Reg) gr.get(loc);

		// Player color must match selected piece color
		if (playerCol != a.getColor())
			return moves;

		// jumps
		if (a.getColor() == Color.RED) {
			if (legalJump(world, x, y, x + 1, y + 1, x + 2, y + 2)) {
				moves.add(new Location(x + 2, y + 2));
				jump = true;
			}
			if (legalJump(world, x, y, x + 1, y - 1, x + 2, y - 2)) {
				moves.add(new Location(x + 2, y - 2));
				jump = true;
			}
		} else {
			if (legalJump(world, x, y, x - 1, y + 1, x - 2, y + 2)) {
				moves.add(new Location(x - 2, y + 2));
				jump = true;
			}
			if (legalJump(world, x, y, x - 1, y - 1, x - 2, y - 2)) {
				moves.add(new Location(x - 2, y - 2));
				jump = true;
			}
		}

		// moves
		if (moves.size() == 0) {
			jump = false;
			if (a.getColor() == Color.RED) {
				if (legalMove(world, x, y, x + 1, y + 1)) {
					moves.add(new Location(x + 1, y + 1));
					move = true;
				}
				if (legalMove(world, x, y, x + 1, y - 1)) {
					moves.add(new Location(x + 1, y - 1));
					move = true;
				}
			} else {
				if (legalMove(world, x, y, x - 1, y + 1)) {
					moves.add(new Location(x - 1, y + 1));
					move = true;
				}
				if (legalMove(world, x, y, x - 1, y - 1)) {
					moves.add(new Location(x - 1, y - 1));
					move = true;
				}
			}
		}
		return moves;
	}
}