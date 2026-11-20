package edu.saferoute.Datos_Repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import edu.saferoute.Modelo_de_Dominio.Conexion;

public interface ConexionRepository extends MongoRepository<Conexion, String> {
    List<Conexion> findByBloqueadaFalse();
}
