# DDD Checklist: Identifying Aggregate Roots

This guide provides a systematic "Litmus Test" to help you decide whether a domain class should be an **Aggregate Root** or simply an **Entity** within another aggregate.

---

## 1. The "Aggregate Root" Litmus Test
Ask yourself these three fundamental questions. If the answer to all of them is **"Yes"**, the class is an Aggregate Root.

### Q1: Independent Lifecycle
*   **Question:** Can this object exist meaningfully even if no other major entity is assigned to it?
*   **Example:** A `Genre` can be created (e.g., "Cyberpunk") before any `Movie` is ever assigned to it. It has its own beginning and end.

### Q2: Global Identity
*   **Question:** Do you need to search for, retrieve, or update this object directly via its ID from a repository?
*   **Example:** An admin panel likely has a dedicated screen to "Edit Genre #5". If you need a `GenreRepository.findById()`, it’s a root.

### Q3: Shared Reference
*   **Question:** Can multiple other Aggregate Roots point to the same instance of this object?
*   **Example:** Both "The Matrix" and "Blade Runner" point to the same "Sci-Fi" `Genre` instance. It is not "owned" exclusively by one parent.

---

## 2. Practical Application: Movie vs. Genre

Based on the test above, here is how we model the `movie-service`:

| Entity | Q1: Independent? | Q2: Global ID? | Q3: Shared? | Verdict |
| :--- | :--- | :--- | :--- | :--- |
| **Movie** | Yes | Yes | Yes (by Showtimes) | **Aggregate Root** |
| **Genre** | Yes | Yes | Yes (by Movies) | **Aggregate Root** |

### Why does this matter?
In DDD, only **Aggregate Roots** should have their own Spring Data JPA Repositories. If an entity fails this test (e.g., a `Ticket` within an `Order`), it should usually be accessed only through its parent Aggregate Root to ensure business invariants are maintained.

---

## 3. Modeling the Relationship
When two Aggregate Roots relate to each other (like Movie and Genre), we follow these rules:
1.  **Reference by Identity:** In a microservices or clean DDD world, prefer storing the `UUID` of the other root rather than a direct JPA object reference to avoid massive, tightly-coupled object trees.
2.  **Consistency:** Use eventual consistency (via Kafka/Outbox) if a change in one Aggregate Root must trigger a change in another.
