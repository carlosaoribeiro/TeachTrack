# 🎓 TeachTrack

An Android application to help English teachers manage their student schedules, including class registrations, recurring lessons, and Firebase integration.

---

## ✅ Features

- 🧑‍🏫 Register and manage students with personal and contact information  
- 🗓️ Schedule daily or recurring weekly classes  
- 🔁 Automatically create class recurrences for monthly students  
- 🔍 Search for students by name with autocomplete  
- ✉️ Auto-fill student email when selected  
- 📅 Native date picker for scheduling  
- 🧮 Table layout for setting fixed weekly class times  
- 🧾 List of scheduled classes with search and filtering  
- 📤 Stores all data in Firebase Firestore  
- 🧠 Long-press (4 seconds) on a card to enter edit mode (WIP)  

---

## 🧪 Requirements

- Android Studio (recommended: Hedgehog or newer)  
- Minimum Android SDK: 24  
- Firebase Firestore enabled in your Firebase project  
- Permissions required:
  - `INTERNET`
  - `ACCESS_NETWORK_STATE`

---

## 🚀 How to Use

1. Install the app on your Android device  
2. Register a student in the **Student Registration** screen  
3. Go to **Schedule** and select a student  
4. Choose the class type: Daily or Monthly  
5. Select the date or define weekly recurrence times  
6. View all scheduled classes in the **Classes** screen  

---

## 🧱 App Screens

- `MainActivity`: Main menu  
- `CadastroAlunoActivity`: Student registration  
- `ListarAlunosActivity`: Student listing and search  
- `AgendaActivity`: Schedule classes and manage recurrence  
- `ListarAulasActivity`: List of scheduled classes with filters  

---

## 🛠️ Technologies Used

- Java  
- MVVM Architecture  
- Firebase Firestore  
- RecyclerView with custom adapters  
- AutoCompleteTextView  
- DatePickerDialog  
- TableLayout  
- Material Design 3 components  

---

## 📁 Data Structure (Firestore)

- `alunos` (students collection)
  - `nome`, `sobrenome`, `email`, `tipoAluno`, `dataCadastro`, etc.
- `aulas` (classes collection)
  - `aluno`, `email`, `tipo`, `data`, `hora`, `horariosSemana`

---

## 📌 Notes

- The project supports both daily and monthly students  
- Classes are grouped visually by cards  
- Schedule recurrence only applies to **monthly** students  
- Currently supports a simple email field; can be expanded with notifications  

---

## 🔐 Security & Privacy

All data is securely stored in **Firebase Firestore** and is tied to the user’s own Firebase configuration. No data is shared externally.

---

## 📄 License

This project is for educational use and can be freely adapted for non-commercial purposes. For commercial use, please contact the author.

---
