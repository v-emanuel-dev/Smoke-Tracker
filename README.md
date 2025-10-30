# TimeTracker - Aplicativo Pomodoro Timer

Um aplicativo completo de Timer Pomodoro para Android desenvolvido em Kotlin com Jetpack Compose.

## 🎯 Funcionalidades

- ⏱️ **Timer Pomodoro Completo**: Sessões de trabalho (25 min), pausas curtas (5 min) e pausas longas (15 min)
- 🔔 **Notificações**: Notificações em tempo real com o status do timer
- 🔊 **Sons de Alarme**: Alarme sonoro ao completar cada sessão
- 📊 **Estatísticas Detalhadas**: Acompanhe suas sessões completadas, tempo total de foco e sequência de dias
- ⚙️ **Configurações Personalizáveis**: Ajuste durações, sons e comportamento do timer
- 🌙 **Tema Escuro/Claro**: Suporte automático ao tema do sistema
- 📱 **Material Design 3**: Interface moderna e bonita

## 🏗️ Arquitetura

O aplicativo segue as melhores práticas de desenvolvimento Android:

- **MVVM** (Model-View-ViewModel)
- **Clean Architecture** com separação de camadas
- **Jetpack Compose** para UI declarativa
- **Hilt** para injeção de dependência
- **Room Database** para persistência local
- **DataStore** para preferências
- **Coroutines & Flow** para programação assíncrona
- **Foreground Service** para manter o timer ativo

## 📁 Estrutura do Projeto

```
app/src/main/java/com/timetracker/
├── data/
│   ├── local/
│   │   ├── Database.kt              # Room Database e DAOs
│   │   └── UserPreferencesManager.kt # DataStore para preferências
│   └── repository/
│       └── TimerRepository.kt       # Repositório central
├── domain/
│   └── model/
│       └── TimerModels.kt           # Modelos de domínio
├── service/
│   └── TimerService.kt              # Foreground Service do timer
├── ui/
│   ├── components/
│   │   ├── CircularTimer.kt         # Componente visual do timer
│   │   ├── SessionIndicator.kt      # Indicador de sessões
│   │   └── StatisticsCard.kt        # Cards de estatísticas
│   ├── screens/
│   │   ├── TimerScreen.kt           # Tela principal
│   │   ├── SettingsScreen.kt        # Tela de configurações
│   │   ├── StatisticsScreen.kt      # Tela de estatísticas
│   │   └── TimerViewModel.kt        # ViewModel principal
│   ├── theme/
│   │   ├── Color.kt                 # Cores do app
│   │   ├── Type.kt                  # Tipografia
│   │   └── Theme.kt                 # Tema Material 3
│   └── Navigation.kt                # Sistema de navegação
├── util/
│   └── Utils.kt                     # Funções utilitárias
├── di/
│   └── AppModule.kt                 # Módulo Hilt
├── MainActivity.kt
└── TimeTrackerApplication.kt
```

## 🚀 Como Compilar

### Pré-requisitos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK 34
- Gradle 8.2.0

### Passos

1. Clone o repositório ou extraia os arquivos
2. Abra o projeto no Android Studio
3. Aguarde o Gradle sincronizar
4. Execute o projeto em um dispositivo ou emulador (API 26+)

## 📦 Dependências Principais

```kotlin
// Compose
androidx.compose.material3:material3
androidx.navigation:navigation-compose

// Hilt (Injeção de Dependência)
com.google.dagger:hilt-android

// Room (Database)
androidx.room:room-runtime
androidx.room:room-ktx

// DataStore (Preferências)
androidx.datastore:datastore-preferences

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-android
```

## 🎨 Telas do Aplicativo

### 1. Tela Principal (Timer)
- Timer circular animado
- Botões de controle (Play/Pause, Reset, Skip)
- Indicador de sessões
- Resumo rápido de estatísticas

### 2. Tela de Configurações
- Duração do tempo de foco (1-60 min)
- Duração da pausa curta (1-30 min)
- Duração da pausa longa (5-60 min)
- Número de sessões até pausa longa
- Auto-início de pausas e sessões
- Controle de som e vibração

### 3. Tela de Estatísticas
- Sessões completadas hoje
- Sequência de dias (streak)
- Total de sessões completadas
- Tempo total de foco
- Histórico de sessões

## 🔧 Funcionalidades Técnicas

### Timer Service
- Roda como Foreground Service
- Mantém o timer ativo mesmo com o app em background
- Notificações persistentes com informações do timer
- Suporta comandos via notificação (futuro)

### Persistência de Dados
- **Room Database**: Armazena histórico de sessões
- **DataStore**: Salva configurações do usuário
- Sincronização automática com a UI via Flow

### Notificações
- Notificação persistente durante timer ativo
- Atualização em tempo real do tempo restante
- Som e vibração ao completar sessões

## 🎵 Sons de Alarme

O aplicativo inclui um som de alarme personalizado localizado em:
```
app/src/main/res/raw/alarm_sound.mp3
```

Você pode substituir este arquivo por qualquer som .mp3 de sua preferência.

## 🔐 Permissões

O aplicativo solicita as seguintes permissões:

- `FOREGROUND_SERVICE`: Para manter o timer rodando em background
- `POST_NOTIFICATIONS`: Para exibir notificações (Android 13+)
- `VIBRATE`: Para vibrar ao completar sessões
- `WAKE_LOCK`: Para manter o dispositivo acordado durante o timer

## 📱 Requisitos do Sistema

- **Mínimo**: Android 8.0 (API 26)
- **Target**: Android 14 (API 34)
- **Recomendado**: Android 10+ para melhor experiência

## 🎯 Técnica Pomodoro

A Técnica Pomodoro é um método de gestão de tempo que utiliza um cronômetro para dividir o trabalho em intervalos:

1. **25 minutos de foco intenso** (1 Pomodoro)
2. **5 minutos de pausa curta**
3. Após 4 Pomodoros, **15 minutos de pausa longa**

## 🚧 Melhorias Futuras

- [ ] Widget para tela inicial
- [ ] Integração com Google Calendar
- [ ] Gráficos de produtividade
- [ ] Temas personalizados
- [ ] Sons de alarme personalizáveis
- [ ] Backup na nuvem
- [ ] Sincronização multi-dispositivo
- [ ] Modo "Não Perturbe" automático

## 📄 Licença

Este projeto é de código aberto e está disponível sob a licença MIT.

## 👨‍💻 Desenvolvimento

Desenvolvido como exemplo de aplicativo Android moderno usando:
- Kotlin
- Jetpack Compose
- Material Design 3
- Clean Architecture
- MVVM Pattern

---

**Nota**: Este é um projeto educacional demonstrando as melhores práticas de desenvolvimento Android.
# Pomodoro
# Pomodoro
