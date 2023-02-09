# Omega SMP plugin

## What is it?
The Omega SMP plugin is a Spigot server plugin that originates from the Omega SMP (I know, shocker).
It's used to give a Minecraft server a unique twist, where each player gets five lives
that they lose upon dying to another player. When a player reaches zero lives they get
death banned, unable to rejoin the server until someone revives them.

## How does it work?
The Omega SMP plugin utilizes the Spigot API to be compatible with nearly every major
Minecraft server software while maintaining stability and longevity. It also utilizes
some third-party libraries like SkullCreator.

## How do I install this?
#### DISCLAIMER: This plugin's native server software is SpigotMC for 1.19. Everything else, such as other softwares (Paper, Purpur, Mirai and so on) MIGHT WORK, but you should be aware that you're using the product AS-IS. Do not contact me about faulty behavior if you're using a less-known server software (everything besides Spigot, Paper and Purpur) or versions older than 1.19.
Glad you're interested!\
First of all you'll need a Minecraft server. For this we will
be using the very server software that's currently being used on the Omega SMP, which
is [PurpurMC](https://purpurmc.org). We will download the latest version of Purpur
and make sure we have Java 17 installed by entering ```java --version``` into a terminal.
If the version is 17 or up, we continue. If not, we install Java 17 and continue.\
After that we will make a folder for our server and put the .jar file we got from PurpurMC
into it. Then we will create a new file. This part is dependant on your OS, so follow along:\
\
Windows: Create a text document named ```start.bat```\
Linux: Create a file named ```start.sh```\
MacOS: Buy a new computer, I don't even know why you have one.\
\
Now open up the file in a text editor and paste the following command:
```shell
java -jar -Xms512M -Xmx2G server.jar nogui
```

Save the file and run it from your terminal:\
\
Windows:
```shell
./start.bat
```

Linux:
```shell
./start.sh
```

MacOS:
```shell
sudo buy new pc why are you using this fkin garbage whats wrong with you
```

Your server should crash, but that's perfectly normal. It has created a new file in 
your server's folder named ```eula.txt```. Open the file and set the value inside to ```true```.\
Now make a directory named ```plugins```. Drop the Omega SMP plugin in there and 
restart your server. It should work fine, but if not you can contact me. Speaking of...

## How do I contact you?
If you're having problems with the plugins you can open an issue on the Issues tab.\
However, if you want a more intricate parasocial relationship, you can join
[my discord server](https://dsc.gg/nvgat).

## I found a bug, how do I report it?
If you found a bug, open an issue and I'll adress it as soon as possible. Keep in mind
that I am not a professional. I'm a 15 year old hobbyist so the next plugin update
might take long to roll out. That being said, I'll try my best.

## Something's not working, what do I do?
Skill issue