// Arquivo: src/model/Pedido.java
package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Pedido {
    private int id;
    private Mesa mesa;
    private Garcom garcom;
    private ArrayList<Produto> produtos;
    private String status;
    private LocalDateTime dataHora;

    public Pedido(Mesa mesa, Garcom garcom, ArrayList<Produto> produtos, String status) {
        this.mesa = mesa;
        this.garcom = garcom;
        this.produtos = produtos;
        this.status = status;
        this.dataHora = LocalDateTime.now();
    }

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }

    public Garcom getGarcom() { return garcom; }
    public void setGarcom(Garcom garcom) { this.garcom = garcom; }

    public ArrayList<Produto> getProdutos() { return produtos; }
    public void setProdutos(ArrayList<Produto> produtos) { this.produtos = produtos; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    @Override
    public String toString() {
        return "Pedido da Mesa " + mesa.getNumero() + ", Garçom: " + garcom.getNome() +
                ", Produtos: " + produtos.size() + ", Status: " + status +
                ", Hora: " + dataHora;
    }
}
