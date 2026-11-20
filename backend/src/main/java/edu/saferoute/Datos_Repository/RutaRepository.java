package edu.saferoute.Datos_Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import edu.saferoute.Modelo_de_Dominio.RutaEvacuacion;

public interface RutaRepository extends MongoRepository<RutaEvacuacion, String> {}
