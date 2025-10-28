# 📋 Instruções Completas - TimeTracker

## 🗂️ Estrutura Completa de Arquivos Criados

### Arquivos de Configuração do Projeto (Raiz)
```
/TimeTracker/
├── build.gradle.kts                    # Configuração Gradle do projeto
├── settings.gradle.kts                 # Configurações do projeto
├── gradle.properties                   # Propriedades do Gradle
└── README.md                          # Documentação principal
```

### Módulo App - Configurações
```
/TimeTracker/app/
├── build.gradle.kts                    # Configuração Gradle do app
├── proguard-rules.pro                  # Regras ProGuard
└── src/main/AndroidManifest.xml       # Manifest do Android
```

### Código Fonte - Pacote Principal
```
/TimeTracker/app/src/main/java/com/timetracker/
├── TimeTrackerApplication.kt           # Classe Application (Hilt)
└── MainActivity.kt                     # Activity principal
```

### Camada de Domínio (Domain Layer)
```
/TimeTracker/app/src/main/java/com/timetracker/domain/model/
└── TimerModels.kt                      # Modelos: TimerState, SessionType, TimerConfig, 
                                        #          PomodoroSession, TimerInfo
```

### Camada de Dados (Data Layer)
```
/TimeTracker/app/src/main/java/com/timetracker/data/
├── local/
│   ├── Database.kt                     # Room Database, DAO, Converters
│   └── UserPreferencesManager.kt       # DataStore para preferências
└── repository/
    └── TimerRepository.kt              # Repositório central
```

### Serviços (Service Layer)
```
/TimeTracker/app/src/main/java/com/timetracker/service/
└── TimerService.kt                     # Foreground Service do timer com:
                                        # - CountDownTimer
                                        # - MediaPlayer para som
                                        # - Vibrador
                                        # - Notificações
```

### Utilitários (Utils)
```
/TimeTracker/app/src/main/java/com/timetracker/util/
└── Utils.kt                            # TimeUtils e StatisticsUtils
```

### Injeção de Dependência (DI)
```
/TimeTracker/app/src/main/java/com/timetracker/di/
└── AppModule.kt                        # Módulo Hilt com providers
```

### UI - Tema (Theme)
```
/TimeTracker/app/src/main/java/com/timetracker/ui/theme/
├── Color.kt                            # Esquema de cores Material 3
├── Type.kt                             # Tipografia
└── Theme.kt                            # Tema principal
```

### UI - Componentes (Components)
```
/TimeTracker/app/src/main/java/com/timetracker/ui/components/
├── CircularTimer.kt                    # Timer circular animado com Canvas
├── SessionIndicator.kt                 # Indicador visual de sessões
└── StatisticsCard.kt                   # Card de estatísticas
```

### UI - Telas (Screens)
```
/TimeTracker/app/src/main/java/com/timetracker/ui/screens/
├── TimerViewModel.kt                   # ViewModel principal
├── TimerScreen.kt                      # Tela do timer
├── SettingsScreen.kt                   # Tela de configurações
└── StatisticsScreen.kt                 # Tela de estatísticas
```

### UI - Navegação
```
/TimeTracker/app/src/main/java/com/timetracker/ui/
└── Navigation.kt                       # NavHost e rotas
```

### Recursos (Resources)
```
/TimeTracker/app/src/main/res/
├── drawable/
│   └── ic_timer.xml                    # Ícone do timer (Vector)
├── raw/
│   └── alarm_sound.mp3                 # Som do alarme
├── values/
│   ├── strings.xml                     # Strings do app
│   └── themes.xml                      # Tema XML
└── xml/
    ├── backup_rules.xml                # Regras de backup
    └── data_extraction_rules.xml       # Regras de extração
```

## 🔧 Detalhamento dos Componentes

### 1. TimerService (Componente Central)
**Localização**: `/app/src/main/java/com/timetracker/service/TimerService.kt`

**Responsabilidades**:
- Gerencia o CountDownTimer
- Mantém notificação em foreground
- Toca som ao completar sessão
- Vibra ao completar sessão
- Atualiza StateFlow com informações do timer
- Gerencia ciclo de sessões (trabalho → pausa curta → pausa longa)

**Recursos Utilizados**:
- CountDownTimer para contagem regressiva
- MediaPlayer para reproduzir alarme
- Vibrator para feedback tátil
- NotificationManager para notificações persistentes

### 2. Room Database
**Localização**: `/app/src/main/java/com/timetracker/data/local/Database.kt`

**Entidades**:
- `PomodoroSessionEntity`: Armazena histórico de sessões

**DAOs**:
- `PomodoroSessionDao`: Interface de acesso aos dados
  - Inserir sessões
  - Buscar todas as sessões
  - Filtrar por data
  - Contar sessões completadas

### 3. DataStore
**Localização**: `/app/src/main/java/com/timetracker/data/local/UserPreferencesManager.kt`

**Dados Armazenados**:
- Duração de trabalho
- Duração de pausas (curta e longa)
- Número de sessões até pausa longa
- Auto-início (pausas e sessões)
- Sons e vibração habilitados
- Estatísticas gerais

### 4. ViewModel
**Localização**: `/app/src/main/java/com/timetracker/ui/screens/TimerViewModel.kt`

**Responsabilidades**:
- Conectar com TimerService via Binder
- Observar mudanças no timer
- Gerenciar configurações
- Salvar sessões completadas
- Calcular estatísticas
- Expor StateFlows para a UI

