package com.shifter.shifter_back.models;

import com.shifter.shifter_back.utils.ListToStringConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "question")
@Data
public class Question extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private int score;
    @Column
    private int duration;                   // in seconds
//    @ElementCollection
//    @CollectionTable(name = "question_choices", joinColumns = @JoinColumn(name = "question_id"))
    @Convert(converter = ListToStringConverter.class)
    @Column(name = "choices")
    private List<String> choices;
    @Column
    @Convert(converter = ListToStringConverter.class)
    private List<String> answer;
    @Column(name = "answer_note")
    private String answerNote;
    @Column(name = "question_type")
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;
    @Column
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}
