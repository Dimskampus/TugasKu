package id.ac.mulia.tugasku.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY CASE WHEN status = 'Selesai' THEN 1 ELSE 0 END, deadline ASC")
    fun observeAll(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun observeById(id: Long): Flow<Task?>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): Task?

    @Upsert suspend fun upsert(task: Task)

    @Delete suspend fun delete(task: Task)
}
