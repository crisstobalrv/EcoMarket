package com.ecomarket.clientes.service;

import com.ecomarket.clientes.model.Cliente;
import com.ecomarket.clientes.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepo;
    private final RestTemplate restTemplate;

    public ClienteService(ClienteRepository clienteRepo, RestTemplate restTemplate) {
        this.clienteRepo = clienteRepo;
        this.restTemplate = restTemplate;
    }

    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepo.save(cliente);
    }


    public Cliente registrar(Cliente cliente) {
        // 1. Validar que exista en Autenticación
        if (!correoExisteEnAutenticacion(cliente.getCorreo())) {
            throw new RuntimeException("Primero debe registrarse como usuario.");
        }

        // 2. Validar que NO esté ya registrado en Clientes
        if (clienteRepo.findByCorreo(cliente.getCorreo()).isPresent()) {
            throw new RuntimeException("Este correo ya tiene un perfil como cliente.");
        }

        // 3. Validar RUT duplicado
        if (clienteRepo.findByRut(cliente.getRut()).isPresent()) {
            throw new RuntimeException("El RUT ya está registrado.");
        }

        return clienteRepo.save(cliente);
    }



    public List<Cliente> obtenerTodos() {
        return clienteRepo.findAll();
    }

    public Cliente obtenerPorId(Long id) {
        return clienteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public Cliente actualizar(Long id, Cliente clienteNuevo) {
        Cliente cliente = clienteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Validar que el correo no cambie
        if (!cliente.getCorreo().equals(clienteNuevo.getCorreo())) {
            throw new RuntimeException("No está permitido modificar el correo electrónico.");
        }

        // Validar que el RUT no cambie
        if (!cliente.getRut().equals(clienteNuevo.getRut())) {
            throw new RuntimeException("No está permitido modificar el RUT.");
        }

        // Actualizar solo los campos permitidos
        cliente.setNombre(clienteNuevo.getNombre());
        cliente.setApellido(clienteNuevo.getApellido());
        cliente.setTelefono(clienteNuevo.getTelefono());
        cliente.setDireccion(clienteNuevo.getDireccion());

        return clienteRepo.save(cliente);
    }


    public void eliminar(Long id) {
        clienteRepo.deleteById(id);
    }

    private boolean correoExisteEnAutenticacion(String correo) {
        String url = "http://localhost:8081/api/autenticacion/existe?email=" + correo;
        try {
            Boolean existe = restTemplate.getForObject(url, Boolean.class);
            return existe != null && existe;
        } catch (Exception e) {
            return false; // En caso de error, mejor dejar registrar
        }
    }
    public class ClienteNoEncontradoException extends RuntimeException {
        public ClienteNoEncontradoException(Long id) {
            super("Cliente con ID " + id + " no encontrado.");
        }
    }




}
