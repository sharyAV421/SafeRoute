package edu.saferoute.Negocio_Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import edu.saferoute.Datos_Repository.*;
import edu.saferoute.Modelo_de_Dominio.*;

class PrioridadServiceTest {
    @Test void doesNotInventPriorityWhileFormulaIsPending(){
        var emergencies=mock(EmergenciaRepository.class);var spaces=mock(EspacioRepository.class);var risks=mock(CaracteristicaRiesgoRepository.class);
        when(emergencies.findById("E")).thenReturn(Optional.of(new Emergencia("E",TipoEmergencia.INCENDIO,GravedadEmergencia.ALTA,"desc",EstadoEmergencia.ACTIVA,LocalDateTime.now(),List.of(new ZonaAfectada("Z","S",EstadoEspacio.AFECTADO,null)))));
        when(spaces.existsById("S")).thenReturn(true);when(risks.findByEspacioId("S")).thenReturn(List.of());
        var result=new PrioridadService(emergencies,spaces,risks).consultar("E");
        assertEquals(1,result.size());assertFalse(result.getFirst().determinable());assertNull(result.getFirst().prioridad());
    }
}
