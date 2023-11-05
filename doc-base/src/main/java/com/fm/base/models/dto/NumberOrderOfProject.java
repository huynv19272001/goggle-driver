package com.fm.base.models.dto;

import com.fm.base.models.sql.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;

@EqualsAndHashCode(callSuper = true)
@With
@AllArgsConstructor
@Data
public class NumberOrderOfProject extends Project {
    private Long countOrder;

    public NumberOrderOfProject(Integer projectId, Long countOrder) {
        this.countOrder = countOrder;
        this.setId(projectId);
    }

}
