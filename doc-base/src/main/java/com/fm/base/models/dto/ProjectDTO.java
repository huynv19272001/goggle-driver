package com.fm.base.models.dto;

import com.fm.base.message.ErrorMessage;
import com.fm.base.models.sql.Project;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Data
@With
public class ProjectDTO {

    private Integer id;

    private DateTime createdAt;

    private DateTime updatedAt;

    private DateTime deletedAt;

    @NotBlank(message = ErrorMessage.PROJECT_NAME_NOT_BLANK)
    @Length(max = 50, message = ErrorMessage.PROJECT_NAME_MORE_50_CHARACTERS)
    private String name;

    @NotBlank(message = ErrorMessage.PROJECT_CODE_NOT_BLANK)
    @Length(max = 20, message = ErrorMessage.PROJECT_CODE_MORE_20_CHARACTERS)
    @Pattern(regexp = "[A-Za-z0-9_]+", message = ErrorMessage.PROJECT_CODE_CONTAIN_ONLY_NUMBER_LETTER)
    private String code;

    @NotNull(message = ErrorMessage.USER_ID_NOT_NULL)
    private Integer userId;

    @NotNull(message = ErrorMessage.DASHBOARD_NOT_NULL)
    private Project.Dashboard dashboard;

    public Project mapToProject() {
        return new Project().withCode(code).withDashboard(dashboard).withName(name).withUserId(userId);
    }

}
