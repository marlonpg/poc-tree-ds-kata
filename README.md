## Requirement K-Dimensional Tree in Scala 3x + Tests

- Tree Data Structure Kata
    - What is the use case for your DS?
    - What are the pros and cons?
    - Show the code
    - Show the tests

---

## What is a KD-Tree?

A **KD-Tree** is like binary search, but for points in 2D (or more) space.  
It organizes points by **alternating splits** on each axis (x, then y, then x…) so you can quickly find the nearest point without checking every single one.

### Example

**5 points:**

```
A(3,6)  B(7,2)  C(9,1)  D(1,8)  E(5,4)
```

```
  y
  8 │ D
  6 │   A
  4 │       E
  2 │           B
  1 │               C
  0 └──┬──┬──┬──┬──┬──┬──┬──→ x
     1  2  3  4  5  6  7  8  9
```

### Building the tree

At each level, pick the **median** on the current axis and alternate axes.

1. **Level 0 (split on X):** sorted by x → `D(1), A(3), E(5), B(7), C(9)` → median = **E(5,4)** → root
2. **Level 1 (split on Y):**
   - Left `{D, A}` → sorted by y → median = **A(3,6)**; child: D(1,8)
   - Right `{B, C}` → sorted by y → median = **B(7,2)**; child: C(9,1)

```
            E(5,4)         ← split on X
           /      \
       A(3,6)    B(7,2)    ← split on Y
          \       /
        D(1,8)  C(9,1)
```

### Searching: nearest neighbor to Q(6,3)

1. **E(5,4):** distance = √2 ≈ 1.41. Best = 1.41. Q.x=6 ≥ 5 → go right.
2. **B(7,2):** distance = √2 ≈ 1.41. Tied. Go right (empty), check left → C(9,1): distance ≈ 3.61 → worse.
3. **Backtrack to E:** distance to split line |6−5| = 1 < 1.41 → check left side too.
4. **A(3,6):** distance ≈ 4.24 → worse. Distance to A's split line |3−6| = 3 > 1.41 → **skip D entirely**.

**Result:** E(5,4) or B(7,2), distance ≈ 1.41. Checked only **4 of 5** points.

### Why it's fast

Each split **eliminates half** the remaining points. With 1 million points, ~20 checks instead of 1,000,000.

### Use cases

- Find nearest coffee shop on a map
- Game collision detection
- KNN classifiers in machine learning

### Pros & Cons

| Pros | Cons |
|---|---|
| Fast nearest-neighbor search — O(log n) avg | Degrades in high dimensions (>20) |
| Efficient range queries | Worst case O(n) if unbalanced |
| Simple to implement | Insertions/deletions can unbalance the tree |
