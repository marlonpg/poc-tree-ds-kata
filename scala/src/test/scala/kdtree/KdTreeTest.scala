package kdtree

import munit.FunSuite

class KdTreeTest extends FunSuite {

  test("buildBalanced creates expected root and direct children for sample points") {
    val points = Seq(
      Point2D(3, 9), // A
      Point2D(5, 7), // B
      Point2D(9, 6), // C
      Point2D(4, 1), // D
      Point2D(8, 1), // E
      Point2D(7, 2)  // F
    )

    val tree = KdTree.buildBalanced(points)
    val root = tree.rootNode.getOrElse(fail("Root should exist"))

    assertEquals(root.point, Point2D(7, 2))
    assertEquals(root.axis, Axis.X)

    val left = root.left.getOrElse(fail("Left child should exist"))
    val right = root.right.getOrElse(fail("Right child should exist"))

    assertEquals(left.point, Point2D(5, 7))
    assertEquals(left.axis, Axis.Y)

    assertEquals(right.point, Point2D(9, 6))
    assertEquals(right.axis, Axis.Y)
  }

  test("nearest returns expected point for dummies example query") {
    val points = Seq(
      Point2D(2, 3),
      Point2D(5, 4),
      Point2D(9, 6),
      Point2D(4, 7),
      Point2D(8, 1),
      Point2D(7, 2)
    )

    val tree = KdTree.buildBalanced(points)
    val nearest = tree.nearest(Point2D(9, 2)).getOrElse(fail("Nearest should exist"))

    assertEquals(nearest, Point2D(8, 1))
  }

  test("insert builds the same shape from insertion order used in README") {
    val pointsInOrder = Seq(
      Point2D(7, 2), // F root
      Point2D(5, 4), // B left of F
      Point2D(9, 6), // C right of F
      Point2D(2, 3), // A left of B
      Point2D(4, 7), // D right of B
      Point2D(8, 1)  // E left of C
    )

    val tree = pointsInOrder.foldLeft(KdTree.empty) { (acc, p) =>
      acc.insert(p)
    }

    val root = tree.rootNode.getOrElse(fail("Root should exist"))
    assertEquals(root.point, Point2D(7, 2))

    val left = root.left.getOrElse(fail("Left child should exist"))
    val right = root.right.getOrElse(fail("Right child should exist"))

    assertEquals(left.point, Point2D(5, 4))
    assertEquals(right.point, Point2D(9, 6))

    assertEquals(left.left.map(_.point), Some(Point2D(2, 3)))
    assertEquals(left.right.map(_.point), Some(Point2D(4, 7)))
    assertEquals(right.left.map(_.point), Some(Point2D(8, 1)))
  }

  test("nearest on empty tree returns None") {
    assertEquals(KdTree.empty.nearest(Point2D(1, 1)), None)
  }
}
