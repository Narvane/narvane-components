# Developer DNA

Personal engineering manifesto: how structure, naming, and hierarchy should communicate meaning without external explanation—**end-to-end**, from repo root and entrypoints through the last file, with **no layer exempt** from coherence.

**Reasoning style:** Structure is designed with **cognitive psychology and information architecture** in mind—how the brain groups elements, holds them in working memory, and builds meaning from patterns. AI-generated layout should respect the same mental model.

---

## 1. Core Philosophy

### Cognitive foundation

The developer treats code organization as **human information architecture**. The brain does not read code linearly; it **chunks** related pieces into units, holds only a few units in focus at once, and **fills in** expected parts when a chunk is recognized. Good structure aligns with that process:

- **Chunking** — Related types or modules should read as **one conceptual unit** (e.g., a subsystem name evokes a small set of expected roles). Contents should **complete the chunk**, not switch to a different abstraction level.
- **Limited working memory** — Lists that are too long or heterogeneous **overload scanning**; the reader cannot hold relationships in mind. Boundaries should keep **visible surface area** within what can be grasped at a glance.
- **Progressive disclosure** — Each navigation step should **add context** that primes understanding of the next step, so meaning compounds instead of resetting.

Structure that fights these patterns forces **extra analysis per element**—high cognitive load with little gain.

### Meaning through navigation

The system should be understandable by **moving through its hierarchy**, not by reading prose elsewhere. Opening the project, then packages, then classes, then methods should **progressively reveal** what the system is and does. Each level is a lens; together they tell the story. This mirrors **progressive disclosure** in IA: project → package → class → method, each layer **clarifying** the next.

### Structure as intent

Layout and boundaries are not neutral—they **communicate intent**. If intent is not visible from structure alone, the structure has failed its primary documentation role. Intent is also **chunk-level**: the name of a package or module should evoke a **single coherent concept** whose parts are the types inside it.

### End-to-end coherence (non-optional)

**Coherence is not optional at any layer.** The same rules that govern packages and classes—**naming, chunking, progressive disclosure, intent-without-commentary**—apply equally to **entrypoints, samples, scripts, and bootstrap code**. There is no split where “outer structure is sacred” and “inner or leaf files are free form.”

Coherence runs **from the first folder through the last file**. If an entrypoint reads like a **disconnected script** while the rest of the codebase reads as **architecture**, the system is **incoherent**: the first file a reader opens (repo root, `main`, a sample) should **continue the same story**, not reset the mental model to one-off procedural noise.

The **whole application**—including how someone **first opens the repo** and lands on a main or sample—must read as **one consistent story**. Treat entrypoints and samples as part of the **same IA** as core modules: they are where progressive disclosure **starts**, so they must chunk and name in line with everything else.

---

## 2. Naming Philosophy

### Context-aware naming

Names should carry **only the information not already fixed by context**. When a class, package, or module already establishes domain or role, inner names must not repeat it for the sake of “clarity”—repetition reads as noise and obscures the real distinctions.

**Example**

- **Prefer** (inside `UserService`): `repository.findOne()`  
- **Avoid**: `userRepository.findUser()`  

**Reason:** `UserService` already fixes the domain. Repeating “user” on the repository and method adds no signal and makes scanning harder.

### Package-level naming

At package scope, **the set of type names** should read like a coherent vocabulary. Someone listing classes in a package should be able to **infer the package’s responsibility** from those names alone, without opening implementations. That list is a **semantic field**: together it should **activate one subsystem concept** (like reading *login*, *token*, *session* activates “auth”) rather than a mixed bag of unrelated concerns.

---

## 3. Context and Hierarchy

### Hierarchy as progressive disclosure

Meaning is **contextual and layered**:

1. **Project / repo surface** — root layout, entrypoints, samples, scripts (first impression; must align with the rest)  
2. **Packages** — bounded areas of responsibility  
3. **Classes** — roles within that area  
4. **Methods** — operations and behaviors  

Each step down should **narrow and sharpen** understanding. If a level does not reduce ambiguity, reconsider boundaries or naming. **Entrypoints and bootstrap are not exempt**—they are the top of the same ladder.

### Packages as semantic groups (chunks)

Package contents must form a **coherent semantic group**—in cognitive terms, a **chunk**: a small set of elements the brain can bind into one concept. Class names together should:

- **Complete one story** at a **consistent abstraction level**  
- Avoid stray concepts that belong elsewhere  
- Make the package’s purpose obvious at a glance  

**Abstraction-level mismatch** breaks the chunk: the container label promises one kind of concept, but the members belong to a different level of description. The reader is forced to switch mental models (e.g., from “parts of a whole” to “ingredients/materials”) and pattern recognition fails.

| Chunk holds | Members fit | Members misfit (same label, wrong level) |
|-------------|-------------|------------------------------------------|
| One concept at one granularity | Roles/parts that compose that concept | Finer-grained or orthogonal vocabulary that does not compose the same idea |

If the package reads like a junk drawer, split or rename until the group is clear and **one glance** activates the right concept.

---

## 4. Code Readability

### Step-by-step exploration

