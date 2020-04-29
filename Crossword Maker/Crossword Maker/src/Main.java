import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

public class Main {
	
	static Random r = new Random();
	static ArrayList<String> fourLetterWords = FileIO.search("dictionary.txt","^....$");
	
	public static void main(String[] args) {
		
		char[][] emptyGrid = new char[4][4];
		
		char[][] grid = make4x4(emptyGrid, fourLetterWords, 1);
		printGrid(grid);
		
	}
	
	public static void printGrid(char[][] grid) {
		System.out.println(Arrays.toString(grid[0]));
		System.out.println(Arrays.toString(grid[1]));
		System.out.println(Arrays.toString(grid[2]));
		System.out.println(Arrays.toString(grid[3]));
	}
	
	public static String getWord(ArrayList<String> matches) {
		String word = null;
		int pick = r.nextInt(matches.size());
		word = matches.get(pick);
		
		return word;
	}
	
	public static char[][] make4x4(char[][] grid, ArrayList<String> options, int step){
		String fourAcross = ""+grid[3][0]+grid[3][1]+grid[3][2]+grid[3][3];
		
		//1 across
		if (step == 1) {
			if (options.isEmpty()) {
				System.out.println("Could not create a grid");
				return null;
			}
			
			String word = getWord(options);
			char[] array = stringToCharArray(word);
			grid[0] = array;
			
			step++;
			ArrayList<String> optionsPrime = getMatches(fourLetterWords, "^"+grid[0][0]+"...$");
			
			System.out.println("1 across: "+word);
			
			//next step did not work, repeat with a new word
			if (make4x4(grid, optionsPrime, step)==null) {
				ArrayList<String> redactedOptions = options;
				redactedOptions.remove(word);
				step--;
				return make4x4(grid, redactedOptions, step);
			}
		}
		
		//1 down
		else if (step == 2) {
			if (options.isEmpty()) {
				return null;
			}
			
			String word = getWord(options);
			char[] array = stringToCharArray(word);
			grid[1][0] = array[1];
			grid[2][0] = array[2];
			grid[3][0] = array[3];
			
			step++;
			ArrayList<String> optionsPrime = getMatches(fourLetterWords, "^"+grid[0][3]+"...$");
			
			System.out.println("1 down: "+word);
			
			if (make4x4(grid, optionsPrime, step)==null) {
				ArrayList<String> redactedOptions = options;
				redactedOptions.remove(word);
				step--;
				return make4x4(grid, redactedOptions, step);
			}
		}
		
		//4 down
		else if (step == 3) {
			if (options.isEmpty()) {
				return null;
			}
			
			String word = getWord(options);
			char[] array = stringToCharArray(word);
			grid[1][3] = array[1];
			grid[2][3] = array[2];
			grid[3][3] = array[3];
			
			step++;
			ArrayList<String> optionsPrime = getMatches(fourLetterWords, "^"+grid[1][0]+".."+grid[1][3]+"$");
			
			System.out.println("4 down: "+word);
			
			if (make4x4(grid, optionsPrime, step)==null) {
				ArrayList<String> redactedOptions = options;
				redactedOptions.remove(word);
				step--;
				return make4x4(grid, redactedOptions, step);
			}
		}
		
		//2 across
		else if (step == 4) {
			if (options.isEmpty()) {
				return null;
			}
			
			String word = getWord(options);
			char[] array = stringToCharArray(word);
			grid[1][1] = array[1];
			grid[1][2] = array[2];
			
			step++;
			ArrayList<String> optionsPrime = getMatches(fourLetterWords, "^"+grid[0][1]+grid[1][1]+"..$");
			
			System.out.println("2 across: "+word);
			
			if (make4x4(grid, optionsPrime, step)==null) {
				ArrayList<String> redactedOptions = options;
				redactedOptions.remove(word);
				step--;
				return make4x4(grid, redactedOptions, step);
			}
		}
		
		//2 down
		else if (step == 5) {
			if (options.isEmpty()) {
				return null;
			}
			
			String word = getWord(options);
			char[] array = stringToCharArray(word);
			grid[2][1] = array[2];
			grid[3][1] = array[3];
			
			step++;
			ArrayList<String> optionsPrime = getMatches(fourLetterWords, "^"+grid[2][0]+grid[2][1]+"."+grid[2][3]+"$");
			
			System.out.println("2 down: "+word);
			
			if (make4x4(grid, optionsPrime, step)==null) {
				ArrayList<String> redactedOptions = options;
				redactedOptions.remove(word);
				step--;
				return make4x4(grid, redactedOptions, step);
			}
		}
		
		//3 across
		else if (step == 6) {
			if (options.isEmpty()) {
				return null;
			}
			
			String word = getWord(options);
			char[] array = stringToCharArray(word);
			grid[2][2] = array[2];
			
			step++;
			ArrayList<String> optionsPrime = getMatches(fourLetterWords, "^"+grid[0][2]+grid[1][2]+grid[2][2]+".$");
			
			System.out.println("3 across: "+word);
			
			if (make4x4(grid, optionsPrime, step)==null) {
				ArrayList<String> redactedOptions = options;
				redactedOptions.remove(word);
				step--;
				return make4x4(grid, redactedOptions, step);
			}
		}
		
		//3 down
		else if (step == 7) {
			if (options.isEmpty()) {
				return null;
			}
			
			String word = getWord(options);
			char[] array = stringToCharArray(word);
			grid[3][2] = array[3];
			
			step++;
			ArrayList<String> optionsPrime = getMatches(fourLetterWords, "^"+grid[0][1]+grid[1][1]+"..$");
			
			System.out.println("3 down: "+word);
			
			if (make4x4(grid, optionsPrime, step)==null) {
				ArrayList<String> redactedOptions = options;
				redactedOptions.remove(word);
				step--;
				return make4x4(grid, redactedOptions, step);
			}
		}
		else if (!(fourLetterWords.contains(fourAcross))) {
			return null;
		}
		else {
			return grid;
		}
		return grid;
	}
	
	public static char[] stringToCharArray(String string) {
		char[] array = new char[string.length()];
		for (int i = 0; i < string.length(); i++) { 
            array[i] = string.charAt(i); 
        }
		return array;
	}
	
	public static String charArraytoString(char[] array) {
		String string = new String(array);
		return string;
	}
	
	public static ArrayList<String> getMatches(ArrayList<String> words, String regex){
		ArrayList<String> matches = new ArrayList<String>();
		Pattern pattern =  Pattern.compile(regex);
		
		for (String string:words) {
			if (pattern.matcher(string).matches()) {
				matches.add(string);
			}
		}
		
		return matches;
	}
	
}
