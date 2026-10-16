# Simple Planes 26.2: Port Pass 3 — not compiled, not playable yet

Target: Minecraft 26.2 / NeoForge 26.2.0.88 / Java 25 / eventual Youer.

Changes against Port Pass 2:
- Migrate plane/parachute item use() from InteractionResultHolder to InteractionResult.
- Migrate plane and description item tooltip signatures to TooltipDisplay/Consumer.
- Update PlaneWorkbenchContainer.clicked to ContainerInput and onCraftedBy signatures.
- Update plane-workbench custom Recipe interface signatures and codec result accessor.
- Temporarily exclude the optional JEI integration classes from Java compilation (their source remains in the archive). JEI recipe browsing is unavailable until that integration is ported.

IMPORTANT:
- This archive was edited and syntax/ZIP-inspected, but has NOT passed a Java 25 NeoForge 26.2 compile or runtime test.
- Major remaining work: plane and parachute renderers (the old buffer API was removed), entity saving/interaction, recipe synchronization between server/client, NeoForge capability migration, networking and datapack reloads.
- The workbench still calls the removed level.getRecipeManager() method and needs a proper modern server/client recipe-sync design; avoid replacing it with a server-only call that leaves the client without recipes.
- Use a separate test world after the build eventually succeeds; do not install into the production Youer server yet.

Next: commit the contents to GitHub, run Actions, upload the latest logs ZIP.

API references:
https://docs.neoforged.net/docs/items/interactions/
https://docs.neoforged.net/docs/resources/server/recipes/custom/
https://aldak.netlify.app/javadoc/26.2.x/net/minecraft/world/item/crafting/recipe


## Port pass 4 (based on logs_105253683562.zip)

- Removed the obsolete `bus` property of all three `@EventBusSubscriber` usages; 26.2 auto-detects event bus.
- Adapted the workbench recipe serializer to Minecraft 26.2's `RecipeSerializer` record, retaining the existing JSON and network codecs.
- Registered a typed `KeyMapping.Category` for aircraft key bindings.
- Corrected the moved `ShulkerModel` imports and non-generic type references.

**Still blocked:** Aircraft rendering uses the obsolete immediate rendering pipeline and must be redesigned for modern entity render states/submission; the energy, item and fluid transfer/capability API has changed, networking and save/load remain incomplete. No successful build or in-game test has been established.

API references:
- https://docs.neoforged.net/docs/concepts/events/
- https://docs.neoforged.net/docs/resources/server/recipes/custom/
- https://aldak.netlify.app/javadoc/26.2.x/net/minecraft/world/item/crafting/recipeserializer
- https://aldak.netlify.app/javadoc/26.2.x/net/minecraft/client/keymapping.category
