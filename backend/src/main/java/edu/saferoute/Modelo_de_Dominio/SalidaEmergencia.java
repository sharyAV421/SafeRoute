package edu.saferoute.Modelo_de_Dominio;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("salidas_emergencia")
public record SalidaEmergencia(@Id String id, String espacioId, String nombre, String ubicacion, EstadoDisponibilidad estado) {}
