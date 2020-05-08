import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileIO {

	public static ArrayList<String> search(String fileName, String regex) {
		try {
			Scanner fileScanner = new Scanner(new File(fileName));
			ArrayList<String> words = new ArrayList<String>();
			Pattern pattern =  Pattern.compile(regex);
			Matcher matcher = null;
			while(fileScanner.hasNextLine()){
				String line = fileScanner.nextLine();
				matcher = pattern.matcher(line);
				if(matcher.find()){
					words.add(line);  
				}
			}
			
			fileScanner.close();
			return words;
		}catch(IOException e){
			
		}
		
		return null;
	}
	
}
