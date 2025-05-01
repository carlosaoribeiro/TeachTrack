# 🎓 TeachTrack

An Android application to help English teachers manage their student schedules, including class registrations, recurring lessons, Firebase integration, and a visually refined experience.

---

## ✅ Features

- 🧑‍🏫 Register and manage students with personal and contact information  
- 🗓️ Schedule daily or recurring weekly classes  
- 🔁 Automatically create class recurrences for monthly students (next 4 weeks)  
- 🔍 Search for students by name with autocomplete  
- ✉️ Auto-fill student email when selected  
- 📅 Native date picker for scheduling  
- 🧮 Table layout for setting fixed weekly class times  
- 📋 List of scheduled classes grouped and sorted by real dates  
- 🧾 Cards for each class with student name, date, time and delete/edit buttons  
- 🧠 Long-press (4 seconds) on a card to enter edit mode  
- 📤 Stores all data in Firebase Firestore  
- 🌙 Modern dark theme with rounded buttons and clean layout  

---

## 🧪 Requirements

- Android Studio (recommended: Hedgehog or newer)  
- Minimum Android SDK: 24  
- Firebase Firestore enabled in your Firebase project  
- Firebase Authentication enabled (email/password)  
- Permissions required:
  - `INTERNET`
  - `ACCESS_NETWORK_STATE`
  - `ACCESS_COARSE_LOCATION`

---

## 🚀 How to Use

1. Install the app on your Android device  
2. Register a user and log in  
3. Add students from the **Student Registration** screen  
4. Schedule a class in **Agenda**:
   - Select the student  
   - Choose type: Daily or Monthly  
   - Pick a date or set weekly recurrence  
5. View and manage scheduled classes in the **Classes** screen  

---

## 🧱 App Screens

- `SplashActivity`: App startup screen  
- `LoginActivity`: Firebase login  
- `CadastroUsuarioActivity`: Firebase signup  
- `MainActivity`: Main menu (navigation drawer)  
- `CadastroAlunoActivity`: Student registration  
- `ListarAlunosActivity`: List and search students  
- `AgendaActivity`: Schedule and edit classes  
- `ListarAulasActivity`: List of scheduled classes (sorted and grouped)  

---

## 🛠️ Technologies Used

- Java  
- MVVM Architecture  
- Firebase Firestore  
- Firebase Authentication  
- RecyclerView with custom adapters  
- AutoCompleteTextView  
- DatePickerDialog  
- TableLayout  
- Material Design 3  
- Dark Theme UI with rounded buttons and structured layout  

---

## 📁 Data Structure (Firestore)

- `usuarios` (users collection)  
  - `nome`, `email`, `dataCriacao`  

- `alunos` (students collection)  
  - `nome`, `email`, `tipoAluno`, `dataCadastro`  

- `aulas` (classes collection)  
  - `aluno`, `email`, `tipo`, `data`, `hora`, `horariosSemana`  
  - Supports both daily and monthly types, recurring generation for 4 weeks  

---

## 📌 Notes

- Monthly students generate recurring classes automatically for 4 weeks  
- All classes are sorted and grouped by date  
- Edit or delete directly from each card  
- Clean UI with improved UX and accessibility  
- Session management with `SessionManager`  
- Profile screen displays user info (name, email, signup date)  

---

## 🔐 Security & Privacy

All data is securely stored in **Firebase Firestore**, and authentication is handled via **Firebase Authentication**. Each user only accesses their own data. No external data sharing.

---

## 📄 License

This project is for educational and personal portfolio use. For commercial licensing or contributions, contact the author.
