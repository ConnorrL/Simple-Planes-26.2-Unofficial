# Minecraft 26.2 – Camera Rotation Test 2 (editable source)

This archive is the full Pass 31 port source project, updated to mirror the **two modified class files** in the Camera Rotation Test 2 JAR. It contains Java source, assets, Gradle wrapper and GitHub Actions build workflow. It is **not** a compiled JAR.

## Changes compared with Pass 31

1. `src/main/java/xyz/przemyk/simpleplanes/mixin/CameraMixin.java`
   - Mixin target switched from `Camera.setup` to `Camera.alignWithEntity`.
   - The first-person camera is anchored to the aircraft's interpolated seat position.
   - The camera eye-height offset is rotated with the aircraft's interpolated attitude.
2. `src/main/java/xyz/przemyk/simpleplanes/client/ClientEventHandler.java`
   - Interpolates plane rotation as one quaternion, composes the player view rotation, then converts once to pitch/yaw/roll.

The two updated methods were reconstructed to match the behavior in the supplied Test 2 JAR; they are not byte-for-byte recoveries of the original pre-compilation source. All other project files come from Pass 31.

## Build

Use the supplied Gradle wrapper / GitHub Actions workflow (see `.github/workflows/build.yml`). Run `./gradlew build` on Linux/macOS or `gradlew.bat build` on Windows with the project's required Java toolchain and NeoForge dependencies. Check the workflow for the Java version. A build pass **has not been verified in this environment**.

**Test status:** Camera Rotation Test 2 has not been confirmed fixed in gameplay. Run a single Simple Planes JAR, back up worlds, and test first person / third person banking and pitching.

## Attribution

The source port is based on the upstream Simple Planes project; preserve the bundled `LICENSE` and original authorship/notice files. The changes are an unofficial compatibility experiment.
