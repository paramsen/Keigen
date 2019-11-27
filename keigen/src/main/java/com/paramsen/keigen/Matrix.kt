package com.paramsen.keigen

import androidx.annotation.IntRange

/**
 * @author Pär Amsen 11/2019
 */
class Matrix(@IntRange(from = 1) var rows: Int, @IntRange(from = 1) var cols: Int, var nativePointer: Long) {
    constructor(@IntRange(from = 1) rows: Int, @IntRange(from = 1) cols: Int, fill: Float = 0f): this(rows, cols, KeigenNativeBridge.initializeFill(rows, cols, fill))

    constructor(@IntRange(from = 1) rows: Int, @IntRange(from = 1) cols: Int, data: FloatArray, @IntRange(from = 1) outerStride: Int = 1, @IntRange(from = 1) innerStride: Int = cols) : this(rows, cols, NULL_PTR) {
        throwIfMatrixSizeExceedDataSize(data.size)
        throwIfStrideOutsideBounds(data.size, outerStride, innerStride)
        nativePointer = KeigenNativeBridge.initializeWithData(rows, cols, data, outerStride, innerStride)
    }

    operator fun plus(m: Matrix): Matrix {
        throwIfNullPointer()
        throwIfDimensNotEqual(m)
        return Matrix(rows, cols, KeigenNativeBridge.matrixPlus(nativePointer, m.nativePointer))
    }

    operator fun plusAssign(m: Matrix) {
        throwIfNullPointer()
        throwIfDimensNotEqual(m)
        KeigenNativeBridge.matrixPlusAssign(nativePointer, m.nativePointer)
    }

    operator fun minus(m: Matrix): Matrix {
        throwIfNullPointer()
        throwIfDimensNotEqual(m)
        return Matrix(rows, cols, KeigenNativeBridge.matrixMinus(nativePointer, m.nativePointer))
    }

    operator fun minusAssign(m: Matrix) {
        throwIfNullPointer()
        throwIfDimensNotEqual(m)
        KeigenNativeBridge.matrixMinusAssign(nativePointer, m.nativePointer)
    }

    operator fun times(m: Matrix): Matrix {
        throwIfNullPointer()
        throwIfInvalidMultiplicationDimens(m)
        return Matrix(rows, m.cols, KeigenNativeBridge.matrixTimes(nativePointer, m.nativePointer))
    }

    /**
     * When rows==cols this operation is performed in place. Else a new native Matrix is allocated
     * to fit the result, and the old one freed. For use in a loop or similar, it might be better
     * to use Matrix.multiplyIntoDst.
     */
    operator fun timesAssign(m: Matrix) {
        throwIfNullPointer()
        throwIfInvalidMultiplicationDimens(m)
        if(rows == m.rows && cols == m.cols) {
            KeigenNativeBridge.matrixTimesAssignRequireSquare(nativePointer, m.nativePointer)
        } else {
            val oldNativePointer = nativePointer
            nativePointer = KeigenNativeBridge.matrixTimes(nativePointer, m.nativePointer)
            KeigenNativeBridge.dispose(oldNativePointer)
            cols = m.cols
        }
    }

    /**
     * The result of (this * multiplyBy) is stored in (dst). No new allocations will be made to fit
     * the result into the dst Matrix.
     */
    fun multiplyIntoDst(multiplyBy: Matrix, dst: Matrix) {
        check(dst.rows == rows && dst.cols == multiplyBy.cols) { "result must fit into dst (dst size (${dst.rows}x${dst.cols}) does not match (${rows}x${multiplyBy.cols}))" }
        KeigenNativeBridge.matrixTimesIntoDst(nativePointer, multiplyBy.nativePointer, dst.nativePointer)
    }

    operator fun times(scalar: Float): Matrix {
        return Matrix(rows, cols, KeigenNativeBridge.matrixTimesScalar(nativePointer, scalar))
    }

    operator fun timesAssign(scalar: Float) {
        KeigenNativeBridge.matrixTimesAssignScalar(nativePointer, scalar)
    }

    operator fun div(scalar: Float): Matrix {
        return Matrix(rows, cols, KeigenNativeBridge.matrixDivScalar(nativePointer, scalar))
    }

    operator fun divAssign(scalar: Float) {
        KeigenNativeBridge.matrixDivAssignScalar(nativePointer, scalar)
    }

    fun transpose() = Matrix(cols, rows, KeigenNativeBridge.matrixTranspose(nativePointer))

    /**
     * This actually creates a new native Matrix due to restrictions in Eigen (which are
     * probably solvable in the C++ layer to actually re-use the same data allocation). The old
     * native Matrix is disposed, and the new assigned to this Kotlin Matrix.
     */
    fun transposeInPlace() {
        val newNativePointer = KeigenNativeBridge.matrixTranspose(nativePointer)
        KeigenNativeBridge.dispose(nativePointer)
        nativePointer = newNativePointer
        val oldRows = rows
        val oldCols = cols
        rows = oldCols
        cols = oldRows
    }

    operator fun get(row: Int, col: Int): Float {
        throwIfNullPointer()
        throwIfOutsideBounds(row, col)
        return KeigenNativeBridge.get(nativePointer, row, col)
    }

    operator fun set(row: Int, col: Int, value: Float) {
        throwIfNullPointer()
        throwIfOutsideBounds(row, col)
        return KeigenNativeBridge.set(nativePointer, row, col, value)
    }

    fun getArray(dst: FloatArray = FloatArray(rows * cols)): FloatArray {
        throwIfNullPointer()
        throwIfMatrixOutsideBounds(dst)
        KeigenNativeBridge.getArray(nativePointer, dst)
        return dst
    }

    fun setArray(src: FloatArray) {
        throwIfNullPointer()
        throwIfMatrixOutsideBounds(src)
        KeigenNativeBridge.setArray(nativePointer, src)
    }

    //TODO swap underlying data
    //TODO other operations (v2+)
    //TODO equals and hash

    fun dispose() {
        if(nativePointer == NULL_PTR) return
        KeigenNativeBridge.dispose(nativePointer)
        nativePointer = NULL_PTR
    }

    private fun throwIfNullPointer() =
        check(nativePointer != NULL_PTR) { "native pointer is null" }

    private fun throwIfDimensNotEqual(m: Matrix) =
        check(rows == m.rows && cols == m.cols) { "dimensions must equal (this: [$rows, $cols], other: [${m.rows}, ${m.cols}])" }

    private fun throwIfOutsideBounds(row: Int, col: Int) =
        check(row < rows && col < cols) { "index overflow (access [$row, $col], bounds [$rows, $cols])" }

    private fun throwIfInvalidMultiplicationDimens(m: Matrix) =
        check(cols == m.rows) { "cols must equal rows ($cols, ${m.rows})" }

    private fun throwIfMatrixSizeExceedDataSize(dataSize: Int) =
        check(dataSize >= rows * cols) { "matrix must fit inside data (data size $dataSize, matrix size: ${rows * cols})" }

    private fun throwIfStrideOutsideBounds(dataSize: Int, outerStride: Int, innerStride: Int) =
        check(dataSize >= rows * outerStride + innerStride) { "strides must not overflow array (array length $dataSize, last stride index: ${rows * outerStride + innerStride})" }

    private fun throwIfMatrixOutsideBounds(array: FloatArray) =
        check(array.size <= rows * cols) { "matrix must fit in array bounds (array length: ${array.size}, matrix length: ${rows * cols})" }

    companion object {
        const val NULL_PTR = 0L
    }
}