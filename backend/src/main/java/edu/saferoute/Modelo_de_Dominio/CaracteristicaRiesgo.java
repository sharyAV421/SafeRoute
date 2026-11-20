package edu.saferoute.Modelo_de_Dominio;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("caracteristicas_riesgo")
public record CaracteristicaRiesgo(@Id String id, String espacioId, TipoCaracteristicaRiesgo tipo, String descripcion) {}
