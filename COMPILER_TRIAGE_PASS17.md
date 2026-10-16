# Simple Planes 26.2 — compiler triage / Pass 17

Input: `logs_105340166134(1).zip`, GitHub Actions compile on Oct 16, 2026.
The compiler reports **75 errors** (prior pass reported 80). The original log
is supplied separately by the user; this summary groups the relevant changes.

## Changes in this pass

- `ModifyUpgradesScreen`: replaced removed immediate-mode entity preview
  rendering with extraction of `EntityRenderState` and `GuiGraphicsExtractor.entity()`.
  Preserves the continuously rotating plane preview. Its GUI scale and orientation
  will still need visual testing.
- `ClientEventHandler`, `CameraMixin`: use the client camera entity accessor,
  guard null when swapping worlds. The existing camera rotation behavior remains.
- Furnace/liquid engine: use the nullable `ItemStackTemplate` crafting remainder
  and materialize it to `ItemStack` without discarding the original container.

## Still outstanding

- Item tint sources: legacy item-color handler and model sampling need a modern
  registered codec and new client item definitions.
- RenderLivingEvent now has three type parameters and exposes a render state,
  not the entity. Its old matrix and head-rotation hooks need redesign.
- NeoForge's energy/fluid/item transfer capability registrations and usages
  must be migrated together, otherwise GUI access and saved upgrades can break.
- The shooter-related upgrade, entity API methods, solar calculations, and
  custom payload serialization still show errors. Do not discard these features
  to force a successful build.
- Complete aircraft, GUI and world-save testing is required before Youer testing.

## Validation

These changes have been source-checked and ZIP integrity-checked; they have
**not** passed Java compilation in the available environment (Java 21 only;
project requires Java 25). Run GitHub Actions for the definitive error list.
