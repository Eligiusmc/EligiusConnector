# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.0](https://github.com/Eligiusmc/EligiusConnector/compare/v1.1.0...v1.2.0) (2026-07-02)


### Features

* bidirectional role sync via Vault/LuckPerms, refactor chat/console/counters/player_events/profile/status/synchronization configs, clean dead code ([ec528ac](https://github.com/Eligiusmc/EligiusConnector/commit/ec528ace8723a01cb5b2071b5412f161a162329b))
* chat bridge via webhook, status embed fix, verify enhancements ([65763a6](https://github.com/Eligiusmc/EligiusConnector/commit/65763a6f6192d676430f944ba91d928e026e50e4))
* panel system + i18n refactor - configs sin textos hardcoded ([e6498db](https://github.com/Eligiusmc/EligiusConnector/commit/e6498db9d26f52568bd453640e775a1acb4ce2a3))
* PlaceholderAPI integration + cleanup ([97df222](https://github.com/Eligiusmc/EligiusConnector/commit/97df222bd57a641ac0b923f34e05d5cb3671c04d))
* server status counter, verified role sync, lang strings ([ce701a5](https://github.com/Eligiusmc/EligiusConnector/commit/ce701a5881f93741f8d03e6de919760a849ebf7a))
* v2.0 modular refactor - compilation fix and server test ([0f5d0e3](https://github.com/Eligiusmc/EligiusConnector/commit/0f5d0e37c0c01ad3e2bbaf3f1413aac3e5ea8e89))
* v2.0 modular refactor - config splitting, events system, webhooks, console reader, player stats, i18n ([28d52fc](https://github.com/Eligiusmc/EligiusConnector/commit/28d52fcc094f033f7709d5a42fb7611763d040e0))


### Bug Fixes

* add missing DB columns for existing tables + keep nickname debug logs ([f416e09](https://github.com/Eligiusmc/EligiusConnector/commit/f416e0936a170595fa0c31f02f18161bbc31a804))
* button handlers deferReply + try-catch, counters auto-detect ([9b73ef1](https://github.com/Eligiusmc/EligiusConnector/commit/9b73ef1b319ae3386b12d1342a6190ac5b1a42cc))
* counter debounce+cooldown prevents Discord rate limits, restore purgeMessages bulk delete, MiniMessage gradient logger, README+assets from ecosystem, restore DM verify, fix first_join migration ([03dc20d](https://github.com/Eligiusmc/EligiusConnector/commit/03dc20d53149aaa9d4ff588afc3cce3b2b51b16c))
* filter recipe advancements with key check (spigot-safe), register player_health/player_food placeholders, use individual deletes in clearChannel for messages &gt;14 days ([9c7dc10](https://github.com/Eligiusmc/EligiusConnector/commit/9c7dc102e5b015e2f03e2eaf8d2c22e433fadf5b))
* getStringOrDefault return default on empty string and null map ([feb594f](https://github.com/Eligiusmc/EligiusConnector/commit/feb594f1b7594736d8151bbb9819e1e5cafdad2d))
* make Modrinth/Hangar steps conditional on token, remove duplicate docs workflow ([f535ee8](https://github.com/Eligiusmc/EligiusConnector/commit/f535ee8d97fb6ea96150004ccba8ab07cdbcd90d))
* make release Modrinth/Hangar steps conditional on token ([f829eff](https://github.com/Eligiusmc/EligiusConnector/commit/f829eff505c42eed4b575d293a166121352771c9))
* nickname sync NPE when getNickname() returns null ([957e9de](https://github.com/Eligiusmc/EligiusConnector/commit/957e9dea8e04aa6ec7533c24f4fbcf84b4e72f07))
* status embed offline, counters rename, config resolution bugs ([789b2ba](https://github.com/Eligiusmc/EligiusConnector/commit/789b2bae019be32e303fcefd3b9ae38048009320))
* support offline players for profile and money buttons, customizable money embed, profile fallback with DB name ([7265933](https://github.com/Eligiusmc/EligiusConnector/commit/72659330f4bf16b92606fe628d94f61486015598))
* use PAPI %server_unique_joins% for all_members counter instead of Discord member count ([3956b4a](https://github.com/Eligiusmc/EligiusConnector/commit/3956b4a4873b01aa78e9f5c2b6ea99ac8818ca01))
* verify ephemeral, status channel cleanup, nickname sync ([a1875ba](https://github.com/Eligiusmc/EligiusConnector/commit/a1875ba965bcaf125cf0e22415eb05414efd553b))

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
