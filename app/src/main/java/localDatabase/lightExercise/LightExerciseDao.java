package localDatabase.lightExercise;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.sql.Date;
import java.util.List;

import localDatabase.enums.ExerciseStatus;

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

    // get status by date
    @Query("SELECT exerciseStatus FROM LightExerciseTable WHERE date = :dateInput")
    ExerciseStatus getLightExerciseStatusByDate(String dateInput);

    // get light exercise obj by date
    @Query("SELECT * FROM LightExerciseTable WHERE date = :dateInput")
    LightExercise getLightExerciseByDate(String dateInput);

    // update to some status by date
    @Query("UPDATE LightExerciseTable SET exerciseStatus = :status WHERE date = :dateInput")
    void setLightExerciseStatusByDate(ExerciseStatus status, String dateInput);

    // update goal finished or not by date
    @Query("UPDATE LightExerciseTable SET exerciseGoalFinished = :isFinished WHERE date = :dateInput")
    void setLightExerciseStatusByDate(Boolean isFinished, String dateInput);

}

