package hibernate;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="order_item")
public class OrderItems implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    
    @ManyToOne
    @JoinColumn(name="stock_id")
    private Stock stock;
    
    @Column(name = "qty", nullable = false)
    private int qty;
    
    @ManyToOne
    @JoinColumn(name="order_status_id")
    private OrderStatus orderStatus;
    
    @ManyToOne
    @JoinColumn(name="delivery_type_id")
    private DeliveryTypes deliveryTypes;
    

    @ManyToOne
    @JoinColumn(name="orders_id")
    private Orders orders;

    public OrderItems() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public DeliveryTypes getDeliveryTypes() {
        return deliveryTypes;
    }

    public void setDeliveryTypes(DeliveryTypes deliveryTypes) {
        this.deliveryTypes = deliveryTypes;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }
    
}
