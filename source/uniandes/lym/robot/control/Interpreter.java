package uniandes.lym.robot.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import uniandes.lym.robot.kernel.*;



/**
 * Receives commands and relays them to the Robot. 
 */

public class Interpreter   {

	/**
	 * Robot's world
	 */
	private RobotWorldDec world;

	private static final String routine = "ROBOT_R";

	private static final String vars = "VARS";

	private static final String begin = "BEGIN";

	private static final String THEGAME = "END";

	private static final String left = "LEFT";

	private static final String right = "RIGHT";

	private static final String around = "AROUND";

	private static final String block = "BLOCK";

	private LinearHashTable<String, Integer> variables;
	
	private HashMap<String, Block> bloques;

	public Interpreter()
	{
	}


	/**
	 * Creates a new interpreter for a given world
	 * @param world 
	 */


	public Interpreter(RobotWorld mundo)
	{
		this.world =  (RobotWorldDec) mundo;
		variables = new LinearHashTable<String, Integer>();
		bloques = new HashMap<String, Block>();
	}


	/**
	 * sets a the world
	 * @param world 
	 */

	public void setWorld(RobotWorld m) 
	{
		world = (RobotWorldDec) m;
		variables = new LinearHashTable<String, Integer>();
		bloques = new HashMap<String, Block>();
	}



	/**
	 *  Processes a sequence of commands. A command is a letter  followed by a ";"
	 *  The command can be:
	 *  M:  moves forward
	 *  R:  turns right
	 *  
	 * @param input Contiene una cadena de texto enviada para ser interpretada
	 */

	public String process(String input) throws Error
	{   
		Date start = new Date(); 
		StringBuffer output = new StringBuffer("SYSTEM RESPONSE: -->\n");	
		input = input.replaceAll("\\s+", "");
		String content = input;
		String[] var;
		try
		{
			if(content.startsWith(routine))
			{
				content = content.split("(?<=)"+routine)[1];
			}
			else
			{
				throw new Exception("The instruction didn't have the correct keyword");
			}
			if(content.contains(vars))
			{
				content = content.split("(?<=)"+vars)[1];
				String actual1 = content.split("(?<=)"+block)[0];
				var = actual1.split(",");
				if(!verificarVar(var))
				{
					throw new Exception("The variable doesn't start with a letter");
				}
				content = content.substring(content.indexOf(block)+block.length(), content.length());
			}
			String[] blocks = content.split(block);
			for(String block : blocks) {
				if(!block.endsWith(THEGAME)) {
					throw new Exception("");
				}
				String nombre = block.substring(0,block.indexOf(begin));
				String instruc = block.substring((block.indexOf(begin)+begin.length()), block.indexOf(THEGAME));
				Block bloque = new Block(instruc);
				bloques.put(nombre, bloque);
			}
			ArrayList<Block> blos = new ArrayList<Interpreter.Block>();
			for(Block block: bloques.values())
			{
				blos.add(block);
			}
			
			for(int i = (blos.size()-1); i > -1; i--) 
			{
				blos.get(i).process(this, output);
			}
		}
		catch (Exception e) 
		{
			output.append("Error!!! " + e.getMessage() + "\n");
			Date finish = new Date();
			output.append("Progam finished in " + (finish.getTime() - start.getTime()) + " ms");
			return output.toString();
		}
		Date finish = new Date();
		output.append("Progam succesfully finished in " + (finish.getTime() - start.getTime()) + " ms");
		return output.toString();
	}

	private boolean verificarVar(String[] array)
	{		
		for(int i = 0; i < array.length; i++)
		{
			char test = array[i].toCharArray()[0];
			if(!Character.isLetter(test))
			{
				return false;
			}
			variables.put(array[i], 0);
		}

		return true;
	}


	public RobotWorldDec getWorld() {
		return world;
	}

	public LinearHashTable<String, Integer> getVariables() {
		return variables;
	}
	
	public HashMap<String, Block> getBloques() {
		return bloques;
	}
	
	public static String getBegin() {
		return begin;
	}


	public static String getEnd() {
		return THEGAME;
	}


	public static String getLeft() {
		return left;
	}


	public static String getRight() {
		return right;
	}


	public static String getAround() {
		return around;
	}


	public static String getBlock() {
		return block;
	}
	
	public void face(String dir) throws Exception
	{
		if(oposite(dir))
		{
			for (int i = 0; i < 2; i++) 
			{
				world.turnRight();
			}
		}
		else if(right(dir))
		{
			world.turnRight();
		}
		else if(left(dir))
		{
			for (int i = 0; i < 3; i++) 
			{
				world.turnRight();
			}
		}
		else if(front(dir))
		{
			return;
		}
		else
			throw new Exception("direccion not valid");
	}
	
