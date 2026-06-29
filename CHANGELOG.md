# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0](https://github.com/Eligiusmc/EligiusConnector/compare/v1.0.0...v1.1.0) (2026-06-29)


### Features

* initial project structure ([2fa3ffd](https://github.com/Eligiusmc/EligiusConnector/commit/2fa3ffdd8c505ef1b2770f111ba88f821ede756e))


### Bug Fixes

* add gradle-wrapper.properties ([3f6ddc9](https://github.com/Eligiusmc/EligiusConnector/commit/3f6ddc971a3064734fbad12e8c26a6fe3445a33e))
* update gradle wrapper jar ([5b33f20](https://github.com/Eligiusmc/EligiusConnector/commit/5b33f20d104f81500151631c589ccf0860c79d63))
* update gradlew script ([f7bcc52](https://github.com/Eligiusmc/EligiusConnector/commit/f7bcc52bbe30ef3840133f38efbf01b8c365648c))

## [Unreleased]

### Added
- Initial project structure
- Account linking system (Discord ↔ Minecraft)
- Chat bridge (MC ↔ Discord)
- Console remote control
- Role synchronization (LuckPerms)
- Player queries (profile, inventory, location)
- Custom events system
- Chat filters with escalation
- Audit logging
- PlaceholderAPI integration
- bStats integration

### Changed
- N/A

### Deprecated
- N/A

### Removed
- N/A

### Fixed
- N/A

### Security
- N/A

## [1.0.0] - 2026-06-29

### Added
- Initial release
- Account linking with code verification
- Chat bridge with configurable formats
- Console remote with blacklist/whitelist
- Role synchronization with LuckPerms
- Player queries from Discord
- Custom events from YAML configuration
- Chat filters with escalating punishment
- Audit logging to database
- PlaceholderAPI expansion
- SQLite, MySQL, and Redis support
- bStats anonymous metrics
- Full i18n support
- Wiki documentation (EN, ES)
