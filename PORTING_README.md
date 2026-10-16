# Simple Planes 26.2 — first API migration pass (NOT BUILDABLE YET)

Source: user-provided Simple Planes 5.3.9 (1.21.1) under LGPLv3.
Target: Minecraft 26.2, NeoForge 26.2.0.88, Java 25.

This pass follows the actual GitHub Actions failure from October 16, 2026:
Gradle configuration and Minecraft artifact downloads passed; compileJava stopped
at the first 100 Java compilation errors (the Java compiler's default limit).

## Changes in this pass
- Use Minecraft's net.minecraft.resources.Identifier instead of ResourceLocation,
  including the readIdentifier and writeIdentifier FriendlyByteBuf method names.
- Update net.minecraft.world.entity.npc.villager.Villager import.
- Update net.minecraft.world.level.gamerules.GameRules import.
- Replace GuiGraphics type references with GuiGraphicsExtractor. This is ONLY
  a name update. Actual GUI draw calls and screen override methods still
  need migration to 26.2's render-state/extraction API.
- Increase maximum compiler errors shown to 250 and cap warnings at 20 so
  future GitHub Actions output is more useful.

## STILL NECESSARY
This is NOT a playable mod. No successful build or runtime testing has occurred.
Major porting work includes modern Minecraft/NeoForge APIs for:
- Render-state extraction, model layers, entity renderers, GUI rendering
- InteractionResult / item and entity interactions
- Packet registration, codecs, synchronization
- Registry setup and entity constructors
- Saved entity data, inventories, stack serialization
- Recipes, data generation, metadata, assets, compatibility/integrations

Keep source license notices and verify client and dedicated NeoForge server
before using on a Youer hybrid server. Back up Minecraft worlds before testing.

## Next GitHub Actions run
Upload the CONTENTS of this archive to your existing GitHub repository,
replacing files as needed. Use Actions > Build Simple Planes 26.2 Port.
Send the next logs ZIP so we can process the compiler failures by category.

Reference:
https://docs.neoforged.net/primer/docs/1.21.11/
https://docs.neoforged.net/primer/docs/26.1/
https://docs.neoforged.net/docs/misc/identifier/
