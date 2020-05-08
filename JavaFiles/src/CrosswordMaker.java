
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

public class CrosswordMaker implements Runnable{
	
	static GUI gui = new GUI();
	static Random r = new Random();
	
	static ArrayList<String> fourLetterWords = FileIO.search("dictionary.txt","^....$");
	static ArrayList<String> allWords = FileIO.search("dictionary.txt", ".*");
	static ArrayList<String> badCombos = FileIO.search("bad_letter_combos.txt", ".*");
	
	public static void main(String[] args) {
		
		/*boolean[][] testGrid = new boolean[21][21];
		testGrid[0][0] = true;
		testGrid[0][1] = true;
		testGrid[0][2] = true;
		testGrid[0][3] = true;
		testGrid[1][0] = true;
		testGrid[1][1] = true;
		testGrid[1][2] = true;
		testGrid[1][3] = true;
		testGrid[2][0] = true;
		testGrid[2][1] = true;
		testGrid[2][2] = true;
		testGrid[2][3] = true;
		testGrid[3][0] = true;
		testGrid[3][1] = true;
		testGrid[3][2] = true;
		testGrid[3][3] = true;
		for(int i=0; i<testGrid.length; i++) {
			for(int j=0; j<testGrid.length; j++) {
				if(testGrid[i][j] == true) {
					testGrid[i][j] = false;
				} else {
					testGrid[i][j] = true;
				}
			}
		}
		makeCustom(testGrid);
		*/
		
		new Thread(new CrosswordMaker()).start();
		
	}
	
	@Override
	public void run() {
		while(true) {
			gui.repaint();
		}
		
	}
	
	public static void printGrid(char[][] grid) {
		for(int i=0;i<grid.length;i++) {
			for(int j=0; j<grid[0].length;j++) {
				System.out.print(grid[j][i]+" ");
			}
			System.out.println();
		}
	}
	
