package edu.saferoute.Modelo_de_Dominio;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("conexiones")
public record Conexion(@Id String id, String origenEspacioId, String destinoEspacioId, double distanciaMetros, boolean bloqueada) {}
