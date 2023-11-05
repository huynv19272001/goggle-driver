package com.fm.api.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchStarredRequest {
    private String type ;
    private int owner ;
    private String ownerEmail ;
    private Date fromDate;
    private Date toDate;
    private String fileName ;
    private String userShared ;
}
