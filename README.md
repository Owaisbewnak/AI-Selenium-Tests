# AI-Selenium-Tests

An intelligent test automation framework that leverages OpenAI's GPT models to automatically generate Selenium + TestNG test scripts from user stories and acceptance criteria.

## Overview

AI-Selenium-Tests is a Java-based test generation tool that bridges the gap between requirement documentation and test automation. By simply providing a user story with acceptance criteria, the system generates production-ready Selenium test code with TestNG framework integration and optional Extent Reports support.

This project eliminates the manual effort of writing boilerplate test code, allowing QA engineers and developers to focus on defining test scenarios rather than implementing repetitive test structures. The generated tests include proper WebDriver setup, teardown, assertions, and comprehensive reporting capabilities.

## Features

- **AI-Powered Test Generation**: Automatically generates Selenium test scripts from natural language user stories using OpenAI GPT models
- **TestNG Integration**: Full support for TestNG annotations, assertions, and test lifecycle management
- **Extent Reports Support**: Optional generation of rich HTML test reports with screenshots on failure
- **JSON Test Case Support**: Generate tests from structured JSON test case definitions
- **Automatic Code Saving**: Generated tests are automatically saved with timestamped filenames for version tracking
- **Configurable API Integration**: Easy configuration for OpenAI API key management
- **ChromeDriver Automation**: Generated tests include proper WebDriver initialization and cleanup

## Prerequisites

Before you begin, ensure you have the following installed on your system:

- **Java Development Kit (JDK) 17** or higher
- **Apache Maven 3.6+**
- **Google Chrome Browser** (for test execution)
- **OpenAI API Key** with access to GPT models

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/Owaisbewnak/AI-Selenium-Tests.git
cd AI-Selenium-Tests/ai-selenium-test-generator
```

### 2. Configure OpenAI API Key

Create or edit the `config.properties` file in the project root directory:

```properties
OPENAI_API_KEY=your_openai_api_key_here
```

Alternatively, you can set the API key as an environment variable:

```bash
export OPENAI_API_KEY=your_openai_api_key_here
```

### 3. Build the Project

```bash
mvn clean install
```

This will download all required dependencies including Selenium, TestNG, OpenAI Java SDK, and ExtentReports.

## Project Structure

```
ai-selenium-test-generator/
├── config.properties              # OpenAI API key configuration
├── pom.xml                        # Maven project configuration
├── generated-tests/               # Output directory for generated tests
├── reports/                       # ExtentReports output directory
│   └── screenshots/               # Screenshot capture on test failures
├── test-output/                   # TestNG default output directory
└── src/
    ├── main/java/
    │   ├── Config.java                        # Configuration properties loader
    │   ├── Main.java                          # Simple OpenAI connection test
    │   ├── OpenAIClientWrapper.java           # OpenAI API client wrapper
    │   ├── SeleniumCodeGenerator.java         # Generate tests from JSON test cases
    │   ├── SeleniumCodeGeneratorWithExtentReport.java  # Generate tests with reports
    │   ├── GenerateTestFromStory.java         # Main class for user story input
    │   ├── GenerateTestFromExternalizeStory.java       # External file-based generation
    │   ├── JsonTestCaseGenerator.java         # JSON test case parser
    │   └── UserStoryReader.java               # User story file reader
    └── test/
        ├── java/                   # Generated and manual test classes
        └── resources/              # Test resources and JSON test cases
```

## Usage

### Basic Test Generation from User Story

Run the `GenerateTestFromStory` class to generate a test from an inline user story:

```bash
mvn exec:java -Dexec.mainClass="GenerateTestFromStory"
```

The class contains a sample user story:

```java
String userStory = """
    As a registered user,
    I want to log in to the website with valid credentials
    so that I can access my dashboard.

    Acceptance Criteria:
    - Navigate to https://opensource-demo.orangehrmlive.com/
    - Verify the Page Title
    - Enter valid username and password
    - Verify successful login by checking dashboard visibility or Page Title
""";
```

### Generate Test from JSON Test Case

Place your JSON test case file in `src/test/resources/` and run:

```bash
mvn exec:java -Dexec.mainClass="SeleniumCodeGenerator"
```

### Generate Test with Extent Reports

For tests with comprehensive reporting capabilities:

```bash
mvn exec:java -Dexec.mainClass="SeleniumCodeGeneratorWithExtentReport"
```

### Running Generated Tests

After generating a test, run it using Maven:

```bash
mvn test -Dtest=GeneratedTest_YYYYMMDD_HHMMSS
```

Or run all tests:

```bash
mvn test
```

## API Reference

### OpenAIClientWrapper

The main class for interacting with OpenAI API.

| Method | Description |
|--------|-------------|
| `generateCodeFromStory(String userStory)` | Generates Selenium + TestNG test code from a user story string |
| `generateCodeFromStoryWithReport(String userStory)` | Generates test code with Extent Reports integration |
| `saveToFile(String code, String fileName)` | Saves generated code to a file |

### Config

Configuration utility class for loading API keys.

| Method | Description |
|--------|-------------|
| `getApiKey()` | Retrieves the OpenAI API key from `config.properties` |

## Generated Test Structure

The AI-generated tests follow this standard structure:

```java
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.testng.Assert;
import org.testng.annotations.*;
import com.aventstack.extentreports.*;

