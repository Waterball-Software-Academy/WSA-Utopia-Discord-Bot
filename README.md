[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=WSA-Utopia-Discord-Bot&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=WSA-Utopia-Discord-Bot)
[![Discord invite](https://img.shields.io/badge/chat-on%20Discord-brightgreen.svg?style=social&amp;logo=discord)](https://discord.gg/waterballsa)

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

更多相關資訊請參考 [烏托邦 WIKI](https://github.com/Waterball-Software-Academy/WSA-Utopia-Discord-Bot/wiki)

# 烏托邦功勛榜
貢獻 feature 前請先參考 [How to contribute a new feature?
](https://github.com/Waterball-Software-Academy/WSA-Utopia-Discord-Bot/wiki/No.2-%E5%BB%BA%E7%AB%8B%E6%96%B0%E7%9A%84%E5%8A%9F%E8%83%BD%E6%A8%A1%E7%B5%84-(Create-Feature-Module))

<a href = "https://github.com/Waterball-Software-Academy/WSA-Utopia-Discord-Bot/graphs/contributors">
  <img src = "https://contrib.rocks/image?repo=Waterball-Software-Academy/WSA-Utopia-Discord-Bot"/>
</a>
