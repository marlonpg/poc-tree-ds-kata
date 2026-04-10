package kdtree

case class Point2D(x: Double, y: Double) {
  def distanceSquaredTo(other: Point2D): Double = {
    val dx = x - other.x
    val dy = y - other.y
    dx * dx + dy * dy
  }
}

enum Axis {
  case X, Y

  def next: Axis = this match {
    case X => Y
    case Y => X
  }
}

case class Node(
    point: Point2D,
    axis: Axis,
    left: Option[Node] = None,
    right: Option[Node] = None
)

class KdTree private (private val root: Option[Node]) {

  def insert(point: Point2D): KdTree =
    KdTree(insertNode(root, point, Axis.X))

  def nearest(query: Point2D): Option[Point2D] =
    root.map { r =>
      val (bestPoint, _) = nearestRecursive(r, query, r.point, r.point.distanceSquaredTo(query))
      bestPoint
    }

  def rootNode: Option[Node] = root

  private def insertNode(current: Option[Node], point: Point2D, axisAtDepth: Axis): Option[Node] = {
    current match {
      case None =>
        Some(Node(point = point, axis = axisAtDepth))

      case Some(node) if node.point == point =>
        // Ignore exact duplicates to keep behavior deterministic.
        current

      case Some(node) =>
        val cmp = compareOnNodeAxis(point, node.point, node.axis)

        if (cmp < 0) {
          Some(node.copy(left = insertNode(node.left, point, node.axis.next)))
        } else {
          // If cmp == 0 but point is different, we still go right.
          // This is a common tie-break rule for KD-Tree insertion.
          Some(node.copy(right = insertNode(node.right, point, node.axis.next)))
        }
    }
  }

  private def nearestRecursive(
      node: Node,
      query: Point2D,
      bestPointSoFar: Point2D,
      bestDistanceSquaredSoFar: Double
  ): (Point2D, Double) = {

    val nodeDistanceSquared = node.point.distanceSquaredTo(query)

    val (bestPointAfterNode, bestDistanceAfterNode) =
      if (nodeDistanceSquared < bestDistanceSquaredSoFar) {
        (node.point, nodeDistanceSquared)
      } else {
        (bestPointSoFar, bestDistanceSquaredSoFar)
      }

    val (nearBranch, farBranch, axisDeltaSquared) = node.axis match {
      case Axis.X =>
        val delta = query.x - node.point.x
        if (delta < 0) {
          (node.left, node.right, delta * delta)
        } else {
          (node.right, node.left, delta * delta)
        }

      case Axis.Y =>
        val delta = query.y - node.point.y
        if (delta < 0) {
          (node.left, node.right, delta * delta)
        } else {
          (node.right, node.left, delta * delta)
        }
    }

    val (bestPointAfterNear, bestDistanceAfterNear) = nearBranch match {
      case Some(child) =>
        nearestRecursive(child, query, bestPointAfterNode, bestDistanceAfterNode)
      case None =>
        (bestPointAfterNode, bestDistanceAfterNode)
    }

    // Core pruning idea:
    // only visit the far branch if the splitting plane is close enough that
    // a point there could still beat our current best distance.
    if (axisDeltaSquared < bestDistanceAfterNear) {
      farBranch match {
        case Some(child) =>
          nearestRecursive(child, query, bestPointAfterNear, bestDistanceAfterNear)
        case None =>
          (bestPointAfterNear, bestDistanceAfterNear)
      }
    } else {
      (bestPointAfterNear, bestDistanceAfterNear)
    }
  }

  private def compareOnNodeAxis(candidate: Point2D, pivot: Point2D, axis: Axis): Double =
    axis match {
      case Axis.X => candidate.x - pivot.x
      case Axis.Y => candidate.y - pivot.y
    }
}

object KdTree {
  val empty: KdTree = KdTree(None)

  def apply(root: Option[Node]): KdTree = new KdTree(root)

  def buildBalanced(points: Seq[Point2D]): KdTree = {
    KdTree(buildBalancedRecursive(points.toVector, Axis.X))
  }

  private def buildBalancedRecursive(points: Vector[Point2D], axis: Axis): Option[Node] = {
    if (points.isEmpty) {
      None
    } else {
      val sorted = axis match {
        case Axis.X => points.sortBy(_.x)
        case Axis.Y => points.sortBy(_.y)
      }

      // For even-size collections, this chooses the upper median.
      // Example size 6 -> index 3.
      val medianIndex = sorted.size / 2
      val medianPoint = sorted(medianIndex)

      val leftPoints = sorted.take(medianIndex)
      val rightPoints = sorted.drop(medianIndex + 1)

      Some(
        Node(
          point = medianPoint,
          axis = axis,
          left = buildBalancedRecursive(leftPoints, axis.next),
          right = buildBalancedRecursive(rightPoints, axis.next)
        )
      )
    }
  }
}
