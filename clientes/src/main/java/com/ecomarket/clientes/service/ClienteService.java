package com.ecomarket.clientes.service;

import com.ecomarket.clientes.model.Cliente;
import com.ecomarket.clientes.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepo;

    public ClienteService(ClienteRepository clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    public Cliente registrar(Cliente cliente) {
        return clienteRepo.save(cliente);
    }

    public List<Cliente> obtenerTodos() {
        return clienteRepo.findAll();
    }

    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepo.findById(id);
    }

    public Cliente actualizar(Long id, Cliente clienteNuevo) {
        Cliente cliente = clienteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setNombre(clienteNuevo.getNombre());
        cliente.setApellido(clienteNuevo.getApellido());
        cliente.setRut(clienteNuevo.getRut());
        cliente.setCorreo(clienteNuevo.getCorreo());
        cliente.setTelefono(clienteNuevo.getTelefono());
        cliente.setDireccion(clienteNuevo.getDireccion());

        return clienteRepo.save(cliente);
    }

    public void eliminar(Long id) {
        clienteRepo.deleteById(id);
    }
}
