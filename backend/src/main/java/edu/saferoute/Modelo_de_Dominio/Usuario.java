package edu.saferoute.Modelo_de_Dominio;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("usuarios")
public record Usuario(@Id String id, String nombre, String correo, String passwordHash, Rol rol) {}
