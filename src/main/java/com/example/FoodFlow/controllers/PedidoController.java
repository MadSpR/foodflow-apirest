package com.example.FoodFlow.controllers;

import com.example.FoodFlow.DTOs.SolicitudPedidoDTO;
import com.example.FoodFlow.models.Pedido;
import com.example.FoodFlow.services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Object> addNewPedido(@RequestBody SolicitudPedidoDTO solicitudPedidoDTO){
        try {
            Pedido newPedido = pedidoService.crearPedido(solicitudPedidoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPedido);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Pedido>> getPedidoByUser(@PathVariable Long userId){
        List<Pedido> pedidos = pedidoService.getPedidoByUser(userId);
        if(pedidos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(pedidos);
    }
}
