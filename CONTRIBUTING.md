# Contributing to EligiusConnector

First off, thank you for considering contributing to EligiusConnector. It's people like you that make EligiusConnector such a great tool.

## Where do I go from here?

If you've noticed a bug or have a feature request, make sure to check our [issues](https://github.com/Eligiusmc/EligiusConnector/issues) if someone else in the community has already made a ticket. If not, go ahead and [make one](https://github.com/Eligiusmc/EligiusConnector/issues/new)!

## Fork & create a branch

If this is something you think you can fix, then [fork EligiusConnector](https://help.github.com/articles/fork-a-repo) and create a branch with a descriptive name.

A good branch name would be (where issue #325 is the ticket you're working on):

```bash
git checkout -b 325-add-japanese-localisation
```

## Build

This project requires **Java 21 LTS** to compile.

```bash
git clone https://github.com/Eligiusmc/EligiusConnector.git
cd EligiusConnector
./gradlew build
```

## Conventions

- **Branching:** Never push directly to `master`. All work should stem from `develop` into `feature/<name>` branches.
- **Commits:** We strictly enforce [Conventional Commits](https://www.conventionalcommits.org/) (e.g., `feat:`, `fix:`, `docs:`). This powers our automated Release Please pipelines.
- **Code Standards:** Maintain the Hexagonal Architecture pattern and ensure Javadocs for core services.
- **Testing:** Ensure all tests pass and maintain 90% code coverage.

## Pull Request Process

1. Update the README.md with details of changes to the interface.
2. Update the wiki documentation with any new features or configuration options.
3. The PR will be merged once you have the sign-off of at least one other developer.

## Code Review Process

All submissions require review before merging. We use GitHub pull requests for this purpose. Consult [GitHub Help](https://help.github.com/articles/about-pull-requests/) for more information on using pull requests.

## Community

Join our [Discord server](https://discord.gg/eligius) for discussion, support, and collaboration.

## License

By contributing to EligiusConnector, you agree that your contributions will be licensed under the [MIT License](LICENSE).
