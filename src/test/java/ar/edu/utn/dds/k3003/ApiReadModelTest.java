package ar.edu.utn.dds.k3003;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import ar.edu.utn.dds.k3003.app.Application;
import com.fasterxml.jackson.databind.*;
import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.*;
import org.springframework.test.web.servlet.*;

@SpringBootTest(
    classes = Application.class,
    properties = {
      "spring.datasource.url=jdbc:h2:mem:donadores6;DB_CLOSE_DELAY=-1",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "donatrack.read-model.publisher.enabled=true",
      "READ_MODEL_SEND_INITIAL_DELAY_MS=3600000",
      "READ_MODEL_ADMIN_KEY=test-key",
      "spring.rabbitmq.dynamic=false",
      "management.health.rabbit.enabled=false",
      "management.datadog.metrics.export.enabled=false"
    })
@AutoConfigureMockMvc
@org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ApiReadModelTest {
  static final HttpServer remote;
  static final Map<String, String> donationDonors = new java.util.concurrent.ConcurrentHashMap<>();
  static final Map<String, String> donationStates = new java.util.concurrent.ConcurrentHashMap<>();
  static final Set<String> pendingNeeds = java.util.concurrent.ConcurrentHashMap.newKeySet();

  static {
    try {
      remote = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
      remote.createContext(
          "/",
          e -> {
            String path = e.getRequestURI().getPath();
            if (path.startsWith("/donaciones/")) {
              String id = path.substring("/donaciones/".length());
              if (!donationDonors.containsKey(id)) {
                e.sendResponseHeaders(404, -1);
                e.close();
                return;
              }
              String body =
                  new ObjectMapper()
                      .writeValueAsString(
                          Map.of(
                              "id",
                              id,
                              "donadorID",
                              donationDonors.get(id),
                              "estado",
                              donationStates.getOrDefault(id, "ACEPTADA")));
              byte[] out = body.getBytes(StandardCharsets.UTF_8);
              e.getResponseHeaders().add("Content-Type", "application/json");
              e.sendResponseHeaders(200, out.length);
              e.getResponseBody().write(out);
              e.close();
              return;
            }
            if (path.equals("/asignaciones")) {
              String body =
                  new ObjectMapper()
                      .writeValueAsString(
                          pendingNeeds.stream()
                              .map(
                                  n ->
                                      Map.of(
                                          "id",
                                          "a-" + n,
                                          "paqueteID",
                                          "p-" + n,
                                          "necesidadID",
                                          n,
                                          "estado",
                                          "ASIGNADA",
                                          "cantidadAsignada",
                                          1))
                              .toList());
              byte[] out = body.getBytes(StandardCharsets.UTF_8);
              e.getResponseHeaders().add("Content-Type", "application/json");
              e.sendResponseHeaders(200, out.length);
              e.getResponseBody().write(out);
              e.close();
              return;
            }
            String content =
                path.startsWith("/productos/")
                    ? "{\"id\":\"p\",\"nombre\":\"Arroz\",\"descripcion\":\"Demo\",\"categoriaID\":\"c\"}"
                    : path.startsWith("/stock/") && !path.equals("/stock/asignaciones")
                        ? "{\"productoID\":\"p\",\"cantidadDisponible\":100}"
                        : path.startsWith("/misiones/") ? "null" : "[]";
            byte[] out = content.getBytes(StandardCharsets.UTF_8);
            e.getResponseHeaders().add("Content-Type", "application/json");
            e.sendResponseHeaders(200, out.length);
            e.getResponseBody().write(out);
            e.close();
          });
      remote.start();
    } catch (Exception ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }

  @DynamicPropertySource
  static void urls(DynamicPropertyRegistry registry) {
    for (String name : List.of("URL_DONACIONES", "URL_LOGISTICA", "URL_INCENTIVOS"))
      registry.add(name, () -> "http://127.0.0.1:" + remote.getAddress().getPort());
  }

  @AfterAll
  static void close() {
    remote.stop(0);
  }

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper mapper;
  @Autowired JdbcTemplate jdbc;
  @Autowired Fachada facade;

  @BeforeEach
  void resetRemoteState() {
    pendingNeeds.clear();
    donationDonors.clear();
    donationStates.clear();
  }

  JsonNode call(String method, String path, Object body) throws Exception {
    var request =
        request(org.springframework.http.HttpMethod.valueOf(method), path)
            .contentType(MediaType.APPLICATION_JSON);
    if (body != null) request.content(mapper.writeValueAsString(body));
    var response = mvc.perform(request).andReturn().getResponse();
    assertTrue(
        response.getStatus() < 300, response.getStatus() + " " + response.getContentAsString());
    String content = response.getContentAsString();
    return content.isBlank() || !content.startsWith("{") && !content.startsWith("[")
        ? mapper.nullNode()
        : mapper.readTree(content);
  }

  Map<String, Object> donor() {
    return Map.of(
        "nombre",
        "Ana",
        "apellido",
        "Demo",
        "edad",
        25,
        "email",
        "ana@example.com",
        "nroDocumento",
        "123",
        "domicilio",
        "Medrano");
  }

  Map<String, Object> entity() {
    return Map.of(
        "razonSocial",
        "Escuela",
        "domicilio",
        "Medrano",
        "telefono",
        "123",
        "correo",
        "e@example.com");
  }

  Map<String, Object> need(String entity, String type) {
    return Map.of(
        "entidadID",
        entity,
        "productoSolicitadoID",
        "p",
        "descripcion",
        "Alimentos",
        "cantidadObjetivo",
        10,
        "nivelDeUrgencia",
        9,
        "tipo",
        type);
  }

  @Test
  void lifecycleEmitsSnapshotsForCreatesUpdatesDeliveriesAndDeletes() throws Exception {
    String d = call("POST", "/donadores", donor()).path("id").asText(),
        e = call("POST", "/entidades", entity()).path("id").asText();
    call("PUT", "/donadores/" + d, donor());
    call("PUT", "/entidades/" + e, entity());
    call("PATCH", "/donadores/" + d + "/categoria", Map.of("categoria", "COLABORADOR"));
    call("PATCH", "/donadores/" + d + "/estado", Map.of("estado", "VERIFICADO"));
    mvc.perform(get("/donadores/" + d + "/puede-donar"))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                .string("true"));
    call("GET", "/donadores/" + d + "/estadisticas", null);
    String n = call("POST", "/necesidades", need(e, "EXTRAORDINARIA")).path("id").asText();
    call("PUT", "/necesidades/" + n, need(e, "EXTRAORDINARIA"));
    call("POST", "/necesidades/" + n + "/satisfaccion?cantidadASatisfacer=4", null);
    assertEquals(4, call("GET", "/necesidades/" + n, null).path("cantidadAsignada").asInt());
    call("GET", "/necesidades?productoSolicitadoID=p", null);
    call("GET", "/necesidades/" + n + "/periodo", null);
    for (int i = 0; i < 10; i++) {
      donationDonors.put("x" + i, d);
      call(
          "POST",
          "/donadores/" + d + "/quejas",
          Map.of("donacionID", "x" + i, "descripcion", "Queja", "fecha", "2026-11-06"));
    }
    assertEquals("BANEADO", call("GET", "/donadores/" + d, null).path("estado").asText());
    assertEquals(10, call("GET", "/donadores/" + d + "/quejas", null).size());
    call("GET", "/donadores/quejas/todas", null);
    for (String path :
        List.of(
            "/donadores",
            "/entidades",
            "/entidades/" + e,
            "/necesidades",
            "/sistema/donadores",
            "/sistema/entidades",
            "/actuator/prometheus",
            "/v3/api-docs")) call("GET", path, null);
    mvc.perform(post("/admin/read-model/republicar").header("X-Admin-Key", "wrong"))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                .isForbidden());
    mvc.perform(post("/admin/read-model/republicar").header("X-Admin-Key", "test-key"))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
    call("DELETE", "/necesidades/" + n, null);
    call("DELETE", "/entidades/" + e, null);
    call("DELETE", "/donadores/" + d, null);
    assertTrue(
        jdbc.queryForObject(
                "select count(*) from read_model_outbox where content like '%DELETE%'",
                Integer.class)
            >= 13);
  }

  @Test
  void recurringNeedsRejectPartialDeliveryAndAreIncludedInBootstrap() throws Exception {
    String e = call("POST", "/entidades", entity()).path("id").asText();
    String n =
        call("POST", "/necesidades?periodo=MENSUAL", need(e, "RECURRENTE")).path("id").asText();
    assertThrows(RuntimeException.class, () -> facade.satisfacerNecesidad(n, 1));
    call("POST", "/necesidades/" + n + "/satisfaccion?cantidadASatisfacer=10", null);
    assertEquals(10, call("GET", "/necesidades/" + n, null).path("cantidadAsignada").asInt());
    int before = jdbc.queryForObject("select count(*) from read_model_outbox", Integer.class);
    jdbc.update(
        "update necesidades set inicio_periodo=?, cantidad_asignada=10 where id=?",
        java.time.LocalDate.now().minusMonths(1),
        n);
    facade.actualizarPeriodos();
    assertEquals(0, call("GET", "/necesidades/" + n, null).path("cantidadAsignada").asInt());
    int after = jdbc.queryForObject("select count(*) from read_model_outbox", Integer.class);
    assertEquals(before + 1, after);
    facade.actualizarPeriodos();
    assertEquals(
        after, jdbc.queryForObject("select count(*) from read_model_outbox", Integer.class));
    facade.republicarModeloLectura();
    facade.limpiarDatos();
    assertTrue(facade.listarNecesidades().isEmpty());
  }

  @Test
  void invalidDonorsAndEditsDoNotPersist() throws Exception {
    for (var entry :
        Map.<String, Object>of(
                "nombre",
                "",
                "apellido",
                "",
                "edad",
                -5,
                "email",
                "invalido",
                "nroDocumento",
                "",
                "domicilio",
                "")
            .entrySet()) {
      var invalid = new HashMap<>(donor());
      invalid.put(entry.getKey(), entry.getValue());
      mvc.perform(
              post("/donadores")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(invalid)))
          .andExpect(
              org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                  .isBadRequest());
    }
    String d = call("POST", "/donadores", donor()).path("id").asText();
    mvc.perform(
            put("/donadores/" + d)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"\"}"))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                .isBadRequest());
    assertEquals("Ana", call("GET", "/donadores/" + d, null).path("nombre").asText());
    mvc.perform(post("/entidades").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                .isBadRequest());
  }

  @Test
  void complaintsValidateDonationAndAreIdempotent() throws Exception {
    String d = call("POST", "/donadores", donor()).path("id").asText();
    var complaint =
        Map.of("donacionID", "claim-test", "descripcion", "Queja", "fecha", "2026-11-06");
    var body = mapper.writeValueAsString(complaint);
    mvc.perform(
            post("/donadores/" + d + "/quejas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                .isNotFound());
    donationDonors.put("claim-test", "another-donor");
    mvc.perform(
            post("/donadores/" + d + "/quejas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                .isBadRequest());
    donationDonors.put("claim-test", d);
    donationStates.put("claim-test", "INGRESADA");
    mvc.perform(
            post("/donadores/" + d + "/quejas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                .isBadRequest());
    donationStates.put("claim-test", "ACEPTADA");
    String first = call("POST", "/donadores/" + d + "/quejas", complaint).path("id").asText();
    donationStates.put("claim-test", "CONQUEJA");
    assertEquals(first, call("POST", "/donadores/" + d + "/quejas", complaint).path("id").asText());
    assertEquals(1, call("GET", "/donadores/" + d + "/quejas", null).size());
    assertEquals("VERIFICADO", call("GET", "/donadores/" + d, null).path("estado").asText());
  }

  @Test
  void pendingAssignmentsPreventDeletingNeedOrEntity() throws Exception {
    String e = call("POST", "/entidades", entity()).path("id").asText();
    String n = call("POST", "/necesidades", need(e, "EXTRAORDINARIA")).path("id").asText();
    pendingNeeds.add(n);
    mvc.perform(delete("/necesidades/" + n))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                .isConflict());
    mvc.perform(delete("/entidades/" + e))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                .isConflict());
    assertEquals(n, call("GET", "/necesidades/" + n, null).path("id").asText());
    pendingNeeds.clear();
    call("DELETE", "/necesidades/" + n, null);
    call("DELETE", "/entidades/" + e, null);
  }

  @Test
  void deliveryReceiptsCommitOnceAndRejectChangedPayload() throws Exception {
    String e = call("POST", "/entidades", entity()).path("id").asText();
    String n = call("POST", "/necesidades", need(e, "EXTRAORDINARIA")).path("id").asText();
    for (int i = 0; i < 3; i++)
      mvc.perform(
              post("/necesidades/" + n + "/satisfaccion?cantidadASatisfacer=3")
                  .header("Idempotency-Key", "delivery-" + n))
          .andExpect(
              org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
    assertEquals(3, call("GET", "/necesidades/" + n, null).path("cantidadAsignada").asInt());
    mvc.perform(
            post("/necesidades/" + n + "/satisfaccion?cantidadASatisfacer=4")
                .header("Idempotency-Key", "delivery-" + n))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                .isConflict());
    String r = call("POST", "/necesidades", need(e, "RECURRENTE")).path("id").asText();
    for (int i = 0; i < 2; i++)
      mvc.perform(
              post("/necesidades/" + r + "/satisfaccion?cantidadASatisfacer=10")
                  .header("Idempotency-Key", "delivery-" + r))
          .andExpect(
              org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
    assertEquals(10, call("GET", "/necesidades/" + r, null).path("cantidadAsignada").asInt());
  }
}
