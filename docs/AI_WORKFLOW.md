# AI Workflow for This Repository

Este guia define como usar a IA neste projeto com base no seu `developer_DNA.md`.

## 1) Fluxo padrao por tarefa

1. Contexto:
   - "Leia `developer_DNA.md` e extraia os pontos aplicaveis para esta tarefa."
2. Planejamento curto:
   - "Diga em 4-6 bullets como voce vai manter chunking, naming e progressao de leitura."
3. Implementacao:
   - "Implemente seguindo a arquitetura atual (modulo + model/logic/layout/ui + tester)."
4. Revisao final:
   - "Releia `developer_DNA.md` e faca uma auto-revisao de coerencia arquitetural."

### Nota obrigatoria de stack

- Este repositorio e Kotlin Multiplatform + Compose.
- Solicitações de componente no formato React/TypeScript devem ser traduzidas para Kotlin/Compose.
- Sempre integrar componente novo no `ComponentsTesterApp` com sample data.

## 2) Prompt base (copiar e colar)

```text
Leia developer_DNA.md antes de codar.

Objetivo:
<descreva aqui>

Regras:
- manter a mesma linha arquitetural do repositorio;
- evitar nomes redundantes;
- preservar progressao de entendimento pela arvore;
- terminar com auto-revisao contra developer_DNA.md.

Entrega:
1) alteracoes implementadas
2) checklist de aderencia ao developer_DNA.md
3) proximos passos
```

## 3) Prompt para revisar uma feature ja pronta

```text
Atue como revisor arquitetural deste repositorio.
Leia developer_DNA.md e revise os arquivos alterados.
Quero:
1) riscos de incoerencia estrutural
2) pontos de naming redundante
3) sugestoes objetivas de refactor por prioridade
```

## 4) Quando usar cada regra do Cursor

- `developer-dna-loop.mdc`: aplica sempre, para obrigar leitura/revisao do manifesto.
- `component-architecture-kmp.mdc`: ativa em Kotlin (`.kt`) para manter padrao de modulo e fronteiras.
- `tester-integration-flow.mdc`: ativa no tester para garantir integracao enxuta e consistente.
