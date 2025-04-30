package com.ecomarket.resenas.service;

import com.ecomarket.resenas.model.Resena;
import com.ecomarket.resenas.repository.ResenaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepo;

    public ResenaService(ResenaRepository resenaRepo) {
        this.resenaRepo = resenaRepo;
    }

    public Resena crear(Resena resena) {
        resena.setFecha(LocalDate.now());
        return resenaRepo.save(resena);
    }

    public List<Resena> listarTodas() {
        return resenaRepo.findAll();
    }

    public List<Resena> listarPorProducto(Long productoId) {
        return resenaRepo.findByProductoId(productoId);
    }

    public List<Resena> listarPorCliente(Long clienteId) {
        return resenaRepo.findByClienteId(clienteId);
    }

    public Optional<Resena> buscarPorId(Long id) {
        return resenaRepo.findById(id);
    }

    public Resena editar(Long id, Resena nuevaResena) {
        Resena actual = resenaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));

        actual.setPuntuacion(nuevaResena.getPuntuacion());
        actual.setComentario(nuevaResena.getComentario());
        actual.setFecha(LocalDate.now());

        return resenaRepo.save(actual);
    }

    public void eliminar(Long id) {
        resenaRepo.deleteById(id);
    }
}
