package com.shifter.shifter_back.dto;

import com.shifter.shifter_back.models.BaseEntity;
import com.shifter.shifter_back.models.Question;
import lombok.Data;

import java.util.List;

@Data
public class CategoryDTO extends BaseEntity {
    private int id;
    private String name;
    private List<Question> questions;
}
