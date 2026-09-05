package com.AGTECH.backend.dtos;

import java.time.LocalDate;
import java.util.UUID;

import com.AGTECH.backend.models.Plantio;

public record PlantioResponse(
    UUID id,
    UUID safraId,
    LocalDate dataPlantio,
    Double areaPlantadaHectares
) {
    public static PlantioResponse from(Plantio plantio) {
        return new PlantioResponse(
            plantio.getId(),
            plantio.getSafra().getId(),
            plantio.getDataPlantio(),
            plantio.getAreaPlantadaHectares()
        );
    }
}
