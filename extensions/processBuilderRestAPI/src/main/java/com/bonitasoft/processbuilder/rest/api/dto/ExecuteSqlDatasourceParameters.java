package com.bonitasoft.processbuilder.rest.api.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor 
@NoArgsConstructor
@Data
public class ExecuteSqlDatasourceParameters {
	@NonNull
	private String queryId = "";
	private String query = "";
	private String queryCount = "";
	private Integer p = 0;
	private Integer c = 10;
	private String o = "";
	private Boolean isPaginated = false;
	private Boolean isOrdered = false;
	private Map<String, Object> params = new HashMap<String, Object>();
	private String datasource = "";
}

