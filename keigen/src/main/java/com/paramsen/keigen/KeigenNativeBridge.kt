package com.paramsen.keigen

/**
 * @author Pär Amsen 11/2019
 */
object KeigenNativeBridge {
    init {
        System.loadLibrary("eigen")
    }

    external fun initialize(rows: Int, cols: Int, fill: Float): Long
    external fun matrixPlus(nativePointerA: Long, nativePointerB: Long): Long
    external fun matrixPlusAssign(nativePointerA: Long, nativePointerB: Long)
    external fun matrixMul(nativePointerA: Long, nativePointerB: Long): Long
    external fun matrixMulAssign(nativePointerA: Long, nativePointerB: Long)
    external fun get(nativePointer: Long, row: Int, col: Int): Float
    external fun dispose(nativePointer: Long)
}
