package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item {
    // 음반, 도서, 영화로 구현체를 생성할 것이기 때문에 추상클래스로 생성

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // 비즈니스 로직 //
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void minusStock(int quantity) {
        int restQuantity = this.stockQuantity - quantity;
        if(restQuantity<0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restQuantity;
    }
    

}
