package com.paramsen.keigen

private const val NULL_PTR = 0L

/**
 * @author Pär Amsen 11/2019
 */
class Matrix(val rows: Int, val cols: Int) {
    var nativePointer = NULL_PTR
        private set

    constructor(rows: Int, cols: Int, fill: Float = 0f): this(rows, cols) {
        nativePointer = KeigenNativeBridge.initialize(rows, cols, fill)
    }

    //init from raw data
    constructor(rows: Int, cols: Int, data: Array<Float>, offset: Int = 0, strideCol: Int = 1, strideRow: Int = cols) : this(rows, cols) {
        //validate that supplied data has enough elements
    }

    constructor(rows: Int, cols: Int, nativePointer: Long) : this(rows, cols) {
        this.nativePointer = nativePointer
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
        TODO("Not yet implemented")
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
        return Matrix(rows, m.cols, KeigenNativeBridge.matrixMul(nativePointer, m.nativePointer))
    }
    // *=,
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
}