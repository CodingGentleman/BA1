package at.fhj.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = Order.TABLE_NAME)
public class Order extends AbstractBaseEntity {
	public static final String TABLE_NAME = "orders";
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "order_type")
	@NotNull
    private OrderType type;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval=true)
    private List<OrderLine> lines = new ArrayList<>();

    @OneToOne(optional = false, cascade = CascadeType.REMOVE)
	@NotNull
	private Address invoiceAddress;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Address deliveryAddress;

    @ManyToMany(mappedBy = "orders", cascade = CascadeType.REMOVE)
    private List<Voucher> vouchers = new ArrayList<>();

	@Column(name = "invoice_amount")
    private long invoiceAmount;

	@Column(name = "order_amount")
    private long orderAmount;

	@Column(name = "invoice_timestamp")
	private Date invoiceTimestamp;

    @Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
	}

	public Address getInvoiceAddress() {
		return invoiceAddress;
	}

	public void setInvoiceAddress(Address invoiceAddress) {
		this.invoiceAddress = invoiceAddress;
	}

	public Address getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(Address deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public List<Voucher> getVouchers() {
		return vouchers;
	}

	public void setVouchers(List<Voucher> vouchers) {
		this.vouchers = vouchers;
	}

	public List<OrderLine> getLines() {
		return lines;
	}

	public void setLines(List<OrderLine> lines) {
    	this.lines = lines;
	}

	public long getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(long invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public long getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(long orderAmount) {
		this.orderAmount = orderAmount;
	}

	public Date getInvoiceTimestamp() {
		return invoiceTimestamp;
	}

	public void setInvoiceTimestamp(Date invoiceTimestamp) {
		this.invoiceTimestamp = invoiceTimestamp;
	}
}
