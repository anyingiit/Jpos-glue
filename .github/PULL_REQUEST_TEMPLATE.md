<!-- Source: GitHub pull request template (official) — https://docs.github.com/en/communities/using-templates-to-encourage-useful-issues-and-pull-requests/creating-a-pull-request-template-for-your-repository -->
## Description

<!-- What does this pull request change, and why? -->

## Related issue

Closes #

## Affected modules

<!-- Tick everything this change touches. -->

- [ ] `control-core` (state machine, retry policy, service/control scaffolding)
- [ ] `comm-adapters/serial-bridge` (transport)
- [ ] `device-mybrand-cashchanger` (example device plugin)
- [ ] Build, CI, or docs

## Protocol impact

<!-- Delete this section if the change does not touch device communication. -->

- Framing or codec changes:
- Retry, timeout, or abort-byte changes:
- Hardware or loopback testing performed:

## Checklist

- [ ] `./gradlew build` passes locally
- [ ] Tests added or updated for the change
- [ ] `CHANGELOG.md` is updated (if applicable)
- [ ] Documentation is updated (if applicable)
