# ReQuests

ReQuests is a Paper quest plugin that presents quests in Minecraft books and
allows players to interact with quests through FancyNpcs NPCs.

## Features

- Interactive written-book quest menus
- FancyNpcs quest-giver integration
- Item-give and crafting objectives
- Money, experience, and item rewards
- Quest duration and cooldown configuration
- Automatic removal of expired quest progress

## Requirements

- Java 21
- Paper 1.21.1 or compatible
- [Vault](https://www.spigotmc.org/resources/vault.34315/)
- A Vault-compatible economy provider
- [FancyNpcs](https://modrinth.com/plugin/fancynpcs)

## Installation

1. Build the plugin:

   ```bash
   ./gradlew shadowJar
   ```

2. Put the generated JAR from `build/libs/` into the server's `plugins`
   directory.
3. Install Vault, an economy provider, and FancyNpcs.
4. Start the server once to generate `plugins/ReQuests/config.yml`.
5. Configure quests and restart or reload the plugin.

## Building

The project uses Gradle. To compile and package the plugin:

```bash
./gradlew build
```

The runnable JAR is created in `build/libs/`.

## Configuration

The plugin currently requires an `sql` section to exist before it loads quests.
The SQL connection fields are not used by the active quest configuration.

```yaml
locale: en

sql:
  enabled: false

quests:
  cherry:
    name: cherry
    display-name: Cherry Blossoms
    description: Bring cherry saplings to the NPC.
    npc: npc_sakura
    duration: 14400000
    cooldown: 86400000
    objectives:
      0:
        type: GIVE
        target: minecraft:cherry_sapling
        amount: 10
    rewards:
      0:
        type: MONEY
        amount: 1000
      1:
        type: EXP
        amount: 100
      2:
        type: ITEM
        identifier: minecraft:diamond
        amount: 3

locales:
  en:
    quest-not-found: Quest not found.
    quest-accepted: Quest accepted.
    syntax-error: Syntax error.
  ru:
```

`duration` and `cooldown` are stored in milliseconds:

- `duration`: time allowed to complete an accepted quest
- `cooldown`: cooldown value stored with quest data

`npc` must match the ID configured for a FancyNpcs NPC.

### Objective types

| Type | Target |
| --- | --- |
| `GIVE` | Minecraft item ID, for example `minecraft:cherry_sapling` |
| `CRAFT` | Minecraft item ID |
| `KILL` | Entity or target identifier |
| `SMELT` | Minecraft item ID |
| `BREW` | Item or potion identifier |

`GIVE` is detected when the player interacts with the quest-giver NPC.
`CRAFT` is detected after a player crafts an item.

### Reward types

| Type | Required fields |
| --- | --- |
| `MONEY` | `amount` |
| `EXP` | `amount` |
| `ITEM` | `identifier`, `amount` |

Vault is required for `MONEY` rewards. Item identifiers should be valid
Minecraft block or item IDs.

## Commands

Quest acceptance is initiated from the player's written-book menu. The
underlying console command is:

```text
/requests <player> <quest-name> accept
```

Example:

```text
/requests Steve cherry accept
```

The command is registered for the server console and requires operator
permission for the command source.

## Development

The source layout follows the standard Gradle Java plugin structure:

- `src/main/java` - plugin sources
- `src/main/resources` - `plugin.yml` and default configuration
- `build/libs` - generated plugin JARs

The project targets Java 21 and uses Paper's modern lifecycle command API.
