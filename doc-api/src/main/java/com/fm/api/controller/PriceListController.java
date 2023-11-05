package com.fm.api.controller;

import com.fm.api.payload.response.ResponseObject;
import com.fm.api.service.PriceListService;
import com.fm.base.models.dto.PriceListDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.fm.api.error.ErrorMessage.PRICE_LIST_NOT_EXISTS;
import static com.fm.api.error.ErrorMessage.PROJECT_ID_NOT_EXISTS;

@AllArgsConstructor
@RestController
@RequestMapping({"/price-list"})
public class PriceListController {

    @Autowired
    private PriceListService priceListService;

    @PostMapping({"/create-price"})
    @ResponseBody
    ResponseEntity<?> createPriceList(@Valid @RequestBody PriceListDTO priceListDTO) {
        return ResponseObject.success(priceListService.createPriceList(priceListDTO.trimCase().mapToPriceList()).mapToPriceListDTO());
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<?> getDetailPriceList(@PathVariable Integer id) {
        return priceListService.getDetailPriceList(id).stream().map(ResponseObject::success)
                .findAny().orElse(ResponseObject.notFound(PRICE_LIST_NOT_EXISTS));
    }

    @GetMapping("/search-project-id")
    public ResponseEntity<?> findAll(@RequestParam(value = "projectId", required = false) Integer projectId) {
        return ResponseObject.success(priceListService.findAllByProjectId(projectId));
    }

    @GetMapping("/search-project-id/pagination")
    public ResponseEntity<?> searchByProjectName(@RequestParam(value = "projectId", required = false) Integer projectId,
                                                 @RequestParam(value = "page", defaultValue = "1") int page,
                                                 @RequestParam(value = "size", defaultValue = "20") int size,
                                                 @RequestParam(value = "orderBy", defaultValue = "created_at") String orderBy,
                                                 @RequestParam(value = "desc", defaultValue = "false") boolean desc) {
        try {
            return ResponseObject.success(priceListService.searchByProjectName(projectId,page,size,orderBy,desc));
        } catch (Exception e) {
            return ResponseObject.notFound(PROJECT_ID_NOT_EXISTS);
        }
    }

}
