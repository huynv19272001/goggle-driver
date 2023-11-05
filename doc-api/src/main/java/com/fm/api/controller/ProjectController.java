
package com.fm.api.controller;


import com.fm.api.payload.response.MessageResponse;
import com.fm.api.payload.response.ResponseObject;
import com.fm.api.service.ProjectService;
import com.fm.base.models.dto.ProjectDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping({"/project", "/projects"})
@AllArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseObject.success(projectService.getAll());
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestParam(value = "name", required = false) String name,
                                    @RequestParam(value = "code", required = false) String code,
                                    @RequestParam(value = "keyWord", required = false) String keyWord,
                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                    @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
                                    @RequestParam(value = "desc", defaultValue = "false") boolean desc) {
        return ResponseObject.success(projectService.filter(name, code, keyWord, page, size, orderBy, desc));
    }

    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        return ResponseObject.createSuccess(projectService.create(projectDTO.mapToProject().trimCase()).mapToProjectDTO());
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<?> updateProject(@PathVariable Integer id, @Valid @RequestBody ProjectDTO projectDTO) {
        return ResponseObject.success(projectService.update(id, projectDTO.mapToProject().trimCase()).mapToProjectDTO());
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return projectService.getById(id).stream().map(ResponseObject::success).findAny().orElse(ResponseObject.notFound(MessageResponse.ID_NOT_EXIST));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@NotNull @Min(0) @PathVariable("id") Integer id) {
        return projectService.delete(id) ? ResponseObject.success() : ResponseObject.notFound(MessageResponse.ID_NOT_EXIST);
    }
}

