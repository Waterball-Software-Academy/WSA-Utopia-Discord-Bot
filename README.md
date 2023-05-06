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

## How to use it

maven

```sh
mvn clean package
```

run jar

```sh
java -jar bot/target/bot-1.0-SNAPSHOT-jar-with-dependencies.jar
```
