package edu.saferoute.Negocio_Service;

import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.saferoute.Datos_Repository.*;
import edu.saferoute.Modelo_de_Dominio.*;

@Service
@Transactional
public class RutaService {
    private final EspacioRepository espacios; private final ConexionRepository conexiones;
    private final SalidaEmergenciaRepository salidas; private final PuntoEncuentroRepository puntos;
    private final RutaRepository rutas;
    public RutaService(EspacioRepository espacios, ConexionRepository conexiones, SalidaEmergenciaRepository salidas,
                       PuntoEncuentroRepository puntos, RutaRepository rutas) {
        this.espacios=espacios;this.conexiones=conexiones;this.salidas=salidas;this.puntos=puntos;this.rutas=rutas;
    }
    public RutaEvacuacion calcular(String origenId) {
        Espacio origen=espacios.findById(origenId).orElseThrow(()->new RecursoNoEncontrado("Espacio de origen no encontrado"));
        if (origen.estado()==EstadoEspacio.BLOQUEADO) throw new ReglaNegocioException("El espacio de origen está bloqueado");
        Map<String,Espacio> nodos=new HashMap<>(); espacios.findAll().forEach(e->nodos.put(e.id(),e));
        List<Destino> destinos=new ArrayList<>();
        salidas.findByEstado(EstadoDisponibilidad.DISPONIBLE).forEach(d->{if(nodos.containsKey(d.espacioId()))destinos.add(new Destino(d.espacioId(),d.nombre(),"SALIDA"));});
        puntos.findByEstado(EstadoDisponibilidad.DISPONIBLE).forEach(d->{if(nodos.containsKey(d.espacioId()))destinos.add(new Destino(d.espacioId(),d.nombre(),"PUNTO_ENCUENTRO"));});
        Map<String,List<Arista>> grafo=new HashMap<>();
        for (Conexion c: conexiones.findAll()) {
            if(!Double.isFinite(c.distanciaMetros())||c.distanciaMetros()<=0)
                throw new ReglaNegocioException("La conexión "+c.id()+" no tiene una distancia válida en metros");
            if(c.bloqueada() || !nodos.containsKey(c.origenEspacioId()) || !nodos.containsKey(c.destinoEspacioId())) continue;
            Espacio a=nodos.get(c.origenEspacioId()), b=nodos.get(c.destinoEspacioId());
            if(a.estado()==EstadoEspacio.BLOQUEADO||b.estado()==EstadoEspacio.BLOQUEADO) continue;
            grafo.computeIfAbsent(a.id(),k->new ArrayList<>()).add(new Arista(b.id(),c.distanciaMetros()));
            grafo.computeIfAbsent(b.id(),k->new ArrayList<>()).add(new Arista(a.id(),c.distanciaMetros()));
        }
        grafo.values().forEach(as->as.sort(Comparator.comparing(Arista::destino)));
        Map<String,Mejor> mejor=new HashMap<>(); PriorityQueue<Estado> cola=new PriorityQueue<>((a,b)->{
            int c=comparar(a.mejor(),b.mejor()); return c!=0?c:a.id().compareTo(b.id());
        });
        Mejor inicial=new Mejor(0,0,List.of(origenId)); mejor.put(origenId,inicial); cola.add(new Estado(origenId,inicial));
        while(!cola.isEmpty()) {
            Estado actual=cola.poll(); if(!actual.mejor().equals(mejor.get(actual.id()))) continue;
            for(Arista arista:grafo.getOrDefault(actual.id(),List.of())) {
                List<String> camino=new ArrayList<>(actual.mejor().camino()); camino.add(arista.destino());
                Mejor candidato=new Mejor(actual.mejor().distancia()+arista.distanciaMetros(),actual.mejor().conexiones()+1,List.copyOf(camino));
                Mejor previo=mejor.get(arista.destino());
                if(previo==null||comparar(candidato,previo)<0) { mejor.put(arista.destino(),candidato); cola.add(new Estado(arista.destino(),candidato)); }
            }
        }
        Optional<DestinoMejor> elegido=destinos.stream().filter(d->mejor.containsKey(d.espacioId()))
                .map(d->new DestinoMejor(d,mejor.get(d.espacioId())))
                .min((a,b)->{int c=comparar(a.mejor(),b.mejor());return c!=0?c:a.destino().espacioId().compareTo(b.destino().espacioId());});
        if(elegido.isEmpty()) throw new RecursoNoEncontrado("No se encontró una ruta a una salida o punto de encuentro disponible");
        DestinoMejor fin=elegido.get(); List<String> ids=fin.mejor().camino();
        List<PasoRuta> pasos=new ArrayList<>();double acumulada=0;
        for(int i=0;i<ids.size();i++){
            if(i>0){String prev=ids.get(i-1),next=ids.get(i);acumulada+=grafo.getOrDefault(prev,List.of()).stream().filter(a->a.destino().equals(next)).findFirst().orElseThrow().distanciaMetros();}
            pasos.add(new PasoRuta(java.util.UUID.randomUUID().toString(),i+1,acumulada,ids.get(i)));
        }
        RutaEvacuacion ruta=new RutaEvacuacion(null,origenId,fin.destino().espacioId(),fin.destino().nombre(),fin.destino().tipo(),fin.mejor().distancia(),ids.size(),"No disponible",pasos);
        return rutas.save(ruta);
    }
    private int comparar(Mejor a,Mejor b) {
        int c=Double.compare(a.distancia(),b.distancia()); if(c!=0)return c;
        c=Integer.compare(a.conexiones(),b.conexiones()); if(c!=0)return c;
        int n=Math.min(a.camino().size(),b.camino().size());
        for(int i=0;i<n;i++){c=a.camino().get(i).compareTo(b.camino().get(i));if(c!=0)return c;}
        return Integer.compare(a.camino().size(),b.camino().size());
    }
    private record Arista(String destino,double distanciaMetros) {}
    private record Mejor(double distancia,int conexiones,List<String> camino) {}
    private record Estado(String id,Mejor mejor) {}
    private record Destino(String espacioId,String nombre,String tipo) {}
    private record DestinoMejor(Destino destino,Mejor mejor) {}
}
