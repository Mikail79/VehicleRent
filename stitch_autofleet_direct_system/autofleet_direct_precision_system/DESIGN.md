---
name: AutoFleet Direct Precision System
colors:
  surface: '#faf8ff'
  surface-dim: '#d2d9f4'
  surface-bright: '#faf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f3ff'
  surface-container: '#eaedff'
  surface-container-high: '#e2e7ff'
  surface-container-highest: '#dae2fd'
  on-surface: '#131b2e'
  on-surface-variant: '#444651'
  inverse-surface: '#283044'
  inverse-on-surface: '#eef0ff'
  outline: '#757682'
  outline-variant: '#c5c5d3'
  surface-tint: '#4059aa'
  primary: '#00236f'
  on-primary: '#ffffff'
  primary-container: '#1e3a8a'
  on-primary-container: '#90a8ff'
  inverse-primary: '#b6c4ff'
  secondary: '#904d00'
  on-secondary: '#ffffff'
  secondary-container: '#fe932c'
  on-secondary-container: '#663500'
  tertiary: '#4b1c00'
  on-tertiary: '#ffffff'
  tertiary-container: '#6e2c00'
  on-tertiary-container: '#f39461'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dce1ff'
  primary-fixed-dim: '#b6c4ff'
  on-primary-fixed: '#00164e'
  on-primary-fixed-variant: '#264191'
  secondary-fixed: '#ffdcc3'
  secondary-fixed-dim: '#ffb77d'
  on-secondary-fixed: '#2f1500'
  on-secondary-fixed-variant: '#6e3900'
  tertiary-fixed: '#ffdbcb'
  tertiary-fixed-dim: '#ffb691'
  on-tertiary-fixed: '#341100'
  on-tertiary-fixed-variant: '#773205'
  background: '#faf8ff'
  on-background: '#131b2e'
  surface-variant: '#dae2fd'
typography:
  headline-xl:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-xl-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.015em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  mono-data-lg:
    fontFamily: JetBrains Mono
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: 0.04em
  mono-data-md:
    fontFamily: JetBrains Mono
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.02em
  mono-data-sm:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-caps:
    fontFamily: Plus Jakarta Sans
    fontSize: 11px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.08em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-tablet: 1.5rem
  margin: 1rem
  margin-tablet: 2rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

The design system establishes a high-performance, utility-first operational environment built for mobile fleet supervisors, in-house workshop technicians, and rental drivers. The brand archetype blends industrial dependability with crisp digital mobility. The aesthetic draws from high-precision automotive instrumentation, avionics dashboards, and rigorous telemetry tooling—prioritizing clarity, visual efficiency, and immediate scanability under varied field conditions (direct sunlight on rental staging lots, grease-lit service bays, and rapid counter handoffs).

Visual principles:
- **Mobility Instrumentation:** Structured data displays, unambiguous state indicators, and monospaced technical telemetry (VIN, license plates, OBD-II error codes, mileage, pricing).
- **Physical Field Ergonomics:** Minimum 48dp tactile hit regions, defensive spacing against mis-taps on grease-resistant glass, high luminance contrast, and deliberate state changes.
- **Architectural Utility:** A tailored hybrid of Modern Corporate utility and Industrial Cockpit minimalism. Pure white structural cards, mechanical hairline dividers, and functional semantic pill tagging instead of decorative styling.

## Colors

The palette leverages high-contrast automotive functionalism. Light canvas foundations keep reflections manageable on field tablets and Android rugged handsets, while rich automotive pigments direct user workflows.

