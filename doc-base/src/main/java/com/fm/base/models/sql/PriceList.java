package com.fm.base.models.sql;

import com.fm.base.models.dto.PriceListDTO;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.lang.annotation.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.fm.base.message.ErrorMessage.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@With
@Entity
@Table(name = "price_list")
public class PriceList extends BaseModel {

    @Column(name = "price")
    private Double price;

    @Column(name = "price_name", columnDefinition ="varchar(255)")
    private String priceName;

    @Column(name = "start_time", columnDefinition = "timestamp")
    private DateTime startTime;

    @Column(name = "end_time", columnDefinition = "timestamp")
    private DateTime endTime;

    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "time_print")
    private Double timePrint;

    @Column(name = "time_packing")
    private Double timePacking;

    @Enumerated(EnumType.STRING)
    private Unit unit;

    public enum Unit {
        SHEET, SET
    }

    public PriceListDTO mapToPriceListDTO()
    {
        return new PriceListDTO().withId(this.getId()).withCreatedAt(this.getCreatedAt()).
                   withDeletedAt(this.getDeletedAt()).withUpdatedAt(this.getUpdatedAt()).withPrice(price).
                   withPriceName(priceName).withStartTime(startTime).withEndTime(endTime).
                   withProjectId(projectId).withTimePrint(timePrint).
                   withTimePacking(timePacking).withUnit(unit);
    }
}
