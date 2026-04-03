# Design System: Cardify Premium
**Project ID:** 299589550712100235

## 1. Visual Theme & Atmosphere
Sleek, futuristic, and premium fintech aesthetic. The interface is dominated by deep space blacks and dramatic glassmorphism (frosted glass) effects. Glowing neo-accents provide high contrast and focus to key financial metrics and interactive elements. The layout feels breathable but dense with data, utilizing subtle borders and extreme background blur to separate layers. 

## 2. Color Palette & Roles
- **Vantablack Base** (#0A0A0A): The absolute lowest layer. Forms the primary background space.
- **Translucent Glass Panel** (#FFFFFF, 5% Opacity with heavy backdrop blur): Forms the elevated cards and dialog surfaces.
- **Neon Violet Primary** (#7C3AED): Used for primary call-to-action buttons, active states, and glowing accents.
- **Neon Cyan Secondary** (#00F0FF): Used for secondary active indicators, charts, or secondary emphasis.
- **Pure White Text** (#FFFFFF): High emphasis headers and values.
- **Muted Stardust Text** (#A1A1AA): Low emphasis secondary information or labels.

## 3. Typography Rules
- Font: Inter or Outfit. 
- Headers are extremely crisp, semi-bold, and properly tracked.
- Body text is regular weight but easily legible against the ultra-dark backgrounds.

## 4. Component Stylings
- **Buttons:** High-intensity glowing gradients or pure glowing neon colors with soft colored drop shadows underneath to pop off the glass surfaces. Fully rounded (pill shaped).
- **Cards/Containers:** Frosted glass panels. The borders are a 1px solid stroke of `rgba(255,255,255,0.1)`. Corners are gently rounded (`rounded-3xl` or 24px corner radius). High shadow diffusion underneath to lift them off the black base.
- **Inputs/Forms:** Translucent glass pills with borderless or very subtle luminous strokes. Active inputs glow with a neon border.

## 5. Layout Principles
- Generous internal padding within cards to allow the glassmorphism texture to breathe.
- Elements are separated using the glass layers rather than harsh dividers or borders.
- Floating components (like bottom navigation or FABs) emphasize the layering.