A developer should be able to **explore the system incrementally**—open, drill down, read names and signatures—and **gradually understand it** without needing walkthroughs or external docs. Readability here includes **navigability**: call sites, package layout, and type names should chain into a clear mental model. That chain **begins** at whatever file or folder they open first; if that step feels ad hoc, the chain is broken before it starts.

### Cognitive load

Structure should **reduce** unnecessary mental effort: clear grouping lets the brain **recognize patterns** instead of **analyzing every item from scratch**. Poor organization spreads attention across unrelated details and raises **cognitive load** without adding meaning. Semantic coherence and chunk-aligned boundaries exist primarily to keep load low while preserving precision.

### Intent without commentary

Where possible, **structure and names replace comments**. If you must explain hierarchy in a README before the code makes sense, the code structure likely needs refinement.

---

## 5. Abstraction Preferences

### Surface area and working memory

**Miller’s law** (and related limits on working memory) inform how much should sit **behind one boundary**. People comfortably hold roughly **5–9 items** in mind when judging relationships at a glance. This is **not a hard cap**—it is a **cognitive guideline**: when a module or package grows past what can be scanned as one pattern, splitting or regrouping often improves comprehension more than adding comments.

Prefer abstractions whose **boundaries match how you want the system to be discovered**—small enough to hold in mind when opening a package, large enough not to fragment the semantic group. If opening a folder forces a long, heterogeneous list, the boundary likely does not match a **single chunk**.

---

## 6. Repetition and Redundancy

### Redundancy that hurts

Avoid **repeating the same semantic information** at multiple scopes when the outer scope already fixes it:

- Domain repeated in every method name inside a domain-scoped class  
- Role repeated on every dependency when the consumer’s name already implies it  
- Package name echoed in every class name inside the package (unless disambiguation is required)

### Repetition that helps

Repetition is acceptable when it **disambiguates** across boundaries (e.g., two different `findOne` concepts in the same file) or when dropping it would make **search or grep** unreliable. Prefer dropping redundancy **within** a fixed context first.

---

## 7. Architectural Preferences

Favor boundaries that make **package responsibility inferable from its public surface** (type names, primary packages), aligning with **semantic grouping at each layer**.

Architectural splits should **preserve chunk integrity**: each package or module should be organizable so that its **public list of types** reads as **one subsystem**. Avoid boundaries that mix abstraction levels under one label or that aggregate unrelated concerns into a single navigational step—both force context switches and increase load.

### Context-sensitive exposure (tree as communication)

What appears in the file tree is part of the architecture's **public narrative**. Exposure decisions should be made by asking: **what context is this project in, and what should a newcomer understand at a glance?**

- In a **focused component project/POC**, it is valid to expose more UI parts as first-class files, because the tree itself is the product's map.
- In a **large monolith**, exposing every implementation detail as sibling files can create semantic noise; more items should remain internal/private when they do not improve top-level understanding.

The key is not "many files" or "few files"; it is whether each exposed file helps the reader form the correct chunk at that level.

### Subcontext decomposition (when to break further)

Do not split one composable into one file per tiny primitive by default. Split when a coherent **subcontext** emerges.

- Use **3-5 major children** per visible box as a practical cognitive guide (not a hard rule).
- If a component grows beyond that and children start mixing purposes, that usually signals a missing intermediate scope.
- If a component has many `Box`/`Row`/`Column` layers but only a few conceptual parts, introduce named subcomponents at the conceptual level.

Applied pattern from this codebase refactor:

- `AgendaPlanner` now acts as composition root (title + frame + editor sheet).
- Inside the frame, two clear subcontexts are exposed: `AgendaHoursColumn` and `AgendaTimelineGrid`.
- Interaction details are internal to their own subcontexts: `AgendaTimeBlock` and `AgendaResizeHandle`.

**Entrypoints, samples, and scripts** are architectural surface, not exceptions. How `main`, CLI bootstrap, and examples are named and structured should **reinforce** the same chunks and vocabulary as the core code. If they live in the repo, they are part of the **same story**.

---

## 8. Code Smells (Personal)

- **Noise naming:** Same word repeated at class and member level without adding distinction.  
- **Opaque packages:** Class names in a package do not cohere; responsibility is unclear without reading code.  
- **Flat or misleading hierarchy:** Opening a level does not narrow meaning—everything feels equally important or unrelated.  
- **Broken chunk / abstraction mismatch:** Package or module name suggests one concept (e.g., a composed whole), but children are named at a **different granularity** (e.g., materials, primitives, or cross-cutting utilities). The reader cannot form a single pattern.  
- **Overload at a glance:** Too many siblings at one level with no sub-structure; working memory cannot hold relationships without external notes.  
- **Entrypoint as script island:** Main, sample, or bootstrap reads like a one-off procedural dump while the rest of the tree reads as structured architecture—**same repo, two mental models**; incoherent.

---

## 9. Decision Heuristics

### When naming something inside a scoped context

1. What context is already fixed by the **enclosing class, package, or module**?  
2. Does the new name add **new** information, or only repeat that context?  
3. If repeated, can it be shortened or generalized (`repository`, `findOne`) without losing precision **at this call site**?

