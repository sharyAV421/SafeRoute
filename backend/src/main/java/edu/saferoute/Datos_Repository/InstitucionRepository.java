package edu.saferoute.Datos_Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import edu.saferoute.Modelo_de_Dominio.Institucion;

public interface InstitucionRepository extends MongoRepository<Institucion, String> {}
