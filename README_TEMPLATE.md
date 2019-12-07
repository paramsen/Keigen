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

    implementation 'com.github.paramsen:keigen:{{version}}'

## Simple and expressive with Kotlin operator overloads

**DoubleMatrix, FloatMatrix, LongMatrix, IntMatrix, ShortMatrix, ByteMatrix.** All Matrix types 
support the same functionality and has the same unit test coverage.  

**Matrix instantiation**  

2 by 3 matrix filled with 0s `val a = LongMatrix(2, 3)`  
2 by 3 matrix filled with 1s `val a = FloatMatrix(2, 3, 1f)`  

256 by 512 matrix from array data:  
  
    val data = ShortArray(256 * 512) { it.toShort() }
    val a = ShortMatrix(256, 512, data)
    
**IMPORTANT (!) Matrix destruction**  

All Matrix types has to be destructed when not in use anymore by calling `matrix.dispose()`. This is
a side effect of building this library on top of the C++ library Eigen, all Matrix types has references
to native allocations that need to be freed to not result in memory leaks.

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

## Development

#### Setup

Eigen is not directly included within this git repository, it's in a git module and is pulled 
from Eigens repository on demand.

To setup Eigen:

1. Run `git submodule init; git submodule update` in project root
2. Check that Eigen exists in `keigen/src/main/native/Eigen`

#### Release

There's a Gradle task that generates the README.md from template and git tags the current commit
with the version number. JitPack builds on push of the tag.

Release steps are:

1. Bump version in `noise/build.gradle`
2. Run `./gradlew release` in project root (generates readme)
3. Push generated readme changes to repo
4. Wait for JitPack to build

## Licenses

Keigen is licensed under the permissive [APL-2.0][keigen_license].  

The included parts of Eigen (included as-is) are licensed under the permissive 
[MPL-2.0][eigen_license], the `EIGEN_MPL2_ONLY` flag is used in the project as described in the link.

### Artwork

The artwork combines the scholar owl in the Eigen artwork and the Kotlin logo colors. In Keigen,
the owl has obviously found a new cool style, heavily inspired by The Matrix - not to be confused
with _a_ matrix.  

_The artwork is made by me._

[eigen_site]: http://eigen.tuxfamily.org/dox/GettingStarted.html
[eigen_license]: https://gitlab.com/libeigen/eigen/blob/master/COPYING.README
[keigen_license]: https://github.com/paramsen/Keigen/blob/master/LICENSE