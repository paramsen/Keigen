# Keigen

[![Release](https://jitpack.io/v/paramsen/keigen.svg)](https://jitpack.io/#paramsen/keigen)

_Keigen is a Kotlin (Android) library for fast matrix operations and linear algebra built on a C++ foundation._  

![Keygen artwork](https://raw.githubusercontent.com/paramsen/Keigen/master/artwork.png)

Keigen is a Kotlin wrapper for [Eigen][eigen_site], a linear algebra library written in C++.  

Most common matrix-to-matrix and matrix-to-scalar operations are implemented. All arithmetic 
operators are covered `+, -, *`, get by row/col `val x = matrix[row, col]`, set by row/col 
`matrix[row, col] = 2`, transpose, raw data array initialize/get/set `FloatMatrix(rows, cols, aFloatArray)`.

## Get started

Add jitpack.io repo to your root `build.gradle`:
    
    allprojects {
        repositories {
            //...
            maven { url "https://jitpack.io" }
        }
    }

Include in Android project:

    implementation 'com.github.paramsen:keigen:1.0'

## Simple and expressive with Kotlin operator overloads

**DoubleMatrix, FloatMatrix, LongMatrix, IntMatrix, ShortMatrix, ByteMatrix.** All Matrix types 
support the same functionality and has the same unit test coverage.  

`val matrixD = matrixA * 2 * matrixB + matrixC`  

**Matrix to matrix operations**  

`matrixC = matrixA + matrixB`  
`matrixC = matrixA - matrixB`  
`matrixC = matrixA * matrixB`  

`matrixA += matrixB`  
`matrixA -= matrixB`  
`matrixA *= matrixB`  

**Matrix to scalar operations**  

`matrixC = matrixA + 2f`  
`matrixC = matrixA - 2f`  
`matrixC = matrixA * 2f`  
`matrixC = matrixA / 2f`  

`matrixA += 2f`  
`matrixA -= 2f`  
`matrixA *= 2f`  
`matrixA /= 2f`  

**Matrix get by index and array**  

`val x = matrix[1, 3]`  
`val floatArray = matrix.getData()`  

**Matrix set by index and array**  

`matrix[1, 3] = 5L`  
`matrix.setData(aLongArray)`  

**Other matrix operations**  

`val matrixB = matrixA.transpose()`  

### Matrix class code generation  

All Kotlin Matrix classes are generated from the FloatMatrix base implementation. The unit tests are 
also generated from the FloatMatrixTest class, which means that each Matrix type has full test coverage.  

The C++ interface for Eigen is implemented using generics (or templates in C++ lingo). It should be 
noted that the Kotlin interface isn't implemented using generics because the exact types need to be
known at the JNI bridge (and due to type erasure in Java/Kotlin, the exact type of a generic 
variable cannot be inferred).

### Licensing

Keigen is licensed under the permissive [APL-2.0][keigen_license].  

The included parts of Eigen is licensed under the permissive [MPL-2.0][eigen_license], the `EIGEN_MPL2_ONLY`
flag is used in the project as described in the link.

### Artwork

The artwork combines the scholar owl in the Eigen artwork and the Kotlin logo colors. In Keigen,
the owl has obviously found a new cool style, heavily inspired by The Matrix - not to be confused
with _a_ matrix.  

_The artwork is made by me._

[eigen_site]: http://eigen.tuxfamily.org/dox/GettingStarted.html
[eigen_license]: https://gitlab.com/libeigen/eigen/blob/master/COPYING.README
[keigen_license]: https://github.com/paramsen/Keigen/blob/master/LICENSE
