package edu.saferoute.Datos_Repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import edu.saferoute.Modelo_de_Dominio.PuntoEncuentro;

public interface PuntoEncuentroRepository extends MongoRepository<PuntoEncuentro, String> {
    List<PuntoEncuentro> findByEstado(edu.saferoute.Modelo_de_Dominio.EstadoDisponibilidad estado);
}
