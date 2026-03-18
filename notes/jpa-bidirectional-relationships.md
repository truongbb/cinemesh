# JPA Best Practices: Managing Bidirectional Relationships

This guide systematizes the challenges and solutions when working with bidirectional 1-N relationships in JPA (e.g., `Room` $\leftrightarrow$ `Seat`), ensuring data integrity and preventing runtime errors.

---

## 1. The "Null Foreign Key" Pitfall
In a bidirectional relationship, one side is the **Owner** (usually the many-side with `@JoinColumn`) and the other is the **Inverse** side (with `mappedBy`).

### The Error
If you only add children to the parent's collection but forget to set the parent on each child, JPA will insert the children with a `NULL` foreign key.

```java
// ❌ INCORRECT: room_id in 'seats' will be NULL
RoomEntity room = new RoomEntity();
SeatEntity seat = new SeatEntity();
room.getSeats().add(seat); 
roomRepository.save(room); 
```

### The Solution: Defensive Linkage
Always implement a helper method in the parent entity to synchronize both sides of the relationship.

```java
// ✅ CORRECT: Entity Helper Method
public void addSeat(SeatEntity seat) {
    this.seats.add(seat);
    seat.setRoom(this); // Crucial: Set the "Owner" side
}
```

---

## 2. The "Infinite Recursion" Pitfall
Bidirectional links create a circular reference (`Room` $\rightarrow$ `Seat` $\rightarrow$ `Room`). This leads to `StackOverflowError` during:
1.  **Lombok/toString()**: Printing one entity triggers the other indefinitely.
2.  **Jackson/JSON Serialization**: Converting the entity to JSON for a REST response.

### Mitigation Strategies

| Level | Tool | Annotation | Purpose |
| :--- | :--- | :--- | :--- |
| **Lombok** | Code Gen | `@ToString.Exclude` | Prevents circularity in logs/debugging. |
| **Jackson** | JSON | `@JsonManagedReference` | Placed on Parent. Tells Jackson to serialize children. |
| **Jackson** | JSON | `@JsonBackReference` | Placed on Child. Tells Jackson to **stop** serializing the parent. |

---

## 3. Deep Dive: Architect's Perspective

### A. The "Dirty Checking" Performance Tax
Hibernate’s "Dirty Checking" mechanism must traverse the entire object graph to see if anything changed. In a complex bidirectional graph, a simple update to a `Room` might trigger Hibernate to inspect 500 `Seats`, leading to high CPU usage and unintentional database locks during peak hours (e.g., a blockbuster movie launch).

### B. The Security Risk of Entity Exposure
In banking, **we never return Entities from Controllers.** 
*   **Leakage:** Entities often contain version numbers, internal IDs, or audit fields (`modified_by`) that should not be visible to the frontend.
*   **API Stability:** If you change your database schema, you shouldn't break your mobile app. DTOs (Data Transfer Objects) provide a stable contract.

### C. Moving Toward Unidirectional Mappings
For truly large-scale systems, we often **avoid bidirectional `@OneToMany` entirely.** 
Instead, we use a unidirectional `@ManyToOne` on the `Seat` and fetch seats via a repository query: `seatRepository.findByRoomId(uuid)`. This keeps the `Room` entity lightweight and prevents the "Circular Reference" problem at the source code level.

---

## 4. Final Recommendation: The DTO Pattern
The most robust solution is to physically remove the cycle using DTOs:

1.  **RoomDto**: Contains `List<SeatDto>`.
2.  **SeatDto**: Contains primitive fields only (no reference back to `RoomDto`).
3.  **Mapper**: Use MapStruct or a manual mapper to convert `RoomEntity` $\rightarrow$ `RoomDto` before returning it to the user.

**Conclusion:** Use `@JsonBackReference` for quick local development, but switch to **DTOs** and **Defensive Linkage** for any system intended for production.
