package ru.gb.timesheet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.gb.timesheet.model.Project;
import ru.gb.timesheet.model.Timesheet;
import ru.gb.timesheet.repository.TimesheetRepository;
import ru.gb.timesheet.service.TimesheetService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimesheetControllerTest {
    @Autowired
    TimesheetRepository timesheetRepository;
    @LocalServerPort
    private int port;
    private RestClient restClient;
    @Autowired
    private TimesheetService timesheetService;

    @BeforeEach
    void beforeEach() {
        restClient = RestClient.create("http://localhost:" + port);
    }

    @Test
    void getByIdNotFound() {
        assertThrows(HttpClientErrorException.NotFound.class, () -> {
            restClient.get()
                    .uri("/timesheets/-1") // Неверный ID
                    .retrieve()
                    .toBodilessEntity();
        });
    }

    @Test
    void getAll() {
        Timesheet timesheet1 = new Timesheet();
        timesheet1.setId(1L);
        timesheetService.create(timesheet1);

        Timesheet timesheet2 = new Timesheet();
        timesheet2.setId(2L);
        timesheetService.create(timesheet2);

        ResponseEntity<List<Timesheet>> response = restClient.get()
                .uri("/timesheets")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Timesheet>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Timesheet> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }


    @Test
    void testCreate() {
        Timesheet toCreate = new Timesheet();
        toCreate.setId(1L);

        ResponseEntity<Timesheet> response = restClient.post()
                .uri("/timesheets")
                .body(toCreate)
                .retrieve()
                .toEntity(Timesheet.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Timesheet responseBody = response.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.getId());
        assertEquals(responseBody.getMinutes(), toCreate.getMinutes());
        assertTrue(timesheetService.findById(responseBody.getId()).isPresent());
    }

    @Test
    void testDeleteById() {
        Timesheet toDelete = new Timesheet();
        toDelete.setId(1L);
        toDelete = timesheetService.create(toDelete);

        ResponseEntity<Void> response = restClient.delete()
                .uri("/timesheets/" + toDelete.getId())
                .retrieve()
                .toBodilessEntity();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(timesheetService.findById(toDelete.getId()).isPresent());
    }
}