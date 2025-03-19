package com.example.FoodFlow.services;

import com.example.FoodFlow.DTOs.ItemPedidoDTO;
import com.example.FoodFlow.DTOs.SolicitudPedidoDTO;
import com.example.FoodFlow.models.*;
import com.example.FoodFlow.repositories.PedidoRepo;
import com.example.FoodFlow.repositories.PlatoRepo;
import com.example.FoodFlow.repositories.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepo pedidoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PlatoRepo platoRepo;


    public Pedido crearPedido(SolicitudPedidoDTO solicitudPedidoDTO){

        try {
            Usuario usu = usuarioRepo.findById(solicitudPedidoDTO.getUsuarioId()).orElse(null);
            if (usu == null){
                throw new RuntimeException("Usuario no encontrado");
            }

            //Crea el pedido
            Pedido pedido = new Pedido();
            pedido.setUsuario(usu);
            pedido.setEstadoPedido(EstadoPedido.PENDIENTE);

            BigDecimal precioTotal = BigDecimal.ZERO;
            List<DetallePedido> detallePedidoList = new ArrayList<>();

            //Validar y agregar los platos al pedido
            for (ItemPedidoDTO item : solicitudPedidoDTO.getItems()){
                Plato plato = platoRepo.findById(item.getPlatoId()).orElse(null);
                if (plato == null){
                    throw new RuntimeException("Plato con ID " + item.getPlatoId() + " no encontrado");
                }

                DetallePedido detallePedido = new DetallePedido();
                detallePedido.setPedido(pedido);
                detallePedido.setPlato(plato);
                detallePedido.setCantidad(item.getCantidad());
                detallePedido.setPrecio_unitario(plato.getPrecio());

                precioTotal = precioTotal.add(plato.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
                detallePedidoList.add(detallePedido);
            }
            pedido.setTotal(precioTotal);
            pedido.setFecha_pedido(LocalDateTime.now());
            pedido.setDetallePedido(detallePedidoList);

            //guarda el pedido en la bbdd
            return pedidoRepo.save(pedido);
        } catch (Exception e){
            throw new RuntimeException("Error al crear el pedido: " + e.toString());
        }
    }

    public List<Pedido> getPedidoByUser(Long userId){
        return pedidoRepo.findByUsuarioId(userId);
    }
}
