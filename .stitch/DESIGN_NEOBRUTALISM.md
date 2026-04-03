# Design System Specification: Neo-Brutalist High Contrast Tactile

## 1. Overview & Creative North Star
**Theme:** Neo-Brutalism / High-Contrast Tactile. 
A stark rejection of soft glassmorphism. This theme uses thick, harsh black borders, pure un-blurred colors, and solid block shadows to create an aggressively tactile "physical" layout mimicking retro print design or 90s web interfaces but with extreme modern polish.

## 2. Colors & Contrast
- **Base Background:** Off-White or very light grey (`#FDFDFD` or `#F4F4F5`). Pure White `#FFFFFF` inside cards only.
- **Accents:** 
  - Pure Bright Green (`#00E640` or `#16A34A` equivalent)
  - Bright Electric Blue (`#2563EB` or `#1E40AF`)
  - Dark Charcoal Black (`#121212`) for heavy elements.
- **Borders & Shadows:** Solid Black (`#000000`).
- **Text:** Solid Black (`#000000`) for stark readability. Never use subtle grays for primary text.

## 3. Shape & Geometry Rules (Crucial)
- **Borders:** Every single container, button, card, and image must have a stark `3px` or `4px` solid black border.
- **Shadows (Block Drop Shadows):** No blur allowed. Shadows must be solid black blocks offset to the bottom right (e.g., `box-shadow: 4px 4px 0px #000000`).
- **Corners:** Completely sharp square borders (`rounded-none`), or very minimal rounding. No `rounded-2xl` pills.

## 4. Typography Rules
- A bold, punchy, retro-modern sans-serif or monospace font (like Space Grotesk, Roboto Mono, or Archivo Black).
- Heavy use of ALL CAPS `uppercase` for labels, small headers, and buttons. 

## 5. UI Components
- **Cards/Containers:** White backgrounds with thick black borders and a hard black drop shadow.
- **Buttons:** Bright Green or Bright Blue backgrounds inside black borders with hard black offset shadows. When clicked, the button translates down right to overlap the shadow (tactile press).
- **Inputs:** Thick black borders, white background.
- **Icons:** Thicker lines, solid black strokes, filled with bright accent colors or pure black. No thin, delicate wireframe icons.