	public static void smallPrintGrid(char[][] grid) {
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				System.out.print(grid[j][i]+" ");
			}
			System.out.println();
		}
	}
	
	public static void smallPrintIntGrid(int[][] grid) {
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				System.out.print(grid[j][i]+" ");
			}
			System.out.println();
		}
	}
	
	public static String getWord(ArrayList<String> matches) {
		String word = null;
		int pick = r.nextInt(matches.size());
		word = matches.get(pick);
		
		return word;
	}
	
	public static char[][] makeCustom(boolean[][] off){
		char[][] firstLetters = getFirstLetters(off);
		//prep the grid
		//! is black square and . is undecided square 
		char[][] grid = new char[21][21];
		for(int i=0;i<21;i++) {
			for(int j=0;j<21;j++) {
				if(off[i][j]==true) {
					grid[i][j]='!';
				} else {
					grid[i][j]='.';
				}
			}
		}
		int[][] calledBy = new int[21][21];
		for(int i=0; i<21; i++) {
			for(int j=0; j<21; j++) {
				calledBy[i][j] = -1;
			}
		}
		
		for(int i=0; i<21; i++) {
			for(int j=0; j<21; j++) {
				if(firstLetters[i][j] == 'a' || firstLetters[i][j] == 'b') {
					ArrayList<String> options = getMatches(allWords, getAcrossRegex(grid, i, j));
					int signature = Integer.parseInt(i+""+j);
					char[][] backupGrid = copy2DArray(grid);
					int[][] backupCalledBy = copy2DIntArray(calledBy);
					grid = fillAcross(grid, i, j, calledBy, options, signature);
					if(grid==null) {
						int culpritSignature = findCulprit(backupGrid, backupCalledBy, i, j);
						i = culpritSignature / 10;
						j = culpritSignature % 10-1;
						grid = removeCulprit(backupGrid, backupCalledBy, culpritSignature);
						calledBy = backupCalledBy;
					}
				}
			}
		}
		
		return grid;
	}
	
	public static char[][] fillAcross(char[][] grid, int i, int j, int[][] calledBy, ArrayList<String> options, int signature){
		if(options.isEmpty()) {
			return null;
		}
		if(isWordAcross(grid, i, j)) {
			return grid;
		}
		char[][] backupGrid = copy2DArray(grid);
		int[][] backupCalledBy = copy2DIntArray(calledBy);
		String chosenWord = getWord(options);
		char[] word = stringToCharArray(chosenWord);
		for(int k=0; k<word.length; k++) {
			grid[i+k][j] = word[k];
			calledBy[i+k][j] = signature;
		}
		int k=0;
		for(; k<word.length; ++k) {
			int[] iAndJ = findDownStart(grid, i+k, j);
			String downWord = getDownRegex(grid, iAndJ[0], iAndJ[1]);
			downWord = downWord.substring(1, downWord.length()-1);
			if(!downWord.contains(".")) {
				if(!allWords.contains(downWord)) {
					ArrayList<String> redactedOptions = new ArrayList<String>(options);
					redactedOptions.remove(chosenWord);
					return fillAcross(backupGrid, i, j, backupCalledBy, redactedOptions, signature);
				} else {
					gui.finishedGrid = grid;
					gui.repaint();
					//if(k==word.length-2) {
						//return grid;
					//}
				}
			}
		}
		int[] iAndJ = findDownStart(grid, i+k-1, j);
		ArrayList<String> downOptions = getMatches(allWords, getDownRegex(grid, iAndJ[0], iAndJ[1]));
		grid = lastDown(grid, iAndJ[0], iAndJ[1], calledBy, downOptions, signature);
		if(grid==null) {
			ArrayList<String> redactedOptions = new ArrayList<String>(options);
			redactedOptions.remove(chosenWord);
			return fillAcross(backupGrid, i, j, backupCalledBy, redactedOptions, signature);
		}
		
		//smallPrintGrid(grid);
		return grid;
	}
	
	public static char[][] lastDown(char[][] grid, int i, int j, int[][] calledBy, ArrayList<String> options, int signature){
		if(options.isEmpty()) {
			return null;
		}
		if(isWordDown(grid, i, j)) {
			return grid;
		}
		String chosenWord = getWord(options);
		char[] word = stringToCharArray(chosenWord);
		for(int k=0; k<word.length; k++) {
			grid[i][j+k] = word[k];
			calledBy[i][j+k] = signature;
		}
		
		int[] iAndJ = findFirstDown(grid, calledBy, i, j, signature);
		ArrayList<String> downOptions = getMatches(allWords, getDownRegex(grid, iAndJ[0], iAndJ[1]));
		return firstDown(grid, iAndJ[0], iAndJ[1], calledBy, downOptions, signature);
	}
	
	public static char[][] firstDown(char[][] grid, int i, int j, int[][] calledBy, ArrayList<String> options, int signature){
		if(options.isEmpty()) {
			return null;
		}
		if(isWordDown(grid, i, j)) {
			return grid;
		}
		char[][] backupGrid = copy2DArray(grid);
		int[][] backupCalledBy = copy2DIntArray(calledBy);
		String chosenWord = getWord(options);
		char[] word = stringToCharArray(chosenWord);
		for(int k=0; k<word.length; ++k) {
			grid[i][j+k] = word[k];
			calledBy[i][j+k] = signature;
			ArrayList<String> acrossOptions = getMatches(allWords, getAcrossRegex(grid, i, j+k));
			grid = fillAcross(grid, i, j+k, calledBy, acrossOptions, signature);
			if(grid==null) {
				ArrayList<String> redactedOptions = new ArrayList<String>(options);
				redactedOptions.remove(chosenWord);
				return firstDown(backupGrid, i, j, backupCalledBy, redactedOptions, signature);
			}
		}
		
		return grid;
	}
	
	public static int findCulprit(char[][] grid, int[][] calledBy, int i, int j) {
		int high = -1;
		for(int k=0; k<grid.length-i; k++) {
			if(grid[i+k][j]=='!') {
				break;
			}
			high = Math.max(high, calledBy[i+k][j]);
		}
		for(int k=0; k<grid.length-j; k++) {
			if(grid[i][j+k]=='!') {
				break;
			}
			high = Math.max(high, grid[i][j+k]);
		}
		return high;
	}
	
	public static char[][] removeCulprit(char[][] grid, int[][] calledBy, int signature){
		for(int i=0; i<grid.length; i++) {
			for(int j=0; j<grid.length; j++) {
				if(calledBy[i][j] == signature) {
					grid[i][j] = '.';
				}
			}
		}
		return grid;
	}
	
	public static int[] findFirstDown(char[][] grid, int[][] calledBy, int i, int j, int signature) {
		for(int k=0; k<calledBy.length; k++) {
			if(calledBy[i-1][j+k]==signature) {
				int l=1;
				while(calledBy[i-l][j+k]==signature) {
					if(i-l==0) {
						int[] iAndJ = new int[2];
						iAndJ[0] = 0;
						iAndJ[1] = j+k;
						return findDownStart(grid, i-l, j+k);
					}
					l++;
				}
				int[] iAndJ = new int[2];
				iAndJ[0] = i-l+1;
				iAndJ[1] = j+k;
				return findDownStart(grid, i-l+1, j+k);
			}
		}
		return null;
	}
	
	public static int[] findDownStart(char[][] grid, int i, int j) {
		if(j==0) {
			int[] iAndJ = new int[2];
			iAndJ[0] = i;
			iAndJ[1] = 0;
			return iAndJ;
		} else {
			for(int k=0; k<=j; k++) {
				if(grid[i][j-k]=='!') {
					int[] iAndJ = new int[2];
					iAndJ[0] = i;
					iAndJ[1] = j-k+1;
					return iAndJ;
				}
			}
			int[] iAndJ = new int[2];
			iAndJ[0] = i;
			iAndJ[1] = 0;
			return iAndJ;
		}
	}
	
	public static int[] findNextDown(char[][] grid, char[][] firstLetters) {
		int[] array = new int[2];
		for(int i=0; i<firstLetters.length; i++) {
			for(int j=0; j<firstLetters.length; j++) {
				if(firstLetters[i][j]=='d'||firstLetters[i][j]=='b') {
					if(isWordDown(grid, i, j)) {
						
					} else {
						array[0] = i;
						array[1] = j;
						return array;
					}
				}
			}
		}
		array = null;
		return array;
	}
	
	public static int[] findNextAcross(char[][] grid, char[][] firstLetters) {
		int[] array = new int[2];
		for(int i=0; i<firstLetters.length; i++) {
			for(int j=0; j<firstLetters.length; j++) {
				if(firstLetters[i][j]=='a'||firstLetters[i][j]=='b') {
					if(isWordAcross(grid, i, j)) {
						
					} else {
						array[0] = i;
						array[1] = j;
						return array;
					}
				}
			}
		}
		array = null;
		return array;
	}
	
	public static boolean isWordAcross(char[][] grid, int i, int j) {
		String word = getAcrossRegex(grid, i, j);
		if (word.contains(".")){
			return false;
		} else if(getMatches(allWords, word).isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean isWordDown(char[][] grid, int i, int j) {
		String word = getDownRegex(grid, i, j);
		if (word.contains(".")){
			return false;
		} else if(getMatches(allWords, word).isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	public static String getDownRegex(char[][] grid, int i, int j) {
		String regex = "^";
		for(; j<grid[i].length; j++) {
			if(grid[i][j]!='!') {
				regex = regex+grid[i][j];
			} else {
				regex = regex+'$';
				return regex;
			}
		}
		regex = regex+'$';
		return regex;
	}
	
	public static String getAcrossRegex(char[][] grid, int i, int j) {
		String regex = "^";
		for(; i<grid.length; i++) {
			if(grid[i][j]!='!') {
				regex = regex+grid[i][j];
			} else {
				regex = regex+'$';
				return regex;
			}
		}
		regex = regex+'$';
		return regex;
	}
	
	public static char[][] getFirstLetters(boolean[][] off){
		char[][] firstLetters = new char[21][21];
		for(int i=0; i<21; i++) {
			for(int j=0; j<21; j++) {
				if(off[i][j]==false) {
					//b = both a = across d = down
					if((off[Math.max(i-1,0)][j] && off[i][Math.max(j-1,0)]) || (i==0 && j==0) || (off[Math.max(i-1,0)][j] && j==0) || (off[i][Math.max(j-1,0)] && i==0)) {
						firstLetters[i][j]= 'b';
					} else if(off[i][Math.max(j-1,0)] || j==0) {
						firstLetters[i][j] = 'd';
					} else if(off[Math.max(i-1,0)][j] || i==0) {
						firstLetters[i][j] = 'a';
					}
				}
			}
		}
		return firstLetters;
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
	
	public static boolean isSecondLetter(char[][] grid, char[][] firstLetters, int i, int j) {
		if(i==0 || j==0) {
			return false;
		} else if(firstLetters[i-1][j]=='a' || firstLetters[i][j-1]=='d' || firstLetters[i-1][j]=='b' || firstLetters[i][j-1]=='b') {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getDownCombo(char[][] grid, int i, int j) {
		if(j==0) {
			return null;
		} else if(grid[i][j-1]=='!') {
			return null;
		} else {
			return ""+ grid[i][j-1] + grid[i][j];
		}
	}
	
	public static String getAcrossCombo(char[][] grid, int i, int j) {
		if(i==0) {
			return null;
		} else if(grid[i-1][j]=='!') {
			return null;
		} else {
			return ""+ grid[i-1][j] + grid[i][j];
		}
	}
	
	public static char[][] copy2DArray(char[][] grid){
		char[][] copy = new char[21][21];
		for(int i=0; i<grid.length; i++) {
			copy[i] = Arrays.copyOf(grid[i], grid[i].length);
		}
		return copy;
	}
	
	public static int[][] copy2DIntArray(int[][] grid){
		int[][] copy = new int[21][21];
		for(int i=0; i<grid.length; i++) {
			copy[i] = Arrays.copyOf(grid[i], grid[i].length);
		}
		return copy;
	}
	
}
