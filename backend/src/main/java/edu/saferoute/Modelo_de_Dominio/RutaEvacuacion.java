package edu.saferoute.Modelo_de_Dominio;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("rutas")
public record RutaEvacuacion(@Id String id, String origenEspacioId, String destinoEspacioId,
                             String destinoNombre, String tipoDestino, double distanciaTotal, int cantidadEspacios,
                             String tiempoEstimado, List<PasoRuta> pasos) {}
