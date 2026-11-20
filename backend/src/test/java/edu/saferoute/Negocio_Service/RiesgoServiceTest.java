package edu.saferoute.Negocio_Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import edu.saferoute.Datos_Repository.CaracteristicaRiesgoRepository;
import edu.saferoute.Datos_Repository.EspacioRepository;
import edu.saferoute.Modelo_de_Dominio.CaracteristicaRiesgo;
import edu.saferoute.Modelo_de_Dominio.TipoCaracteristicaRiesgo;

class RiesgoServiceTest {
    private final CaracteristicaRiesgoRepository riesgos=mock(CaracteristicaRiesgoRepository.class);
    private final EspacioRepository espacios=mock(EspacioRepository.class);
    private final RiesgoService service=new RiesgoService(riesgos,espacios);

    @Test
    void savesCharacteristicLinkedToExistingSpace() {
        when(espacios.existsById("S1")).thenReturn(true);
        when(riesgos.save(any())).thenAnswer(invocation->{
            CaracteristicaRiesgo x=invocation.getArgument(0);
            return new CaracteristicaRiesgo("R1",x.espacioId(),x.tipo(),x.descripcion());
        });

        CaracteristicaRiesgo saved=service.crear(new CaracteristicaRiesgo(null,"S1",TipoCaracteristicaRiesgo.GAS,"Zona con tanque de gas"));

        assertEquals("S1",saved.espacioId());
        assertEquals(TipoCaracteristicaRiesgo.GAS,saved.tipo());
    }

    @Test
    void rejectsCharacteristicForUnknownSpace() {
        when(espacios.existsById("missing")).thenReturn(false);

        org.junit.jupiter.api.Assertions.assertThrows(ReglaNegocioException.class,
                ()->service.crear(new CaracteristicaRiesgo(null,"missing",TipoCaracteristicaRiesgo.GAS,"Descripción")));
    }
}
