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

    public void getUserImageOutputPath(Scanner userInput) {
        System.out.println("Masukkan alamat absolut untuk gambar hasil kompresi!");
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
        System.out.println("Masukkan luas blok minimum!");
        System.out.println("> ");
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
        System.out.println("Pilih metode perhitungan error!");
        System.out.println("1. Metode variance");
        System.out.println("2. Metode mean absolute deviation (MAD)");
        System.out.println("3. Metode max pixel difference");
        System.out.println("4. Metode entropy");
        
        int input = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print("(1 - 4)> ");
            String line = userInput.nextLine();
            try {
                input = Integer.parseInt(line);
                if (input >= 1 && input <= 4) {
                    valid = true;
                } else {
                    System.out.println("Input harus berupa angka 1 sampai 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid, silakan masukkan angka integer.");
            }
        }

        switch (input) {
            case 1 -> method = ErrorCalculationMethod.VARIANCE;
            case 2 -> method = ErrorCalculationMethod.MAD;
            case 3 -> method = ErrorCalculationMethod.MAX_PIXEL_DIFFERENCE;
            case 4 -> method = ErrorCalculationMethod.ENTROPY;
        }
    }

    public void getUserThreshold(Scanner userInput) {
        System.out.println("Masukkan ambang batas kompresi!");
        System.out.println("> ");
        boolean valid = false;
        while (!valid) {
            String input = userInput.nextLine();
            try {
                errorThreshold = Float.parseFloat(input);
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid, silakan masukkan angka desimal!");
                System.out.print("> ");
            }
        }
    }

    public void getUserImage(Scanner userInput) {
        System.out.println("Masukkan alamat absolut gambar: ");
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

