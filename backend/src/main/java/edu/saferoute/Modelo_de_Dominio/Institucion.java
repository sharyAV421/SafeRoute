package edu.saferoute.Modelo_de_Dominio;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("instituciones")
public record Institucion(@Id String id, String nombre, String descripcion) {}
