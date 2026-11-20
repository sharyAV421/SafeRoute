package edu.saferoute.Presentacion_Controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import edu.saferoute.Datos_Repository.EspacioRepository;
import edu.saferoute.Modelo_de_Dominio.*;
import edu.saferoute.Negocio_Service.RutaService;

class RutaControllerIntegrationTest {
    private RutaService service; private EspacioRepository espacios; private MockMvc mvc;
    @BeforeEach void setup(){
        service=mock(RutaService.class);espacios=mock(EspacioRepository.class);
        mvc=MockMvcBuilders.standaloneSetup(new RutaController(service,espacios)).build();
    }
    @Test void routeRequestReturnsStepsAndPendingTime()throws Exception{
        when(service.calcular("A")).thenReturn(new RutaEvacuacion("R","A","B","Salida","SALIDA",12,2,"No disponible",List.of(new PasoRuta("1",1,0,"A"),new PasoRuta("2",2,12,"B"))));
        when(espacios.findById("A")).thenReturn(Optional.of(new Espacio("A","P","Aula","AULA",20,NivelRiesgo.BAJO,EstadoEspacio.DISPONIBLE)));
        when(espacios.findById("B")).thenReturn(Optional.of(new Espacio("B","P","Salida","SALIDA",0,NivelRiesgo.BAJO,EstadoEspacio.DISPONIBLE)));
        mvc.perform(post("/api/rutas").contentType(MediaType.APPLICATION_JSON).content("{\"origenEspacioId\":\"A\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.distanciaTotal").value(12))
                .andExpect(jsonPath("$.tiempoEstimado").value("No disponible"))
                .andExpect(jsonPath("$.pasos[0].nombreEspacio").value("Aula"));
    }
}
