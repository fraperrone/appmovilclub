package com.example.data.repository

interface Repository<T> {
    fun insertar(item: T): Long
    fun actualizar(item: T): Int
    fun eliminar(id: Int): Int
    fun obtenerPorId(id: Int): T?
    fun listarTodos(): List<T>
}
