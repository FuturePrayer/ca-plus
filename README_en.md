# **CenturyAvenue-Plus**

*A tool that maps APIs of commercial or open-source large language models (LLMs) to OpenAI-style APIs. Named after Shanghai's busiest subway transfer station, "Century Avenue."*

## **Features**
- 1. Currently, supports all LLMs with OpenAI-compatible APIs (ChatGPT, DeepSeek, Zhipu, etc.).
- 2. Currently **only** supports the following endpoints:
    - `/v1/chat/completions`
    - `/v1/models`
    - `/v1/embeddings`
- 3. Supports forwarding the above endpoints to specified URLs.

## **Usage**

### **Using Pre-built Releases**
0. **Prerequisites**:
    - JDK 21
    - Properly configured `JAVA_HOME` and `Path` environment variables.

1. Download the latest JAR build from the [Releases](https://github.com/FuturePrayer/ca-plus/releases) page.

2. Configure initialization via a YAML file. Example `config.yaml`:
   ```yaml  
   auth:  
     username: "Web console login username (optional, default: admin)"  
     password: "Web console login password (optional, randomly generated and logged if not set)"  
     api-key: "API key for calling endpoints (optional, no authentication if omitted)"  
   ```  

   Alternatively, you may use environment variables:

   **Linux/macOS**:
   ```bash  
   export CAPLUS_USERNAME=admin  
   export CAPLUS_PASSWORD=admin  
   export CAPLUS_API_KEY=your_api_key  
   ```  

   **Windows**:
   ```bash  
   set CAPLUS_USERNAME=admin  
   set CAPLUS_PASSWORD=admin  
   set CAPLUS_API_KEY=your_api_key  
   ```  

3. Run the JAR:
   ```bash  
   java -jar ca-plus.jar  
   ```  
   Replace `ca-plus.jar` with your downloaded build name.  
   *If using a config file, append:* `--spring.config.import=file:/path/to/config.yaml`

4. Access `http://localhost:14523/v1/models` to view the list of supported LLMs.  
   Access `http://localhost:14523/index.html` to view the web console.

### **Building from Source**
0. **Prerequisites**:
    - JDK 21
    - Maven
    - Properly configured `JAVA_HOME` and `Path` environment variables.

1. Clone the repository:
   ```bash  
   git clone https://github.com/FuturePrayer/ca-plus.git  
   ```  

2. Navigate to the project directory:
   ```bash  
   cd century_avenue  
   ```  

3. Build the project (skipping tests):
   ```bash  
   mvn -Dmaven.test.skip=true clean package  
   ```  

## **Limitations**
- 1. Limited supported models due to constraints in resources (time, budget)—unable to purchase every commercial LLM or adapt every open-source model. Contributions are welcome!
- 2. Function calling is untested; use with caution.

## **Contributing**
- 1. Merge requests must include full test reports (format flexible), covering:
    - Streaming calls
    - Non-streaming calls
    - Model listing endpoint behavior (with/without configured parameters)

## **Acknowledgments**
- [@FuturePrayer](https://github.com/FuturePrayer): sihuangwlp@petalmail.com
- *(And you!)*