# **CenturyAvenue-Plus**

*Read this in [English](README_en.md).*

一个将商业或开源大语言模型的api映射为OpenAI风格的api的工具，名称取自上海换乘客流最大地铁站“世纪大道”。

## **特征**
- 1、目前支持所有使用OpenAI兼容接口的大模型（ChatGPT、DeepSeek、智谱等）
- 2、目前（仅）支持`/v1/chat/completions`、`/v1/models`、`/v1/embeddings`接口
- 3、支持上述两个接口向指定url的转发

## **用法**
### 通过Releases使用
0、前提条件：
- Jdk21
- JAVA_HOME及Path环境变量配置

1、从[Releases](https://github.com/FuturePrayer/ca-plus/releases)页面下载最新版jar构建；

2、你可以通过配置文件初始化。`config.yaml`配置文件示例：
```yaml
auth:
  username: 登录web控制台的用户名（可选，默认admin）
  password: 登录web控制台的密码（可选，默认随机生成并打印在日志中）
  api-key: 调用api的api-key（可选，不配置时不进行鉴权）
```
如果你不想写配置文件，你也可以通过环境变量指定这些参数：
Linux/macOS：
```bash
export CAPLUS_USERNAME=admin
export CAPLUS_PASSWORD=admin
export CAPLUS_API_KEY=your_api_key
```
Windows：
```bash
set CAPLUS_USERNAME=admin
set CAPLUS_PASSWORD=admin
set CAPLUS_API_KEY=your_api_key
```
3、执行`java -jar`命令
```bash
java -jar ca-plus.jar
```
注意将jar名称修改为你下载的构建的名称。如果你使用了配置文件，需要在上述命令后面追加：`--spring.config.import=file:/path/to/config.yaml`

4、访问 `http://localhost:14523/v1/models` 即可看到当前支持的大模型列表，访问 `http://localhost:14523/index.html` 即可访问web控制台。

### 从源代码构建
0、前提条件：
- Jdk21
- Maven
- JAVA_HOME及Path环境变量配置

1、
```bash
git clone https://github.com/FuturePrayer/ca-plus.git
```
2、
```bash
cd century_avenue
```
3、
```
mvn -Dmaven.test.skip=true clean package
```

## **局限性**
- 1、当前支持的大模型较少，本人精力、财力有限，无法购买每一款商业大模型或运行每一款开源大模型用来做适配，故欢迎各位大佬们参与贡献；
- 2、函数调用操作未经验证，请谨慎使用。

## **参与贡献**
- 1、代码合并需提供完整测试报告，格式不限，但内容需要包括流式调用、非流式调用、配置了对接参数的情况下模型列表接口的返回以及未配置时是否可用。

## **鸣谢**
- [@FuturePrayer](https://github.com/FuturePrayer)：sihuangwlp@petalmail.com
- （还有你）