	private boolean oposite(String dir)
	{
		if(world.getOrientacion() == RobotWorld.NORTH && dir.equalsIgnoreCase("SOUTH")){
			return true;
		}else if(world.getOrientacion() == RobotWorld.SOUTH && dir.equalsIgnoreCase("WEST")){
			return true;
		}else if(world.getOrientacion() == RobotWorld.EAST && dir.equalsIgnoreCase("WEST")){
			return true;
		}else if(world.getOrientacion() == RobotWorld.WEST && dir.equalsIgnoreCase("EAST")){
			return true;
		}
		else
			return false;
	}
	
	private boolean right(String dir)
	{
		if(world.getOrientacion() == RobotWorld.NORTH && dir.equalsIgnoreCase("EAST")){
			return true;
		}else if(world.getOrientacion() == RobotWorld.SOUTH && dir.equalsIgnoreCase("WEST")){
			return true;
		}else if(world.getOrientacion() == RobotWorld.EAST && dir.equalsIgnoreCase("SOUTH")){
			return true;
		}else if(world.getOrientacion() == RobotWorld.WEST && dir.equalsIgnoreCase("NORTH")){
			return true;
		}
		else
			return false;
	}
	private boolean front(String dir)
	{
		if(world.getOrientacion() == RobotWorld.NORTH && dir.equalsIgnoreCase("NORTH")){
			return true;
		}else if(world.getOrientacion() == RobotWorld.SOUTH && dir.equalsIgnoreCase("SOUTH")){
			return true;
		}else if(world.getOrientacion() == RobotWorld.EAST && dir.equalsIgnoreCase("EAST")){
			return true;
		}else if(world.getOrientacion() == RobotWorld.WEST && dir.equalsIgnoreCase("WEST")){
			return true;
		}
		else
			return false;
	}
	
	private boolean left(String dir)
	{
		if(world.getOrientacion() == RobotWorld.NORTH && dir.equalsIgnoreCase("WEST")){
			return true;
		}else if(world.getOrientacion() == RobotWorld.SOUTH && dir.equalsIgnoreCase("EAST")){
			return true;
		}else if(world.getOrientacion() == RobotWorld.EAST && dir.equalsIgnoreCase("NORTH")){
			return true;
		}else if(world.getOrientacion() == RobotWorld.WEST && dir.equalsIgnoreCase("SOUTH")){
			return true;
		}
		else
			return false;
	}
	
	public void moveDir(int pasos, String dir)throws Error
	{
		if(dir.equalsIgnoreCase("front"))
		{
			world.moveForward(pasos);
		}
		else if(dir.equalsIgnoreCase("back"))
		{
			turnAround();
			world.moveForward(pasos);
			turnAround();
		}
		else if(dir.equalsIgnoreCase("left"))
		{
			turnLeft();
			world.moveForward(pasos);
			world.turnRight();
		}
		else if(dir.equalsIgnoreCase("right"))
		{
			world.turnRight();
			world.moveForward(pasos);
			turnLeft();
		}
		else
		{
			throw new Error("Enter a valid direction");
		}
	}
	
	public void turnLeft() 
	{
		for (int i = 0; i < 3; i++) 
		{
			world.turnRight();
		}
	}

	public void turnAround() 
	{
		for (int i = 0; i < 2; i++) 
		{
			world.turnRight();
		}		
	}


	public boolean canMove(String dir) throws Exception
	{
		if(dir.equalsIgnoreCase("North"))
		{
			return (world.estaArriba() && world.facingNorth());
		}
		else if(dir.equalsIgnoreCase("south"))
		{
			return (world.estaAbajo() && world.facingSouth());
		}
		else if(dir.equalsIgnoreCase("west"))			
		{
			return (world.estaIzquierda() && world.facingWest());
		}
		else if(dir.equalsIgnoreCase("east"))
		{
			return (world.estaDerecha() && world.facingEast());
		}
		else
		{
			throw new Error("Enter a valid direction");
		}
	}

	public class Block {

		private String content;
		
		public Block(String content) {
			this.content = content;
		}
		
