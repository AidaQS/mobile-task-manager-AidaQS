[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/wTylcrtj)
# 📱 Mobile Task Manager – Final

This repository contains the implementation for **Assignment 13** of the Task Manager App project.

## 🎯 Assignment Goal

Elevate the look, feel and usability of the TaskManager by applying Material Desing 3 principles and modern UI patterns. Focusing on presentation, interaction and consistency, building on the clean architecture implemented in previous assignments.

## ✅ Implemented Features

- Apply a Material 3 theme in "themes.xml".
- Define a custom color scheme and topography styles.
- Remove or override legacy styles from "styles.xml".
- Ensure the theme is applied consistently across all screens.
- Define a small, coherent set of colors in "colors.xml".
- Reference those colors from the theme.
- Define at least a couple custrom text appearance style.
- Avoid hardcoded colors and text sizes in layouts.
- Replace any legacy toolbar/app bar with MaterialToolbar.
- Ensure icons and text tint correctly using the theme.
- Use the toolbar for app-level actions as appropiate.
- Migrate existing widgets to Material equivalents.
- Ensure the correct use of:
    - hints/labels.
    - error display.
    - accessibility-friendly inputs.
- Add a FloattingActionButton.
- Remove the old "Add" action from the app bar.
- Each task item shown in the list will be presented using a MaterialCardView.
- Ensure task items look tappable and visually separated.
- keep the content simple: title, completion state, optional metadata.
- Convert the Task detail/edit UI into a Modal Bottom Sheet.
- The bottom sheet will include a TopAppBar at the top, containing:
    - A title.
    - An Edit action icon in view mode.
    - A Save action icon in edit mode with optional Cancel.
- Oppening the botton shows the task in view mode (read-only).
- Tapping the Edit switches the UI to edit mode.
- Tapping Save persists changes via the ViewModel + repository dismisses the sheet.
- Replace manual date input with MaterialDatePicker.
- Connect pickers to the ViewModel using callbacks or data binding.
- Translate all visible text.
- Verify by switching the emulator/device language.
- Add snackbar feeddback.
- Progress indicators during save/refresh.
- Visual cues for pending sync.


## 🚧 Known Issues

- 

## 📝 Notes

- The save botton and the edit botton are also FABs

---

> This assignment is part of the Mobile Development course at Escola Superior de Enxeñaría Informática at Universidade de Vigo.  
> See the course syllabus and lab instructions for more details.
ee the course syllabus and lab instructions for more details.
