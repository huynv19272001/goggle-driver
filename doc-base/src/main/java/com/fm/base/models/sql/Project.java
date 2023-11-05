package com.fm.base.models.sql;

import com.fm.base.message.ErrorMessage;
import com.fm.base.models.dto.ProjectDTO;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@With
@Table(name = "projects")
public class Project extends BaseModel {

    @Column(name = "name")
    private String name;

    @Column(name = "code", columnDefinition = "varchar(255) ")
    private String code;

    @Column(name = "user_id")
    private Integer userId;


    @Column(name = "dashboard", nullable = false)
    @Enumerated(EnumType.STRING)
    private Dashboard dashboard;

    public enum Dashboard {
        SHOW, HIDE
    }

    public Project trimCase() {
        name = name.replaceAll("\\s+", " ").trim();
        code = code.replaceAll("\\s+", " ").trim();
        return this;
    }

    public ProjectDTO mapToProjectDTO() {
        return new ProjectDTO()
                .withCode(code)
                .withDashboard(dashboard)
                .withName(name)
                .withUserId(userId)
                .withId(this.getId())
                .withCreatedAt(this.getCreatedAt())
                .withUpdatedAt(this.getUpdatedAt())
                .withDeletedAt(this.getDeletedAt());
    }

}
