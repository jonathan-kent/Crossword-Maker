
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Pattern;

public class CrosswordMaker implements Runnable{
	
	static GUI gui = new GUI();
	static Random r = new Random();
	
	static ArrayList<String> fourLetterWords = FileIO.search("dictionary.txt","^....$");
	static ArrayList<String> allWords = FileIO.search("dictionary.txt", ".*");
	static ArrayList<String> badCombos = FileIO.search("bad_letter_combos.txt", ".*");
	static HashSet<String> allWordsHash = new HashSet<String>(allWords);
	
	static int[][][] globalCalledBy = new int[21][21][2];
	
	public static void main(String[] args) {
		
		char[][] grid = new char[21][21];
		for(int i=0; i<grid.length; i++) {
			for(int j=0; j<grid.length; j++) {
				grid[i][j] = '.';
			}
		}
		gui.finishedGrid = grid;
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
	
	
	public static char[][] makeCustom(boolean[][] off, char[][] submittedGrid){
		char[][] firstLetters = getFirstLetters(off);
		//prep the grid
		//! is black square and . is undecided square 
		char[][] grid = new char[21][21];
		for(int i=0;i<21;i++) {
			for(int j=0;j<21;j++) {
				if(off[i][j]==true) {
					grid[i][j]='!';
				} else {
					grid[i][j] = submittedGrid[i][j];
				}
			}
		}
		//add entered words to word list
		for(int i=0; i<grid.length; i++) {
			for(int j=0; j<grid.length; j++) {
				if(firstLetters[i][j] == 'a' || firstLetters[i][j] == 'b') {
					String acrossRegex = getAcrossRegex(grid, i, j);
					if(!acrossRegex.contains(".")) {
						String word = acrossRegex;
						word = word.substring(1, word.length()-1);
						if(!allWordsHash.contains(word)) {
							allWordsHash.add(word);
						}
					}
				} else if(firstLetters[i][j] == 'd' || firstLetters[i][j] == 'b') {
					String downRegex = getDownRegex(grid, i, j);
					if(!downRegex.contains(".")) {
						String word = downRegex;
						word = word.substring(1, word.length()-1);
						if(!allWordsHash.contains(word)) {
							allWordsHash.add(word);
						}
					}
				}
			}
		}
		int[][][] calledBy = new int[21][21][2];
		for(int i=0; i<21; i++) {
			for(int j=0; j<21; j++) {
				calledBy[i][j][0] = -1;
				calledBy[i][j][1] = -1;
			}
		}
		globalCalledBy = copy3DArray(calledBy);
		gui.calledBy = calledBy;
		
		for(int i=0; i<21; i++) {
			int j=0;
			while(j<21) {
				char[][] newGrid = new char[21][21];
				if(firstLetters[i][j] == 'a' || firstLetters[i][j] == 'b') {
					String acrossRegex = getAcrossRegex(grid, i, j);
					String word = acrossRegex;
					word = word.substring(1, word.length()-1);
					if(!allWordsHash.contains(word)) {
						ArrayList<String> options = getMatches(allWords, getAcrossRegex(grid, i, j));
						int[] signature = {i, j};
						char[][] backupGrid = copy2DArray(grid);
						int[][][] backupCalledBy = copy3DArray(globalCalledBy);
						grid = fillAcross(grid, i, j, calledBy, options, signature);
						calledBy = globalCalledBy;
						if(grid==null) {
							int[] culpritSignature = findCulprit(backupGrid, backupCalledBy, signature, i, j);
							i = culpritSignature[0];
							j = culpritSignature[1];
							newGrid = removeCulprit(backupGrid, backupCalledBy, culpritSignature);
							for(int k=0; k<newGrid.length; k++) {
								for(int l=0; l<newGrid.length; l++) {
									if(submittedGrid[k][l]!='.') {
										newGrid[k][l] = submittedGrid[k][l];
									}
								}
							}
							calledBy = copy3DArray(backupCalledBy);
						}
					}
				}
				if(grid!=null) {
					j++;
				} else {
					grid = copy2DArray(newGrid);
				}
				gui.calledBy = calledBy;
				gui.finishedGrid = grid;
			}
		}
		
		return grid;
	}
	
	public static char[][] fillAcross(char[][] grid, int i, int j, int[][][] calledBy, ArrayList<String> options, int[] signature){
		if(options.isEmpty()) {
			return null;
		}
		if(isWordAcross(grid, i, j)) {
			return grid;
		}
		String chosenWord= getWord(options);
		char[][] backupGrid = copy2DArray(grid);
		int[][][] backupCalledBy = copy3DArray(calledBy);
		int checkedWords = 0;
		int completeWords = 0;
		while(checkedWords < chosenWord.length()) {
			if(options.isEmpty()) {
				return null;
			}
			chosenWord = getWord(options);
			char[] word = stringToCharArray(chosenWord);
			for(int k=0; k<word.length; k++) {
				grid[i+k][j] = word[k];
				calledBy[i+k][j] = Arrays.copyOf(signature, signature.length);
			}
			gui.calledBy = calledBy;
			globalCalledBy = calledBy;
			gui.finishedGrid = grid;
			for(int k=0; k<word.length; ++k) {
				int[] iAndJ = findDownStart(grid, i+k, j);
				String downWord = getDownRegex(grid, iAndJ[0], iAndJ[1]);
				downWord = downWord.substring(1, downWord.length()-1);
				if(!downWord.contains(".")) {
					completeWords++;
					checkedWords++;
					if(!allWordsHash.contains(downWord)) {
						completeWords = 0;
						checkedWords = 0;
						options.remove(chosenWord);
						break;
					} 
				} else {
					if(completeWords == chosenWord.length()) {
						return grid;
					}
					checkedWords++;
					globalCalledBy = calledBy;
					gui.calledBy = calledBy;
					gui.finishedGrid = grid;
				}
			}
		}
		
		int[] iAndJ = findDownStart(grid, i+(chosenWord.length()-1), j);
		ArrayList<String> downOptions = getMatches(allWords, getDownRegex(grid, iAndJ[0], iAndJ[1]));
		grid = lastDown(grid, iAndJ[0], iAndJ[1], calledBy, downOptions, signature);
		if(grid==null) {
			ArrayList<String> redactedOptions = new ArrayList<String>(options);
			redactedOptions.remove(chosenWord);
			char[][] newGrid = fillAcross(backupGrid, i, j, backupCalledBy, redactedOptions, signature);
			calledBy = copy3DArray(backupCalledBy);
			return newGrid;
		}
		return grid;
	}
	
	public static char[][] lastDown(char[][] grid, int i, int j, int[][][] calledBy, ArrayList<String> options, int[] signature){
		if(options.isEmpty()) {
			return null;
		}
		if(isWordDown(grid, i, j)) {
			return grid;
		}
		String chosenWord = getWord(options);
		char[] word = stringToCharArray(chosenWord);
		for(int k=0; k<word.length; ++k) {
			grid[i][j+k] = word[k];
			calledBy[i][j+k] = Arrays.copyOf(signature, signature.length);
		}
		gui.calledBy = calledBy;
		globalCalledBy = calledBy;
		gui.finishedGrid = grid;
		
		int[] iAndJ = findDownStart(grid, signature[0], signature[1]);
		ArrayList<String> downOptions = getMatches(allWords, getDownRegex(grid, iAndJ[0], iAndJ[1]));
		return firstDown(grid, iAndJ[0], iAndJ[1], calledBy, downOptions, signature);
	}
	
	public static char[][] firstDown(char[][] grid, int i, int j, int[][][] calledBy, ArrayList<String> options, int signature[]){
		if(options.isEmpty()) {
			return null;
		}
		if(isWordDown(grid, i, j)) {
			globalCalledBy = copy3DArray(calledBy);
			return grid;
		}
		char[][] backupGrid = copy2DArray(grid);
		int[][][] backupCalledBy = copy3DArray(calledBy);
		String chosenWord = getWord(options);
		char[] word = stringToCharArray(chosenWord);
		for(int k=0; k<word.length; ++k) {
			grid[i][j+k] = word[k];
			calledBy[i][j+k] = Arrays.copyOf(signature, signature.length);
			ArrayList<String> acrossOptions = getMatches(allWords, getAcrossRegex(grid, i, j+k));
			grid = fillAcross(grid, i, j+k, calledBy, acrossOptions, signature);
			if(grid==null) {
				ArrayList<String> redactedOptions = new ArrayList<String>(options);
				redactedOptions.remove(chosenWord);
				char[][] newGrid = firstDown(backupGrid, i, j, backupCalledBy, redactedOptions, signature);
				calledBy = copy3DArray(backupCalledBy);
				gui.calledBy = calledBy;
				return newGrid;
			}
		}
		gui.calledBy = calledBy;
		return grid;
	}
	
	public static int[] findCulprit(char[][] grid, int[][][] calledBy, int[] signature, int i, int j) {
		int highI = -1;
		int highJ = -1;
		for(int k=0; k<grid.length-i; k++) {
			if(grid[i+k][j]=='!') {
				break;
			}
			if(calledBy[i+k][j+k][0]!=signature[0] && calledBy[i+k][j+k][1]!=signature[1]) {
				highI = Math.max(highI, calledBy[i+k][j][0]);
				if(calledBy[i+k][j][0]==highI) {
					highJ = Math.max(highJ, calledBy[i+k][j][1]);
				}
			}
		}
		for(int k=0; k<grid.length-j; k++) {
			if(grid[i][j+k]=='!') {
				break;
			}
			if(calledBy[i][j+k][0]!=signature[0] && calledBy[i][j+k][1]!=signature[1]) {
				highI = Math.max(highI, calledBy[i][j+k][0]);
				if(calledBy[i][j+k][0]==highI) {
					highJ = Math.max(highJ, calledBy[i][j+k][1]);
				}
			}
		}
		int[] culprit = {highI, highJ};
		return culprit;
	}
	
	public static char[][] removeCulprit(char[][] grid, int[][][] calledBy, int[] signature){
		for(int i=0; i<grid.length; i++) {
			for(int j=0; j<grid.length; j++) {
				if(calledBy[i][j][0] == signature[0] && calledBy[i][j][1] == signature[1]) {
					grid[i][j] = '.';
				}
			}
		}
		return grid;
	}
	
	public static String getWord(ArrayList<String> matches) {
		String word = null;
		int pick = r.nextInt(matches.size());
		word = matches.get(pick);
		
		return word;
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
		word = word.substring(1, word.length()-1);
		if (word.contains(".")){
			return false;
		} else if(allWordsHash.contains(word)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isWordDown(char[][] grid, int i, int j) {
		String word = getDownRegex(grid, i, j);
		word = word.substring(1, word.length()-1);
		if (word.contains(".")){
			return false;
		} else if(allWordsHash.contains(word)) {
			return true;
		} else {
			return false;
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
	
	
	public static char[][] copy2DArray(char[][] array){
		char[][] copy = new char[21][21];
		for(int i=0; i<array.length; i++) {
			copy[i] = Arrays.copyOf(array[i], array[i].length);
		}
		return copy;
	}
	
	public static int[][][] copy3DArray(int[][][] array){
		int[][][] copy = new int[21][21][2];
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array.length; j++) {
				copy[i][j] = Arrays.copyOf(array[i][j], array[i][j].length);
			}
		}
		return copy;
	}
	
	
	
	
	
	
	
	
	
	
	//4x4 proof of concept
	
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
	
}
