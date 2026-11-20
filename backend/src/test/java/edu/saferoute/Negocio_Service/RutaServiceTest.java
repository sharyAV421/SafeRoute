package edu.saferoute.Negocio_Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import edu.saferoute.Datos_Repository.*;
import edu.saferoute.Modelo_de_Dominio.*;

class RutaServiceTest {
    private final EspacioRepository espacios=mock(EspacioRepository.class);
    private final ConexionRepository conexiones=mock(ConexionRepository.class);
    private final SalidaEmergenciaRepository salidas=mock(SalidaEmergenciaRepository.class);
    private final PuntoEncuentroRepository puntos=mock(PuntoEncuentroRepository.class);
    private final RutaRepository rutas=mock(RutaRepository.class);
    private final RutaService service=new RutaService(espacios,conexiones,salidas,puntos,rutas);

    @Test void excluyeExplicitlyBlockedSpacesAndConnections() {
        when(espacios.findById("A")).thenReturn(java.util.Optional.of(space("A",false)));
        when(espacios.findAll()).thenReturn(List.of(space("A",false),space("B",true),space("D",false)));
        when(conexiones.findAll()).thenReturn(List.of(edge("A","B",1,false),edge("B","D",1,false),edge("A","D",5,false)));
        when(salidas.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of(new SalidaEmergencia("exit","D","Salida","Exterior",EstadoDisponibilidad.DISPONIBLE)));
        when(puntos.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of());
        when(rutas.save(any())).thenAnswer(i->i.getArgument(0));

        RutaEvacuacion route=service.calcular("A");

        assertEquals(List.of("A","D"),route.pasos().stream().map(PasoRuta::espacioId).toList());
        assertEquals(5,route.distanciaTotal());
        assertEquals("No disponible",route.tiempoEstimado());
    }

    @Test void resolvesEqualRoutesByConnectionCountThenStableIds() {
        when(espacios.findById("A")).thenReturn(java.util.Optional.of(space("A",false)));
        when(espacios.findAll()).thenReturn(List.of(space("A",false),space("B",false),space("C",false),space("D",false)));
        when(conexiones.findAll()).thenReturn(List.of(edge("A","C",1,false),edge("C","D",1,false),edge("A","B",1,false),edge("B","D",1,false)));
        when(salidas.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of(new SalidaEmergencia("exit","D","Salida","Exterior",EstadoDisponibilidad.DISPONIBLE)));
        when(puntos.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of());
        when(rutas.save(any())).thenAnswer(i->i.getArgument(0));

        RutaEvacuacion route=service.calcular("A");

        assertEquals(List.of("A","B","D"),route.pasos().stream().map(PasoRuta::espacioId).toList());
        assertEquals(3,route.cantidadEspacios());
    }

    @Test void traversesAnExplicitConnectionInBothDirections() {
        when(espacios.findById("A")).thenReturn(java.util.Optional.of(space("A",false)));
        when(espacios.findAll()).thenReturn(List.of(space("A",false),space("D",false)));
        when(conexiones.findAll()).thenReturn(List.of(edge("D","A",1,false)));
        when(salidas.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of(new SalidaEmergencia("exit","D","Salida","Exterior",EstadoDisponibilidad.DISPONIBLE)));
        when(puntos.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of());
        when(rutas.save(any())).thenAnswer(i->i.getArgument(0));

        RutaEvacuacion route=service.calcular("A");

        assertEquals(List.of("A","D"),route.pasos().stream().map(PasoRuta::espacioId).toList());
        assertEquals(1,route.distanciaTotal());
    }

