import info.gridworld.actor.Actor;
import info.gridworld.grid.Location;
import java.awt.Color;
import info.gridworld.grid.Grid;

public class Player {
	private int pCount;
	private Color pColor;

	public Player(Color playerColor) {
		pCount = 12;
		pColor = playerColor;

	}

	public Color getColor() {
		return pColor;
	}

	public int getCount() {
		return this.pCount;

	}

	// Counts pieces left
	public void count(MouseWorld world) {
		Grid<Actor> gr = world.getGrid();
		pCount = 0;

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				Location place = new Location(x, y);
				// If piece at location is not null and is same color as player
				if (gr.get(place) != null) {
					Piece p = (Piece) gr.get(place);
					if (p.getColor() == pColor)
						pCount++;
				}
			}
		}
	}
}
