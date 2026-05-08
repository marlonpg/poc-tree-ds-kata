# Java Data Structures Kata

This directory contains Java implementations of various data structures and algorithms.

## Project Structure

```
java/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── example/
│   │               └── datastructures/
│   │                   └── kdtree/
│   │                       └── KDTree.java
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── datastructures/
│                       └── kdtree/
│                           └── (test files)
├── build/              (compiled output)
├── target/             (Maven/Gradle output)
├── README.md
└── .gitignore
```

## Implementations

### KDTree
A K-Dimensional Tree implementation supporting:
- **Insert**: O(log n) average case - recursively adds points by alternating dimensions
- **Search**: O(log n) average case - finds points in the tree
- **Delete**: O(log n) average case - removes nodes while maintaining structure
- **Find Minimum**: finds minimum value along a specific axis
- **Range Search**: finds all points within a rectangular region

## Building & Running with Maven

Build the project:
```bash
./mvnw clean compile
```

Run the application:
```bash
./mvnw exec:java
```

Run tests:
```bash
./mvnw test
```

Build a JAR file:
```bash
./mvnw clean package
```

Run the generated JAR:
```bash
java -jar target/data-structures-kata-1.0.0.jar
```

## Alternative: Direct Java Compilation

If you prefer to use javac directly:
```bash
javac -d target/classes src/main/java/com/example/datastructures/**/*.java
java -cp target/classes com.example.datastructures.kdtree.KDTree
```

## Testing

Place unit tests in `src/test/java/` following the same package structure as the main code.

Run all tests with Maven:
```bash
./mvnw test
```

Example test structure:
```
src/test/java/com/example/datastructures/kdtree/
└── KDTreeTest.java
```
