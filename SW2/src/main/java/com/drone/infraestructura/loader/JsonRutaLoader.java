package com.drone.infraestructura.loader;

import com.drone.dominio.strategy.PuntoGeografico;
import com.drone.dominio.strategy.Ruta;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Cargador de infraestructura encargado de leer y parsear las rutas
 * geograficas definidas en formato JSON mediante la libreria Jackson.
 */
public class JsonRutaLoader {

    private final ObjectMapper objectMapper;

    public JsonRutaLoader() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Carga y parsea la lista de rutas desde un archivo JSON ubicado en los
     * recursos del proyecto o en el sistema de archivos.
     *
     * @param rutaRecurso nombre o ruta del recurso (por ejemplo: "ruta.json")
     * @return lista de objetos {@link Ruta}
     * @throws Exception si el archivo no existe o ocurre un error de parseo
     */
    public List<Ruta> cargarRutas(String rutaRecurso) throws Exception {
        List<Ruta> lista = new ArrayList<>();
        InputStream is = null;

        String recursoNormalizado = rutaRecurso.startsWith("/") ? rutaRecurso : "/" + rutaRecurso;
        is = getClass().getResourceAsStream(recursoNormalizado);

        if (is == null) {
            String relativo = recursoNormalizado.substring(1);
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(relativo);
        }

        if (is == null) {
            File f = new File("src/main/resources/" + rutaRecurso.replaceFirst("^/", ""));
            if (f.exists()) {
                is = new FileInputStream(f);
            }
        }

        if (is == null) {
            throw new Exception("No se pudo localizar el archivo JSON de rutas: " + rutaRecurso);
        }

        try (InputStream input = is) {
            JsonNode root = objectMapper.readTree(input);
            JsonNode rutasNode = root.path("rutas");

            if (rutasNode.isArray()) {
                for (JsonNode rNode : rutasNode) {
                    String id = rNode.path("id").asText();
                    String nombre = rNode.path("nombre").asText();

                    JsonNode origNode = rNode.path("origen");
                    PuntoGeografico origen = new PuntoGeografico(
                            origNode.path("nombre").asText("Origen"),
                            origNode.path("latitud").asDouble(),
                            origNode.path("longitud").asDouble()
                    );

                    JsonNode destNode = rNode.path("destino");
                    PuntoGeografico destino = new PuntoGeografico(
                            destNode.path("nombre").asText("Destino"),
                            destNode.path("latitud").asDouble(),
                            destNode.path("longitud").asDouble()
                    );

                    lista.add(new Ruta(id, nombre, origen, destino));
                }
            }
        }

        return lista;
    }
}
