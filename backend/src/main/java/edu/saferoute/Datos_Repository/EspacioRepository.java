package edu.saferoute.Datos_Repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import edu.saferoute.Modelo_de_Dominio.Espacio;

public interface EspacioRepository extends MongoRepository<Espacio, String> {
    List<Espacio> findByPisoId(String pisoId);
}
