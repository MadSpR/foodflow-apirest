package com.example.FoodFlow.controllers;

import com.example.FoodFlow.models.Usuario;
import com.example.FoodFlow.repositories.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@RestController
@RequestMapping(path = "/user") //URL starts with /user (after app path)
public class UsuarioController {
    @Autowired
    private UsuarioRepo usuarioRepo;

    private Base64.Encoder encoder = Base64.getEncoder();

    private String encryptPass(String pass){
        //password encryption
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(pass.getBytes());
            byte[] resumen = md.digest();
            pass = encoder.encodeToString(resumen);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } finally {
            return pass;
        }
    }

    @PostMapping(path = "/add") //maps ONLY POST petitions
    public @ResponseBody String addNewUser (@RequestParam String nombre, @RequestParam String email, @RequestParam String pass, @RequestParam String direccion){
        //@ResponseBody means the returned String is the response, not a view name
        //@RequestParam means it is a parameter from the GET or POST request
        Usuario usu = new Usuario();
        usu.setNombre(nombre);
        usu.setEmail(email);
        pass = encryptPass(pass);
        usu.setPassword(pass);
        usu.setDireccion(direccion);

        usuarioRepo.save(usu);
        return "Saved\n";
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Usuario> getAllUsers(){
        //this returns a JSON or XML with the users
        return usuarioRepo.findAll();
    }
}
