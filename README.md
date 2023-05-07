# 環境準備
- Java 17
- Maven

可以透過下列指令檢查使用的是否為 java 17 版本
```
> java --version
openjdk 17.0.4 2022-07-19
OpenJDK Runtime Environment Temurin-17.0.4+8 (build 17.0.4+8)
OpenJDK 64-Bit Server VM Temurin-17.0.4+8 (build 17.0.4+8, mixed mode, sharing)

> mvn --version
Apache Maven 3.8.6 (84538c9988a25aec085021c365c560670ad80f63)
Maven home: /opt/homebrew/Cellar/maven/3.8.6/libexec
Java version: 18.0.2, vendor: Homebrew, runtime: /opt/homebrew/Cellar/openjdk/18.0.2/libexec/openjdk.jdk/Contents/Home
Default locale: zh_TW_#Hant, platform encoding: UTF-8
OS name: "mac os x", version: "13.0.1", arch: "aarch64", family: "mac"
```

# 如何在本地環境執行專案

## 環境變數
| 環境變數  | description |
|----------------|-----------------------|
| BOT_TOKEN      |   Discord bot token   |
| CHATGPT_TOKEN  |   OpenAI API key      |
| DEPLOYMENT_ENV |   [beta, prod]        |

如何設定環境變數
export {環境變數}=..

範例
```
export DEPLOYMENT_ENV=beta
```
## 執行

打包 jar 
```
> mvn clean package
```

執行

```sh
java -jar main/target/main-1.0-SNAPSHOT-jar-with-dependencies.jar
```