### 5. Componentes de UI

#### CircularTimer
**Localização**: `/app/src/main/java/com/timetracker/ui/components/CircularTimer.kt`

- Canvas customizado
- Animação suave de progresso
- Cores dinâmicas por tipo de sessão
- Display do tempo formatado

#### SessionIndicator
**Localização**: `/app/src/main/java/com/timetracker/ui/components/SessionIndicator.kt`

- Indicadores circulares
- Mostra progresso no ciclo
- Sessão atual destacada

#### StatisticsCard
**Localização**: `/app/src/main/java/com/timetracker/ui/components/StatisticsCard.kt`

- Card Material 3
- Título, valor principal e subtítulo
- Usado na tela de estatísticas

## 🎨 Fluxo de Dados

```
User Input (UI)
    ↓
ViewModel
    ↓
TimerService ←→ Repository ←→ Database/DataStore
    ↓                              ↓
StateFlow                     Preferences
    ↓
UI (Recompose)
```

## 🔄 Ciclo de Vida do Timer

1. **IDLE**: Timer parado, pronto para iniciar
2. **RUNNING**: Timer em execução, contando regressivamente
3. **PAUSED**: Timer pausado, pode ser retomado
4. **FINISHED**: Sessão completada, preparando próxima

## 📊 Fluxo de Sessões

```
Trabalho (25min) 
    → Pausa Curta (5min) 
    → Trabalho 
    → Pausa Curta 
    → Trabalho 
    → Pausa Curta 
    → Trabalho 
    → Pausa Longa (15min) 
    → [Ciclo reinicia]
```

## 🚀 Como Importar no Android Studio

1. **Abra o Android Studio**
2. **File → Open** 
3. Navegue até a pasta `/TimeTracker`
4. Clique em **OK**
5. Aguarde o Gradle sincronizar (primeira vez pode demorar)
6. Vá em **Build → Make Project** para compilar
7. Execute em um dispositivo/emulador

## 🐛 Solução de Problemas Comuns

### Erro: "Manifest merger failed"
- Verifique se todas as permissões estão no AndroidManifest.xml
- Limpe o projeto: Build → Clean Project

### Erro: "Unresolved reference"
- Sincronize o Gradle: File → Sync Project with Gradle Files
- Invalide caches: File → Invalidate Caches / Restart

### Erro: "Failed to resolve: hilt"
- Verifique a conexão com internet
- Tente novamente: Build → Rebuild Project

### Som do alarme não toca
- Verifique se o arquivo alarm_sound.mp3 está em res/raw/
- Certifique-se de que as permissões de áudio estão concedidas

### Notificações não aparecem
- Android 13+: Verifique se a permissão POST_NOTIFICATIONS foi concedida
- Vá em Configurações do App → Notificações → Habilitar

## 📱 Testando o App

### Teste Básico
1. Abra o app
2. Clique em Play ▶️
3. Observe o timer contando
4. Clique em Pause ⏸️ para pausar
5. Clique em Reset 🔄 para resetar
6. Clique em Skip ⏭️ para pular sessão

### Teste de Configurações
1. Clique no ícone de Settings ⚙️
2. Ajuste as durações com os sliders
3. Ative/desative auto-início
4. Ative/desative som e vibração
5. Clique em "Salvar Configurações"
6. Volte e inicie o timer - deve usar novas configurações

### Teste de Estatísticas
1. Complete pelo menos uma sessão
2. Clique no ícone de Statistics 📊
3. Verifique se a sessão aparece no histórico
4. Verifique se os contadores foram atualizados

### Teste de Notificação
1. Inicie o timer
2. Pressione Home para minimizar o app
3. A notificação deve permanecer visível
4. O timer deve continuar contando
5. Complete a sessão - deve tocar alarme e vibrar

## 📝 Checklist de Verificação

- [ ] Projeto compila sem erros
- [ ] App instala no dispositivo/emulador
- [ ] Timer inicia e conta corretamente
- [ ] Pausa funciona
- [ ] Reset funciona
- [ ] Skip muda para próxima sessão
- [ ] Som toca ao completar sessão
- [ ] Vibração funciona
- [ ] Notificação aparece em foreground
- [ ] Notificação persiste em background
- [ ] Configurações salvam corretamente
- [ ] Estatísticas atualizam corretamente
- [ ] Navegação entre telas funciona
- [ ] Tema escuro/claro funciona

## 🎓 Conceitos Aprendidos

Este projeto demonstra:

✅ **Jetpack Compose** - UI declarativa moderna  
✅ **Material Design 3** - Design system do Google  
✅ **MVVM** - Arquitetura recomendada  
✅ **Clean Architecture** - Separação de responsabilidades  
✅ **Hilt** - Injeção de dependência  
✅ **Room** - Persistência local com SQL  
✅ **DataStore** - Armazenamento de preferências  
✅ **Coroutines & Flow** - Programação assíncrona  
✅ **Foreground Service** - Serviço persistente  
✅ **Notifications** - Sistema de notificações Android  
✅ **Canvas** - Desenho customizado  
✅ **Navigation Compose** - Navegação entre telas  
✅ **ViewModel** - Gerenciamento de estado  
✅ **StateFlow** - Estado reativo  

## 📚 Recursos Adicionais

- [Documentação Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Hilt Documentation](https://dagger.dev/hilt/)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Foreground Services](https://developer.android.com/guide/components/foreground-services)

---

**Criado com ❤️ usando Kotlin e Jetpack Compose**
