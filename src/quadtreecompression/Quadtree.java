package quadtreecompression;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Quadtree {
    private class Node {
        int x, y, width, height;
        Color averageColor;
        Node topLeft, topRight, bottomLeft, bottomRight;
        boolean isLeaf;

        public Node(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    private Node root;
    private final BufferedImage image;
    private final double errorThreshold;
    private final int minBlockSize;
    private final ErrorCalculationMethod method;

    public Quadtree(BufferedImage image, double errorThreshold, int minBlockSize, ErrorCalculationMethod method) {
        this.image = image;
        this.errorThreshold = errorThreshold;
        this.minBlockSize = minBlockSize;
        this.method = method;
    }

    private Node buildTree(int x, int y, int width, int height) {
        Node node = new Node(x, y, width, height);
        double error = calculateError(x, y, width, height);

        if (error < errorThreshold || width <= minBlockSize || height <= minBlockSize) {
            node.isLeaf = true;
            node.averageColor = calculateAverageColor(x, y, width, height);
        } else {
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            node.topLeft = buildTree(x, y, halfWidth, halfHeight);
            node.topRight = buildTree(x + halfWidth, y, width - halfWidth, halfHeight);
            node.bottomLeft = buildTree(x, y + halfHeight, halfWidth, height - halfHeight);
            node.bottomRight = buildTree(x + halfWidth, y + halfHeight, width - halfWidth, height - halfHeight);
        }
        return node;
    }

    public void build() {
        root = buildTree(0, 0, image.getWidth(), image.getHeight());
    }

    private Color calculateAverageColor(int x, int y, int width, int height) {
        long sumR = 0, sumG = 0, sumB = 0;
        int count = 0;
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                int rgb = image.getRGB(i, j);
                Color color = new Color(rgb);
                sumR += color.getRed();
                sumG += color.getGreen();
                sumB += color.getBlue();
                count++;
            }
        }
        return new Color((int)(sumR/count), (int)(sumG/count), (int)(sumB/count));
    }

    private double calculateError(int x, int y, int width, int height) {
        switch (method) {
            case ErrorCalculationMethod.VARIANCE -> {
                return calculateErrorByVariance(x, y, width, height);
            }
            case ErrorCalculationMethod.MAD -> {
                return calculateErrorByMAD(x, y, width, height);
            }
            case ErrorCalculationMethod.MAX_PIXEL_DIFFERENCE -> {
                return calculateErrorByMPD(x, y, width, height);
            }
            case ErrorCalculationMethod.ENTROPY -> {
                return calculateErrorByEntropy(x, y, width, height);
            }
            case ErrorCalculationMethod.SSIM -> {
                return calculateErrorBySSIM(x, y, width, height);
            }
            default -> {
                return 0.0;
            }
        }
    }

    private double calculateErrorByVariance(int x, int y, int width, int height) {
        Color avgColor = calculateAverageColor(x, y, width, height);
        double avgR = avgColor.getRed(), avgG = avgColor.getGreen(), avgB = avgColor.getBlue();

        double sumR = 0.0, sumG = 0.0, sumB = 0.0;
        int count = 0;

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                int rgb = image.getRGB(i, j);
                Color color = new Color(rgb);
                double valueR = color.getRed() - avgR;
                double valueG = color.getGreen() - avgG;
                double valueB = color.getBlue() - avgB;
                sumR += valueR * valueR;
                sumG += valueG * valueG;
                sumB += valueB * valueB;
                count++;
            }
        }

        double varR = sumR / count;
        double varG = sumG / count;
        double varB = sumB / count;

        return (varR + varG + varB) / 3.0;
    }

    private double calculateErrorByMAD(int x, int y, int width, int height) {
        Color avgColor = calculateAverageColor(x, y, width, height);
        double avgR = avgColor.getRed(), avgG = avgColor.getGreen(), avgB = avgColor.getBlue();

        double valueR = 0.0, valueG = 0.0, valueB = 0.0;
        int count = 0;

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                int rgb = image.getRGB(i, j);
                Color color = new Color(rgb);
                valueR += Math.abs(color.getRed() - avgR);
                valueG += Math.abs(color.getGreen() - avgG);
                valueB += Math.abs(color.getBlue() - avgB);
                count++;
            }
        }

        double madR = valueR / count;
        double madG = valueG / count;
        double madB = valueB / count;

        return (madR + madG + madB) / 3.0;
    }

    private double calculateErrorByMPD(int x, int y, int width, int height) {
        int maxR = 0, maxG = 0, maxB = 0;
        int minR = 255, minG = 255, minB = 255;

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                int rgb = image.getRGB(i, j);
                Color color = new Color(rgb);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                if (r > maxR) {
                    maxR = r;
                }
                if (g > maxG) {
                    maxG = g;
                }
                if (b > maxB) {
                    maxB = b;
                }
                if (r < minR) {
                    minR = r;
                }
                if (g < minG) {
                    minG = g;
                }
                if (b < minB) {
                    minB = b;
                }
            }
        }

        double diffR = maxR - minR;
        double diffG = maxG - minG;
        double diffB = maxB - minB;

        return (diffR + diffG + diffB) / 3.0;
    }

    private double calculateErrorByEntropy(int x, int y, int width, int height) {
        int[] histogram = new int[256];
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                int rgb = image.getRGB(i, j);
                Color color = new Color(rgb);
                int intensity = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                histogram[intensity]++;
            }
        }

        int totalPixels = width * height;
        double entropy = 0.0;
        for (int value : histogram) {
            if (value > 0) {
                double probability = (double) value / totalPixels;
                entropy += -probability * (Math.log(probability) / Math.log(2));
            }
        }

        return entropy;
    }

    private double calculateErrorBySSIM(int x, int y, int width, int height) {
        final double K1 = 0.01;
        final double K2 = 0.03;
        final double L = 255;
        final double C1 = (K1 * L) * (K1 * L);
        final double C2 = (K2 * L) * (K2 * L);

        int count = width * height;
        double sumR = 0.0, sumG = 0.0, sumB = 0.0;
        double sumSquaredR = 0.0, sumSquaredG = 0.0, sumSquaredB = 0.0;

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                int rgb = image.getRGB(i, j);
                Color color = new Color(rgb);
                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();
                sumR += r;
                sumG += g;
                sumB += b;
                sumSquaredR += r * r;
                sumSquaredG += g * g;
                sumSquaredB += b * b;
            }
        }

        double muR = sumR / count;
        double muG = sumG / count;
        double muB = sumB / count;

        double varR = (sumSquaredR / count) - (muR * muR);
        double varG = (sumSquaredG / count) - (muG * muG);
        double varB = (sumSquaredB / count) - (muB * muB);

        double ssimR = C2 / (varR + C2);
        double ssimG = C2 / (varG + C2);
        double ssimB = C2 / (varB + C2);

        return 1.0 - (ssimR + ssimG + ssimB) / 3.0;
    }

    public int getTreeDepth() {
        return countTreeDepth(root);
    }

    private int countTreeDepth(Node node) {
        if (node == null) {
            return 0;
        }

        if (node.isLeaf) {
            return 1;
        }

        int depthTopLeft = countTreeDepth(node.topLeft);
        int depthTopRight = countTreeDepth(node.topRight);
        int depthBottomLeft = countTreeDepth(node.bottomLeft);
        int depthBottomRight = countTreeDepth(node.bottomRight);
        return 1 + (Math.max(Math.max(depthTopLeft, depthTopRight), Math.max(depthBottomLeft, depthBottomRight)));
    }

    public int getNodeCount() {
        return countNodes(root);
    }

    private int countNodes(Node node) {
        if (node == null) {
            return 0;
        }

        int count = 1;
        if(!node.isLeaf) {
            count += countNodes(node.topLeft);
            count += countNodes(node.topRight);
            count += countNodes(node.bottomLeft);
            count += countNodes(node.bottomRight);
        }
        return count;
    }

    public BufferedImage getCompressedImage() {
        BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        drawTree(root, output);
        return output;
    }

    private void drawTree(Node node, BufferedImage output) {
        if (node == null) {
            return;
        }
        if (node.isLeaf) {
            for (int i = node.x; i < node.x + node.width; i++) {
                for (int j = node.y; j < node.y + node.height; j++) {
                    output.setRGB(i, j, node.averageColor.getRGB());
                }
            }
            return;
        }
        drawTree(node.topLeft, output);
        drawTree(node.topRight, output);
        drawTree(node.bottomLeft, output);
        drawTree(node.bottomRight, output);
    }
}
