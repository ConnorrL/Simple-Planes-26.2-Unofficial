# Simple Planes — Minecraft 26.2 port STARTER (not finished)

Original uploaded source: Simple Planes 5.3.9 for Minecraft 1.21.1, LGPLv3.

This archive preserves source and assets while switching the basic development toolchain to Gradle 9.2.1, Java 25, ModDevGradle 2.0.148 and NeoForge 26.2.0.88. This is a porting scaffold, **not a working port**.

## GitHub workflow
1. Create a GitHub repository, upload the **contents** of this folder to the root (including `.github/workflows/build.yml`).
2. Open Actions and run the 'Build Simple Planes 26.2 Port' workflow.
3. Read the *first* compiler error in the workflow logs. Port affected code using 26.2 API. Repeat.
4. Expect changes in entity rendering, menus, inventory, data component serialization, item registration, networking, recipes and integrations.
5. Once compilation passes, test on a regular NeoForge client and dedicated NeoForge server before testing Youer. Do not use in your main world without backups.

## Known pending tasks
- Old JEI integration is disabled as a dependency but the JEI compatibility classes may still need to be excluded or updated.
- Client-side classes need validation for headless dedicated server startup.
- 1.21.1 Java source has NOT been migrated to 26.2 API.
- No compilation or runtime verification has been performed.
- Keep original mod license and attribution when distributing modified builds.
