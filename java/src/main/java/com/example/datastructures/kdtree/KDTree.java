package com.example.datastructures.kdtree;

import java.util.*;

public class KDTree {
    private static class Node {
        int[] point;
        Node left;
        Node right;

        Node(int[] point) {
            this.point = point;
        }
    }

    private Node root;
    private int k;

    public KDTree(int k) {
        this.k = k;
        this.root = null;
    }

    public void insert(int[] point) {
        if (point.length != k) {
            throw new IllegalArgumentException("Point must have " + k + " dimensions");
        }
        root = insertRec(root, point, 0);
    }

    private Node insertRec(Node node, int[] point, int depth) {
        if (node == null) {
            return new Node(point);
        }

        int axis = depth % k;

        if (point[axis] < node.point[axis]) {
            node.left = insertRec(node.left, point, depth + 1);
        } else {
            node.right = insertRec(node.right, point, depth + 1);
        }

        return node;
    }

    public boolean search(int[] point) {
        return searchRec(root, point, 0);
    }

    private boolean searchRec(Node node, int[] point, int depth) {
        if (node == null) {
            return false;
        }

        if (Arrays.equals(node.point, point)) {
            return true;
        }

        int axis = depth % k;

        if (point[axis] < node.point[axis]) {
            return searchRec(node.left, point, depth + 1);
        } else {
            return searchRec(node.right, point, depth + 1);
        }
    }

    public void delete(int[] point) {
        root = deleteRec(root, point, 0);
    }

    private Node deleteRec(Node node, int[] point, int depth) {
        if (node == null) {
            return null;
        }

        int axis = depth % k;

        if (Arrays.equals(node.point, point)) {
            if (node.right != null) {
                Node minNode = findMinRec(node.right, axis, depth + 1);
                node.point = minNode.point;
                node.right = deleteRec(node.right, minNode.point, depth + 1);
            } else if (node.left != null) {
                Node minNode = findMinRec(node.left, axis, depth + 1);
                node.point = minNode.point;
                node.right = deleteRec(node.left, minNode.point, depth + 1);
                node.left = null;
            } else {
                return null;
            }
        } else if (point[axis] < node.point[axis]) {
            node.left = deleteRec(node.left, point, depth + 1);
        } else {
            node.right = deleteRec(node.right, point, depth + 1);
        }

        return node;
    }

    public int[] findMin(int axis) {
        if (axis < 0 || axis >= k) {
            throw new IllegalArgumentException("Axis must be between 0 and " + (k - 1));
        }
        Node minNode = findMinRec(root, axis, 0);
        return minNode != null ? minNode.point : null;
    }

    private Node findMinRec(Node node, int axis, int depth) {
        if (node == null) {
            return null;
        }

        int currentAxis = depth % k;

        if (axis == currentAxis) {
            if (node.left == null) {
                return node;
            }
            return findMinRec(node.left, axis, depth + 1);
        }

        Node leftMin = findMinRec(node.left, axis, depth + 1);
        Node rightMin = findMinRec(node.right, axis, depth + 1);

        Node minNode = node;

        if (leftMin != null && leftMin.point[axis] < minNode.point[axis]) {
            minNode = leftMin;
        }

        if (rightMin != null && rightMin.point[axis] < minNode.point[axis]) {
            minNode = rightMin;
        }

        return minNode;
    }

    public List<int[]> rangeSearch(int[] min, int[] max) {
        List<int[]> result = new ArrayList<>();
        rangeSearchRec(root, min, max, 0, result);
        return result;
    }

    private void rangeSearchRec(Node node, int[] min, int[] max, int depth, List<int[]> result) {
        if (node == null) {
            return;
        }

        boolean inRange = true;
        for (int i = 0; i < k; i++) {
            if (node.point[i] < min[i] || node.point[i] > max[i]) {
                inRange = false;
                break;
            }
        }

        if (inRange) {
            result.add(node.point);
        }

        int axis = depth % k;
        if (min[axis] <= node.point[axis]) {
            rangeSearchRec(node.left, min, max, depth + 1, result);
        }
        if (max[axis] >= node.point[axis]) {
            rangeSearchRec(node.right, min, max, depth + 1, result);
        }
    }

    public static void main(String[] args) {
        KDTree tree = new KDTree(2);

        System.out.println("=== KDTree Example (2D) ===\n");

        int[][] points = {
            {2, 3},
            {5, 4},
            {9, 6},
            {4, 7},
            {8, 1},
            {7, 2}
        };

        System.out.println("Inserting points:");
        for (int[] point : points) {
            System.out.println("  " + Arrays.toString(point));
            tree.insert(point);
        }

        System.out.println("\nSearching for points:");
        int[] searchPoint = {5, 4};
        System.out.println("  Search for " + Arrays.toString(searchPoint) + ": " + tree.search(searchPoint));
        System.out.println("  Search for [10, 10]: " + tree.search(new int[]{10, 10}));

        System.out.println("\nFinding minimum on each axis:");
        int[] minX = tree.findMin(0);
        int[] minY = tree.findMin(1);
        System.out.println("  Min X-axis: " + Arrays.toString(minX));
        System.out.println("  Min Y-axis: " + Arrays.toString(minY));

        System.out.println("\nRange search (min: [3, 2], max: [8, 7]):");
        List<int[]> rangeResults = tree.rangeSearch(new int[]{3, 2}, new int[]{8, 7});
        for (int[] point : rangeResults) {
            System.out.println("  " + Arrays.toString(point));
        }

        System.out.println("\nDeleting point " + Arrays.toString(searchPoint));
        tree.delete(searchPoint);
        System.out.println("  Search for " + Arrays.toString(searchPoint) + " after deletion: " + tree.search(searchPoint));
    }
}
