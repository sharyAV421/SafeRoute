package edu.saferoute.Datos_Repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import edu.saferoute.Modelo_de_Dominio.Institucion;
import edu.saferoute.Modelo_de_Dominio.Conexion;
import edu.saferoute.Modelo_de_Dominio.*;

/**
 * Opt-in integration test against a dedicated MongoDB test database.
 * Set MONGODB_TEST_URI to run it; use a database isolated from application data.
 */
@DataMongoTest
@EnabledIfEnvironmentVariable(named = "MONGODB_TEST_URI", matches = ".+")
class MongoPersistenceIntegrationTest {
    @Autowired private InstitucionRepository instituciones;
    @Autowired private ConexionRepository conexiones;
    @Autowired private EdificioRepository edificios;
    @Autowired private PisoRepository pisos;
    @Autowired private EspacioRepository espacios;
    @Autowired private SalidaEmergenciaRepository salidas;
    @Autowired private PuntoEncuentroRepository puntos;
    @Autowired private EmergenciaRepository emergencias;
    @Autowired private CaracteristicaRiesgoRepository riesgos;
    @Autowired private UsuarioRepository usuarios;
    @Autowired private RutaRepository rutas;

    @DynamicPropertySource
    static void mongoUri(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> System.getenv("MONGODB_TEST_URI"));
    }

    @Test
    void persistsAndReadsInstitutionUsingSpringDataMongo() {
        String nombre = "Prueba de persistencia " + UUID.randomUUID();
        Institucion saved = instituciones.save(new Institucion(null, nombre, "Prueba de integración"));
        try {
            assertTrue(saved.id() != null && !saved.id().isBlank());
            assertEquals(nombre, instituciones.findById(saved.id()).orElseThrow().nombre());
        } finally {
            instituciones.deleteById(saved.id());
        }
    }

    @Test
    void persistsConnectionEndpointsDistanceAndBlockStatus() {
        String id="mongo-test-"+UUID.randomUUID();
        Conexion expected=new Conexion(id,"space-a","space-b",12.5,true);
        conexiones.save(expected);
        try {
            Conexion actual=conexiones.findById(id).orElseThrow();
            assertEquals("space-a",actual.origenEspacioId());
            assertEquals("space-b",actual.destinoEspacioId());
            assertEquals(12.5,actual.distanciaMetros());
            assertTrue(actual.bloqueada());
        } finally {
            conexiones.deleteById(id);
        }
    }

    @Test
    void persistsAllDomainCollectionsAndEmbeddedRouteAndAffectedZone() {
        String suffix=UUID.randomUUID().toString();
        String institutionId="it-institution-"+suffix,buildingId="it-building-"+suffix,floorId="it-floor-"+suffix;
        String spaceId="it-space-"+suffix,connectionId="it-connection-"+suffix,exitId="it-exit-"+suffix;
        String meetingId="it-meeting-"+suffix,eventId="it-event-"+suffix,riskId="it-risk-"+suffix;
        String userId="it-user-"+suffix,routeId="it-route-"+suffix;
        try {
            instituciones.save(new Institucion(institutionId,"Institución", "Persistencia"));
            edificios.save(new Edificio(buildingId,institutionId,"Edificio",""));
            pisos.save(new Piso(floorId,buildingId,"Piso 1",1));
            espacios.save(new Espacio(spaceId,floorId,"Aula","AULA",30,NivelRiesgo.MEDIO,EstadoEspacio.DISPONIBLE));
            conexiones.save(new Conexion(connectionId,spaceId,"space-other",14.0,false));
            salidas.save(new SalidaEmergencia(exitId,spaceId,"Salida","Exterior",EstadoDisponibilidad.DISPONIBLE));
            puntos.save(new PuntoEncuentro(meetingId,spaceId,"Encuentro","Patio",100,EstadoDisponibilidad.DISPONIBLE));
            emergencias.save(new Emergencia(eventId,TipoEmergencia.SISMO,GravedadEmergencia.MEDIA,"Prueba",EstadoEmergencia.ACTIVA,
                    java.time.LocalDateTime.now(),List.of(new ZonaAfectada("zone-"+suffix,spaceId,EstadoEspacio.AFECTADO,"Sector"))));
            riesgos.save(new CaracteristicaRiesgo(riskId,spaceId,TipoCaracteristicaRiesgo.ALTA_OCUPACION,"Ocupación declarada"));
            usuarios.save(new Usuario(userId,"Prueba","mongo-test-"+suffix+"@example.edu","hash",Rol.USUARIO_EVACUADO));
            rutas.save(new RutaEvacuacion(routeId,spaceId,"space-other","Salida","SALIDA",14.0,2,"No disponible",
                    List.of(new PasoRuta("step-"+suffix,1,0,spaceId))));

            assertEquals(institutionId,instituciones.findById(institutionId).orElseThrow().id());
            assertEquals(buildingId,edificios.findById(buildingId).orElseThrow().id());
            assertEquals(floorId,pisos.findById(floorId).orElseThrow().id());
            assertEquals(NivelRiesgo.MEDIO,espacios.findById(spaceId).orElseThrow().nivelRiesgo());
            assertEquals(14.0,conexiones.findById(connectionId).orElseThrow().distanciaMetros());
            assertEquals(EstadoDisponibilidad.DISPONIBLE,salidas.findById(exitId).orElseThrow().estado());
            assertEquals(100,puntos.findById(meetingId).orElseThrow().capacidad());
            assertEquals(EstadoEspacio.AFECTADO,emergencias.findById(eventId).orElseThrow().zonasAfectadas().getFirst().estado());
            assertEquals(TipoCaracteristicaRiesgo.ALTA_OCUPACION,riesgos.findById(riskId).orElseThrow().tipo());
            assertEquals(Rol.USUARIO_EVACUADO,usuarios.findById(userId).orElseThrow().rol());
            assertEquals("No disponible",rutas.findById(routeId).orElseThrow().tiempoEstimado());
        } finally {
            rutas.deleteById(routeId); usuarios.deleteById(userId); riesgos.deleteById(riskId); emergencias.deleteById(eventId);
            puntos.deleteById(meetingId); salidas.deleteById(exitId); conexiones.deleteById(connectionId); espacios.deleteById(spaceId);
            pisos.deleteById(floorId); edificios.deleteById(buildingId); instituciones.deleteById(institutionId);
        }
    }
}
