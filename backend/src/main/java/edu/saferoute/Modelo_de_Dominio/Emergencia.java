package edu.saferoute.Modelo_de_Dominio;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("emergencias")
public record Emergencia(@Id String id, TipoEmergencia tipo, GravedadEmergencia gravedad, String descripcion,
                         EstadoEmergencia estado, LocalDateTime fechaHora, List<ZonaAfectada> zonasAfectadas) {}
