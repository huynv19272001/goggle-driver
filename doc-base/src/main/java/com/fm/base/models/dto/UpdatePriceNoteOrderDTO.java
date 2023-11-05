package com.fm.base.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class UpdatePriceNoteOrderDTO {
    private String note ;
    private Double price;
}
