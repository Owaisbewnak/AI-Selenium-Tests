import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SeleniumCodeGenerator {
	
	public static void main(String[] args ) throws IOException {
		String resourcePath = "src/test/resources/GeneratedTest_JSON_Test.json";
//
//		System.out.println("Reading The Test Cases from path: " + resourcePath);
//		String userStory = UserStoryReader.readFromClassPath(resourcePath);
		
		System.out.println("Reading JOSN test Cases File: " + resourcePath);
		String userStory = Files.readString(Path.of(resourcePath));
		System.out.println("\nSending Test Case  to AI for Selenium Script generation...\n ");
		
		try {
			String generatedCode = OpenAIClientWrapper.generateCodeFromStory(userStory);
			System.out.println("\nAI Generated Test Code:\n");
			//System.out.println(generatedCode);
			
			// Build a timestamped filename and save 
			String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			String fileName = "src/test/java/GeneratedTest_" + ts + ".java";
			OpenAIClientWrapper.saveToFile(generatedCode, fileName);
			System.out.println("\nSaved generated File: " + fileName);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
