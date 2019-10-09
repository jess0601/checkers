import java.awt.Color;
import java.util.ArrayList;
import info.gridworld.actor.Actor;
import info.gridworld.grid.Location;

public abstract class Piece extends Actor {
	private Color pColor;

	public Piece() {
		super();
	}

	public Color getColor() {
		return this.pColor;
	}

	public void move(MouseWorld world, Location loc) {
		moveTo(loc);
	}

	public abstract boolean legalJump(MouseWorld world, int r1, int c1, int r2, int c2, int r3, int c3);

	public abstract boolean legalMove(MouseWorld world, int r1, int c1, int r2, int c2);

	public abstract ArrayList<Location> checkLegalMoves(MouseWorld world, Location loc, Player pl);

	public abstract boolean getCanJump();

	public abstract boolean getCanMove();

	public abstract boolean getJumped();

	public abstract void setJumped(boolean didJump);
}