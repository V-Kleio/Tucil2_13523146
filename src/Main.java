
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
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

            File outputFile = new File(outputPath);
            try {
                javax.imageio.ImageIO.write(compressedImage, "png", outputFile);
            } catch (IOException e) {
                System.out.println("Terjadi kesalahan saat menyimpan gambar: " + e.getMessage());
            }

            long compressedSize = outputFile.length();
            int treeDepth = quadtree.getTreeDepth();
            int nodeCount = quadtree.getNodeCount();
            double compressionPercentage = (1 - ((double) compressedSize / originalSize)) * 100;

            System.out.println("Execution time: " + executionTime);
            System.out.println("Ukuran gambar sebelum: " + originalSize);
            System.out.println("Ukuran gambar setelah: " + compressedSize);
            System.out.printf("Persentase kompresi: %.2f%%\n", compressionPercentage);
            System.out.println("Kedalaman pohon: " + treeDepth);
            System.out.println("Banyak simpul pohon: " + nodeCount);
        }
    }
}