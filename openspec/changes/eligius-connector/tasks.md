# Tasks: EligiusConnector

## Phase 1 — MVP

### 1. Project Setup
- [ ] Create Gradle project structure
- [ ] Configure build.gradle.kts
- [ ] Add dependencies (JDA, PacketEvents, LuckPerms, PlaceholderAPI)
- [ ] Create main plugin class
- [ ] Create configuration manager
- [ ] Create database manager (SQLite/MySQL/Redis)

### 2. Account Linking
- [ ] Implement Account domain
- [ ] Implement AccountRepository port
- [ ] Implement SQLite/MySQL adapter
- [ ] Implement Discord command /verify
- [ ] Implement MC command /verify
- [ ] Implement MC command /unlink
- [ ] Add rate limiting
- [ ] Add code expiration

### 3. Chat Bridge
- [ ] Implement Chat domain
- [ ] Implement ChatFormatter
- [ ] Implement ChatFilter
- [ ] Implement Discord adapter
- [ ] Implement Minecraft adapter
- [ ] Add filter escalation
- [ ] Add webhooks support

### 4. Console Remote
- [ ] Implement Console domain
- [ ] Implement ConsoleRepository port
- [ ] Implement Discord command /console
- [ ] Add blacklist/whitelist
- [ ] Add rate limiting
- [ ] Add audit logging

### 5. Role Sync
- [ ] Implement RoleSync domain
- [ ] Implement LuckPerms adapter
- [ ] Implement Discord adapter
- [ ] Add cycle-based sync
- [ ] Add on-link sync
- [ ] Add multi-server support

### 6. Notifications
- [ ] Implement Notification domain
- [ ] Implement NotificationRepository port
- [ ] Implement EventListener
- [ ] Add configurable messages
- [ ] Add Minecraft display (chat/actionbar/title)

### 7. Player Queries
- [ ] Implement Query domain
- [ ] Implement QueryRepository port
- [ ] Implement Discord commands (/player *)
- [ ] Add PacketEvents adapter
- [ ] Add LuckPerms adapter
- [ ] Add PlaceholderAPI integration

### 8. Chat Filters
- [ ] Implement Filter domain
- [ ] Implement FilterRepository port
- [ ] Implement FilterEngine
- [ ] Add spam detection
- [ ] Add profanity detection
- [ ] Add link detection
- [ ] Add caps detection
- [ ] Add punishment escalation

### 9. Audit Log
- [ ] Implement Audit domain
- [ ] Implement AuditRepository port
- [ ] Implement SQLite/MySQL adapter
- [ ] Add export functionality
- [ ] Add search functionality

### 10. Security
- [ ] Implement SecurityManager
- [ ] Add token validation
- [ ] Add permission validation
- [ ] Add rate limiting
- [ ] Add encryption
- [ ] Add authorized servers

### 11. PlaceholderAPI
- [ ] Implement ConnectorExpansion
- [ ] Register expansion
- [ ] Add cache
- [ ] Add external placeholder support

### 12. Testing
- [ ] Unit tests for all domains
- [ ] Integration tests for Paper API
- [ ] Mock tests for JDA, PacketEvents, LuckPerms
- [ ] Achieve 90% coverage

### 13. Documentation
- [ ] Create wiki structure
- [ ] Write getting started guide
- [ ] Write configuration guide
- [ ] Write commands guide
- [ ] Write permissions guide
- [ ] Write placeholders guide
- [ ] Update README
- [ ] Generate Javadocs

### 14. CI/CD
- [ ] Create GitHub Actions workflow
- [ ] Configure build
- [ ] Configure test
- [ ] Configure release
- [ ] Configure Release Please

### 15. Publishing
- [ ] Publish to Modrinth
- [ ] Publish to Hangar
- [ ] Publish to SpigotMC
- [ ] Publish to GitHub Releases

## Phase 2 — Post-MVP

### 16. Custom Events
- [ ] Implement Event domain
- [ ] Implement EventRepository port
- [ ] Implement YAML parser
- [ ] Implement timer trigger
- [ ] Implement command trigger
- [ ] Add hooks (on-start, during, on-end)

### 17. Integrations
- [ ] Implement PlotProtectionStones adapter
- [ ] Implement AdvancedJobs adapter

### 18. Multi-Server
- [ ] Implement Redis adapter
- [ ] Add cross-server sync
- [ ] Add conflict resolution

### 19. Web Dashboard
- [ ] Create web dashboard
- [ ] Add authentication
- [ ] Add configuration UI
- [ ] Add analytics

## Timeline
- **Week 1-2:** Phase 1 (MVP)
- **Week 3-4:** Phase 2 (Post-MVP)
- **Week 5:** Testing & Documentation
- **Week 6:** Publishing & Release
