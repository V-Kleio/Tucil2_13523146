package quadtreecompression;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class InputManager {
    private BufferedImage image = null;
    private ErrorCalculationMethod method;
    private float errorThreshold;
    private int minimumBlockSize;
    private String imageOutputPath;
    private String imageInputPath;
    private double minCompressionPercentage;
    private int maxSearchAttempts = 10;

    public BufferedImage getImage() {
        return image;
    }

    public float getErrorThreshold() {
        return errorThreshold;
    }

    public int getMinimumBlockSize() {
        return minimumBlockSize;
    }

    public ErrorCalculationMethod getMethod() {
        return method;
    }

    public String getImageOutputPath() {
        return imageOutputPath;
    }

    public String getImageInputPath() {
        return imageInputPath;
    }

    public double getMinCompressionPercentage() {
        return minCompressionPercentage;
    }

    public int getMaxSearchAttempts() {
        return maxSearchAttempts;
    }

    public void setErrorThreshold(float threshold) {
        this.errorThreshold = threshold;
    }

    public void setMinimumBlockSize(int size) {
        this.minimumBlockSize = size;
    }

    public void getUserImageOutputPath(Scanner userInput) {
        System.out.println("Masukkan alamat absolut untuk gambar hasil kompresi.");
        boolean valid = false;
        while (!valid) {
            System.out.print("> ");
            String input = userInput.nextLine();
            File outputFile = new File(input);
            if (!outputFile.isAbsolute()) {
                System.out.println("Masukkan alamat path absolut!");
                continue;
            }
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && parentDir.exists() && parentDir.canWrite()) {
                imageOutputPath = input;
                valid = true;
            } else {
                System.out.println("Direktori tidak ada atau tidak memiliki akses. Silakan masukkan alamat yang valid!");
            }
        }
    }

    public void getUserMinimumBlockSize(Scanner userInput) {
        System.out.println("Masukkan luas blok minimum.");
        System.out.print("> ");
        boolean valid = false;
        while (!valid) {
            String input = userInput.nextLine();
            try {
                minimumBlockSize = Integer.parseInt(input);
                if (minimumBlockSize > 0) {
                    valid = true;
                } else {
                    System.out.println("Luas blok tidak boleh kurang dari 1.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid, silakan masukkan integer!");
                System.out.print("> ");
            }
        }
    }

    public void getUserErrorMethod(Scanner userInput) {
        System.out.println("Pilih metode perhitungan error.");
        System.out.println("1. Metode variance");
        System.out.println("2. Metode mean absolute deviation (MAD)");
        System.out.println("3. Metode max pixel difference");
        System.out.println("4. Metode entropy");
        System.out.println("5. Metode SSIM");
        
        int input = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print("(1 - 5)> ");
            String line = userInput.nextLine();
            try {
                input = Integer.parseInt(line);
                if (input >= 1 && input <= 5) {
                    valid = true;
                } else {
                    System.out.println("Input harus berupa angka 1 sampai 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid, silakan masukkan integer.");
            }
        }

        switch (input) {
            case 1 -> method = ErrorCalculationMethod.VARIANCE;
            case 2 -> method = ErrorCalculationMethod.MAD;
            case 3 -> method = ErrorCalculationMethod.MAX_PIXEL_DIFFERENCE;
            case 4 -> method = ErrorCalculationMethod.ENTROPY;
            case 5 -> method = ErrorCalculationMethod.SSIM;
        }
    }

    public void getUserThreshold(Scanner userInput) {
        float minThreshold = 0.0f, maxThreshold = 0.0f;

        switch (method) {
            case ErrorCalculationMethod.VARIANCE -> {
                minThreshold = 0.0f;
                maxThreshold = 16256.25f;
            }
            case ErrorCalculationMethod.MAD -> {
                minThreshold = 0.0f;
                maxThreshold = 127.5f;
            }
            case ErrorCalculationMethod.MAX_PIXEL_DIFFERENCE -> {
                minThreshold = 0.0f;
                maxThreshold = 255;
            }
            case ErrorCalculationMethod.ENTROPY -> {
                minThreshold = 0.0f;
                maxThreshold = 8.0f;
            }
            case ErrorCalculationMethod.SSIM -> {
                minThreshold = 0.0f;
                maxThreshold = 1.0f;
            }
        }

        System.out.println("Masukkan ambang batas kompresi.");
        System.out.printf("Rentang untuk ambang batas kompresi: %.2f - %.2f\n", minThreshold, maxThreshold);
        System.out.print("> ");

        boolean valid = false;
        while (!valid) {
            String input = userInput.nextLine();
            try {
                float value = Float.parseFloat(input);

                if (value < minThreshold || value > maxThreshold) {
                    System.out.printf("Nilai %.2f berada di luar rentang (%.2f - %.2f).\n", value, minThreshold, maxThreshold);
                    System.out.print("> ");
                    continue;
                }

                errorThreshold = value;
                valid = true;

            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid, silakan masukkan angka desimal!");
                System.out.print("> ");
            }
        }
    }

    public void getUserMinimumCompressionPercentage(Scanner userInput) {
        System.out.println("Masukkan persentase kompresi minimum yang diinginkan (0.0 - 1.0).");
        System.out.println("0.0 = fitur ini dinonaktifkan, masukkan parameter secara manual");
        System.out.print("> ");

        boolean valid = false;
        while (!valid) {
            String input = userInput.nextLine();
            try {
                double value = Double.parseDouble(input);
                if (value < 0.0 || value > 1.0) {
                    System.out.println("Nilai harus antara 0.0 dan 1.0");
                    System.out.print("> ");
                    continue;
                }

                minCompressionPercentage = value;
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid, masukkan angka desimal.");
                System.out.print("> ");
            }
        }
    }

    public void getUserMaxSearchAttempts(Scanner userInput) {
        System.out.println("Masukkan jumlah percobaan maksimum untuk pencarian persentase kompresi.");
        System.out.println("Default: 10");
        System.out.print("> ");

        boolean valid = false;
        while (!valid) {
            String input = userInput.nextLine();

            if (input.trim().isEmpty()) {
                System.out.println("Menggunakan jumlah percobaan default: 10");
                maxSearchAttempts = 10;
                valid = true;
                continue;
            }

            try {
                int value = Integer.parseInt(input);
                if (value < 1) {
                    System.out.println("Jumlah percobaan minimal 1.");
                    System.out.print("> ");
                } else {
                    maxSearchAttempts = value;
                    valid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid, masukkan angka bulat.");
                System.out.print("> ");
            }
        }
    }

    public void getUserImage(Scanner userInput) {
        System.out.println("Masukkan alamat absolut gambar.");
        boolean valid = false;
        while (!valid) {
            System.out.print("> ");
            String imagePath = userInput.nextLine();

            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.out.println("File tidak ditemukan, silakan masukkan alamat yang valid!");
                continue;
            }
            if (!imageFile.canRead()) {
                System.out.println("File tidak dapat dibaca, silakan masukkan alamat dengan akses read!");
                continue;
            }
            try {
                image = ImageIO.read(imageFile);
                if (image == null) {
                    System.out.println("Format gambar tidak didukung atau file rusak");
                } else {
                    imageInputPath = imagePath;
                    valid = true;
                }
            } catch (IOException e) {
                System.out.println("Terjadi kesalahan saat membaca gambar: " + e.getMessage());
            }
        }
    }
}

