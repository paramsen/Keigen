package com.paramsen.keigen

/**
 * @author Pär Amsen 11/2019
 */
class Matrix(var rows: Int, var cols: Int, var nativePointer: Long) {
    constructor(rows: Int, cols: Int, fill: Float = 0f): this(rows, cols, KeigenNativeBridge.initializeFill(rows, cols, fill))
    constructor(rows: Int, cols: Int, data: FloatArray, outerStride: Int = 1, innerStride: Int = cols) : this(rows, cols, NULL_PTR) {
        //TODO validate that supplied data has enough elements etc
        nativePointer = KeigenNativeBridge.initializeWithData(rows, cols, data, outerStride, innerStride)
    }

    //matrix operations:
    // +,
    operator fun plus(m: Matrix): Matrix {
        throwIfNullPointer()
        throwIfDimensNotEqual(m)
        return Matrix(rows, cols, KeigenNativeBridge.matrixPlus(nativePointer, m.nativePointer))
    }

    // +=,
    operator fun plusAssign(m: Matrix) {
        throwIfNullPointer()
        throwIfDimensNotEqual(m)
        KeigenNativeBridge.matrixPlusAssign(nativePointer, m.nativePointer)
    }
    // -,
    // -=,
    // *,
    operator fun times(m: Matrix): Matrix {
        throwIfNullPointer()
        throwIfInvalidMultiplicationDimens(m)
        return Matrix(rows, m.cols, KeigenNativeBridge.matrixTimes(nativePointer, m.nativePointer))
    }
    // *=,
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
    // /,
    // /=

    //scalar operations:
    // +,
    // +=,
    // -,
    // -=,
    // *,
    // *=,
    // /,
    // /=

    //get index
    operator fun get(row: Int, col: Int): Float {
        throwIfOutsideBounds(row, col)
        return KeigenNativeBridge.get(nativePointer, row, col)
    }
    //get raw data
    //set index
    operator fun set(row: Int, col: Int, value: Float) {
        throwIfOutsideBounds(row, col)
        return KeigenNativeBridge.set(nativePointer, row, col, value)
    }

    //swap underlying data
    //other operations (v2+)
    //equals and hash

    fun dispose() {
        if(nativePointer == NULL_PTR) return
        KeigenNativeBridge.dispose(nativePointer)
        nativePointer = NULL_PTR
    }

    private fun throwIfNullPointer() = check(nativePointer != NULL_PTR) { "nativePointer is null" }
    private fun throwIfDimensNotEqual(m: Matrix) = check(rows == m.rows && cols == m.cols) { "dimensions must equal (this: [$rows, $cols], other: [${m.rows}, ${m.cols}])" }
    private fun throwIfOutsideBounds(row: Int, col: Int) = check(row < rows && col < cols) { "index overflow (access [$row, $col], bounds [${rows}, ${cols}])" }
    private fun throwIfInvalidMultiplicationDimens(m: Matrix) = check(cols == m.rows) { "cols must equal rows ($cols, ${m.rows})" }

    companion object {
        const val NULL_PTR = 0L
    }
}