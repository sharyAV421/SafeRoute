package edu.saferoute.Negocio_Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import edu.saferoute.Datos_Repository.*;
import edu.saferoute.Modelo_de_Dominio.*;

class InfraestructuraServiceTest {
    private final InstitucionRepository instituciones=mock(InstitucionRepository.class);
    private final EdificioRepository edificios=mock(EdificioRepository.class);
    private final PisoRepository pisos=mock(PisoRepository.class);
    private final EspacioRepository espacios=mock(EspacioRepository.class);
    private final ConexionRepository conexiones=mock(ConexionRepository.class);
    private final SalidaEmergenciaRepository salidas=mock(SalidaEmergenciaRepository.class);
    private final PuntoEncuentroRepository puntos=mock(PuntoEncuentroRepository.class);
    private final InfraestructuraService service=new InfraestructuraService(instituciones,edificios,pisos,espacios,conexiones,salidas,puntos);

    @Test
    void editingInstitutionPreservesItsIdentifier() {
        when(instituciones.findById("I1")).thenReturn(java.util.Optional.of(new Institucion("I1","Nombre anterior","Descripción anterior")));
        when(instituciones.save(any())).thenAnswer(invocation->invocation.getArgument(0));

        Institucion updated=service.actualizarInstitucion("I1",new Institucion(null,"Nombre nuevo","Descripción nueva"));

        assertEquals("I1",updated.id());
        assertEquals("Nombre nuevo",updated.nombre());
    }

    @Test
    void editingSpacePreservesItsHierarchyAndBlockState() {
        Espacio original=new Espacio("S1","P1","Aula anterior","AULA",20,NivelRiesgo.BAJO,EstadoEspacio.BLOQUEADO);
        when(espacios.findById("S1")).thenReturn(java.util.Optional.of(original));
        when(espacios.save(any())).thenAnswer(invocation->invocation.getArgument(0));

        Espacio updated=service.actualizarEspacio("S1",new Espacio(null,null,"Aula nueva","LABORATORIO",18,NivelRiesgo.ALTO,null));

        assertEquals("P1",updated.pisoId());
        assertEquals("Aula nueva",updated.nombre());
        assertEquals(NivelRiesgo.ALTO,updated.nivelRiesgo());
        assertEquals(EstadoEspacio.BLOQUEADO,updated.estado());
    }

    @Test
    void destinationAvailabilityCanBeChangedWithoutChangingItsAssociation() {
        when(salidas.findById("E1")).thenReturn(java.util.Optional.of(new SalidaEmergencia("E1","S1","Salida","Patio",EstadoDisponibilidad.DISPONIBLE)));
        when(salidas.save(any())).thenAnswer(invocation->invocation.getArgument(0));

        SalidaEmergencia updated=service.actualizarDisponibilidadSalida("E1",EstadoDisponibilidad.NO_DISPONIBLE);

        assertEquals("S1",updated.espacioId());
        assertEquals(EstadoDisponibilidad.NO_DISPONIBLE,updated.estado());
    }
}
