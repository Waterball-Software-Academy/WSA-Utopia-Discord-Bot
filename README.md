# WSA-ALPHA-Bot

## Create a personal discord bot

* Create a discord bot
    1. Register for a Discord account. if you already own it, you can skip this step
    2. Go to Discord Developer website
        * <https://discord.com/developers/applications>
    3. Register a new application
        * Enter the name of your application
        * Agree to Discord developer policy
          <img src=".\docs\create-a-discord-bot\new-application.png" title="New Application"/>
    4. In the settings sidebar, click `Bot`, and then select `Add Bot`
       <img src=".\docs\create-a-discord-bot\add-bot.png" title="Add bot"/>
* Grant gateway intents permissions
    1. In bot settings page, find the `Privileged Gateway Intents`
    2. Enable `PRESENCE INTENT`, `SERVER MEMBERS INTENT`,and `MESSAGE CONTENT INTENT`
    3. Don't enable `REQUIRES OAUTH2 CODE GRANT`, otherwise you will not be able to create an invitation link
       <img src=".\docs\create-a-discord-bot\grant-gateway-intents-permissions.png" title="Grant gateway intents permissions"/>
* Grant bot access to the `火球軟體學院(Beta)` Discord server
    1. In the settings sidebar, click `OAuth2` and select `URL Generator`
        * Under `SCOPES`, enable `bot` permission
          <img src=".\docs\create-a-discord-bot\bot-permission.png" title="Bot permission"/>
        * Under `BOT PERMISSIONS`, enable `Administrator` permission
          <img src=".\docs\create-a-discord-bot\administrator-permission.png" title="Admin permission"/>
        * Copy the `GENERATED URL` below
          <img src=".\docs\create-a-discord-bot\generated-url.png" title="Generated url"/>
    2. Enter generated url
        * Select `火球軟體學院(Beta)` server to add the bot
        * Note: Please notify `Alpha Tech` to get the invitation link for `火球軟體學院(Beta)`
          <img src=".\docs\create-a-discord-bot\add-bot-to-server.png" title="Add bot to server"/>
        * Grate the bot access to the `火球軟體學院(Beta)` server
          <img src=".\docs\create-a-discord-bot\grate-bot-access-server.png" title="Grate bot access server"/>
        * Verify that the bot is granted
          <img src=".\docs\create-a-discord-bot\grate-bot.png" title="Grate bot"/>
* Generate a Discord bot token.
    1. Return to Discord Developer website
    2. Click the `Reset Token` button in bot settings page
       <img src=".\docs\create-a-discord-bot\reset-token.png" title="Reset token"/>
    3. Save this token to your note
       <img src=".\docs\create-a-discord-bot\copy-token.png" title="Copy token"/>

## Recommend IDE Setting

* Auto import package
* Auto ensure every saved file ends with a line break
* Setup SDK -> Min SDK version: 17

### IntelliJ IDEA Setting

* Auto import package
    1. Open your IDE and go to `Settings` page (hotkey: Ctrl + Alt + S)
    2. Search for keyword: `Auto Import`
    3. Kotlin: Enable `Add unambiguous imports on the fly`
       <img src=".\docs\intellij-idea-setting\auto-import-package.png" title="Auto import package"/>
* Auto ensure every saved file ends with a line break
    1. Open your IDE and go to `Settings` page
    2. Search for keyword: `Editor General on save`
    3. Enable `Ensure every saved file ends with a line break`
       <img src=".\docs\intellij-idea-setting\auto-ensure-every-saved-file.png" title="Auto ensure every saved file"/>
* Setup SDK
    1. Open your IDE and go to `Project Structure` page (hotkey: Ctrl + Alt + Shift + S)
    2. In the sidebar: Project Settings -> Project
    3. Set the `SDK` for your project to java version 17 or higher
       <img src=".\docs\intellij-idea-setting\java-version.png" title="Java version"/>
* Add running environment variables
    1. Find the main file of your project:
        * Path: `{Project folder}`/WSA-Utopia-Discord-Bot\main\src\main\kotlin\tw\waterballsa\utopia\Main.kt
    2. Modify the run configuration: `Modify Run Configuration`
       <img src=".\docs\intellij-idea-setting\modify-run-config.png" title="Modify run config"/>
    3. In the Configuration tab, go to `Environment variables` and add the following variables:

       ```
       BOT_TOKEN=<your_discord_bot_token>;
       CHATGPT_TOKEN=;
       DEPLOYMENT_ENV=beta;
       ```

       <img src=".\docs\intellij-idea-setting\set-env-config.png" title="Set env config"/>

### Create a new feature module

* To create a new feature module, follow these steps:

