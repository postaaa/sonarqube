/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.server.measure.ws;

import org.sonar.api.measures.Metric;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.server.ws.WebService.Param;
import org.sonar.api.utils.text.JsonWriter;
import org.sonar.core.measure.db.MetricDto;
import org.sonar.core.persistence.DbSession;
import org.sonar.core.persistence.MyBatis;
import org.sonar.server.db.DbClient;
import org.sonar.server.es.SearchOptions;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class ListAction implements MetricsWsAction {

  public static final String PARAM_IS_CUSTOM = "is_custom";

  public static final String FIELD_ID = "id";
  public static final String FIELD_KEY = "key";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_DESCRIPTION = "description";
  public static final String FIELD_DOMAIN = "domain";
  public static final String FIELD_TYPE = "type";
  private static final Set<String> POSSIBLE_FIELDS = newHashSet(FIELD_ID, FIELD_KEY, FIELD_NAME, FIELD_DESCRIPTION, FIELD_DOMAIN, FIELD_TYPE);

  private final DbClient dbClient;

  public ListAction(DbClient dbClient) {
    this.dbClient = dbClient;
  }

  @Override
  public void define(WebService.NewController context) {
    WebService.NewAction action = context.createAction("list")
      .setSince("5.2")
      .setResponseExample(getClass().getResource("example-list.json"))
      .addPagingParams(100)
      .addFieldsParam(POSSIBLE_FIELDS)
      .setHandler(this);

    action.createParam(PARAM_IS_CUSTOM)
      .setExampleValue("true")
      .setDescription("Choose custom metrics following 3 cases:" +
        "<ul>" +
        "<li>true: only custom metrics are returned</li>" +
        "<li>false: only non custom metrics are returned</li>" +
        "<li>not specified: all metrics are returned</li>" +
        "</ul>");
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    SearchOptions searchOptions = new SearchOptions()
      .setPage(request.mandatoryParamAsInt(Param.PAGE),
        request.mandatoryParamAsInt(Param.PAGE_SIZE));
    Boolean isCustom = request.paramAsBoolean(PARAM_IS_CUSTOM);
    DbSession dbSession = dbClient.openSession(false);
    try {
      List<MetricDto> metrics = dbClient.metricDao().selectEnabled(dbSession, isCustom, searchOptions);
      JsonWriter json = response.newJsonWriter();
      json.beginObject();
      Set<String> desiredFields = desiredFields(request.paramAsStrings(Param.FIELDS));
      writeMetrics(json, metrics, desiredFields);
      json.endObject();
      json.close();
    } finally {
      MyBatis.closeQuietly(dbSession);
    }
  }

  private Set<String> desiredFields(@Nullable List<String> fields) {
    if (fields == null || fields.isEmpty()) {
      return POSSIBLE_FIELDS;
    }

    return newHashSet(fields);
  }

  private void writeMetrics(JsonWriter json, List<MetricDto> metrics, Set<String> desiredFields) {
    json.name("metrics");
    json.beginArray();
    for (MetricDto metric : metrics) {
      json.beginObject();
      json.prop(FIELD_ID, String.valueOf(metric.getId()));
      json.prop(FIELD_KEY, metric.getKey());
      writeIfDesired(json, FIELD_NAME, metric.getShortName(), desiredFields);
      writeIfDesired(json, FIELD_DESCRIPTION, metric.getDescription(), desiredFields);
      writeIfDesired(json, FIELD_DOMAIN, metric.getDomain(), desiredFields);
      writeIfDesired(json, FIELD_TYPE, Metric.ValueType.descriptionOf(metric.getValueType()), desiredFields);
      json.endObject();
    }
    json.endArray();
  }

  private void writeIfDesired(JsonWriter json, String field, String value, Set<String> desiredFields) {
    if (desiredFields.contains(field)) {
      json.prop(field, value);
    }
  }
}
