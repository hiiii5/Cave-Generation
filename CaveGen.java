import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;

public class CaveGen extends JPanel implements ActionListener
{
	static int C = 100;
	static int worldWidth = C, worldHeight = C;
	static int[][] world = new int[worldWidth][worldHeight];
	static int tileWidth = 6, tileHeight = 6;

	static float chanceToStartAlive = 0.45f;
	static int birthLimit = 4, deathLimit = 3, numberOfSteps = 3, canvasPadding = 250;
	
	static int canvasWidth = (worldWidth * tileWidth) + canvasPadding, canvasHeight = (worldHeight * tileHeight) + canvasPadding;
	
	static Button iterateButton, newMapButton, placeTreasureButton, placePlayerButton;
	
	public CaveGen()
	{		
		iterateButton = new Button("do Simulation Step");
		newMapButton = new Button("New World");
		placeTreasureButton = new Button("Place Treasure");
		placePlayerButton = new Button("Place Player");
		
		add(iterateButton);
		add(newMapButton);
		add(placeTreasureButton);
		add(placePlayerButton);

		iterateButton.addActionListener(this);
		newMapButton.addActionListener(this);
		placeTreasureButton.addActionListener(this);
		placePlayerButton.addActionListener(this);

		world = generateMap();
		repaint();
	}
	
	public void iterate()
	{
		world = doSimulationStep(world);
		repaint();
	}
	
	public void recreate()
	{
		birthLimit = 4;
		deathLimit = 3;
		chanceToStartAlive = 0.4f;
		numberOfSteps = 2;
		
		world = generateMap();
		repaint();
	}
	
	public int[][] generateMap()
	{
		//Create a new map
		int[][] map = new int[worldWidth][worldHeight];
		//Set up the map with random values
		initialiseMap(map);
		//And now run the simulation for a set number of steps
		for(int i = 0; i < numberOfSteps; i++)
		{
			map = doSimulationStep(map);
		}
		
		return map;
	}
	
	public int[][] initialiseMap(int[][] map)
	{
		for(int x = 0; x < worldWidth; x++)
		{
			for(int y = 0; y < worldHeight; y++)
			{
				map[x][y] = 0;
			}
		}
	
		for(int x = 0; x < worldWidth; x++)
		{
			for(int y = 0; y < worldHeight; y++)
			{
				if(Math.random() < chanceToStartAlive)
				{
					map[x][y] = 1;
				}
			}
		}
		return map;
	}
	
	public int[][] doSimulationStep(int[][] map)
	{
		int[][] newMap = new int[worldWidth][worldHeight];
		//Loop over each row and column of the map
		for(int x=0; x < map.length; x++)
		{
			for(int y=0; y < map[0].length; y++)
			{
				int nbs = countAliveNeighbours(map, x, y);
				//The new value is based on our simulation rules
				//First, if a cell is alive but has too few neighbours, kill it.
				if(map[x][y] > 0)
				{
					if(nbs < deathLimit)
					{
						newMap[x][y] = 0;
					}
					else
					{
						newMap[x][y] = 1;
					}
				} //Otherwise, if the cell is dead now, check if it has the right number of neighbours to be 'born'
				else
				{
					if(nbs > birthLimit)
					{
						newMap[x][y] = 1;
					}
					else
					{
						newMap[x][y] = 0;
					}
				}
			}
		}

		return newMap;
	}
	
	//Returns the number of cells in a ring around (x,y) that are alive.
	public int countAliveNeighbours(int[][] map, int x, int y)
	{
		int count = 0;
		for(int i=-1; i<2; i++)
		{
			for(int j=-1; j<2; j++)
			{
				int nb_x = i+x;
				int nb_y = j+y;
				//If we're looking at the middle point
				if(i == 0 && j == 0)
				{
					//Do nothing, we don't want to add ourselves in!
				}
				//In case the index we're looking at it off the edge of the map
				else if(nb_x < 0 || nb_y < 0 || nb_x >= map.length || nb_y >= map[0].length)
				{
					count = count + 1;
				}
				//Otherwise, a normal check of the neighbour
				else if(map[nb_x][nb_y] == 1)
				{
					count = count + 1;
				}
			}
		}

		return count;
	}
	
	public void placeTreasure()
	{
		int treasureLimit = 5;
		
		for(int x = 0; x < worldWidth; x++)
		{
			for(int y = 0; y < worldHeight; y++)
			{
				if(world[x][y] == 0)
				{
					int nbs = countAliveNeighbours(world, x, y);
					
					if(nbs >= treasureLimit)
					{
						world[x][y] = 2;
					}
				}
			}
		}
		repaint();
	}
	
	public void placePlayer()
	{
		int playerLimit = 1;
		int playerPlaced = 0;
		
		for(int x = 0; x < worldWidth; x++)
		{
			for(int y = 0; y < worldHeight; y++)
			{
				if(world[x][y] == 3)
				{
					world[x][y] = 0;
				}
			
				if(world[x][y] == 0)
				{
					int nbs = countAliveNeighbours(world, x, y);
					
					if(nbs <= playerLimit)
					{
						if(playerPlaced != 1)
						{
							if(Math.random() > chanceToStartAlive)
							{
								world[x][y] = 3;
								playerPlaced = 1;
							}
						}
					}
				}
			}
		}
		repaint();
	}
	
	public static void main(String[] args)
	{
		CaveGen canvas = new CaveGen();
	
		JFrame frame = new JFrame("Cave Gen v1.0.001 Build: 0.0.0.127");
		JPanel panel = new JPanel();
		
		frame.add(canvas);
		frame.pack();
		frame.setSize(canvasWidth, canvasHeight);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		
		g.setColor(Color.black);
		g.fillRect(0, 0,canvasWidth, canvasHeight);
		
		for(int x = 0; x < worldWidth; x++)
		{
			for(int y = 0; y < worldHeight; y++)
			{
				if(world[x][y] == 0)
				{
					g.setColor(Color.blue);
				}
				else if(world[x][y] == 2)
				{
					g.setColor(Color.yellow);
				}
				else if(world[x][y] == 3)
				{
					g.setColor(Color.green);
				}
				else
				{
					g.setColor(Color.black);
				}
				
				g.fillRect( x*tileWidth + canvasPadding/2, y*tileHeight + canvasPadding/2, tileWidth, tileHeight);
			}
		}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == iterateButton)
		{
			iterate();
		}
		else if(e.getSource() == newMapButton)
		{
			recreate();
		}
		else if(e.getSource() == placeTreasureButton)
		{
			placeTreasure();
		}
		else if(e.getSource() == placePlayerButton)
		{
			placePlayer();
		}
	}
}