# 🎓 TeachTrack

An Android application to help English teachers manage their student schedules, including class registrations, recurring lessons, and Firebase integration.

✅ Features
🧑‍🏫 Register and manage students with personal and contact information

🗓️ Schedule daily or recurring weekly classes

🔁 Automatically create class recurrences for monthly students

🔍 Search for students by name with autocomplete

✉️ Auto-fill student email when selected

📅 Native date picker for scheduling

🧮 Table layout for setting fixed weekly class times

🧾 List of scheduled classes with search and filtering

📤 Stores all data in Firebase Firestore

🧠 Long-press (4 seconds) on a card to enter edit mode (WIP)

🧪 Requirements
Android Studio (recommended: Hedgehog or newer)

Minimum Android SDK: 24

Firebase Firestore enabled in your Firebase project

Permissions required:

INTERNET

ACCESS_NETWORK_STATE

🚀 How to Use
Install the app on your Android device

Register a student in the Student Registration screen

Go to Schedule and select a student

Choose the class type: Daily or Monthly

Select the date or define weekly recurrence times

View all scheduled classes in the Classes screen

🧱 App Screens
MainActivity: main menu

CadastroAlunoActivity: student registration

ListarAlunosActivity: student listing and search

AgendaActivity: schedule classes and manage recurrence

ListarAulasActivity: list of scheduled classes with filters

🛠️ Technologies Used
Java

MVVM Architecture

Firebase Firestore

RecyclerView with custom adapters

AutoCompleteTextView

DatePickerDialog

TableLayout

Material Design 3 components

📁 Data Structure (Firestore)
alunos (students collection)

nome, sobrenome, email, tipoAluno, dataCadastro, etc.

aulas (classes collection)

aluno, email, tipo, data, hora, horariosSemana

📌 Notes
The project supports both daily and monthly students

Classes are grouped visually by cards

Schedule recurrence only applies to monthly students

Currently supports simple email field; can be expanded with notifications

🔐 Security & Privacy
All data is securely stored in Firebase Firestore and is tied to the user’s project configuration. No data is shared externally.

📄 License
This project is for educational use and can be freely adapted for non-commercial purposes. For commercial use, please contact the author.



