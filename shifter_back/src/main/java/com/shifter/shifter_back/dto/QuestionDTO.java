package com.shifter.shifter_back.dto;

import com.shifter.shifter_back.models.BaseEntity;
import com.shifter.shifter_back.models.Category;
import com.shifter.shifter_back.models.Difficulty;
import com.shifter.shifter_back.models.QuestionType;
import com.shifter.shifter_back.utils.ListToStringConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class QuestionDTO extends BaseEntity {
    private int id;
    private String name;
    private String description;
    private int score;
    private int duration;                   // in seconds
    private List<String> choices;
//    private List<String> answer;
//    private String answerNote;
    private QuestionType questionType;
    private Difficulty difficulty;
    private Category category;

}
