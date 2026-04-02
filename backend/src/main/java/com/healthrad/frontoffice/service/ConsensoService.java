package com.healthrad.frontoffice.service;

import com.healthrad.frontoffice.model.Cliente;
import com.healthrad.frontoffice.model.Consenso;
import com.healthrad.frontoffice.repository.ClienteRepository;
import com.healthrad.frontoffice.repository.ConsensoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ConsensoService {

    @Autowired
    private ConsensoRepository consensoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    public Consenso allegaConsenso(String cfCliente, String tipologia, MultipartFile filePdf) throws IOException {
        Cliente cliente = clienteRepository.findById(cfCliente)
            .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato"));

        Consenso consenso = new Consenso();
        consenso.setCliente(cliente);
        consenso.setTipologia(tipologia);
        // file.getBytes() lo carica in memoria e si affida al Driver (o JPA) per trasformarlo in un set di blob-bytes
        consenso.setFile(filePdf.getBytes());

        return consensoRepository.save(consenso);
    }
}
