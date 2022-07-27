package localDatabase.lightExercise;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.sql.Date;
import java.sql.Time;

import localDatabase.enums.ExerciseSet;
import localDatabase.enums.ExerciseStatus;
import localDatabase.utils.DateConverter;
import localDatabase.utils.TimeConverter;

@Entity(tableName = "LightExerciseTable")
public class LightExercise {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    public String date;

    @ColumnInfo(name = "exerciseReminder")
    @TypeConverters(TimeConverter.class)
    public Time exerciseReminder;

    @ColumnInfo(name = "exerciseStatus")
    public ExerciseStatus exerciseStatus;

    @ColumnInfo(name = "currentSet")
    public ExerciseSet currentSet;

    @ColumnInfo(name = "currentStep")
    public String currentStep;

    @ColumnInfo(name = "exerciseGoalFinished")
    public Boolean exerciseGoalFinished;

    @ColumnInfo(name="step1Completion")
    public Boolean stepOneCompleted;

    @ColumnInfo(name="step2Completion")
    public Boolean stepTwoCompleted;

    @ColumnInfo(name="step3Completion")
    public Boolean stepThreeCompleted;

    @ColumnInfo(name="step4Completion")
    public Boolean stepFourCompleted;

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public Time getExerciseReminder() {
        return exerciseReminder;
    }

    public void setExerciseReminder(Time exerciseReminder) {
        this.exerciseReminder = exerciseReminder;
    }

    public ExerciseStatus getExerciseStatus() {
        return exerciseStatus;
    }

    public void setExerciseStatus(ExerciseStatus exerciseStatus) {
        this.exerciseStatus = exerciseStatus;
    }

    public ExerciseSet getCurrentSet() {
        return currentSet;
    }

    public void setCurrentSet(ExerciseSet currentSet) {
        this.currentSet = currentSet;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public Boolean getExerciseGoalFinished() {
        return exerciseGoalFinished;
    }

    public void setExerciseGoalFinished(Boolean exerciseGoalFinished) {
        this.exerciseGoalFinished = exerciseGoalFinished;
    }

    public Boolean getStepOneCompleted() {
        return stepOneCompleted;
    }

    public void setStepOneCompleted(Boolean stepOneCompleted) {
        this.stepOneCompleted = stepOneCompleted;
    }

    public Boolean getStepTwoCompleted() {
        return stepTwoCompleted;
    }

    public void setStepTwoCompleted(Boolean stepTwoCompleted) {
        this.stepTwoCompleted = stepTwoCompleted;
    }

    public Boolean getStepThreeCompleted() {
        return stepThreeCompleted;
    }

    public void setStepThreeCompleted(Boolean stepThreeCompleted) {
        this.stepThreeCompleted = stepThreeCompleted;
    }

    public Boolean getStepFourCompleted() {
        return stepFourCompleted;
    }

    public void setStepFourCompleted(Boolean stepFourCompleted) {
        this.stepFourCompleted = stepFourCompleted;
    }
}


