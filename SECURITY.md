<!-- Source: GitHub security policy and private vulnerability reporting (official) — https://docs.github.com/en/code-security/how-tos/report-and-fix-vulnerabilities/configure-vulnerability-reporting/add-security-policy -->
# Security Policy

## Supported Versions

This project has no tagged releases yet. Security fixes land on the `master` branch, which is the only supported version.

| Version | Supported |
| --- | --- |
| `master` | :white_check_mark: |
| Older commits and forks | :x: |

## Scope

This repository is a clean-room skeleton for JavaPOS cash-changer integrations. Reports that are in scope include, for example:

- Flaws in the device state machine, retry policy, or framing logic that let a malicious or malfunctioning device drive the host into an unsafe state (for example, a dispense that is retried after it already succeeded).
- Unbounded reads, buffer handling, or parsing bugs in `SerialBridge` or a codec that a crafted device response can trigger.
- Anything in this repository that leaks credentials, device secrets, or cardholder data.

Vulnerabilities in the vendored third-party jars (`jSerialComm`, `javapos`) belong to their upstream projects, but please tell us anyway so the dependency can be updated here.

## Reporting a Vulnerability

**Please do not report security vulnerabilities through public issues, discussions, or pull requests.**

Report it privately instead. Open the **Security** tab of this repository and choose **Report a vulnerability**, or go directly to https://github.com/anyingiit/Jpos-glue/security/advisories/new. If private vulnerability reporting is unavailable, email leoycwan@gmail.com.

Please include:

- A description of the vulnerability and the impact you expect it to have.
- Steps to reproduce it, or a proof of concept.
- The versions that are affected.

We will acknowledge your report and keep you informed as we investigate and fix the issue.