* Create a new module using Maven. Make sure you have added the module name to the list of modules in the
  root `pom.xml`.
    1. Open your project in an IDE, such as IntelliJ IDEA
    2. Right-click on the root directory of your project, and select `New` -> `Module`
    3. In the sidebar: Choose `Maven Archetype` template
    4. Describe your project, enter your project name and choose java version 17 or higher
    5. In the `Advanced Settings` dialog, enter the following details:
        * `GroupId`: the identifier for your organization or group (e.g., "com.example")
        * `ArtifactId`: the identifier for your module (e.g., "my-module")
        * `Version`: the version of your module (e.g., "1.0-SNAPSHOT")
    6. Click `Create` to create the module and wait for building the Maven project to get `BUILD SUCCESS` message

* Declare your module's dependency in `pom.xml`
    1. Modify the default version with `${revision}` to keep it consistent with the version in the root directory.

       ```xml
       <parent>
         <artifactId>root</artifactId>
         <groupId>tw.waterballsa.utopia</groupId>
         <version>${revision}</version>
       </parent>
       ```

    2. Modify module detail

       ```xml
       <artifactId><!-- your module artifactId --></artifactId>
       <name><!-- your module name --></name>
       <description><!-- your module description --></description>
       ```

    3. Add WSA-Utopia `commons` and `discord-impl-jda` module to dependencies

       ```xml
       <dependencies>
        <dependency>
            <groupId>tw.waterballsa.utopia</groupId>
            <artifactId>commons</artifactId>
        </dependency>
        <dependency>
            <groupId>tw.waterballsa.utopia</groupId>
            <artifactId>discord-impl-jda</artifactId>
        </dependency>
       </dependencies>
       ```

    4. Reload maven project

* Make your module compile successfully in WSA-Utopia project
    1. Check your module has been successfully added to the modules in `pom.xml` of the root project

       ```xml
       <modules>
           <module>main</module>
           <module>discord-impl-jda</module>
           <module>commons</module>
           <module>landing-experience</module>
           <module>chatgpt-api</module>
           <module>forum-experience</module>
           <module><!-- your module --></module>
       </modules>
       ```

    2. Add your dependency to dependencies of dependencyManagement in `pom.xml` of root project

        ```xml
        <dependency>
            <groupId>tw.waterballsa.utopia</groupId>
            <artifactId><!-- your module --></artifactId>
            <version>${revision}</version>
        </dependency>
       ```

    3. Add your dependency to `pom.xml` of main module

       ```xml
       <dependency>
         <groupId>tw.waterballsa.utopia</groupId>
         <artifactId><!-- your module --></artifactId>
       </dependency>
       ```

    4. Reload maven project

* Create your project base architecture
    1. Remove unnecessary items from `module/src/main/resources` folder in your project
    2. Add a Kotlin folder from `module/src/main` folder and reload maven project
    3. Add `tw.waterballsa.utopia.<!-- your feature name -->` package from `module/src/main/kotlin` folder
    4. Add a Kotlin class to your module and name it following feature modules and PascalCase

* Run a Discord command on your project
    1. Obtaining the WSA academy Discord channel ID from it.
        * Dependency injection of `WsaDiscordProperties`
        * Get discord listener from JDA package

       ```
       fun helloWorld(wsa: WsaDiscordProperties) = listener {
       }
       ```

    2. Register a feature module command
        * Commands.slash(`<!-- feature module name -->`, `<!-- feature module command description -->`)
        * SubcommandData(`<!-- feature name -->`, `<!-- feature name command description -->`)
        * addOption(`<!-- command parameter data type -->`, `<!-- command parameter name -->`,
          `<!-- command parameter description -->`, `<!-- command parameter are required or not -->`)

        ```
        command {
           Commands.slash("hello", "Hello!")
              .addSubcommands(
                 SubcommandData("world", "Greeting")
                    .addOption(OptionType.STRING, "name", "Who am I greeting to.", true)
           )
        }
        ```

    3. Implement command action
        * Listening event -> on<SlashCommandInteractionEvent>
        * Check the received command in the event listener
        * Valid command parameter -> Valid function provided by WSA-Utopia
        * Asynchronously executing commands -> reply(`<!-- reply to user message -->`).queue()
        * Synchronously executing commands -> reply(`<!-- reply to user message -->`).complete()

          ```
           on<SlashCommandInteractionEvent> {
              if (fullCommandName != "hello world") {
                  return@on
              }
       
              val name = getOptionAsStringWithLimitedLength("name", 1..10)
              reply("hello world $name!").queue()

              //reply("hello world $name").complete()
           }
          ```

    4. Great! It's time to run your feature module and submit your own commands on `火球軟體學院(Beta)` !!!

## How to use it

maven

```sh
mvn clean package
```

run jar

```sh
java -jar bot/target/bot-1.0-SNAPSHOT-jar-with-dependencies.jar
```
