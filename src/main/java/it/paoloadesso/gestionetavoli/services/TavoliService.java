package it.paoloadesso.gestionetavoli.services;

import it.paoloadesso.gestionetavoli.dto.CreaTavoliDTO;
import it.paoloadesso.gestionetavoli.dto.TavoliDTO;
import it.paoloadesso.gestionetavoli.entities.TavoliEntity;
import it.paoloadesso.gestionetavoli.enums.StatoTavolo;
import it.paoloadesso.gestionetavoli.mapper.TavoliMapper;
import it.paoloadesso.gestionetavoli.repositories.TavoliRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TavoliService {

    private final TavoliRepository tavoliRepository;
    private final TavoliMapper tavoliMapper;


    public TavoliService(TavoliRepository tavoliRepository, TavoliMapper tavoliMapper) {
        this.tavoliRepository = tavoliRepository;
        this.tavoliMapper = tavoliMapper;
    }

    @Transactional
    public TavoliDTO creaTavolo(CreaTavoliDTO dto) {
        if (tavoliRepository.existsByNumeroNomeTavoloIgnoreCase(dto.getNumeroNomeTavolo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esiste già un tavolo con numero/nome «" + dto.getNumeroNomeTavolo() + "»"
            );
        }

        TavoliEntity tavolo = tavoliRepository.save(tavoliMapper.createTavoliDtoToEntity(dto));
        return tavoliMapper.simpleEntityToDto(tavolo);
    }

    public List<TavoliDTO> getTavoli() {
        List<TavoliEntity> listaTavoli = tavoliRepository.findAll();
        List<TavoliDTO> tavoliResponseDto;
        tavoliResponseDto = listaTavoli.stream()
                .map(el -> tavoliMapper.simpleEntityToDto(el))
                .toList();
        return tavoliResponseDto;
    }

    public List<TavoliDTO> getTavoliLiberi() {
        List<TavoliEntity> listaTavoli = tavoliRepository.findByStatoTavolo(StatoTavolo.LIBERO);
        List<TavoliDTO> tavoliResponseDto;
        tavoliResponseDto = listaTavoli.stream()
                .map(el -> tavoliMapper.simpleEntityToDto(el))
                .toList();
        return tavoliResponseDto;
    }

    @Transactional
    public TavoliDTO aggiornaTavolo(Long id, TavoliDTO dtoTavolo) {
        if (!tavoliRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Tavolo con ID " + id + " non trovato.");
        }

        // Uso il mapper per convertire DTO → Entity
        TavoliEntity tavolo = tavoliMapper.dtoToEntity(dtoTavolo);
        tavolo.setId(id);  // Setto l'ID manualmente

        TavoliEntity tavoloAggiornato = tavoliRepository.save(tavolo);
        return tavoliMapper.simpleEntityToDto(tavoloAggiornato);
    }

    @Transactional
    public void eliminaTavoloByIdERelativiOrdiniCollegati(Long idTavolo) {
        // Cerco il tavolo e se non c'è restituisco relativo messaggio
        TavoliEntity tavolo = tavoliRepository.findById(idTavolo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tavolo con ID " + idTavolo + " non trovato."));
        // Elimino il tavolo trovato
        tavoliRepository.delete(tavolo);
    }
}
