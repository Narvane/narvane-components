# Component Blueprint (Current Project Pattern)

Este documento extrai a essencia da estrutura atual para criar novos componentes no mesmo estilo.

## 1) Arquitetura base observada

- App testadora no root (`src/.../components`) para navegar e visualizar componentes.
- Cada componente complexo em modulo proprio (exemplo: `:day-planner`).
- Separacao interna por responsabilidade dentro do modulo:
  - `model`: estado, acoes, contratos.
  - `logic`: reducer, regras, validacoes e transicoes.
  - `layout`: calculos de posicionamento/renderizacao.
  - `ui`: composables e interacao visual.
- Dados de exemplo fora do modulo, em `src/jvmMain/kotlin/com/narvane/sampledata`.

## 2) Contrato entre aplicacao e componente (small interface)

O componente deve expor interface pequena para a aplicacao:

- estado de entrada tipado;
- callbacks seletivos (somente eventos que importam para fora);
- detalhes de interacao e comportamento complexo ficam internos.

Regra pratica:

- complexidade interna -> dentro do componente;
- ponto de integracao com app -> interface minima e explicita.

## 3) Passo a passo para novo componente

1. Criar modulo:
   - adicionar `include(":novo-componente")` em `settings.gradle.kts`;
   - criar `novo-componente/build.gradle.kts` seguindo o padrao multiplatform.
2. Definir pacote e fronteiras:
   - `com.narvane.client.<dominio>.model`
   - `com.narvane.client.<dominio>.logic`
   - `com.narvane.client.<dominio>.layout`
   - `com.narvane.client.<dominio>.ui`
3. Criar composable raiz:
   - `NovoComponente(state, modifier, callbacks...)`
   - manter API externa curta.
4. Criar sample data no root:
   - `src/jvmMain/kotlin/com/narvane/sampledata/NovoComponenteSampleData.kt`
5. Conectar no tester:
   - registrar `ComponentMenuItem` em `ComponentsTesterApp`;
   - usar sample data para renderizacao inicial.

## 4) Checklist de qualidade (antes de concluir)

- nomes internos nao repetem contexto ja definido;
- cada pasta conta uma historia unica (sem mistura de niveis de abstracao);
- arquivo raiz do componente orquestra, nao concentra mecanicas detalhadas;
- tester continua generico (catalogo + preview), sem logica de dominio;
- entrada do projeto (main/tester/readme) comunica a mesma narrativa arquitetural.

## 5) Definition of Done para novos componentes

- modulo compilando em Android e Desktop;
- item visivel no menu de componentes;
- preview funcional com sample data;
- sem quebra de fronteira (`model`/`logic`/`layout`/`ui`);
- auto-revisao final contra `developer_DNA.md`.