public class GeneratedTest_YYYYMMDD_HHMMSS {
    
    WebDriver driver;
    ExtentReports extent;
    ExtentTest test;
    
    @BeforeClass
    public void setUp() {
        // ChromeDriver initialization
        // ExtentReports setup (if enabled)
    }
    
    @Test
    public void testScenario() {
        // Test steps with assertions
        // Logging with test.info(), test.pass(), test.fail()
    }
    
    @AfterClass
    public void tearDown() {
        // Driver cleanup
        // Report flush
    }
}
```

## Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| selenium-java | 4.34.0 | Browser automation |
| testng | 7.11.0 | Test framework |
| openai-java | 4.6.1 | OpenAI API integration |
| extentreports | 5.1.2 | HTML test reporting |
| json | 20250517 | JSON parsing |

## Configuration Options

### config.properties

| Property | Required | Description |
|----------|----------|-------------|
| `OPENAI_API_KEY` | Yes | Your OpenAI API key with GPT model access |

### Model Selection

The default model used is `GPT_5`. You can modify this in `OpenAIClientWrapper.java`:

```java
.model(ChatModel.GPT_5)  // Change to desired model
```

## Examples

### Example 1: Login Test Generation

**Input User Story:**
```
As a registered user,
I want to log in to the website with valid credentials
so that I can access my dashboard.

Acceptance Criteria:
- Navigate to https://opensource-demo.orangehrmlive.com/
- Verify the Page Title
- Enter valid username and password
- Verify successful login by checking dashboard visibility
```

**Generated Test:**
The system will generate a complete Java class with:
- ChromeDriver setup with WebDriverManager
- Navigation to the login page
- Title verification assertion
- Username and password input
- Login button click
- Dashboard visibility assertion
- Proper cleanup in teardown

### Example 2: JSON Test Case Format

```json
{
  "testName": "User Profile Update Test",
  "testSteps": [
    {
      "step": 1,
      "action": "navigate",
      "target": "https://example.com/profile",
      "description": "Navigate to profile page"
    },
    {
      "step": 2,
      "action": "click",
      "target": "edit-button",
      "description": "Click edit button"
    },
    {
      "step": 3,
      "action": "input",
      "target": "name-field",
      "value": "John Doe",
      "description": "Enter new name"
    },
    {
      "step": 4,
      "action": "click",
      "target": "save-button",
      "description": "Save changes"
    },
    {
      "step": 5,
      "action": "assert",
      "target": "success-message",
      "expected": "Profile updated successfully",
      "description": "Verify success message"
    }
  ]
}
```

## Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| `API key must not be blank` | Ensure `OPENAI_API_KEY` is set in `config.properties` |
| `No content in OpenAI response` | Check API rate limits and model availability |
| `ChromeDriver not found` | Update to Selenium 4.x which includes WebDriverManager |
| `Connection refused` | Verify internet connectivity and API endpoint access |

### Debug Mode

Enable detailed logging by adding to your run command:

```bash
mvn exec:java -Dexec.mainClass="GenerateTestFromStory" -Djava.util.logging.config.file=logging.properties
```

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [OpenAI](https://openai.com/) for providing the GPT API
- [Selenium](https://www.selenium.dev/) for browser automation capabilities
- [TestNG](https://testng.org/) for the testing framework
- [ExtentReports](http://extentreports.com/) for beautiful test reporting

## Author

**Owaisbewnak**

- GitHub: [@Owaisbewnak](https://github.com/Owaisbewnak)
- Repository: [AI-Selenium-Tests](https://github.com/Owaisbewnak/AI-Selenium-Tests)

---

*This project was generated with the help of AI-powered test automation.*
