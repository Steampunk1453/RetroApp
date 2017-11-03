package com.feedback.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.feedback.domain.Points;
import com.feedback.repository.PointsRepository;
import com.feedback.security.AuthoritiesConstants;
import com.feedback.security.SecurityUtils;
import com.feedback.service.PointsService;
import com.feedback.service.UserService;
import com.feedback.service.dto.PointsDTO;
import com.feedback.web.rest.util.HeaderUtil;
import com.feedback.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Points.
 */
@RestController
@RequestMapping("/api")
public class PointsResource {

    private final Logger log = LoggerFactory.getLogger(PointsResource.class);

    private static final String ENTITY_NAME = "points";

    private final PointsService pointsService;

    private final UserService userService;

    private final PointsRepository pointsRepository;

    public PointsResource(PointsService pointsService, UserService userService, PointsRepository pointsRepository) {
        this.pointsService = pointsService;
        this.userService = userService;
        this.pointsRepository = pointsRepository;
    }

    /**
     * POST  /points : Create a new points.
     *
     * @param pointsDTO the pointsDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pointsDTO, or with status 400 (Bad Request) if the points has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/points")
    @Timed
    public ResponseEntity<PointsDTO> createPoints(@Valid @RequestBody PointsDTO pointsDTO) throws URISyntaxException {
        log.debug("REST request to save Points : {}", pointsDTO);
        if (pointsDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists",
                "A new points cannot already have an ID")).body(null);
        } if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
                log.debug("No user passed in, using current user: {}",
                    SecurityUtils.getCurrentUserLogin());
            pointsDTO.setUserId(userService.findOneByLogin(SecurityUtils.getCurrentUserLogin()).getId());
        }
        PointsDTO result = pointsService.save(pointsDTO);
        return ResponseEntity.created(new URI("/api/points/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /points : Updates an existing points.
     *
     * @param pointsDTO the pointsDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pointsDTO,
     * or with status 400 (Bad Request) if the pointsDTO is not valid,
     * or with status 500 (Internal Server Error) if the pointsDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/points")
    @Timed
    public ResponseEntity<PointsDTO> updatePoints(@Valid @RequestBody PointsDTO pointsDTO) throws URISyntaxException {
        log.debug("REST request to update Points : {}", pointsDTO);
        if (pointsDTO.getId() == null) {
            return createPoints(pointsDTO);
        }
        PointsDTO result = pointsService.save(pointsDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, pointsDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /points : get all the points.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of points in body
     */
    @GetMapping("/points")
    @Timed
    public ResponseEntity<List<PointsDTO>> getAllPoints(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Points");
        Page<Points> page;
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            page = pointsRepository.findAllByOrderByDateDesc(pageable);

        } else {
            page = pointsRepository.findByUserIsCurrentUser(pageable);

        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/points");
        return new ResponseEntity<>(pointsService.getPointsList(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /points/:id : get the "id" points.
     *
     * @param id the id of the pointsDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pointsDTO, or with status 404 (Not Found)
     */
    @GetMapping("/points/{id}")
    @Timed
    public ResponseEntity<PointsDTO> getPoints(@PathVariable Long id) {
        log.debug("REST request to get Points : {}", id);
        PointsDTO pointsDTO = pointsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(pointsDTO));
    }

    /**
     * DELETE  /points/:id : delete the "id" points.
     *
     * @param id the id of the pointsDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/points/{id}")
    @Timed
    public ResponseEntity<Void> deletePoints(@PathVariable Long id) {
        log.debug("REST request to delete Points : {}", id);
        pointsService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
