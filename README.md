# Jpos-glue

Clean-room JavaPOS cash-changer stack for brownfield devices (RS-232/TCP) on Linux. The design here comes from migrating 1,600+ payment terminals off a vendor-locked service into a layered, testable runtime that shipped five monthly releases with zero Sev-1 incidents. This repo keeps the architecture and patterns while scrubbing vendor specifics; `device-mybrand-cashchanger` is a drop-in style example.

## Why this exists
- Remove ambiguity in half-duplex serial comms (ENQ/response races) by using deterministic framing and explicit status commands instead of timing luck.
- Isolate responsibilities: device state machine ↔ retry policy ↔ transport bridge ↔ codec ↔ control surface (JavaPOS).
- Make reverse-engineered protocols maintainable: add automated testing/observability hooks so refactors are safe even without complete vendor docs.
- Support phased cutovers: run new comms in parallel, toggle traffic gradually, and retain the ability to abort/revert at any boundary.

## Module map
```
control-core/               // Device state, retry/timeout policy, service/control scaffolding
comm-adapters/serial-bridge // Transport adapter (jSerialComm) with ACK gating and framed reads
device-mybrand-cashchanger/ // Example device plugin: codec + service + control + bridge wiring
```

## Reliability patterns baked in
- **Deterministic state**: `DeviceStateMachine` keeps device lifecycle explicit (CLOSED/CLAIMED/ENABLED/BUSY/IDLE/ERROR).
- **Retry + timeout + abort**: `RetryPolicy` wraps each command with bounded retries, timeouts, and device-specific abort bytes.
- **Framed I/O**: `SerialBridge` enforces ACK-before-body and terminates reads via predicates (no leaking partials across calls).
- **Codec isolation**: Device-specific framing/parsing lives in `MyCashChangerCodec`, keeping transport and business logic orthogonal.
- **Thread safety**: Single-threaded executor in the service plus synchronized control methods prevent concurrent line collisions.

## Running the example
Prereqs: Java 17+ and the Gradle wrapper.

Build everything:
```
./gradlew build
```

Build only the sample device:
```
./gradlew :device-mybrand-cashchanger:build
```

Lightweight usage sketch (replace `COM3` with your loopback/USB-serial port and a real codec):
```java
var control = new com.example.mybrand.MyCashChangerControl();
control.dispenseCash("1000,1");      // command with retry/timeout policy
var counts = control.readCashCounts();
control.smartDispense(5000);         // device-specific extension
```

## Porting this skeleton to a real device
1) Clone `device-mybrand-cashchanger`, rename the package, and point the bridge at your port/baud.  
2) Implement `encode`/`decode` helpers and end-of-message predicates in your codec.  
3) Map device operations to `Command` (or add more), wiring them through `runWithPolicy`.  
4) Set per-command `RetryPolicy` timeouts/abort bytes based on protocol guarantees.  
5) Add integration tests with a serial emulator or loopback fixture; capture traces to lock down framing.  
6) Layer in observability (correlation IDs, structured logs, health checks) before rollout.  

## Production results (context for this repo)
- 1,600+ terminals migrated from a black-box service to this layered runtime.
- Eliminated ENQ/response ambiguity; risk window reduced to structurally impossible by replacing ENQ with a distinct status command.
- Automated tests (JUnit/Gradle/Jenkins) reached ~79% line / 70% branch coverage (JaCoCo), enabling fearless refactors in a reverse-engineered codebase.
- Five consecutive monthly releases, zero Sev-1 incidents, zero rollbacks.

## Backlog / TODOs
- Add integration tests against a serial mock or hardware-in-the-loop harness.
- Provide structured logging/metrics hooks (latency, retries, aborts, framing errors).
- Publish artifacts to an internal Maven repo and document JavaPOS service.xml wiring.
- Expand the sample to include TCP transport and a second device to illustrate multi-tenant deployments.
