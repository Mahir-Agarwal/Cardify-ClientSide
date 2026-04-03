# Design System Specification: Crimson Glassmorphism

## 1. Overview & Creative North Star
**Theme:** Elegant, minimal, human-designed dark mode fintech.
We are moving away from loud, neon, heavily saturated "gamer" aesthetics. This design is highly sophisticated, focusing on depth, realistic frosted glass effects, and restrained use of color. 

## 2. Colors & Atmospheric Depth
- **Base Environment:** Charcoal/Deep Black (`#0A0A0A` or `#111111`).
- **Primary Accent:** Rich Red/Muted Crimson (`#EF4444`). Used sparingly for essential call-to-actions, active states, and focus.
- **Secondary Accent:** Soft transparent grays and whites.
- **Text:** Pure White for headers, muted grays (`#9CA3AF`) for body and secondary text.

## 3. Frosted Glass Instructions (Crucial)
- **Background Fill:** Translucent white at `10% - 20% opacity`.
- **Blur:** Heavy background blur (`backdrop-filter`) to achieve pure "frosted glass".
- **Borders:** 1px soft, light white border (`rgba(255,255,255,0.15)`) on all cards to define edges.
- **Corners:** Large rounded corners, typically `16dp` to `20dp` (`rounded-2xl`).
- **Shadows:** Soft, subtle ambient shadows. No harsh drop shadows, no neon glowing shadows. Layering is entirely achieved through transparency and blurring.

## 4. Typography Rules
- Clean, minimal sans-serif. 
- Strict hierarchy: Title -> Subtitle -> Content.
- Use readable spacing and generous line heights. Avoid clutter.

## 5. UI Components
- **Cards/Containers:** Frosted glass panels as defined above. 
- **Buttons:** Muted crimson red backgrounds with soft/no gradients. Obvious, rounded but not necessarily fully-pill shaped (perhaps rounded-xl).
- **Steppers/Indicators:** Soft red highlights for the active step, dimmed transparent grays for inactive.
- **Inputs:** Translucent glass fields with white borders acting as soft containers.
