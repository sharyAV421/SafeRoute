package edu.saferoute.Presentacion_Controller;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import edu.saferoute.Negocio_Service.*;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(RecursoNoEncontrado.class) public ResponseEntity<?> missing(RecursoNoEncontrado e){return error(HttpStatus.NOT_FOUND,e.getMessage());}
 @ExceptionHandler(ReglaNegocioException.class) public ResponseEntity<?> rule(ReglaNegocioException e){return error(HttpStatus.BAD_REQUEST,e.getMessage());}
 @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<?> validation(MethodArgumentNotValidException e){return error(HttpStatus.BAD_REQUEST,"La solicitud contiene campos inválidos");}
 private ResponseEntity<?> error(HttpStatus s,String m){return ResponseEntity.status(s).body(Map.of("timestamp",Instant.now().toString(),"status",s.value(),"error",m));}
}
