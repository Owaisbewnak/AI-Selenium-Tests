import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GenerateTestFromExternalizeStory {

	public static void main(String[] args) {
//		String story = UserStoryReader.readFromClassPath("user-story.txt");
//		System.out.println(story);
		
		String resourcePath = "user-story.txt";
		try {
			System.out.println("Reading user story from resource: " + resourcePath);
			String story = UserStoryReader.readFromClassPath(resourcePath);
			
			System.out.println("User story content:\n" + story + "\n");
			System.out.println("Sending user story to AI....");
			
			String generatedCode = OpenAIClientWrapper.generateCodeFromStory(story);
			System.out.println("\nAI Generated Test Code:\n");
			//System.out.println(generatedCode);
			

			// Build a timestamped filename and save 
			String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			String fileName = "generated-tests/GeneratedTest_" + ts + ".java";
			OpenAIClientWrapper.saveToFile(generatedCode, fileName);
			System.out.println("\nSaved generated File: " + fileName);
			
		} catch (Exception e) {
			
		}
	}
}
