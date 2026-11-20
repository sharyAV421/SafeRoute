package edu.saferoute.Datos_Repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import edu.saferoute.Modelo_de_Dominio.CaracteristicaRiesgo;

public interface CaracteristicaRiesgoRepository extends MongoRepository<CaracteristicaRiesgo, String> {
    List<CaracteristicaRiesgo> findByEspacioId(String espacioId);
}
