# CodeRandom-Economy

A simple, lightweight economy system for Minecraft using Vault, with support for MySQL or JSON storage. This plugin requires CodeRandom-Core and Vault.

## Features

- Lightweight and easy to use
- Supports MySQL and JSON storage options
- Integrates with Vault for economy management

## Requirements

- [CodeRandom-Core](https://github.com/your-link-to-coderandom-core)
- [Vault](https://dev.bukkit.org/projects/vault)

## Installation

1. Download the latest release from the [Releases](https://github.com/D4RKJ0K3R17/CodeRandom-Economy/releases) page.
2. Place the JAR file in your Minecraft server's `plugins` directory.
3. Restart the server to generate the default configuration files.
4. Configure the plugin as needed in the generated config files.

## Configuration

Refer to the `config.yml` file for detailed configuration options. 
You can choose between MySQL and JSON for data storage. depending on your settings in CodeRandomCore configs.

## Commands

#### Default
- `/balance` - Check your current balance.
- `/pay <player> <amount>` - Pay another player.

#### Admin
- `/economy` - Shows the commands available to work with economy.
- `/economy <player>` - Shows the players balance.
- `/economy set <player> <anount>` - Set a players balance.
- `/economy deposit <player> <amount>` - Deposit money into a players account.
- `/economy withdraw <player> <amount>` - Withdraw money from a players account.


## Permissions
###### Child commands are granted if parent command is given

- `code_random.economy.admin` - Access to administrative economy commands.
  - `code_random.economy.admin.balance` - Access to `/economy <player>`
  - `code_random.economy.admin.set` - Access to `/economy set <player> <amount>`
  - `code_random.economy.admin.deposit` - Access to `/economy deposit <player> <amount>`
  - `code_random.economy.admin.withdraw` - Access to `/economy withdraw <player> <amount>`
- `code_random.economy.user` - Access to basic economy commands like checking balance and paying players.
  - `code_random.economy.user.balance` - Access to `/balance`
  - `code_random.economy.user.pay` - Access to `/pay <player> <amount>`

## Issues and Contributions

Feel free to open issues and contribute to the project by submitting pull requests. For major changes, please open an issue first to discuss what you would like to change.

## License

This project is licensed under the Creative Commons Attribution-NonCommercial 4.0 International Public License.

## Acknowledgments

- [Vault](https://dev.bukkit.org/projects/vault) - Economy API
- [CodeRandom-Core](https://github.com/your-link-to-coderandom-core) - Core functionalities

For more information, visit the [GitHub repository](https://github.com/D4RKJ0K3R17/CodeRandom-Economy).
