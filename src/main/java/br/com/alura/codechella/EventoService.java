package br.com.alura.codechella;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EventoService {

    @Autowired
    private EventoRepository repositorio;

    public Flux<EventoDto> obterTodos() {
        return repositorio.findAll().map(EventoDto::toDto);
    }

    public Mono<EventoDto> obterPorId(Long id) {
        return repositorio.findById(id).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(EventoDto::toDto);
    }

    public Mono<EventoDto> cadastrar(EventoDto dto) {
        return repositorio.save(dto.toEntity())
                .map(EventoDto::toDto);
    }

    public Mono<Void> excluir(Long id) {
        return repositorio.findById(id)
                .flatMap(repositorio::delete);
    }

    public Mono<EventoDto> atualizar(EventoDto dto) {

        return repositorio.save(dto.toEntity())
                .map(EventoDto::toDto);
    }

    public Mono<EventoDto> atualizar(Long id, EventoDto dto) {

        return repositorio.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Id do evento não encontrado.")))
                .flatMap(evento -> {
                    evento.setTipo(dto.tipo());
                    evento.setNome(dto.nome());
                    evento.setData(dto.data());
                    evento.setDescricao(dto.descricao());
                    return repositorio.save(evento);
                })
                .map(EventoDto::toDto);
    }

    public Flux<EventoDto> obterPorTipo(String tipo) {
        TipoEvento tipoEvento = TipoEvento.valueOf(tipo.toUpperCase());
        return repositorio.findByTipo(tipoEvento).map(EventoDto::toDto);
    }

    public Mono<String> obterTraducao(Long id, String idioma) {

        return repositorio.findById(id)
                .flatMap(e ->TraducaoDeTextos.obterTraducao(e.getDescricao(), idioma));
    }
}
