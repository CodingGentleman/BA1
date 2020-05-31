package at.fhj.entities;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = Voucher.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
public class Voucher extends AbstractBaseEntity {
	public static final String TABLE_NAME = "voucher";

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
	
    private String code;
    private Double value;

    @ManyToMany
    @JoinTable(
            name = "voucher_order",
            joinColumns = { @JoinColumn(name = "voucher_code") },
            inverseJoinColumns = { @JoinColumn(name = "order_id") }
    )
    private Collection<Order> orders = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "voucher_orderline",
            joinColumns = { @JoinColumn(name = "voucher_code") },
            inverseJoinColumns = { @JoinColumn(name = "orderline_id") }
    )
    private Collection<OrderLine> orderLines = new ArrayList<>();

    @Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Collection<Order> getOrders() {
		return orders;
	}

	public void setOrders(Collection<Order> orders) {
		this.orders = orders;
	}

	public Collection<OrderLine> getOrderLines() {
		return orderLines;
	}

	public void setOrderLines(Collection<OrderLine> orderLines) {
		this.orderLines = orderLines;
	}

    
}
