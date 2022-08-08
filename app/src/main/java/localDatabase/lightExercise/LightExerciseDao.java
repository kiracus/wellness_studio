package localDatabase.lightExercise;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.sql.Date;
import java.util.List;

import localDatabase.enums.ExerciseSet;
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

    // get goal status by date
    @Query("SELECT exerciseGoalFinished FROM LightExerciseTable WHERE date = :dateInput")
    Boolean getGoalFinishedByDate(String dateInput);

    // get current set by date
    @Query("SELECT currentSet FROM LightExerciseTable WHERE date = :dateInput")
    ExerciseSet getCurrentSetByDate(String dateInput);

    // get light exercise obj by date
    @Query("SELECT * FROM LightExerciseTable WHERE date = :dateInput")
    LightExercise getLightExerciseByDate(String dateInput);

    // update to some status by date
    @Query("UPDATE LightExerciseTable SET exerciseStatus = :status WHERE date = :dateInput")
    void setLightExerciseStatusByDate(ExerciseStatus status, String dateInput);

    // update goal finished or not by date
    @Query("UPDATE LightExerciseTable SET exerciseGoalFinished = :isFinished WHERE date = :dateInput")
    void setExerciseGoalByDate(Boolean isFinished, String dateInput);

    // update current set by date
    @Query("UPDATE LightExerciseTable SET currentSet = :set WHERE date = :date")
    void setCurrSet(ExerciseSet set, String date);

    // get a list of dates of a month when goal is finished
    // list will have no more than 31 items
    @Query("SELECT date FROM LightExerciseTable WHERE exerciseGoalFinished = 1 AND date LIKE :yearMonthInput LIMIT 31")
    List<String> getFinishedDatesOfMonth(String yearMonthInput);

    // update goal finished or not by date
    @Query("UPDATE LightExerciseTable SET exerciseStatus = :exerciseStatus WHERE date = :dateInput")
    void setExerciseStatus(ExerciseStatus exerciseStatus, String dateInput);

    // update step1Completion status by date
    @Query("UPDATE LightExerciseTable SET step1Completion = :stepOneCompleted WHERE date = :date")
    void setStepOneCompleted(Boolean stepOneCompleted, String date);

    // update step1Completion status by date
    @Query("UPDATE LightExerciseTable SET step2Completion = :stepOneCompleted WHERE date = :date")
    void setStepTwoCompleted(Boolean stepOneCompleted, String date);

    // update step1Completion status by date
    @Query("UPDATE LightExerciseTable SET step3Completion = :stepOneCompleted WHERE date = :date")
    void setStepThreeCompleted(Boolean stepOneCompleted, String date);

    // update step1Completion status by date
    @Query("UPDATE LightExerciseTable SET step4Completion = :stepOneCompleted WHERE date = :date")
    void setStepFourCompleted(Boolean stepOneCompleted, String date);

    // update to currentStep by date
    @Query("UPDATE LightExerciseTable SET currentStep = :currentStep WHERE date = :dateInput")
    void setLightExerciseStepByDate(String currentStep, String dateInput);
}

