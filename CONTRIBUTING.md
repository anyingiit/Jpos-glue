<!-- Source: GitHub Open Source Guides (CC BY 4.0, structure only) — https://opensource.guide/starting-a-project/; GitHub Docs (official) — https://docs.github.com/en/communities/setting-up-your-project-for-healthy-contributions/setting-guidelines-for-repository-contributors -->
# Contributing

Thanks for your interest in Jpos-glue. Every kind of contribution is welcome, from a one-line typo fix to a new device plugin.

## Code of Conduct

This project is governed by its [Code of Conduct](CODE_OF_CONDUCT.md). By taking part in this project you agree to abide by its terms.

## Ways to contribute

- Report a bug you ran into.
- Suggest a feature or an improvement.
- Improve the documentation, including examples and typo fixes.
- Contribute code through a pull request — a test harness, a transport adapter, or another device plugin modelled on `device-mybrand-cashchanger` are all especially useful.

## Reporting bugs

Search the existing issues first, in case someone has already reported the same problem. If nothing matches, open a new issue from the Issues tab (Issues, then New issue) and pick the **Bug report** form. Please fill in every required field: a report that says what you expected, what happened instead, and how to reproduce it is usually fixed much faster.

For communication problems, include the transport and port settings, and a trace of the exchange if you can capture one. Framing bugs are nearly impossible to diagnose without the bytes.

## Suggesting features

Open a new issue from the Issues tab and pick the **Feature request** form. Describe the problem you want to solve before the solution you have in mind, so that other ways of solving it can be considered as well.

## Reporting security issues

Do not report security issues in public. Follow the steps in the [security policy](SECURITY.md) instead, so that the problem can be fixed before it becomes widely known.

## Submitting pull requests

1. Fork the repository and create a branch from `master`.
2. Follow the existing code style: 4-space indentation, and the layering the modules already use (state machine ↔ retry policy ↔ transport bridge ↔ codec ↔ control surface).
3. Add or update tests that cover your change.
4. If the change is worth recording, add an entry under `Unreleased` in the [changelog](CHANGELOG.md).
5. Make sure the CI checks pass.
6. Open a pull request and fill in the template.

Smaller, focused pull requests are easier to review and get merged sooner than large ones. If you plan a bigger change, it is worth opening an issue first to agree on the approach.

### Keeping the layers separate

The point of this repository is that transport, framing, and device behavior do not leak into each other. When you add code, keep it in the layer it belongs to:

- Device-specific framing and parsing goes in a codec, not in the bridge.
- Transport details (ports, baud rates, ACK gating) go in the bridge, not in the service.
- Retry counts, timeouts, and abort bytes go through `RetryPolicy`, not into ad-hoc loops.

### Vendored jars

`jSerialComm` and `javapos` are checked in under `*/libs/` because JavaPOS is awkward to resolve from public repositories. If you add a dependency that *is* published to Maven Central, declare it in the module's `build.gradle` rather than committing another jar.

## Development setup

Prerequisites: JDK 17 or newer and Git. The Gradle wrapper is checked in, so you do not need to install Gradle.

```sh
git clone https://github.com/anyingiit/Jpos-glue.git
cd Jpos-glue
./gradlew build
```

Build a single module while you work on it:

```sh
./gradlew :device-mybrand-cashchanger:build
```

CI runs `./gradlew build` on JDK 17 and 21, so run it locally before you push.

## Questions

For questions about using the project, start a thread in the Discussions tab instead of opening an issue. Issues are reserved for bug reports and feature requests.