### When organizing a package

1. If you listed only the **class names**, would a newcomer guess the package’s job?  
2. Does any class **break the story**? If yes, move or rename.  
3. Does the hierarchy from project → package → class **progressively** answer “what is this?”  
4. **Chunk check:** Does this set of elements **form one recognizable concept** at one abstraction level?  
5. **Scan check:** Is this structure **easy for the brain to scan**—few enough related pieces that relationships are obvious?  
6. **Level check:** Are children **at the same kind of abstraction** as the container’s implied concept, or does the list force a category shift?

### When adding entrypoints, samples, or scripts

1. Would this file **fit the same chunking and naming** rules as a package in the core?  
2. Does the **first open** of the repo (README, main, sample) **continue** progressive disclosure or **reset** it to unstructured script?  
3. If the core is architecture, the entrypoint must **read as architecture**—same intent-without-commentary standard.

### When splitting UI into subcomponents

1. First identify the **conceptual children** (not Compose primitives): what are the 2-5 meaningful boxes this component is made of?  
2. Expose children as files when they help the tree tell the subsystem story in this project's context.  
3. Keep details internal when they are implementation mechanics that do not add navigational meaning at that level.  
4. If sibling count or heterogeneity grows, create a **subcontext component** before creating many peer files.  
5. Re-check the parent file: it should read mostly as composition/orchestration, not low-level mechanics.

---

## 10. Examples

| Context        | Prefer              | Avoid                    | Rationale                          |
|----------------|---------------------|--------------------------|------------------------------------|
| Inside `UserService` | `repository.findOne()` | `userRepository.findUser()` | Domain already established by class |
| Package layout | Names read as one domain | Mixed unrelated concepts | Infer responsibility from listing |
| Navigation     | Each level narrows meaning | Flat or redundant layers | Progressive disclosure             |
| Chunk / package | Members complete one concept (e.g., auth: login, token, session) | Same label, members at wrong level (e.g., “house” full of materials only) | One abstraction level; pattern completes |
| Module size    | Scannable list, one pattern | Very long or heterogeneous sibling list | Working memory; Miller-style guideline |
| End-to-end     | Main/sample/bootstrap chunks like core | Entrypoint as disconnected script | One story from first folder to last file |
| UI composition (focused project) | Expose meaningful parts (`AgendaPlanner`, `AgendaHoursColumn`, `AgendaTimelineGrid`) | Keep everything in one giant file | Tree communicates component structure |
| UI internals | Keep mechanics under subcontext (`AgendaTimeBlock`, `AgendaResizeHandle`) | Promote every tiny helper to top-level peer without narrative value | Preserve scanability without noise |

---

## 11. Refactor Evidence (Applied)

Use concrete refactor examples as evidence of the model, not abstract preference.

### Example: `AgendaPlanner` decomposition in this project

At one point, the planner UI lived mostly in a single file with mixed concerns. It was split into explicit subcontexts based on:

1. **Conceptual composition** (what the parent is made of at first glance).  
2. **Narrative value in the tree** (what should be visible to orient a newcomer).  
3. **Mechanics vs. meaning** (keep implementation details under the right subcontext).

Resulting structure:

- `AgendaPlanner` as composition root (title + frame + editor sheet).  
- `AgendaHoursColumn` and `AgendaTimelineGrid` as first-level children (clear structural meaning).  
- `AgendaTimeBlock` and `AgendaResizeHandle` extracted from the large file and grouped as interaction mechanics.  
- Domain model/rules/layout logic moved out of UI files (`model`, `logic`, `layout`) to preserve abstraction boundaries.

Reasoning statement:

> I broke files this way because the previous surface mixed too many levels at once.  
> I split by conceptual boxes first, exposed only boxes that improved tree readability, and kept low-level mechanics inside their subcontext.

---

## 12. Heuristics Are Cognitive Triggers, Not Hard Limits

Line count, number of blocks, and sibling count are **not rigid restrictions**. They are prompts to stop and ask:

- "Is this still understandable to another person at a glance?"  
- "Does this container still communicate one clear concept?"  
- "Would someone infer intent without opening many files?"

Typical scanability guideline:

- Around **3-5 meaningful children** per visible container is often easier to understand quickly.
- More than that is not "wrong", but usually indicates either:
  - mixed abstraction levels, or
  - a missing intermediate subcontext.

### House-box thought experiment (why this matters)

If a box contains:

- `Floor`, `Roof`, `Ceiling`  

A reader can likely infer: this is a "House structure" concept.

If a box contains:

- `Floor`, `Roof`, `Ceiling`, `Brick`, `Sand`, `Mortar`, `Bathroom`, `Tile`, `Attic`...

The reader now asks:

- Is "House" in this system a high-level concept or a deep implementation bucket?
- Do these names describe parts, materials, rooms, construction phases, or all at once?

When this happens, understanding requires opening many boxes/files, which increases time and decreases confidence in the mental model.

Design implication:

> Prefer boundaries that let a newcomer infer purpose early.  
> Use quantitative heuristics to detect cognitive overload, then restructure semantically.

---

*Document evolves with new preferences; merge and refine rather than duplicate.*
