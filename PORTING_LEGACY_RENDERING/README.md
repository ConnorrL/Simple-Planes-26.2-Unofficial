# Legacy aircraft upgrade rendering, pre-Minecraft 26.2

These are copies of the old immediate-render methods, preserved for feature parity work.
They **cannot compile** against 26.2 because `MultiBufferSource`, old `RenderType`, and direct model `renderToBuffer` usages were replaced.

Port Pass 12 adds functioning queued draw submissions for standard upgrade meshes and seat/wooden additions directly through `PlaneRenderer`. These legacy methods are archived here rather than silently discarded.

Still to migrate: customized block-model attachments (cargo chest, supply crate, payload, jukebox), dynamic banners and folding-animation lid, glint / armor-window transparency, and detailed materials. DO NOT consider the rendering complete.