		public void process(Interpreter interpreter, StringBuffer output) throws Exception 
		{
			
			String[] ins = content.split(";");
			for (int i = 0; i < ins.length; i++) 
			{
				ins[i] = ins[i].replace("(", "");
				ins[i] = ins[i].replace(")", "");
				if(ins[i].startsWith("Assing"))
				{
					ins[i] = ins[i].split("(?<=Assing)")[1];
					String[] param = ins[i].split(",");
					try {
						interpreter.getVariables().put(param[0], Integer.parseInt(param[1]));
					} 
					catch (Exception e) {
						throw new Exception("Please inset a number in the parameters");
					}
					output.append("Variable " + param[0] + " assingned the value of " + interpreter.getVariables().get(param[0]) + "\n");
				}
				
				else if(ins[i].startsWith("Turn"))
				{
					ins[i] = ins[i].split("(?<=Turn)")[1];
					if(ins[i].equalsIgnoreCase(interpreter.getLeft()))
					{
						interpreter.turnLeft();
						output.append("Turning " + interpreter.getLeft() + "direction\n");
					}
					else if(ins[i].equalsIgnoreCase(interpreter.getRight()))
					{
						interpreter.getWorld().turnRight();
						output.append("Turning " + interpreter.getRight() + "direction\n");
					}
					else if(ins[i].equalsIgnoreCase(interpreter.getAround()))
					{
						interpreter.turnAround();
						output.append("Turning " + interpreter.getAround() + "direction\n");
					}
					else
						throw new Exception("Enter a valid direction");
					
				}
				else if(ins[i].startsWith("Face"))
				{
					ins[i] = ins[i].split("(?<=Face)")[1];
					output.append("Facing " + ins[i] + " direction\n");
					interpreter.face(ins[i]);
				}
				else if(ins[i].startsWith("Put"))
				{
					ins[i] = ins[i].split("(?<=Put)")[1];
					String[] par = ins[i].split(",");
					int n;
					try
					{
						n = Integer.parseInt(par[1]);
					}
					catch (NumberFormatException e) 
					{
						if(!interpreter.getVariables().contains(par[1]))
							throw new Exception("Enter a number or a valid variable");
						else
						{
							n =interpreter.getVariables().get(par[1]);
						}
					}
					if(par[0].startsWith("B"))
					{
						interpreter.getWorld().putBalloons(n);
						output.append("Putting " + n + " balloons\n");
					}
					else if(par[0].startsWith("C"))
					{
						interpreter.getWorld().putChips(n);
						output.append("Putting " + n + " chips");
					}
					else
						throw new Exception("Enter a valid parameter");
				}
				else if(ins[i].startsWith("Pick"))
				{
					ins[i] = ins[i].split("(?<=Pick)")[1];
					String[] par = ins[i].split(",");
					int n;
					try
					{
						n = Integer.parseInt(par[1]);
					}
					catch (NumberFormatException e) 
					{
						if(!interpreter.getVariables().contains(par[1]))
							throw new Exception("Enter a number or a valid variable");
						else
						{
							n =interpreter.getVariables().get(par[1]);
						}
					}
					if(par[0].startsWith("B"))
					{
						interpreter.getWorld().grabBalloons(n);
						output.append("Picking " + n + " balloons\n");
					}
					else if(par[0].startsWith("C"))
					{
						interpreter.getWorld().pickChips(n);
						output.append("Picking " + n + " chips");
					}
					else
						throw new Exception("Enter a valid parameter");
				}
				else if(ins[i].startsWith("MoveDir"))
				{
					ins[i] = ins[i].split("(?<=MoveDir)")[1];
					int n;
					String[] par = ins[i].split(",");
					try
					{
						n = Integer.parseInt(par[0]);
					}
					catch (NumberFormatException e) 
					{
						if(!interpreter.getVariables().contains(par[0]))
							throw new Exception("Enter a number or a valid variable");
						else
						{
							n =interpreter.getVariables().get(par[0]);
						}
					}
					interpreter.moveDir(n, par[1]);
					output.append("Move " + n + " steps " + par[1] + " direction\n");
				}
				else if(ins[i].startsWith("Move"))
				{
					ins[i] = ins[i].split("(?<=Move)")[1];
					int n;
					if(!ins[i].contains(","))
					{
						try
						{
							n = Integer.parseInt(ins[i]);
						}
						catch (NumberFormatException e) 
						{
							if(!interpreter.getVariables().contains(ins[i]))
								throw new Exception("Enter a number or a valid variable");
							else
							{
								n =interpreter.getVariables().get(ins[i]);
							}
						}
						interpreter.getWorld().moveForward(n);
						output.append("Move " + n + " steps forward \n");
					}
					else
					{
						String[] par = ins[i].split(",");
						try
						{
							n = Integer.parseInt(par[0]);
						}
						catch (NumberFormatException e) 
						{
							if(!interpreter.getVariables().contains(par[0]))
								throw new Exception("Enter a number or a valid variable");
							else
							{
								n =interpreter.getVariables().get(par[0]);
							}
						}
						interpreter.face(par[1]);
						output.append("Facing " + par[1] + " direction");
						interpreter.getWorld().moveForward(n);
						output.append("Move " + n + " steps forward \n");
					}
				}
				else if(ins[i].startsWith("Skip"))
				{
					break;
				}
				else if(ins[i].startsWith("if"))
				{
					ins[i] = ins[i].split("(?<=if)")[1];
					String cond = ins[i].substring(0, ins[i].indexOf(","));
					String blocks = ins[i].substring((ins[i].indexOf(",")+",".length()));
					String[] bloques = blocks.split(",");
					if(executeCond(cond, interpreter))
					{
						interpreter.getBloques().get(bloques[0]).process(interpreter, output);
					}
					else
						interpreter.getBloques().get(bloques[1]).process(interpreter, output);
				}
				else if(ins[i].startsWith("while"))
				{
					ins[i] = ins[i].split("(?<=while)")[1];
					String cond = ins[i].substring(0, ins[i].indexOf(","));
					String blocks = ins[i].substring((ins[i].indexOf(",")+",".length()));
					while(executeCond(cond, interpreter))
					{
						interpreter.getBloques().get(blocks).process(interpreter, output);
					}
				}
				else if(ins[i].startsWith("Repeat"))
				{
					ins[i] = ins[i].split("(?<=Repeat)")[1];
					int n;
					String[] par = ins[i].split(",");
					try
					{
						n = Integer.parseInt(par[0]);
					}
					catch (NumberFormatException e) 
					{
						if(!interpreter.getVariables().contains(par[0]))
							throw new Exception("Enter a number or a valid variable");
						else
						{
							n =interpreter.getVariables().get(par[0]);
						}
					}
					for(int in = 0; in < n; in++)
					{
						interpreter.getBloques().get(par[1]).process(interpreter, output);
					}
					
				}
				else
					throw new Exception("No valid instruction found");
			}
			
			
		}
		