    @Test void excludesAnExplicitlyBlockedConnection() {
        when(espacios.findById("A")).thenReturn(java.util.Optional.of(space("A",false)));
        when(espacios.findAll()).thenReturn(List.of(space("A",false),space("B",false),space("C",false),space("D",false)));
        when(conexiones.findAll()).thenReturn(List.of(edge("A","B",1,true),edge("B","D",1,false),edge("A","C",2,false),edge("C","D",2,false)));
        when(salidas.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of(new SalidaEmergencia("exit","D","Salida","Exterior",EstadoDisponibilidad.DISPONIBLE)));
        when(puntos.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of());
        when(rutas.save(any())).thenAnswer(i->i.getArgument(0));

        RutaEvacuacion route=service.calcular("A");

        assertEquals(List.of("A","C","D"),route.pasos().stream().map(PasoRuta::espacioId).toList());
        assertEquals("SALIDA",route.tipoDestino());
    }

    @Test void resolvesEqualDistanceByFewerConnections() {
        when(espacios.findById("A")).thenReturn(java.util.Optional.of(space("A",false)));
        when(espacios.findAll()).thenReturn(List.of(space("A",false),space("B",false),space("D",false)));
        when(conexiones.findAll()).thenReturn(List.of(edge("A","B",1,false),edge("B","D",1,false),edge("A","D",2,false)));
        when(salidas.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of(new SalidaEmergencia("exit","D","Salida","Exterior",EstadoDisponibilidad.DISPONIBLE)));
        when(puntos.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of());
        when(rutas.save(any())).thenAnswer(i->i.getArgument(0));

        RutaEvacuacion route=service.calcular("A");

        assertEquals(List.of("A","D"),route.pasos().stream().map(PasoRuta::espacioId).toList());
    }

    @Test void affectedSpaceRemainsTraversableUntilExplicitlyBlocked() {
        when(espacios.findById("A")).thenReturn(java.util.Optional.of(space("A",false)));
        when(espacios.findAll()).thenReturn(List.of(space("A",false),new Espacio("B","P","B","AULA",20,NivelRiesgo.BAJO,EstadoEspacio.AFECTADO),space("D",false)));
        when(conexiones.findAll()).thenReturn(List.of(edge("A","B",1,false),edge("B","D",1,false)));
        when(salidas.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of(new SalidaEmergencia("exit","D","Salida","Exterior",EstadoDisponibilidad.DISPONIBLE)));
        when(puntos.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of());
        when(rutas.save(any())).thenAnswer(i->i.getArgument(0));

        RutaEvacuacion route=service.calcular("A");

        assertEquals(List.of("A","B","D"),route.pasos().stream().map(PasoRuta::espacioId).toList());
    }

    @Test void rejectsPersistedConnectionWithInvalidMetricDistance() {
        when(espacios.findById("A")).thenReturn(java.util.Optional.of(space("A",false)));
        when(espacios.findAll()).thenReturn(List.of(space("A",false),space("D",false)));
        when(conexiones.findAll()).thenReturn(List.of(edge("A","D",Double.NaN,false)));

        assertThrows(ReglaNegocioException.class,()->service.calcular("A"));
    }

    @Test void ignoresUnavailableExitAndUsesAvailableMeetingPoint() {
        when(espacios.findById("A")).thenReturn(java.util.Optional.of(space("A",false)));
        when(espacios.findAll()).thenReturn(List.of(space("A",false),space("B",false),space("C",false)));
        when(conexiones.findAll()).thenReturn(List.of(edge("A","B",1,false),edge("A","C",3,false)));
        when(salidas.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of());
        when(puntos.findByEstado(EstadoDisponibilidad.DISPONIBLE)).thenReturn(List.of(
                new PuntoEncuentro("M1","C","Encuentro","Patio",80,EstadoDisponibilidad.DISPONIBLE)));
        when(rutas.save(any())).thenAnswer(i->i.getArgument(0));

        RutaEvacuacion route=service.calcular("A");

        assertEquals("PUNTO_ENCUENTRO",route.tipoDestino());
        assertEquals("C",route.destinoEspacioId());
    }

    private static Espacio space(String id,boolean blocked){return new Espacio(id,"P",id,"AULA",20,NivelRiesgo.BAJO,blocked?EstadoEspacio.BLOQUEADO:EstadoEspacio.DISPONIBLE);}
    private static Conexion edge(String a,String b,double d,boolean blocked){return new Conexion(a+b,a,b,d,blocked);}
}
