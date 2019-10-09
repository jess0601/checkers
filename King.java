import java.awt.Color;
import java.util.ArrayList;
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public class King extends Piece {
	private Color pColor;
	public boolean jump;
	public boolean move;
	public boolean jumped;

	public King(Color pieceColor) {
		pColor = pieceColor;
		setColor(pColor);
		jump = false;
		move = false;
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

		if (this.getColor() == Color.RED) {
			if (gr.get(p2).getColor() != Color.BLACK)
				// There is no black piece to jump.
				return false;
			jump = true; // The jump is legal.
			return true;
		} else {
			if (gr.get(p2).getColor() != Color.RED)
				// There is no red piece to jump.
				return false;
			jump = true; // The jump is legal.
			return true;
		}
	}

	public boolean legalMove(MouseWorld world, int r1, int c1, int r2, int c2) {
		Location p2 = new Location(r2, c2);
		Grid<Actor> gr = world.getGrid();

		if (gr.isValid(p2) == false)
			// (r2,c2) is off the board.
			return false;

		if (gr.get(p2) instanceof Reg || gr.get(p2) instanceof King)
			// (r2,c2) already contains a piece.
			return false;

		move = true;
		return true;
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
		King a = (King) gr.get(loc);

		// jumps
		if (playerCol == a.getColor()) {
			if (legalJump(world, x, y, x + 1, y + 1, x + 2, y + 2)) {
				moves.add(new Location(x + 2, y + 2));
				jump = true;
			}
			if (legalJump(world, x, y, x + 1, y - 1, x + 2, y - 2)) {
				moves.add(new Location(x + 2, y - 2));
				jump = true;
			}
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
			if (playerCol == a.getColor()) {
				if (legalMove(world, x, y, x + 1, y + 1)) {
					moves.add(new Location(x + 1, y + 1));
					move = true;
				}
				if (legalMove(world, x, y, x + 1, y - 1)) {
					moves.add(new Location(x + 1, y - 1));
					move = true;
				}
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