package com.example.practica08.domain

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.practica08.data.Usuario
import com.example.practica08.data.UsuarioDao

@Database(
    entities = [Usuario::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    // Nos permite acceder a las funciones de UsuarioDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {

        // Mantiene una sola instancia de la base de datos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                // Creamos la base de datos
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mi_base_datos"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}