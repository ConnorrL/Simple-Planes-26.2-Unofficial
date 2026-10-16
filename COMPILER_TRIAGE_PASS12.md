# Pass 12: input build log `logs_105280439705.zip`

The compile log still stopped at the configured 250-error cap; the log bundles duplicate output from the same failed compile task (not multiple builds).
The earliest errors in this log are obsolete `MultiBufferSource`, `RenderType`, and `ItemRenderer` usages in the upgrade render methods, then removed `ModelData`, client rendering and GUI APIs, and transfer-capability mismatches.

This pass specifically migrates the standard attached meshes to queued submissions; other errors remain. In particular:
- `RegisterColorHandlersEvent.Item` / obsolete plane item tint code.
- old GUI `blit` signatures and removed Gui.rightHeight.
- capability changes `Capabilities.ItemHandler`, `FluidHandler`, `EnergyStorage` -> `Capabilities.Item`/`Fluid`/`Energy` and `ResourceHandler` / `EnergyHandler`.
- entity item drops and all other gameplay errors.

Check the next Actions log to assess whether compiler errors actually decrease; do not take ZIP validation as build success.
