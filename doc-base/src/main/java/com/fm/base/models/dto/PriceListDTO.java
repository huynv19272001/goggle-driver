package com.fm.base.models.dto;

import com.fm.base.models.sql.PriceList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.fm.base.message.ErrorMessage.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@With
public class PriceListDTO {

    private Integer id;

    private DateTime createdAt;

    private DateTime updatedAt;

    private DateTime deletedAt;
    @Min(value = 0, message = PRICE_MUST_BE_MORE_THAN_0)
    private Double price;

    @NotBlank(message = PRICE_NAME_NOT_NULL)
    @Length(max = 50, message = LENGTH_PRICE_NAME_NOT_LONGER_THAN_50_CHARACTERS)
    private String priceName;

    @NotNull(message = START_TIME_NOT_NULL)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private DateTime startTime;

    @NotNull(message = END_TIME_NOT_NULL)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private DateTime endTime;

    @NotNull(message = PROJECT_ID_NOT_NULL)
    private Integer projectId;

    @NotNull(message = TIME_PRINT_NOT_NULL)
    @Min(value = 0, message = TIME_PRINT_MUST_BE_MORE_THAN_0)
    private Double timePrint;

    @NotNull(message = TIME_PACKING_NOT_NULL)
    @Min(value = 0, message = TIME_PACKING_MUST_BE_MORE_THAN_0)
    private Double timePacking;

    @NotNull(message = UNIT_NOT_NULL)
    private PriceList.Unit unit;

    public PriceListDTO trimCase() {
        priceName = priceName.replaceAll("\\s+", " ").trim();
        return this;
    }

    public PriceList mapToPriceList() {
        return new PriceList().withPrice(price)
                .withPriceName(priceName)
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withProjectId(projectId)
                .withTimePrint(timePrint)
                .withTimePacking(timePacking)
                .withUnit(unit);
    }
}
