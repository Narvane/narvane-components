# Day Planner POC - Run rapido

## O que ja esta pronto

- Root app: `src/jvmMain/kotlin/com/example/components`
- Modulo de componente: `day-planner`
- Componente principal: `day-planner/src/jvmMain/kotlin/com/example/client/day/ui/DayPlanner.kt`
- App de preview: `src/jvmMain/kotlin/com/example/components/ComponentsTesterApp.kt`
- Main desktop: `src/jvmMain/kotlin/com/example/components/Main.kt`
- Sample data no root source: `src/jvmMain/kotlin/com/example/sampledata/DayPlannerSampleData.kt`

> Observacao: este repositorio adota Kotlin Multiplatform + Compose para componentes.
> Componentes novos devem ser adicionados ao `ComponentsTesterApp` para teste visual.

## Como rodar no IntelliJ

1. Abra a pasta no IntelliJ e espere sincronizar o Gradle.
2. Rode a task Gradle `run`.
3. Ou rode a classe `MainKt`.

## Como rodar no Android Studio

1. Abra a pasta do projeto no Android Studio e aguarde o sync.
2. Selecione um device Android (emulador ou celular).
3. Rode a configuração `composeApp [android]` (ou execute a task `installDebug`).
4. A activity de entrada e `com.narvane.MainActivity`.

## Como reaproveitar no seu app atual

No seu `setContent { ... }`, renderize `DayPlanner(...)`.
Para dados de exemplo, use `DayPlannerSampleData.defaultState()`.

## Interacao implementada

- Grade por hora entre `startHour` e `endHour` (ex.: 06h-20h)
- Selecao por arrasto em slots de 15 minutos
- Desktop: arraste imediato
- Mobile: long press + arraste (melhora scroll vertical)
- Ao soltar: abre editor para titulo do evento
- Persistencia em memoria local (POC)
