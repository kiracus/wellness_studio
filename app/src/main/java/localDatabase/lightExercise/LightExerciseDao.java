package localDatabase.lightExercise;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LightExerciseDao {

    @Query("SELECT * FROM LightExerciseTable")
    List<LightExercise> getLightExercise();

    @Insert
    void insertLightExercise(LightExercise ...lightExercise);

    @Update
    void updateLightExercise(LightExercise lightExercise);

    @Delete
    void deleteLightExercise(LightExercise lightExercise);
}

