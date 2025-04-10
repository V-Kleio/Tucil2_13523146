
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import quadtreecompression.InputManager;
import quadtreecompression.Quadtree;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            InputManager inputManager = new InputManager();
            inputManager.getUserImage(scanner);
            inputManager.getUserThreshold(scanner);
            inputManager.getUserMinimumBlockSize(scanner);
            inputManager.getUserErrorMethod(scanner);
            inputManager.getUserImageOutputPath(scanner);

            File inputFile = new File(inputManager.getImageInputPath());
            long originalSize = inputFile.length();

            long startTime = System.currentTimeMillis();
            Quadtree quadtree = new Quadtree(inputManager.getImage(), inputManager.getErrorThreshold(), inputManager.getMinimumBlockSize(), inputManager.getMethod());
            quadtree.build();

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            BufferedImage compressedImage = quadtree.getCompressedImage();
            String outputPath = inputManager.getImageOutputPath();
            String extension = outputPath.substring(outputPath.lastIndexOf('.') + 1).toLowerCase();

            File outputFile = new File(outputPath);
            try {
                ImageIO.write(compressedImage, extension, outputFile);
            } catch (IOException e) {
                System.out.println("Terjadi kesalahan saat menyimpan gambar: " + e.getMessage());
            }

            long compressedSize = outputFile.length();
            int treeDepth = quadtree.getTreeDepth();
            int nodeCount = quadtree.getNodeCount();
            double compressionPercentage = (1 - ((double) compressedSize / originalSize)) * 100;
            double compressionRatio = (double) originalSize / compressedSize;

            System.out.println("\n========== COMPRESSION STATISTICS ==========");
            System.out.println("Waktu eksekusi               : " + executionTime + " ms");
            System.out.println("Ukuran gambar original       : " + formatFileSize(originalSize));
            System.out.println("Ukuran gambar hasil kompresi : " + formatFileSize(compressedSize));
            System.out.printf("Rasio kompresi               : %.2f:1\n", compressionRatio);
            System.out.printf("Persentase kompresi          : %.2f%%\n", compressionPercentage);
            System.out.println("Kedalaman pohon              : " + treeDepth);
            System.out.println("Banyak node                  : " + nodeCount);
        }
    }

    private static String formatFileSize(long size) {
        final String[] units = new String[] {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double dataSize = size;

        while (dataSize >= 1024 && unitIndex < units.length - 1) {
            dataSize /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", dataSize, units[unitIndex]);
    }
}