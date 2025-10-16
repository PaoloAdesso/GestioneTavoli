package it.paoloadesso.gestionetavoli.controllers;

import it.paoloadesso.gestionetavoli.dto.TavoliDTO;
import it.paoloadesso.gestionetavoli.dto.TavoloApertoConDettagliOrdineDTO;
import it.paoloadesso.gestionetavoli.dto.TavoloApertoDTO;
import it.paoloadesso.gestionetavoli.dto.TavoloEliminatoDTO;
import it.paoloadesso.gestionetavoli.services.CassaService;
import it.paoloadesso.gestionetavoli.services.OrdineService;
import it.paoloadesso.gestionetavoli.services.TavoliService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cassa")
@Validated
public class CassaController {

    private final CassaService cassaService;
    private final TavoliService tavoliService;

    public CassaController(CassaService cassaService, TavoliService tavoliService) {
        this.cassaService = cassaService;
        this.tavoliService = tavoliService;
    }

    @GetMapping("/tavoli-aperti")
    public ResponseEntity<List<TavoloApertoDTO>> getTavoliAperti() {
        return ResponseEntity.ok(cassaService.getTavoliAperti());
    }

    @GetMapping("/dettaglio-tavoli-aperti")
    public ResponseEntity<List<TavoloApertoConDettagliOrdineDTO>> getTavoliApertiConDettagliOrdini() {
        return ResponseEntity.ok(cassaService.getTavoliApertiConDettagliOrdini());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TavoliDTO> aggiornaTavolo(
            @PathVariable Long id,
            @RequestBody @Valid TavoliDTO tavolo
    ) {
        return ResponseEntity.ok(tavoliService.aggiornaTavolo(id, tavolo));
    }

    @DeleteMapping("/{idTavolo}")
    public ResponseEntity<Void> deleteTavolo(@PathVariable Long idTavolo){
        tavoliService.eliminaTavoloByIdERelativiOrdiniCollegati(idTavolo);
        return ResponseEntity.noContent().build();
    }

}
