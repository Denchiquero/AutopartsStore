package ru.mirea.autopartsstore.common.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }


    @Test
    void resourceNotFound_shouldReturn404()
            throws Exception {

        mockMvc.perform(
                        get("/test/not-found")
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Not Found")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Test resource not found")
                )
                .andExpect(
                        jsonPath("$.path")
                                .value("/test/not-found")
                );
    }


    @Test
    void illegalArgument_shouldReturn400()
            throws Exception {

        mockMvc.perform(
                        get("/test/bad-request")
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Bad Request")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Invalid argument")
                );
    }


    @Test
    void insufficientStock_shouldReturn409()
            throws Exception {

        mockMvc.perform(
                        get("/test/stock")
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Conflict")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Not enough stock")
                );
    }


    @Test
    void validation_shouldReturnFieldErrors()
            throws Exception {

        mockMvc.perform(
                        post("/test/validation")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                {
                                  "name": ""
                                }
                                """)
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Validation failed")
                )
                .andExpect(
                        jsonPath("$.errors.name")
                                .value("Name must not be blank")
                );
    }


    @RestController
    static class TestController {

        @GetMapping("/test/not-found")
        void notFound() {

            throw new ResourceNotFoundException(
                    "Test resource not found"
            );
        }


        @GetMapping("/test/bad-request")
        void badRequest() {

            throw new IllegalArgumentException(
                    "Invalid argument"
            );
        }


        @GetMapping("/test/stock")
        void stock() {

            throw new InsufficientStockException(
                    "Not enough stock"
            );
        }


        @PostMapping("/test/validation")
        void validation(
                @Valid
                @RequestBody TestRequest request
        ) {
        }
    }


    record TestRequest(

            @NotBlank(
                    message = "Name must not be blank"
            )
            String name

    ) {
    }
}