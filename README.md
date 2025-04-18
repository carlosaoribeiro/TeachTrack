# 🎓 TeachTrack

Aplicativo Android para gerenciamento de aulas particulares, desenvolvido para professores de idiomas. Permite o cadastro de alunos, agendamento de aulas (diárias ou mensais), visualização de agendas e edição de aulas, com suporte ao Firebase Firestore para armazenamento em nuvem.

---

## ✅ Funcionalidades

- 👨‍🎓 Cadastro completo de alunos com nome, sobrenome, e-mail, telefone e tipo (diário/mensal)
- 🗓 Agendamento de aulas com seleção de datas e horários
- 🔁 Suporte a aulas recorrentes para alunos mensais com seleção por dias da semana
- 🔍 Busca de aluno com preenchimento automático do e-mail
- 📆 Tela de listagem de aulas agendadas com filtro e organização por aluno
- ✏️ Edição de aulas com toque longo no card (4 segundos)
- 🧠 Interface intuitiva e adaptada para o dia a dia do professor particular

---

## 🔧 Tecnologias Utilizadas

- Java
- Android SDK (API 24+)
- Firebase Firestore
- Material Design
- RecyclerView + Adapter customizado
- AutoCompleteTextView com integração ao Firestore

---

## 🧪 Requisitos

- Android Studio (recomendado: Hedgehog ou mais recente)
- SDK Mínimo: 24 (Android 7.0)
- Permissões:
  - `INTERNET`

---

## 🚀 Como Usar

1. Clone o repositório e abra no Android Studio
2. Configure o Firebase com o `google-services.json`
3. Execute o app no emulador ou dispositivo real
4. Cadastre alunos, agende aulas e visualize sua agenda de forma intuitiva

---

## 🧱 Estrutura de Telas

- `MainActivity`: Tela inicial com botões de navegação
- `CadastroAlunoActivity`: Cadastro ou edição de alunos
- `ListarAlunosActivity`: Lista de alunos com busca e acesso à edição
- `AgendaActivity`: Tela de agendamento de aulas com suporte a recorrência
- `ListarAulasActivity`: Tela de listagem de aulas com cards e organização por aluno

---

## 📱 Experiência do Usuário

- Uso de `AutoCompleteTextView` para facilitar a seleção de alunos
- Cards estilizados para exibir aulas agendadas
- Layout responsivo e preparado para teclado na tela
- Feedbacks visuais com `Dialog` para operações de sucesso ou erro

---

## 🔒 Privacidade

Todos os dados dos alunos e aulas são armazenados apenas no Firestore do próprio usuário, sem qualquer exposição externa ou integração com terceiros.

---

## 📄 Licença

Projeto de uso educacional e pessoal, livre para modificação e adaptação em contextos similares. Para distribuição comercial, entre em contato com o autor.

---

