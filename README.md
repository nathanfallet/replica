# Replica

## Presentation

The Replica is a mini game where you have to replica the picture on the table before you the most quickly. The game spends in differents sleeves : the last player who had finished is eradicated.

## Installation

You have to add [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) on your server else the Replica doesn't work !
Take the version which correspond to your server.

### Needed tools

To install this mini game on your server, download it [here](https://www.nathanfallet.me/project/replica/download). Move the downloaded file in the folder `/plugins/` of yout server.

### Configuration

To configure the plugin, start your server a first time to generate the file `/plugins/Replica/config.yml`. When the file is generated, open it.
You will find all the options in this table :

| Property        | Description                                                                                                                 |
| --------------- | --------------------------------------------------------------------------------------------------------------------------- |
| games-amount    | The number of games to load                                                                                                 |
| countdown       | The delay before the game start                                                                                             |
| spawn-command   | The command the player will use automatically to teleport to the spawn at the end of the game.                              |
| reward-commands | The commands the console has to do when a player has won the game. Use `%player%` like the player nickname.                 |
| pictures        | Here you can find all the pictures make with [ReplicaPictureMaker](https://www.nathanfallet.me/project/replicapicturemaker) |

You have to reload/restart your server to apply changes.

## Commands

| Syntax             | Description                                                                                                                   |
| ------------------ | ----------------------------------------------------------------------------------------------------------------------------- |
| /replica goto      | Allow to teleport in the Replica's world                                                                                      |
| /replica buildmode | Allow to enable/disable the buildmode. The buildmode allow to build in the Replica's area to edit the build area of the game. |

## Video tutorials (Only in French)

https://www.youtube.com/watch?v=EOxJrOYIPDw