- **Primary Canvas & Surfaces:** Background is fixed to Slate 50 (`#F8FAFC`). Primary cards and utility modules use pure White (`#FFFFFF`) with 1px border lines in Slate 200 (`#E2E8F0`). Secondary grouped wells and inactive inspection segments use Slate 100 (`#F1F5F9`).
- **Primary Navy (`#1E3A8A`):** Anchors brand authority, global navigation, primary transaction triggers (e.g., "Dispatch Vehicle", "Complete Check-In"), and top-level app headers.
- **Workshop Amber (`#D97706`):** The operational accent. Reserved for active technician service tickets, scheduled bay maintenance, pending inspection warnings, and diagnostic alerts requiring prompt oversight.
- **Text & Hierarchy:** Heading and primary data readouts use Slate 900 (`#0F172A`). Secondary technical descriptors, captions, and table headers use Slate 600 (`#475569`). Inactive or placeholder items rely on Slate 400 (`#94A3B8`).
- **Operational Semantic States:**
  - *Ready / Available:* Emerald 700 (`#15803D`) text on Emerald 100 (`#DCFCE7`) surface.
  - *In-Workshop / Critical Down:* Red 700 (`#B91C1C`) text on Red 100 (`#FEE2E2`) surface.
  - *In-Transit / Rented:* Sky 700 (`#0369A1`) text on Sky 100 (`#E0F2FE`) surface.
  - *Pending Inspection:* Amber 800 (`#92400E`) text on Amber 100 (`#FEF3C7`) surface.

## Typography

Typography enforces a strict dichotomy between interface vernacular and mechanical vehicle data:

- **Interface Layer (Plus Jakarta Sans):** Handles headings, dialogs, operational instructional text, form field prompts, and contextual body copy. It provides clean geometry, high legibility at modest sizes, and balanced stroke weights for standard Android screen densities.
- **Telemetry & Telematics Layer (JetBrains Mono):** Dedicated exclusively to structured operational values:
  - License plate numbers (rendered uppercase with slight positive tracking)
  - Vehicle Identification Numbers (VIN)
  - Odometer & diagnostic readouts (`124,592 mi`, `38 PSI`, `14.2 V`)
  - Diagnostic Trouble Codes (`OBD-II: P0420`, `B1000`)
  - Operational rates, rental pricing tallies, and shop invoice itemizations.
- **Display Treatment:** All technical telemetry tags must render with tabular figures (tnum) to prevent column jittering when data streams update in real time.

## Layout & Spacing

The layout model is anchored to a strict 4dp baseline grid (8dp major increments), optimized for Android handheld units (phones, barcode sleds) and bay-mounted Android tablets.

- **Grid Architecture:**
  - *Mobile (<600dp):* Single-column vertical stream. 16dp outer margins (`margin`), 16dp horizontal item gutters (`gutter`). All interactive cards span full content width.
  - *Tablet & Foldables (600dp - 1024dp):* 2-column or master-detail split layout. 32dp outer margins (`margin-tablet`), 24dp central gutters (`gutter-tablet`). Workshop ticket work orders appear in master list (left 380dp) with repair diagrams on the secondary pane.
- **Rhythm & Touch Standards:**
  - Component padding utilizes `space-md` (16dp) for outer card envelopes, reducing to `space-sm` (8dp) for grouped meta-tokens.
  - Interactive touch thresholds maintain an uncompromised minimum height and width of 48dp (`space-xl` + `space-md` composition) across all input fields, list tiles, and inspection toggle zones.

## Elevation & Depth

Visual hierarchy rejects heavy, diffuse shadows in favor of a crisp architectural outline system paired with structural surface layering. This guarantees clarity on greasy or scratched mobile display covers in direct shop illumination.

- **Level 0 (App Canvas):** Slate 50 (`#F8FAFC`). Flat foundation.
- **Level 1 (Operational Cards & Work Tiles):** Pure White (`#FFFFFF`) with a 1px solid hairline border in Slate 200 (`#E2E8F0`). Flat elevation; no default drop shadow.
- **Level 2 (Active/Pressed Cards & Modals):** Pure White surface with 1px border in Slate 300 (`#CBD5E1`) and a tight physical shadow: `0px 2px 4px rgba(15, 23, 42, 0.06), 0px 4px 12px rgba(15, 23, 42, 0.04)`.
- **Level 3 (Inspection Drawers & Persistent Action Bars):** Floating bottom sheets and bay checkout docks use `#FFFFFF` accompanied by an elevated edge line (`#E2E8F0`) and an ambient anchor shadow: `0px -4px 16px rgba(15, 23, 42, 0.08)`.
- **Focus & Selection:** Active elements receive a sharp 2px exterior ring in Deep Automotive Navy (`#1E3A8A`) or Workshop Amber (`#D97706`), eliminating ambiguity during rapid inventory audits.

## Shapes

The design system implements a controlled, technical corner geometry:

- **Cards & Primary Modules:** 12dp corner radius (`rounded-lg` under roundedness level 2). Delivers structural cohesion without appearing bubbly or casual.
- **Input Fields & Search Bins:** 8dp corner radius (`rounded-base`). Aligns cleanly with keyboard docks and data entry layouts.
- **Badges, Status Pills & Micro-Indicators:** Full pill radius (9999dp) to immediately differentiate dynamic status chips from rectangular data input controls.
- **Action Buttons:** 8dp corner radius for standard actions; full-width bottom pinned primary triggers utilize 12dp to match underlying modal card boundaries.

## Components

### Buttons
- **Primary Button:** Background Deep Automotive Navy (`#1E3A8A`), text White (`#FFFFFF`), min-height 48dp, 8dp radius. Full-width on mobile check-in flows. Active state shifts to Deep Navy shade `#172554`.
- **Operational Amber Button:** Background Workshop Amber (`#D97706`), text White (`#FFFFFF`). Used exclusively for active maintenance triggers (e.g., "Log Fault", "Hold in Bay").
- **Secondary/Outline:** Transparent background, 1.5px border `#CBD5E1`, text `#0F172A`. Minimum 48dp tap area.
- **Icon Utility Actions:** 48x48dp square hit target with 8dp rounded surface, Slate 100 (`#F1F5F9`) background, and Slate 700 (`#334155`) icon glyphs (e.g., barcode scan, camera capture).

### Chips & Semantic Status Badges
- **Status Pills:** Height 28dp, pill-shaped radius (9999dp), inline horizontal padding 10dp. Bold uppercase typography (`label-caps`).
  - *Ready:* Text `#15803D`, Background `#DCFCE7`.
  - *In-Workshop:* Text `#B91C1C`, Background `#FEE2E2`.
  - *Maintenance Hold:* Text `#92400E`, Background `#FEF3C7`.
- **Filter Chips:** Height 36dp, 8dp radius. Inactive: Slate 100 (`#F1F5F9`) with Slate 700 (`#334155`) text. Active: Deep Automotive Navy (`#1E3A8A`) with White (`#FFFFFF`) text.

### Cards
- **Vehicle Inspection Card:** Pure White background, 12dp radius, 1px solid border `#E2E8F0`, padding 16dp. Header displays license plate in JetBrains Mono (`mono-data-lg`) paired with the vehicle make/model in Plus Jakarta Sans (`headline-sm`).
- **Telemetry Card Segment:** Embedded nested wells inside primary cards with 6dp radius, background Slate 50 (`#F8FAFC`), separating VIN, current mileage, and fuel/battery percentage.

### Input Fields
- Height 48dp minimum, background `#FFFFFF`, border 1.5px `#CBD5E1`, radius 8dp, horizontal padding 12dp.
- Text rendered in JetBrains Mono when accepting license plates, mileage, or bay numbers; Plus Jakarta Sans for generic notes.
- Focus state: Border color changes to `#1E3A8A` with a 2px offset focus ring.
- Error state: Border `#B91C1C`, helper text `#B91C1C` with an alert icon prefix.

### Lists & Inventory Rows
- Tappable fleet asset rows must have a minimum vertical footprint of 64dp.
- Divider: 1px hairline border `#F1F5F9` spanning the row, inset by left-padding to maintain visual grouping.
- Content distribution: Left side contains make/model and license plate badge; right side displays operational status pill and odometer telemetry.

### Checkboxes & Radio Controls
- Minimum 48x48dp interactive bounding box surrounding a crisp 20x20dp physical control with 4dp radius (checkbox) or circular boundary (radio).
- Active fill: `#1E3A8A` with white checkmark/dot. Inactive border: 2px `#94A3B8`.

### Specialized Domain Components
- **Odometer & Fuel Gauge Readout:** Dual-tone horizontal telemetry widget utilizing monospace values aligned next to visual segmented bar graphs.
- **OBD-II Fault Log Item:** Amber/Red left indicator strip (4dp width) on a pure white tile showing diagnostic trouble code, detection timestamp, and clear/diagnose triggers.
- **Vehicle Staging Bay Selector:** Grid array of 48dp tactile blocks representing physical workshop and parking stalls, displaying occupied (Navy), open (Green), or maintenance (Amber) states.