		private boolean executeCond(String exe, Interpreter interprete) throws Exception
		{
			if(exe.startsWith("facing"))
			{
				exe = exe.split("(?<=facing)")[1];
				if(exe.equalsIgnoreCase("North"))
				{
					return interprete.getWorld().facingNorth();
				}
				else if(exe.equalsIgnoreCase("South"))
				{
					return interprete.getWorld().facingSouth();
				}
				else if(exe.equalsIgnoreCase("East"))
				{
					return interprete.getWorld().facingEast();
				}
				else if(exe.equalsIgnoreCase("West"))
				{
					return interprete.getWorld().facingWest();
				}
			}
			else if(exe.startsWith("canPick"))
			{
				exe = exe.split("(?<=canPick)")[1];
				int n;
				String[] par = exe.split(",");
				try
				{
					n = Integer.parseInt(par[0]);
				}
				catch (NumberFormatException e) 
				{
					if(!interprete.getVariables().contains(par[1]))
						throw new Exception("Enter a number or a valid variable");
					else
					{
						n =interprete.getVariables().get(par[0]);
					}
				}
				if(par[0].startsWith("B"))
				{
					return (interprete.getWorld().contarGlobos() > n);
				}
				else if(par[0].startsWith("C"))
				{
					return (interprete.getWorld().chipsToPick() > n);
				}
				else
					throw new Exception("Enter a valid parameter");		
			}
			else if(exe.startsWith("canPut"))
			{
				exe = exe.split("(?<=canPut)")[1];
				int n;
				String[] par = exe.split(",");
				try
				{
					n = Integer.parseInt(par[0]);
				}
				catch (NumberFormatException e) 
				{
					if(!interprete.getVariables().contains(par[1]))
						throw new Exception("Enter a number or a valid variable");
					else
					{
						n =interprete.getVariables().get(par[0]);
					}
				}
				if(par[0].startsWith("B"))
				{
					return (interprete.getWorld().getMisGlobos() > n);
				}
				else if(par[0].startsWith("C"))
				{
					return (interprete.getWorld().getMisFichas() > n);
				}
				else
					throw new Exception("Enter a valid parameter");		
			}
			else if(exe.startsWith("canMove"))
			{
				exe = exe.split("(?<=canMove)")[1];
				return interprete.canMove(exe);
			}
			else if(exe.startsWith("not"))
			{
				exe = exe.split("(?<=not)")[1];
				return !executeCond(exe, interprete);
			}
			
			throw new Exception("Enter a valid condition");
		}
	};

}