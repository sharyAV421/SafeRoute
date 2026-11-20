package edu.saferoute.Modelo_de_Dominio;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("edificios")
public record Edificio(@Id String id, String institucionId, String nombre, String descripcion) {}
