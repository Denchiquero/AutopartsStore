package ru.mirea.autopartsstore.vin.controller;

import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.vin.dto.DecodedVin;
import ru.mirea.autopartsstore.vin.service.VinService;



@RestController
@RequestMapping("/api/vin")
public class VinController {

    private final VinService vinService;

    public VinController(VinService vinService) {
        this.vinService = vinService;
    }

    @GetMapping("/{vin}")
    public DecodedVin decode(@PathVariable String vin) {
        return vinService.decode(vin);
    }
}