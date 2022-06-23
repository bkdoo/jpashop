package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count ){
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        //cascade 설정으로 delivery와 orderItem은 persist를 별도로 하지 않아도됨.
        //delivery와 orderItem은 다른 엔티티에서 참조하지 않으며, 오직 Order에서만 사용하므로 order에서 cascade 설정

        Order order = Order.createOrder(member, delivery, orderItem);
        // Order와 OrderItem은 생성 메소드가 존재하여, 일반적인 생성->setter 를 막아두는 것이 좋음.
        // 해당 Entity 내의 기본 생성자를 protected 로 만들어주면 됨
        // @NoArgsConstructor(access = AccessLevel.PROTECTED) 선언으로 쉽게 가능
        orderRepository.save(order);
        return order.getId();
    }

    //취소
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancelOrder();
    }

    //검색
    
}
