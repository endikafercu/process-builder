package com.bonitasoft.processbuilder.rest.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import groovy.sql.GroovyRowResult;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;


@Value // Generates getters, a constructor for all fields, equals, hashCode, and toString.
@Builder // Generates the full builder pattern (a builder class and a builder() factory method).
@JsonDeserialize(builder = ResultExecuteSqlDatasource.ResultExecuteSqlDatasourceBuilder.class)
public class ResultExecuteSqlDatasource {
    private final List<GroovyRowResult> result;
    private final Long count;
    @JsonIgnore
    private final LocalDate currentDate = LocalDate.now();
}
