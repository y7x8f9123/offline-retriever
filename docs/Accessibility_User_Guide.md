# Accessibility User Guide

## Offline Accessible Multimodal Local Content Retrieval System

**Version:** Final Project Version  
**Platform:** Windows Desktop  
**Date:** 2026-08

---

## 1. Introduction

This guide describes the accessibility features implemented in the Offline Accessible Multimodal Local Content Retrieval System.

The application includes accessibility-focused interface features designed to improve usability for users with different interaction and visual needs.

The implemented accessibility features include:

- Keyboard-only navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic labels for assistive technologies

The accessibility design follows the project's WCAG 2.1 AA design objectives.

---

## 2. Keyboard Navigation

The application supports keyboard-based navigation so that major interface functions can be accessed without relying entirely on a mouse.

Available keyboard controls include:

| Key | Function |
|---|---|
| Tab | Move to the next interactive control |
| Shift + Tab | Move to the previous interactive control |
| Enter | Activate the selected control |
| Space | Activate supported buttons, switches, and controls |

Users can navigate between major controls using the keyboard and activate supported interface elements through standard keyboard interaction.

---

## 3. High Contrast Mode

High Contrast Mode increases the visual distinction between foreground and background interface elements.

This feature is intended to improve readability and interface visibility.

To enable High Contrast Mode:

1. Open the **Settings** page.
2. Turn on the **High Contrast Mode** switch.
3. The application theme updates immediately.

To return to the standard appearance, turn off the same option.

---

## 4. Font Size Adjustment

The application provides dynamic font scaling to improve text readability.

Four predefined font size levels are available:

- Small
- Medium
- Large
- Extra Large

To change the font size:

1. Open the **Settings** page.
2. Adjust the **Font Size** control.
3. Select the preferred font size level.
4. The interface updates without requiring the application to restart.

This allows users to adjust text presentation according to their visual preferences.

---

## 5. Semantic Labels and Assistive Technologies

Important interface controls include semantic information to improve compatibility with assistive technologies.

Semantic labels are used for major interface elements including:

- Navigation controls
- Search controls
- Accessibility settings
- Main application sections
- Interactive buttons and controls

These labels provide additional information about the purpose of interface elements to supported accessibility tools.

---

## 6. Accessible Search Interaction

The search interface is designed to support clear and predictable interaction.

The application provides:

- Clearly identifiable search controls
- Keyboard-accessible input and actions
- Feedback for empty search queries
- Structured search-result presentation
- Adjustable text size
- High-contrast presentation options

These features are intended to make the main retrieval workflow easier to operate using different interaction methods.

---

## 7. Accessible Navigation

The Windows desktop interface provides navigation between the main application areas.

Keyboard navigation allows users to move through supported controls without requiring pointer-only interaction.

Interactive controls use standard Flutter interface components where possible to maintain predictable keyboard and accessibility behaviour.

---

## 8. Accessibility Settings

Accessibility-related options are available through the application settings.

The primary configurable accessibility features are:

```text
High Contrast Mode
Font Size
```

Changes to these settings are reflected directly in the user interface.

The available font size levels are:

```text
Small
Medium
Large
Extra Large
```

---

## 9. Current Accessibility Scope

The final Windows implementation includes the accessibility features developed and validated during the project.

Implemented features include:

- Keyboard navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic accessibility labels

These features support the project's accessibility objectives and improve usability for users with different interaction requirements.

The project uses WCAG 2.1 AA as an accessibility design objective. The current project does not claim formal third-party WCAG certification.

---

## 10. Known Limitations

Accessibility support depends partly on the behaviour of Flutter, Windows, and the assistive technology being used.

The project has focused on the main application workflow and major interface controls.

Formal accessibility testing with a wide range of external assistive technologies and user groups was outside the scope of the eight-week project.

These areas could be expanded through additional accessibility evaluation in future development.

---

## 11. Conclusion

The final Windows desktop application includes accessibility-focused features as part of the main user interface.

Keyboard navigation, High Contrast Mode, Dynamic Font Scaling, and semantic labels provide multiple ways to improve interaction and readability.

Accessibility was treated as a core design consideration throughout the project rather than as a separate optional feature.