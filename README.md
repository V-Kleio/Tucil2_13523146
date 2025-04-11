<div align="center">
  <img width="100%" src="https://capsule-render.vercel.app/api?type=blur&height=280&color=0:d8dee9,100:2e3440&text=QuadTree%20Compression%20%E2%9C%A8&fontColor=81a1c1&fontSize=50&animation=twinkling&" />
</div>

<p align="center">
  <img src="https://img.shields.io/badge/Status-Finished-green" />
  <img src="https://img.shields.io/badge/Recent_Build-Release-brightgreen" />
  <img src="https://img.shields.io/badge/Version-1.0.0-brightgreen" />
  <img src="https://img.shields.io/badge/License-MIT-yellowgreen" />
  <img src="https://img.shields.io/badge/Built_With-Java-blue" />
</p>

---

## 📦 Table of Contents

- [✨ Overview](#-overview)
- [⚙️ Features](#️-features)
- [📸 Preview](#-preview)
- [📥 Installation](#-installation)
- [🚀 Usage](#-usage)
- [📂 Project Structure](#-project-structure)
- [👤 Author](#-author)

---

## ✨ Overview
A Java implementation of image compression using the quadtree method with multiple error metrics. This program compresses images by adaptively subdividing them into quadrants based on region homogeneity, providing a balance between file size reduction and image quality.

---

## ⚙️ Features

- Compression using quadtree algorithm
- Five different error calculation method (variance, MAD, MPD, entropy, and SSIM)
- Choose your desired target compression percentage
- Display statistics
- Works with PNG, JPEG, and other common image formats

---

## 📸 Preview

Example image before and after compression

### Cat
![](https://github.com/V-Kleio/Tucil2_13523146/blob/main/test/input/cat.png)

![](https://github.com/V-Kleio/Tucil2_13523146/blob/main/test/output/cat_compressed.png)

---

## 📥 Installation

### 🔧 Prerequisites

- Java Development Kit (JDK) 11 or higher
- Unix-like environment (for build.sh) or Windows (for build.bat)

### 📦 Install

#### 📦 Manual Installation

1. Clone this repository
```bash
git clone https://github.com/V-Kleio/Tucil2_13523146.git
cd Tucil2_13523146
```
2. Make the build script executable (Unix-like systems)
```bash
chmod +x build.sh
```
3. Build the program using script

Unix-like system
```bash
./build.sh
```
Windows
```powershell
build.bat
```

#### 📦 Using JAR File

1. Download the jar file in the release section
2. Navigate to the directory of your downloaded jar file
3. Run the following command
```bash
java -jar QuadtreeCompression.jar
```

> [!NOTE]\
> You can also manually build the jar file using the buildjar.sh script in Unix-like system

---

## 🚀 Usage

Run the program using build script or jar file.

The program will prompt you for the following inputs:
1. Input Image Path: Absolute path to the image file
2. Error Calculation Method: Variance, Mean Absolute Deviation (MAD), Maximum Pixel Difference, Entropy, Structural Similarity (SSIM)
4. Target Compression Percentage (optional): If specified, the program will automatically find parameters to achieve this target
5. Maximum Search Attempts (if target compression is specified): How many iterations to use for finding optimal parameters
6. Error Threshold (if target compression is not specified): The threshold below which a region is considered uniform
7. Minimum Block Size: The smallest allowable block dimension
8. Output Image Path: Where to save the compressed image 

Example:
```bash
Masukkan alamat absolut gambar.
> /home/user/images/sample.png
Pilih metode perhitungan error.
1. Metode variance
2. Metode mean absolute deviation (MAD)
3. Metode max pixel difference
4. Metode entropy
5. Metode SSIM
(1 - 5)> 4
Masukkan persentase kompresi minimum yang diinginkan (0.0 - 1.0).
0.0 = fitur ini dinonaktifkan, masukkan parameter secara manual
> 0.4
Masukkan jumlah percobaan maksimum untuk pencarian persentase kompresi.
Default: 10
> 50
Masukkan luas blok minimum.
> 1
Masukkan alamat absolut untuk gambar hasil kompresi.
> /home/user/images/compressed_sample.png

[Compression statistics will be displayed here]
```

### Error Metrics

The program offers five different error metrics:

Variance: Measures the average squared deviation of pixels from their mean.

Range: 0 to 16,256.25.

Mean Absolute Deviation (MAD): Calculates the average absolute difference between pixels and their mean.

Range: 0 to 127.5.

Maximum Pixel Difference: Finds the maximum difference between any two pixels in the block.

Range: 0 to 255.

Entropy: Measures the randomness or information content of the block.

Range: 0 to 8.

Structural Similarity (SSIM): Compares the structural information before and after compression.

Range: 0 to 1.

### Parameter Optimization
If you specify a target compression percentage, the program will try iteratively to find the best threshold. This works by:

- Starting with a low threshold
- Incrementally adjusting the threshold based on the resulting compression
- Tracking the best parameters that meet or exceed the target
- Reporting the optimal threshold after the specified number of attempts

### Performance Considerations
- Image Size: Processing time increases with image dimensions
- Error Threshold: Lower thresholds result in more subdivisions, increasing processing time
- Minimum Block Size: Smaller values allow more detailed compression but increase processing time

---

## 🏠 Project Structure
```bash
Tucil2_13523146/
├── bin/                                 # Compiled classes
├── doc/                                 # File for documentation
├── src/
│   ├── Main.java                        # Main program entry point
│   ├── quadtreecompression/
│       ├── Quadtree.java                # Core quadtree implementation
│       ├── InputManager.java            # User input handling
│       ├── ErrorCalculationMethod.java  # Error method enumeration
├── test/                                # File for documentation
│   ├── input/                           # Image example for input
│   ├── output/                          # Output image after compression
├── build.sh                             # Unix build script
├── build.bat                            # Windows build script
├── buildjar.sh                          # Unix jar build script
└── README.md                            # This file
```

---

## 👤 Author

<p align="center"> <a href="https://github.com/V-Kleio"> <img src="https://avatars.githubusercontent.com/u/101655336?v=4" width="100px;" style="border-radius: 50%;" alt="V-Kleio"/> <br /> <sub><b>Rafael Marchel Darma Wijaya</b></sub> </a> </p>
<div align="center" style="color:#6A994E;"> 🌿 Crafted with care | 2025 🌿</div>
