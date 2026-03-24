# Interactive 3D Receipt

A cloth physics simulation rendered entirely with **Jetpack Compose Canvas** — no OpenGL, no third-party engines.

A receipt hangs from its top edge and reacts to gravity, wind, and touch. Grab any point on the receipt and drag it around; the cloth deforms in real time with smooth per-vertex lighting.

## How it works

| Layer | File | Responsibility |
|-------|------|----------------|
| Mesh | `ReceiptMeshGenerator.kt` | 25x50 particle grid, UV coords, structural/shear/bending constraints |
| Physics | `ClothSimulation.kt` | Verlet integration, gravity, wind, constraint relaxation (15 iterations) |
| Texture | `ReceiptTextureGenerator.kt` | Generates the receipt bitmap (1024x2048) with all text and layout |
| Rendering | `ReceiptRenderer.kt` | 3D projection, `drawBitmapMesh` for texture, `drawVertices` for per-vertex lighting |
| Screen | `InteractiveReceiptScreen.kt` | Animation loop, touch → ray-cast interaction, Compose UI |

## Tech stack

- Kotlin 2.0 + Jetpack Compose (BOM 2024.09)
- `android.graphics.Canvas.drawBitmapMesh` for texture-mapped mesh rendering
- `android.graphics.Canvas.drawVertices` for smooth interpolated lighting overlay
- Pure CPU physics — no GPU compute or native code

## Build & run

```
git clone https://github.com/chands/AnimationExpo.git
```

Open in Android Studio, sync Gradle, run on a device or emulator (API 24+).

## Credits

Ported from the [Interactive 3D Receipt](https://codepen.io/flornkm) CodePen by flornkm (WebGL + JavaScript) to pure Compose Canvas.
