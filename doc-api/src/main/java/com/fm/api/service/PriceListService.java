package com.fm.api.service;

import com.fm.api.utils.ListResult;
import com.fm.base.models.sql.PriceList;
import com.fm.base.models.sql.Project;
import com.fm.base.repository.sql.PriceListRepository;
import com.fm.base.repository.sql.ProjectRepository;
import com.fm.base.utils.PageableUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static com.fm.api.error.ErrorMessage.*;

@Service
@Slf4j
@AllArgsConstructor
public class PriceListService {

    private final PriceListRepository priceListDAO;
    private final ProjectRepository projectDAO;

    public PriceList createPriceList(PriceList priceList) {
        if (priceList.getStartTime().isAfter(priceList.getEndTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, START_TIME_NOT_BEFORE_END_TIME);
        }
        int numberOfDays = convertDateTimeToString(priceList.getStartTime()).compareTo(convertDateTimeToString(DateTime.now()));
        if (numberOfDays < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, START_TIME_NOT_AFTER_DATE_NOW);
        }
        Optional<Project> optionalProject = projectDAO.findByIdDeletedAtNull(priceList.getProjectId());
        if (optionalProject.isPresent()) {
            if (priceListDAO.findNamePriceExits(priceList.getPriceName(), priceList.getProjectId()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PRICE_NAME_IS_EXISTS);
            }
            return priceListDAO.save(priceList);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, PROJECT_ID_NOT_EXISTS);
        }
    }

    public List<PriceList> findAllByProjectId(Integer projectId) {
        return priceListDAO.listPriceListByProjectId(projectId);
    }

    public ListResult<PriceList> searchByProjectName(Integer projectId, int page, int size, String orderBy, boolean desc) {
        if (projectId == null) {
            return ListResult.from(priceListDAO.listPriceList(PageableUtils.pageable(page, size, orderBy, desc)));
        } else
            return ListResult.from(priceListDAO.listPriceListByProjectName(projectId, PageableUtils.pageable(page, size, orderBy, desc)));
    }

    public Optional<PriceList> getDetailPriceList(Integer id) {
        return priceListDAO.findById(id);
    }

    public String convertDateTimeToString(DateTime date) {
        return date.toString("yyyy-MM-dd");
    }
}