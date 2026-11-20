package edu.saferoute.Datos_Repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import edu.saferoute.Modelo_de_Dominio.Piso;

public interface PisoRepository extends MongoRepository<Piso, String> {
    List<Piso> findByEdificioId(String edificioId);
}
