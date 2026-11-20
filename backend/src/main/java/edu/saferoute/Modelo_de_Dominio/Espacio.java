package edu.saferoute.Modelo_de_Dominio;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("espacios")
public record Espacio(@Id String id, String pisoId, String nombre, String tipo, int capacidad,
                      NivelRiesgo nivelRiesgo, EstadoEspacio estado) {}
