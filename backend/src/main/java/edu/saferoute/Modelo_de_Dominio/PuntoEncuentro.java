package edu.saferoute.Modelo_de_Dominio;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("puntos_encuentro")
public record PuntoEncuentro(@Id String id, String espacioId, String nombre, String ubicacion, int capacidad, EstadoDisponibilidad estado) {}
