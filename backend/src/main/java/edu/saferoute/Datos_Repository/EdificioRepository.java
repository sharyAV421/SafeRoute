package edu.saferoute.Datos_Repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import edu.saferoute.Modelo_de_Dominio.Edificio;

public interface EdificioRepository extends MongoRepository<Edificio, String> {
    List<Edificio> findByInstitucionId(String institucionId);
}
