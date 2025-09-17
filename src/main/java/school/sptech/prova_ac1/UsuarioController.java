package school.sptech.prova_ac1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        List<Usuario> usuariosListados = usuarioRepository.findAll();
        if (usuariosListados.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(usuariosListados);
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario)
    {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent() || usuarioRepository.findByCpf(usuario.getCpf()).isPresent())
            return ResponseEntity.status(409).build();

        Usuario criarUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.status(201).body(criarUsuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        return usuarioRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (!usuarioRepository.existsById(id))
            return ResponseEntity.notFound().build();
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> buscarPorDataNascimento(@RequestParam("nascimento") String nascimento) {
        LocalDate data = LocalDate.parse(nascimento);
        List<Usuario> usuarios = usuarioRepository.findByDataNascimentoAfter(data);
        if (usuarios.isEmpty())
            return ResponseEntity.noContent().build(); // 204
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        if (!usuarioRepository.existsById(id))
            return ResponseEntity.notFound().build();
        Optional<Usuario> emailExistente = usuarioRepository.findByEmail(usuario.getEmail());
        if (emailExistente.isPresent() && !emailExistente.get().getId().equals(id))
            return ResponseEntity.status(409).build();

        Optional<Usuario> cpfExistente = usuarioRepository.findByCpf(usuario.getCpf());
        if (cpfExistente.isPresent() && !cpfExistente.get().getId().equals(id))
            return ResponseEntity.status(409).build();

        usuario.setId(id);
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuarioAtualizado);
    }
}
