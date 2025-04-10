
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import quadtreecompression.ErrorCalculationMethod;
import quadtreecompression.InputManager;
import quadtreecompression.Quadtree;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            InputManager inputManager = new InputManager();
            inputManager.getUserImage(scanner);
            inputManager.getUserErrorMethod(scanner);

            inputManager.getUserMinimumCompressionPercentage(scanner);

            if (inputManager.getMinCompressionPercentage() > 0.0) {
                inputManager.getUserMaxSearchAttempts(scanner);
            }

            if (inputManager.getMinCompressionPercentage() <= 0.0) {
                inputManager.getUserThreshold(scanner);
                inputManager.getUserMinimumBlockSize(scanner);
            }

            inputManager.getUserImageOutputPath(scanner);

            File inputFile = new File(inputManager.getImageInputPath());
            long originalSize = inputFile.length();

            if (inputManager.getMinCompressionPercentage() > 0.0) {
                findOptimalParameters(inputManager, originalSize);
            }

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

            if (inputManager.getMinCompressionPercentage() > 0.0) {
                System.out.println("\nTarget kompresi             : " + 
                                 (inputManager.getMinCompressionPercentage() * 100) + "%");
                System.out.println("Parameter yang digunakan.");
                System.out.println("- Error threshold           : " + inputManager.getErrorThreshold());
                System.out.println("- Minimum block size        : " + inputManager.getMinimumBlockSize());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
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

    private static double getMinThresholdForMethod(ErrorCalculationMethod method) {
        switch (method) {
            case ErrorCalculationMethod.VARIANCE -> {
                return 1.0;
            }
            case ErrorCalculationMethod.MAD -> {
                return 0.5;
            }
            case ErrorCalculationMethod.MAX_PIXEL_DIFFERENCE -> {
                return 1.0;
            }
            case ErrorCalculationMethod.ENTROPY -> {
                return 0.05;
            }
            case ErrorCalculationMethod.SSIM -> {
                return 0.1;
            }
            default -> {
                return 0.1;
            }
        }
    }

    private static double getMaxThresholdForMethod(ErrorCalculationMethod method) {
        switch (method) {
            case ErrorCalculationMethod.VARIANCE -> {
                return 16256.25;
            }
            case ErrorCalculationMethod.MAD -> {
                return 127.25;
            }
            case ErrorCalculationMethod.MAX_PIXEL_DIFFERENCE -> {
                return 255.0;
            }
            case ErrorCalculationMethod.ENTROPY -> {
                return 8.0;
            }
            case ErrorCalculationMethod.SSIM -> {
                return 1.0;
            }
            default -> {
                return 100.0;
            }
        }
    }

    private static void findOptimalParameters(InputManager inputManager, long originalSize) throws IOException {
        System.out.println("\nMencari parameter optimal...");

        double targetCompressionPercentage = inputManager.getMinCompressionPercentage();
        ErrorCalculationMethod method = inputManager.getMethod();

        double minThreshold = getMinThresholdForMethod(method);
        double maxThreshold = getMaxThresholdForMethod(method);
        double range = maxThreshold - minThreshold;

        double initialPercentageRange = 0.02;
        double currentThreshold = minThreshold + (maxThreshold - minThreshold) * initialPercentageRange;
        double currentCompressionPercentage = 0;
        double previousThreshold = currentThreshold;

        int currentBlockSize = 2;
        int minBlockSize = 1;
        int maxBlockSize = 32;

        int maxAttempts = inputManager.getMaxSearchAttempts();
        boolean targetReached = false;

        double bestCompressionPercentage = 0.0;
        double bestThreshold = currentThreshold;
        int bestBlockSize = currentBlockSize;

        double bestTargetCompressionPercentage = Double.MAX_VALUE;
        double bestTargetThreshold = 0;
        int bestTargetBlockSize = 0;

        System.out.println("Mencoba beberapa parameter kompresi...");

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            if (attempt > 0) {
                double increment;

                if (range < 10) {
                    increment = 1.3;
                } else if (range < 1000) {
                    increment = 1.6;
                } else {
                    increment = 2.0;
                }

                boolean isAboveTarget = currentCompressionPercentage >= targetCompressionPercentage;

                if (!isAboveTarget) {
                    double diff = targetCompressionPercentage - currentCompressionPercentage;
                    if (diff > 0.3) {
                        increment *= 2;
                    } else if (diff < 0.1) {
                        increment = 1.1;
                    }

                    if (currentThreshold > maxThreshold * 0.7 && attempt % 2 == 0) {
                        currentBlockSize = Math.min(currentBlockSize * 2, maxBlockSize);
                    }
                    else if (attempt % 3 == 0) {
                        currentBlockSize = Math.min(currentBlockSize * 2, maxBlockSize);
                    }
                } else {
                    double diff = currentCompressionPercentage - targetCompressionPercentage;
                    if (diff > 0.3) {
                        increment = 0.5;
                    } else if (diff > 0.1) {
                        increment = 0.7;
                    } else {
                        increment = 0.9;
                    }
                    if (currentBlockSize > minBlockSize && attempt % 2 == 1) {
                        currentBlockSize = currentBlockSize / 2;
                    }
                }

                previousThreshold = currentThreshold;
                currentThreshold = Math.min(currentThreshold * increment, maxThreshold);

            }

            System.out.printf("\nPercobaan %d / %d: Threshold = %.2f (%.1f%% dari maksimum), Block Size = %d\n", attempt + 1, maxAttempts, currentThreshold, (currentThreshold / maxThreshold) * 100, currentBlockSize);

            Quadtree test = new Quadtree(inputManager.getImage(), currentThreshold, currentBlockSize, method);
            test.build();

            BufferedImage compressedImage = test.getCompressedImage();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String extension = inputManager.getImageOutputPath().substring(inputManager.getImageOutputPath().lastIndexOf('.') + 1).toLowerCase();

            ImageIO.write(compressedImage, extension, outputStream);
            long estimatedSize = outputStream.size();

            currentCompressionPercentage = 1.0 - ((double)estimatedSize / originalSize);
            System.out.printf("Hasil: Kompresi %.2f%% (Target: %.2f%%)\n", currentCompressionPercentage * 100, targetCompressionPercentage * 100);

            if (currentCompressionPercentage > bestCompressionPercentage) {
                bestCompressionPercentage = currentCompressionPercentage;
                bestThreshold = currentThreshold;
                bestBlockSize = currentBlockSize; 
            }

            if (currentCompressionPercentage >= targetCompressionPercentage) {
                targetReached = true;
                
                if (currentCompressionPercentage < bestTargetCompressionPercentage) {
                    bestTargetCompressionPercentage = currentCompressionPercentage;
                    bestTargetThreshold = currentThreshold;
                    bestTargetBlockSize = currentBlockSize;
                }
            }

            if (attempt > 0 && currentCompressionPercentage < bestCompressionPercentage * 0.08) {
                currentThreshold = (previousThreshold + currentThreshold) / 2;
                if (currentBlockSize > minBlockSize) {
                    currentBlockSize = currentBlockSize / 2;
                }
            }
        }

        if (targetReached) {
            System.out.println("Berhasil menemukan parameter yang memenuhi target kompresi.");
            inputManager.setErrorThreshold((float) bestTargetThreshold);
            inputManager.setMinimumBlockSize(bestTargetBlockSize);
        } else {
            System.out.printf("\nTidak dapat mencapai target kompresi %.2f%% dalam %d percobaan.\n", (targetCompressionPercentage * 100), maxAttempts);
            System.out.printf("Kompresi yang dapat dicapai: %.2f%%\n", (bestCompressionPercentage * 100));
            inputManager.setErrorThreshold((float) bestThreshold);
            inputManager.setMinimumBlockSize(bestBlockSize);
        }

        System.out.printf("Menggunakan parameter: Threshold = %.2f, Block Size = %d\n", inputManager.getErrorThreshold(), inputManager.getMinimumBlockSize());
    }
}