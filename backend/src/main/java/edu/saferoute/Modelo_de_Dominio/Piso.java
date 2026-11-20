package edu.saferoute.Modelo_de_Dominio;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("pisos")
public record Piso(@Id String id, String edificioId, String nombre, Integer nivel) {}
