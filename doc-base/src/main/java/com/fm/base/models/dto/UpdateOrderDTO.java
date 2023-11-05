package com.fm.base.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class UpdateOrderDTO {
   private List<Integer> orderIds;
   private String fileName;
   private String note;
   private Double price;
   private String customerName;
}
