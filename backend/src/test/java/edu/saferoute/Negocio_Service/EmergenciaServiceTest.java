package edu.saferoute.Negocio_Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.saferoute.Datos_Repository.EmergenciaRepository;
import edu.saferoute.Datos_Repository.EspacioRepository;
import edu.saferoute.Modelo_de_Dominio.*;

class EmergenciaServiceTest {
    private final EmergenciaRepository emergencias=mock(EmergenciaRepository.class);
    private final EspacioRepository espacios=mock(EspacioRepository.class);
    private final EmergenciaService service=new EmergenciaService(emergencias,espacios);

    @Test
    void editingEmergencyDataPreservesStateDateAndAffectedZones() {
        LocalDateTime fecha=LocalDateTime.now();
        List<ZonaAfectada> zones=List.of(new ZonaAfectada("Z1","S1",EstadoEspacio.AFECTADO,"Sector"));
        Emergencia original=new Emergencia("EM1",TipoEmergencia.INCENDIO,GravedadEmergencia.ALTA,"Anterior",EstadoEmergencia.ACTIVA,fecha,zones);
        when(emergencias.findById("EM1")).thenReturn(java.util.Optional.of(original));
        when(emergencias.save(any())).thenAnswer(invocation->invocation.getArgument(0));

        Emergencia updated=service.actualizarDatos("EM1",TipoEmergencia.SISMO,GravedadEmergencia.MEDIA,"Actualizada");

        assertEquals(TipoEmergencia.SISMO,updated.tipo());
        assertEquals("Actualizada",updated.descripcion());
        assertEquals(EstadoEmergencia.ACTIVA,updated.estado());
        assertEquals(fecha,updated.fechaHora());
        assertEquals(zones,updated.zonasAfectadas());
    }

    @Test
    void updatingAffectedZonesDoesNotChangeSpaceBlockState() {
        Emergencia original=new Emergencia("EM1",TipoEmergencia.INCENDIO,GravedadEmergencia.ALTA,"Descripción",
                EstadoEmergencia.ACTIVA,LocalDateTime.now(),List.of(new ZonaAfectada("Z1","S1",EstadoEspacio.AFECTADO,null)));
        when(emergencias.findById("EM1")).thenReturn(java.util.Optional.of(original));
        when(espacios.findAllById(List.of("S2"))).thenReturn(List.of(new Espacio("S2","P1","Sala","AULA",10,NivelRiesgo.BAJO,EstadoEspacio.DISPONIBLE)));
        when(emergencias.save(any())).thenAnswer(invocation->invocation.getArgument(0));

        Emergencia updated=service.actualizarZonasAfectadas("EM1",List.of("S2","S2"));

        assertEquals(EstadoEmergencia.ACTIVA,updated.estado());
        assertEquals(List.of("S2"),updated.zonasAfectadas().stream().map(ZonaAfectada::espacioId).toList());
        assertEquals(EstadoEspacio.AFECTADO,updated.zonasAfectadas().getFirst().estado());
        verify(espacios,never()).save(any());
    }
}
